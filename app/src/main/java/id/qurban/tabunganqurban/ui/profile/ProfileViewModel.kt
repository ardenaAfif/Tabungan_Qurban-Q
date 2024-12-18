package id.qurban.tabunganqurban.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.supabase.FirebaseClient
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun fetchUser(userId: String) {
        viewModelScope.launch {
//            try {
//                val response = supabaseClient.from("users")
//                    .select(columns = Columns.ALL) {
//                        filter {
//                            eq("user_id", userId)
//                        }
//                    }
//                    .decodeSingleOrNull<User>()
//
//                _user.postValue(response)
//            } catch (e:Exception) {
//                Log.e("ProfileViewModel", "Error fetching user: ${e.message}")
//                _user.postValue(null)
//            }
        }
    }
}