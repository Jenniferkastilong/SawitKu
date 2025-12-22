package com.example.sawitku

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfilAdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profil_admin, container, false)

        val tvNama = view.findViewById<TextView>(R.id.tvNamaAdmin)
        val tvRole = view.findViewById<TextView>(R.id.tvRoleAdmin)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmailAdmin)
        val btnEdit = view.findViewById<Button>(R.id.btnEditProfil)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val user = FirebaseAuth.getInstance().currentUser

        // DATA ADMIN
        tvNama.text = "Admin"
        tvRole.text = "Admin Utama"
        tvEmail.text = user?.email ?: "-"

        // EDIT PROFIL
        btnEdit.setOnClickListener {
            startActivity(Intent(requireContext(), SetupProfileActivity::class.java))
        }

        // LOGOUT
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        return view
    }
}
