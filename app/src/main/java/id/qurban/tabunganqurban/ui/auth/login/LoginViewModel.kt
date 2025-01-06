package id.qurban.tabunganqurban.ui.auth.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.supabase.FirebaseClient
import id.qurban.tabunganqurban.utils.Constant.Constant.INTRODUCTION_KEY
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseClient: FirebaseClient
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        viewModelScope.launch {
            if (user != null) {
                _navigate.emit(TABUNGAN_ACTIVITY) // Langsung ke halaman Home
            } else if (isButtonClicked) {
                _navigate.emit(LOGIN_ACTIVITY) // Ke halaman Login
            } else {
                Unit
            }
        }
    }


    fun loginUser(email: String, password: String) {
        viewModelScope.launch { _login.emit(Resource.Loading()) }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    val user = firebaseClient.getUserDetails()
                    Log.d(">>LoginVM", "User Details: $user")
                    if (user?.prodi == "Admin") {
                        _navigate.emit(ADMIN_TABUNGAN)
                    } else {
                        _navigate.emit(TABUNGAN_ACTIVITY)
                    }
                    _login.emit(Resource.Success(it.user!!))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    companion object {
        const val ADMIN_TABUNGAN = 20
        const val TABUNGAN_ACTIVITY = 23
        const val LOGIN_ACTIVITY = 24
    }
}