package id.qurban.tabunganqurban.ui.auth.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.data.UserResponse
import id.qurban.tabunganqurban.supabase.SupabaseClient
import id.qurban.tabunganqurban.utils.PasswordHasher
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginStatus = MutableLiveData<UserResponse>()
    val loginStatus: LiveData<UserResponse> get() = _loginStatus

    private val supabaseClient = SupabaseClient.supabase

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginStatus.postValue(UserResponse.Loading)
            try {
                val response = supabaseClient.from("users")
                    .select(
                        columns = Columns.ALL
                    ) {
                        filter {
                            eq("email", email)
                            eq("password", password)
                        }
                    }
                    .decodeSingleOrNull<User>()

                if (response != null) {
                    _loginStatus.postValue(UserResponse.Success("Login Berhasil"))
                    saveUserId(response.user_id)
                } else {
                    Log.d("Login", "Password verified failed")
                    _loginStatus.postValue(UserResponse.Error("Email atau Password salah"))
                }
            } catch (e: Exception) {
                _loginStatus.postValue(UserResponse.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }

    private fun saveUserId(userId: String) {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("user_id", userId)
            .apply()

        Log.d("LoginViewModel", "User ID saved: $userId")
    }
}