package id.qurban.tabunganqurban.data

sealed class UserResponse {
    object Loading: UserResponse()
    data class Success(val message: String): UserResponse()
    data class Error(val message: String): UserResponse()
}