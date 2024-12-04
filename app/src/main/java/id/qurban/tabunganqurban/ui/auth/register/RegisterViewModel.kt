package id.qurban.tabunganqurban.ui.auth.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.data.UserResponse
import id.qurban.tabunganqurban.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _registrationStatus = MutableLiveData<UserResponse>()
    val registrationStatus: LiveData<UserResponse> get() = _registrationStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val supabase =
        SupabaseClient.supabase // Menggunakan SupabaseClient yang sudah diinisiasi

    fun registerUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _registrationStatus.value = UserResponse.Loading

            try {
                val result = supabase.from("users").insert(user)
//                    mapOf(
//                        "id" to user.userId,
//                        "first_name" to user.firstName,
//                        "last_name" to user.lastName,
//                        "email" to user.email,
//                        "password" to user.password,
//                        "prodi" to user.prodi,
//                        "semester" to user.semester,
//                        "total_tabungan" to user.totalTabungan,
//                        "created_at" to user.createdAt
//                    )
//                )
                Log.d("SupabaseDebug", "Result: ${result.data}, Error: ${result.headers}")

                if (result.data != null) {
                    _registrationStatus.value = UserResponse.Success("Akun berhasil dibuat.")
                } else {
                    _registrationStatus.value =
                        UserResponse.Error("Terjadi kesalahan: ${result.data}.")
                }
                Log.d("RegisterViewModel", "Registering user: $user")
            } catch (e: Exception) {
                _registrationStatus.value = UserResponse.Error(e.message ?: "Terjadi kesalahan.")
            } finally {
                _isLoading.value = false
            }

        }
    }

}