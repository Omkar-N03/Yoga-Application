package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dao.ProgressTrackerDao
import com.example.dao.UserDao
import com.example.dao.WeeklyRoutineDao
import com.example.dao.YogaPoseDao
import com.example.db.DatabaseHelper
import com.example.model.ProgressTracker
import com.example.model.User
import com.example.model.WeeklyRoutine
import com.example.model.YogaPose
import com.example.util.SessionManager
import com.example.util.YogaMediaUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class YogaViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper.getInstance(application)
    private val userDao = UserDao(application)
    private val poseDao = YogaPoseDao(application)
    private val routineDao = WeeklyRoutineDao(application)
    private val progressDao = ProgressTrackerDao(application)
    private val sessionManager = SessionManager(application)

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _selectedDay = MutableStateFlow(getCurrentDayOfWeek())
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    private val _currentDayRoutines = MutableStateFlow<List<WeeklyRoutine>>(emptyList())
    val currentDayRoutines: StateFlow<List<WeeklyRoutine>> = _currentDayRoutines.asStateFlow()

    private val _allPoses = MutableStateFlow<List<YogaPose>>(emptyList())
    val allPoses: StateFlow<List<YogaPose>> = _allPoses.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(listOf("All"))
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow("All")
    val selectedDifficulty: StateFlow<String> = _selectedDifficulty.asStateFlow()

    private val _progressLogs = MutableStateFlow<List<ProgressTracker>>(emptyList())
    val progressLogs: StateFlow<List<ProgressTracker>> = _progressLogs.asStateFlow()

    private val _totalCompletedAllTime = MutableStateFlow(0)
    val totalCompletedAllTime: StateFlow<Int> = _totalCompletedAllTime.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkSession()
        loadInitialData()
    }

    private fun checkSession() {
        if (sessionManager.isLoggedIn) {
            val userId = sessionManager.userId
            viewModelScope.launch(Dispatchers.IO) {
                val user = userDao.getUserById(userId)
                withContext(Dispatchers.Main) {
                    _currentUser.value = user
                    if (user != null) {
                        loadUserData(user.id)
                    }
                }
            }
        }
    }

    fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Ensure seeded
            dbHelper.seedYogaPosesIfEmpty()
            val cats = poseDao.allCategories
            val poses = poseDao.getAllPoses()

            withContext(Dispatchers.Main) {
                _categories.value = cats
                _allPoses.value = poses
            }
        }
    }

    fun loadUserData(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val routines = routineDao.getRoutinesForDay(userId, _selectedDay.value)
            val logs = progressDao.getLogsForUser(userId)
            val completedCount = routineDao.getTotalCompletedAllTime(userId)

            withContext(Dispatchers.Main) {
                _currentDayRoutines.value = routines
                _progressLogs.value = logs
                _totalCompletedAllTime.value = completedCount
            }
        }
    }

    fun login(identifier: String, pass: String, onSuccess: () -> Unit) {
        if (identifier.isBlank() || pass.isBlank()) {
            _authError.value = "Please enter username/email and password"
            return
        }
        _isLoading.value = true
        _authError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.login(identifier.trim(), pass)
            withContext(Dispatchers.Main) {
                _isLoading.value = false
                if (user != null) {
                    sessionManager.createLoginSession(user.id, user.username, user.email, user.profileImagePath)
                    _currentUser.value = user
                    loadUserData(user.id)
                    onSuccess()
                } else {
                    _authError.value = "Invalid username or password"
                }
            }
        }
    }

    fun register(username: String, email: String, pass: String, passConfirm: String, onSuccess: () -> Unit) {
        if (username.isBlank() || email.isBlank() || pass.isBlank()) {
            _authError.value = "Please fill in all fields"
            return
        }
        if (pass != passConfirm) {
            _authError.value = "Passwords do not match"
            return
        }
        if (pass.length < 4) {
            _authError.value = "Password must be at least 4 characters"
            return
        }

        _isLoading.value = true
        _authError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            if (userDao.isUsernameTaken(username.trim())) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _authError.value = "Username already taken"
                }
                return@launch
            }
            if (userDao.isEmailTaken(email.trim())) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _authError.value = "Email is already registered"
                }
                return@launch
            }

            val newUser = User(username.trim(), email.trim(), pass, "")
            val id = userDao.registerUser(newUser)
            if (id > 0) {
                newUser.id = id.toInt()
                sessionManager.createLoginSession(newUser.id, newUser.username, newUser.email, "")
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _currentUser.value = newUser
                    loadUserData(newUser.id)
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _authError.value = "Registration failed, please try again"
                }
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        _currentUser.value = null
        _currentDayRoutines.value = emptyList()
        _progressLogs.value = emptyList()
        _totalCompletedAllTime.value = 0
    }

    fun selectDay(day: String) {
        _selectedDay.value = day
        _currentUser.value?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                val routines = routineDao.getRoutinesForDay(user.id, day)
                withContext(Dispatchers.Main) {
                    _currentDayRoutines.value = routines
                }
            }
        }
    }

    fun toggleRoutineCompletion(routine: WeeklyRoutine) {
        val newStatus = if (routine.isCompleted == 1) 0 else 1
        val userId = _currentUser.value?.id ?: return

        viewModelScope.launch(Dispatchers.IO) {
            routineDao.updateRoutineCompletion(routine.routineId, newStatus)
            val updatedRoutines = routineDao.getRoutinesForDay(userId, _selectedDay.value)
            val completedAllTime = routineDao.getTotalCompletedAllTime(userId)
            withContext(Dispatchers.Main) {
                _currentDayRoutines.value = updatedRoutines
                _totalCompletedAllTime.value = completedAllTime
            }
        }
    }

    fun addRoutineItem(day: String, poseId: String, durationSec: Int, notes: String, onDone: () -> Unit) {
        val userId = _currentUser.value?.id ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val item = WeeklyRoutine(userId, day, poseId, durationSec, 0, notes)
            routineDao.addRoutine(item)
            val updatedRoutines = routineDao.getRoutinesForDay(userId, _selectedDay.value)
            withContext(Dispatchers.Main) {
                _currentDayRoutines.value = updatedRoutines
                onDone()
            }
        }
    }

    fun updateRoutineItem(routineId: Int, durationSec: Int, notes: String, onDone: () -> Unit) {
        val userId = _currentUser.value?.id ?: return

        viewModelScope.launch(Dispatchers.IO) {
            routineDao.updateRoutine(routineId, durationSec, notes)
            val updatedRoutines = routineDao.getRoutinesForDay(userId, _selectedDay.value)
            withContext(Dispatchers.Main) {
                _currentDayRoutines.value = updatedRoutines
                onDone()
            }
        }
    }

    fun deleteRoutineItem(routineId: Int) {
        val userId = _currentUser.value?.id ?: return

        viewModelScope.launch(Dispatchers.IO) {
            routineDao.deleteRoutine(routineId)
            val updatedRoutines = routineDao.getRoutinesForDay(userId, _selectedDay.value)
            val completedAllTime = routineDao.getTotalCompletedAllTime(userId)
            withContext(Dispatchers.Main) {
                _currentDayRoutines.value = updatedRoutines
                _totalCompletedAllTime.value = completedAllTime
            }
        }
    }

    fun addProgressLog(imagePath: String, poseName: String, onDone: () -> Unit) {
        val userId = _currentUser.value?.id ?: return
        val dateLogged = YogaMediaUtils.getFormattedCurrentDate()

        viewModelScope.launch(Dispatchers.IO) {
            val log = ProgressTracker(userId, imagePath, dateLogged, poseName)
            progressDao.addProgressLog(log)
            val logs = progressDao.getLogsForUser(userId)
            withContext(Dispatchers.Main) {
                _progressLogs.value = logs
                onDone()
            }
        }
    }

    fun deleteProgressLog(logId: Int) {
        val userId = _currentUser.value?.id ?: return

        viewModelScope.launch(Dispatchers.IO) {
            progressDao.deleteProgressLog(logId)
            val logs = progressDao.getLogsForUser(userId)
            withContext(Dispatchers.Main) {
                _progressLogs.value = logs
            }
        }
    }

    fun updateProfileImage(imagePath: String) {
        val user = _currentUser.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateProfileImage(user.id, imagePath)
            sessionManager.updateProfileImage(imagePath)
            val refreshed = userDao.getUserById(user.id)
            withContext(Dispatchers.Main) {
                _currentUser.value = refreshed
            }
        }
    }

    fun filterPoses(query: String = _searchQuery.value, category: String = _selectedCategory.value, difficulty: String = _selectedDifficulty.value) {
        _searchQuery.value = query
        _selectedCategory.value = category
        _selectedDifficulty.value = difficulty

        viewModelScope.launch(Dispatchers.IO) {
            val filtered = poseDao.searchAndFilter(query, category, difficulty)
            withContext(Dispatchers.Main) {
                _allPoses.value = filtered
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    private fun getCurrentDayOfWeek(): String {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            Calendar.SUNDAY -> "Sunday"
            else -> "Monday"
        }
    }
}
