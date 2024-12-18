package id.qurban.tabunganqurban.utils

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Email tidak boleh kosong")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Format email salah")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty())
        return RegisterValidation.Failed("Password tidak boleh kosong")

    if (password.length < 6)
        return RegisterValidation.Failed("Password minimal 6 karakter")

    return RegisterValidation.Success
}

fun validateFirstName(name: String): RegisterValidation{
    if (name.isEmpty())
        return RegisterValidation.Failed("Nama Pertama tidak boleh kosong")

    return RegisterValidation.Success
}

fun validateProdi(prodi: String): RegisterValidation{
    if (prodi.isEmpty())
        return RegisterValidation.Failed("Prodi tidak boleh kosong")

    return RegisterValidation.Success
}

fun validateSemester(semester: String): RegisterValidation{
    if (semester.isEmpty())
        return RegisterValidation.Failed("Semester tidak boleh kosong")

    return RegisterValidation.Success
}

fun validateEmpty(name: String, password: String, email: String): RegisterValidation{
    if (name.isEmpty() && email.isEmpty() && password.isEmpty())
        return RegisterValidation.Failed("Harap diisi dengan lengkap!")

    return RegisterValidation.Success
}