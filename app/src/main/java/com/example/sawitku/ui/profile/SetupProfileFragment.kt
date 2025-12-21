package com.example.sawitku.ui.profile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sawitku.HomeActivity
import com.example.sawitku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SetupProfileFragment : Fragment() {

    private lateinit var etNama: EditText
    private lateinit var etPhone: EditText   // <--- Kalau ini hilang, error muncul
    private lateinit var etAlamat: EditText  // <--- Kalau ini hilang, error muncul
    private lateinit var spinnerRegion: Spinner
    private lateinit var btnSimpan: Button
    private lateinit var tvTitle: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Daftar wilayah untuk dropdown (Sesuaikan dengan kebutuhanmu)
    private val listWilayah = arrayOf("Pilih Wilayah", "Aceh", "Sumut", "Riau", "Jambi", "Sumsel", "Lampung", "Kalimantan", "Sulawesi", "DIY", "Jateng", "Jatim")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup_profile, container, false)

        // Inisialisasi View
        etNama = view.findViewById(R.id.etNama)
        etPhone = view.findViewById(R.id.etPhone)
        etAlamat = view.findViewById(R.id.etAlamat)
        spinnerRegion = view.findViewById(R.id.spinnerRegion)
        btnSimpan = view.findViewById(R.id.btnSimpan)
        tvTitle = view.findViewById(R.id.tvTitle)

        // Setup Spinner Adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listWilayah)
        spinnerRegion.adapter = adapter

        // 1. CEK MODE EDIT (Sinyal dari Activity)
        val isEditMode = arguments?.getBoolean("IS_EDIT_MODE") ?: false

        if (isEditMode) {
            tvTitle.text = "Edit Profil"
            btnSimpan.text = "Update Profil"
            loadUserData() // Download data lama
        }

        btnSimpan.setOnClickListener {
            simpanData(isEditMode)
        }

        return view
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        // Tampilkan loading sederhana (opsional)
        btnSimpan.isEnabled = false
        btnSimpan.text = "Memuat..."

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    etNama.setText(doc.getString("name"))
                    etPhone.setText(doc.getString("phone"))
                    etAlamat.setText(doc.getString("address"))

                    // Set spinner sesuai data lama
                    val region = doc.getString("region")
                    if (region != null) {
                        val position = listWilayah.indexOf(region)
                        if (position >= 0) spinnerRegion.setSelection(position)
                    }
                }
                btnSimpan.isEnabled = true
                btnSimpan.text = "Update Profil"
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                btnSimpan.isEnabled = true
            }
    }

    private fun simpanData(isEditMode: Boolean) {
        val nama = etNama.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val region = spinnerRegion.selectedItem.toString()

        if (nama.isEmpty() || phone.isEmpty() || alamat.isEmpty() || region == "Pilih Wilayah") {
            Toast.makeText(context, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return

        // Data yang akan disimpan
        val userMap = hashMapOf(
            "name" to nama,
            "phone" to phone,
            "address" to alamat,
            "region" to region,
            "profileComplete" to true // Penanda profil sudah lengkap
        )

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Menyimpan data...")
        progressDialog.show()

        // Simpan ke Firestore (SetOptions.merge() agar tidak menimpa field lain spt role/email)
        db.collection("users").document(uid)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                progressDialog.dismiss()

                // PENTING: Update SharedPreferences agar wilayah baru langsung terbaca sistem
                val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("user_region", region) // Update region lokal
                    apply()
                }

                Toast.makeText(context, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show()

                if (isEditMode) {
                    // Jika mode edit, tutup activity (kembali ke Settings)
                    requireActivity().finish()
                } else {
                    // Jika pengguna baru, lanjut ke Home
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(context, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}