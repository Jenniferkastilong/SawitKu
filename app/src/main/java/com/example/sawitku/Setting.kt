package com.example.sawitku // Pastikan package ini sesuai lokasi file kamu (mungkin com.example.sawitku.ui)

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sawitku.LoginActivity
import com.example.sawitku.R
import com.example.sawitku.SetupProfileActivity
import com.google.firebase.auth.FirebaseAuth

// Nama class disesuaikan dengan nama file kamu: "Setting"
class Setting : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout XML yang baru kita perbagus
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // Inisialisasi View berdasarkan ID di XML baru
        // Kita pakai LinearLayout karena di XML tombolnya berupa LinearLayout
        val btnEditProfile = view.findViewById<LinearLayout>(R.id.btn_edit_profile)
        val btnLogout = view.findViewById<LinearLayout>(R.id.btn_logout)
        val btnFaq = view.findViewById<LinearLayout>(R.id.btn_faq)
        val btnHubungi = view.findViewById<LinearLayout>(R.id.btn_hubungi)

        // 1. Logic Tombol Edit Profil
        btnEditProfile.setOnClickListener {
            val intent = Intent(activity, SetupProfileActivity::class.java)
            intent.putExtra("is_edit_mode", true) // Kirim sinyal kalau ini mode edit
            startActivity(intent)
        }

        // 2. Logic Tombol Logout
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        // 3. Logic Tombol FAQ (Sementara Toast dulu)
        btnFaq.setOnClickListener {
            Toast.makeText(context, "Menu FAQ akan segera hadir!", Toast.LENGTH_SHORT).show()
        }

        // 4. Logic Tombol Hubungi (Sementara Toast dulu)
        btnHubungi.setOnClickListener {
            Toast.makeText(context, "Silakan hubungi admin via WhatsApp/Email", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(context)
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        // 1. Sign out dari Firebase Authentication
        FirebaseAuth.getInstance().signOut()

        // 2. Hapus data session lokal (Region & Role) agar bersih
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // 3. Kembali ke LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        // Hapus history stack agar user tidak bisa tekan tombol 'Back' kembali ke dashboard
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // 4. Tutup Activity saat ini
        activity?.finish()

        Toast.makeText(context, "Berhasil keluar", Toast.LENGTH_SHORT).show()
    }
}