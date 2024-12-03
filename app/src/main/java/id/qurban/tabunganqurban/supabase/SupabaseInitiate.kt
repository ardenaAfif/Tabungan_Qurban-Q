package id.qurban.tabunganqurban.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class SupabaseInitiate(
    private val supabaseUrl: String = "https://snvscjiwhjvsymcbeisn.supabase.co",
    private val supabaseKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNudnNjaml3aGp2c3ltY2JlaXNuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMyMTI1NTYsImV4cCI6MjA0ODc4ODU1Nn0.r2V5Ecjqa9jpr1QHhXbzZx4atk0JV0V02AT5LoppnNc"
) {
    val supabase = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Auth)
        install(Postgrest)
    }
}
