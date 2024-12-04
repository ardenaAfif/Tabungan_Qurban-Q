package id.qurban.tabunganqurban.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClient {
    private val supabaseUrl: String = "https://snvscjiwhjvsymcbeisn.supabase.co"
    private val supabaseKey: String =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNudnNjaml3aGp2c3ltY2JlaXNuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMyMTI1NTYsImV4cCI6MjA0ODc4ODU1Nn0.r2V5Ecjqa9jpr1QHhXbzZx4atk0JV0V02AT5LoppnNc"

    val supabase = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey,
    ) {
        defaultSerializer = KotlinXSerializer(
            Json {
                ignoreUnknownKeys = true // Abaikan properti yang tidak dikenal
                isLenient = true         // Parsing JSON longgar
                encodeDefaults = true    // Encode properti default
                prettyPrint = false      // Hindari pretty print untuk efisiensi
            }
        )
//        install(Auth)
        install(Postgrest)
    }
}