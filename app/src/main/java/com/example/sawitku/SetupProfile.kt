package com.example.sawitku.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.sawitku.HomeActivity
import com.example.sawitku.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class SetupProfileFragment : Fragment() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etNoHp: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etLuas: TextInputEditText
    private lateinit var spinnerWilayah: Spinner
    private lateinit var btnLanjut: Button
    private lateinit var btnSelesai: Button
    private lateinit var ivFotoProfil: ImageView
    private lateinit var btnAmbilFoto: Button
    private lateinit var btnUploadFoto: Button

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var selectedWilayahId: String = ""
    private var fotoProfilUrl: String? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? android.graphics.Bitmap
            bitmap?.let {
                ivFotoProfil.setImageBitmap(it)
                uploadFotoToFirebase(it)
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            ivFotoProfil.setImageURI(it)
            uploadFotoToFirebase(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_setup_profile, container, false)

        etNama = v.findViewById(R.id.et_nama_lengkap)
        etNoHp = v.findViewById(R.id.et_no_hp)
        etAlamat = v.findViewById(R.id.et_alamat)
        etLuas = v.findViewById(R.id.et_luas_lahan)
        spinnerWilayah = v.findViewById(R.id.spinner_wilayah)
        btnLanjut = v.findViewById(R.id.btn_lanjut)
        btnSelesai = v.findViewById(R.id.btn_selesai)
        ivFotoProfil = v.findViewById(R.id.iv_foto_lahan) // ganti nama variabel aja, ID XML boleh tetap
        btnAmbilFoto = v.findViewById(R.id.btn_ambil_foto)
        btnUploadFoto = v.findViewById(R.id.btn_upload_foto)

        setupSpinnerWilayah()
        setupButtonListeners(v)
        setupPhotoButtons()

        return v
    }

    private fun setupSpinnerWilayah() {
        val wilayahList = listOf(
            "Pilih Wilayah",
            "Aceh", "Bali", "Banten", "Bengkulu", "Daerah Istimewa Yogyakarta",
            "DKI Jakarta", "Gorontalo", "Jambi", "Jawa Barat", "Jawa Tengah",
            "Jawa Timur", "Kalimantan Barat", "Kalimantan Selatan", "Kalimantan Tengah",
            "Kalimantan Timur", "Kalimantan Utara", "Kepulauan Bangka Belitung",
            "Kepulauan Riau", "Lampung", "Maluku", "Maluku Utara", "Nusa Tenggara Barat",
            "Nusa Tenggara Timur", "Papua", "Papua Barat", "Papua Barat Daya",
            "Papua Pegunungan", "Papua Selatan", "Papua Tengah", "Riau",
            "Sulawesi Barat", "Sulawesi Selatan", "Sulawesi Tengah",
            "Sulawesi Tenggara", "Sulawesi Utara", "Sumatera Barat",
            "Sumatera Selatan", "Sumatera Utara"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, wilayahList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWilayah.adapter = adapter

        spinnerWilayah.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedWilayahId = if (position > 0) wilayahList[position] else ""
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtonListeners(view: View) {
        btnLanjut.setOnClickListener {
            if (etNama.text.isNullOrBlank() || etNoHp.text.isNullOrBlank() || selectedWilayahId.isEmpty()) {
                Snackbar.make(view, "Isi semua data pada langkah 1 terlebih dahulu", Snackbar.LENGTH_LONG).show()
            } else {
                view.findViewById<LinearLayout>(R.id.layout_step1).visibility = View.GONE
                view.findViewById<ScrollView>(R.id.layout_step2).visibility = View.VISIBLE
                btnSelesai.visibility = View.VISIBLE
            }
        }

        btnSelesai.setOnClickListener {
            simpanDataKeFirestore(view)
        }
    }

    private fun setupPhotoButtons() {
        btnAmbilFoto.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }
        btnUploadFoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    /** ========= Upload Foto Profil ========= **/
    private fun uploadFotoToFirebase(bitmap: android.graphics.Bitmap) {
        val uid = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("users/profile/$uid.jpg")
        val baos = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    fotoProfilUrl = uri.toString()
                    Toast.makeText(requireContext(), "Foto profil berhasil diunggah ✅", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal upload foto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadFotoToFirebase(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("users/profile/$uid.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    fotoProfilUrl = downloadUri.toString()
                    Toast.makeText(requireContext(), "Foto profil berhasil diunggah ✅", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal upload foto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /** ========= Simpan Profil ========= **/
    private fun simpanDataKeFirestore(view: View) {
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val luas = etLuas.text.toString().trim().toDoubleOrNull()
        val noHp = etNoHp.text.toString().trim()
        val wilayah = selectedWilayahId
        val fotoUrl = fotoProfilUrl ?: ""

        if (nama.isEmpty() || wilayah.isEmpty() || luas == null) {
            Snackbar.make(view, "Pastikan data wajib terisi dengan benar", Snackbar.LENGTH_LONG).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return

        val dataUser = hashMapOf(
            "id" to uid,
            "nama" to nama,
            "alamat" to alamat,
            "no_hp" to noHp,
            "luas_lahan" to luas,
            "foto_url" to fotoUrl,
            "wilayahId" to wilayah,
            "profileComplete" to true,
            "role" to "Petani"
        )

        firestore.collection("users").document(uid)
            .set(dataUser)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profil berhasil disimpan 🎉", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}