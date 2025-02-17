package id.qurban.tabunganqurban.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        fetchUser()
    }

    fun fetchUser() {
        viewModelScope.launch { _user.emit(Resource.Loading()) }
        firestore.collection("users").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(user))
                        }
                    }
                }
            }
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        prodi: String,
        semester: Int
    ) {
        val userId = auth.uid ?: return
        val updatedData = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "prodi" to prodi,
            "semester" to semester
        )

        viewModelScope.launch {
            _user.emit(Resource.Loading())
            firestore.collection("users").document(userId)
                .update(updatedData)
                .addOnSuccessListener {
                    fetchUser()
                }
                .addOnFailureListener { exception ->
                    _user.value = Resource.Error(exception.message.toString())
                }
        }
    }

    fun logout() {
        auth.signOut()
    }
}