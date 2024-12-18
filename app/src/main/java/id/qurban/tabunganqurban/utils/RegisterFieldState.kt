package id.qurban.tabunganqurban.utils

sealed class RegisterValidation() {
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}

data class RegisterFieldState(
    val empty: RegisterValidation,
    val name: RegisterValidation,
    val prodi: RegisterValidation,
    val semester: RegisterValidation,
    val email: RegisterValidation,
    val password: RegisterValidation
)

