package id.qurban.tabunganqurban.supabase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseClient(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val tabunganCollection = firestore.collection("users").document(auth.uid!!).collection("tabungan")

}