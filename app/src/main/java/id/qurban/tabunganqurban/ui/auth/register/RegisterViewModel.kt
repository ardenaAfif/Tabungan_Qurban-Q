package id.qurban.tabunganqurban.ui.auth.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.utils.Constant.Constant.USER_COLLECTION
import id.qurban.tabunganqurban.utils.RegisterFieldState
import id.qurban.tabunganqurban.utils.RegisterValidation
import id.qurban.tabunganqurban.utils.Resource
import id.qurban.tabunganqurban.utils.validateEmail
import id.qurban.tabunganqurban.utils.validateEmpty
import id.qurban.tabunganqurban.utils.validateFirstName
import id.qurban.tabunganqurban.utils.validatePassword
import id.qurban.tabunganqurban.utils.validateProdi
import id.qurban.tabunganqurban.utils.validateSemester
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun createAccuntWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerFieldState = RegisterFieldState(
                validateEmpty(user.firstName, user.email, password),
                validateEmail(user.email),
                validatePassword(password),
                validateSemester(user.semester.toString()),
                validateProdi(user.prodi),
                validateFirstName(user.firstName)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldRegister
    }

}