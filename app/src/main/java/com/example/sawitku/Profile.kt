package com.example.sawitku

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val textName: TextView = view.findViewById(R.id.text_name)
        val textId: TextView = view.findViewById(R.id.text_id)
        val textPhone: TextView = view.findViewById(R.id.text_phone)
        val textAddress: TextView = view.findViewById(R.id.text_address)
        val textBirth: TextView = view.findViewById(R.id.text_birth)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        textName.text = document.getString("name") ?: "User"
                        textId.text = "ID: ${document.getString("id") ?: "-"}"
                        textPhone.text = "No. HP: ${document.getString("phone") ?: "-"}"
                        textAddress.text = "Alamat: ${document.getString("address") ?: "-"}"
                        textBirth.text = "Tanggal Lahir: ${document.getString("birth") ?: "-"}"
                    }
                }

        }


        return view
    }
}