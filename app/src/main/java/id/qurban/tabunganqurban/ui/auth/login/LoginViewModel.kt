package id.qurban.tabunganqurban.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.data.UserResponse
import id.qurban.tabunganqurban.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginStatus = MutableLiveData<UserResponse>()
    val loginStatus: LiveData<UserResponse> get() = _loginStatus

    private val supabaseClient = SupabaseClient.supabase

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginStatus.postValue(UserResponse.Loading)
            try {
                val response = supabaseClient.from("users").select().decodeSingleOrNull<User>()

                if (response != null) {
                    _loginStatus.postValue(UserResponse.Success("Login Berhasil"))
                } else {
                    _loginStatus.postValue(UserResponse.Error("Email atau Password salah"))
                }
            } catch (e: Exception) {
                _loginStatus.postValue(UserResponse.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }

}