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

class ProfilKonsultanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profil_konsultan, container, false)

        val tvNama = view.findViewById<TextView>(R.id.tvNamaKonsultan)
        val tvRole = view.findViewById<TextView>(R.id.tvRoleKonsultan)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmailKonsultan)
        val btnEdit = view.findViewById<Button>(R.id.btnEditProfil)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val user = FirebaseAuth.getInstance().currentUser

        // DATA ADMIN
        tvNama.text = "Pengurus"
        tvRole.text = "Pengurus Utama"
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

