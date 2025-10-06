package com.example.petanisawit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sawitku.HomeActivity
import com.example.sawitku.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SetupProfilFragment : Fragment() {

    // --- Views dari Layout ---
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var btnSelesai: Button
    private lateinit var btnLewati: Button

    // Step 1
    private lateinit var layoutStep1: LinearLayout
    private lateinit var tilNamaLengkap: TextInputLayout
    private lateinit var etNamaLengkap: TextInputEditText
    private lateinit var btnLanjutStep1: Button

    // Step 2
    private lateinit var layoutStep2: LinearLayout
    private lateinit var spinnerWilayah: Spinner
    private lateinit var tilAlamatLengkap: TextInputLayout
    private lateinit var etAlamatLengkap: TextInputEditText
    private lateinit var tilLuasLahan: TextInputLayout
    private lateinit var etLuasLahan: TextInputEditText
    private lateinit var btnAmbilFoto1: Button
    private lateinit var btnUploadGaleri1: Button
    private lateinit var ivPhoto1: ImageView
    private lateinit var ivPhoto2: ImageView

    // --- Variabel Logika ---
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private var currentPhotoUri1: Uri? = null
    private var currentPhotoUri2: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setup_profile, container, false)
        initViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showStep1()

        // Step 1: Lanjut
        btnLanjutStep1.setOnClickListener {
            val namaLengkap = etNamaLengkap.text.toString().trim()
            if (namaLengkap.isEmpty()) {
                tilNamaLengkap.error = "Nama Lengkap tidak boleh kosong"
            } else {
                tilNamaLengkap.error = null
                showStep2()
            }
        }

        // Step 2: Spinner setup
        val wilayahOptions = arrayOf("Pilih Wilayah (Provinsi/Kabupaten)", "Riau", "Kalimantan Tengah", "Sumatera Utara")
        spinnerWilayah.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, wilayahOptions).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val satuanOptions = arrayOf("Ha (Hektar)", "Meter Persegi")
        val spinnerSatuanLuas: Spinner = view.findViewById(R.id.spinner_satuan_luas)
        spinnerSatuanLuas.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, satuanOptions).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Step 2: Ambil / upload foto
        btnAmbilFoto1.setOnClickListener { dispatchTakePictureIntent() }
        btnUploadGaleri1.setOnClickListener { dispatchPickFromGalleryIntent() }

        // Step 2: Selesai
        btnSelesai.setOnClickListener {
            if (validateInputsStep2()) {
                Toast.makeText(requireContext(), "Setup Profil dan Lahan Awal Selesai!", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        btnLewati.setOnClickListener {
            Toast.makeText(requireContext(), "Lengkapi profil nanti di pengaturan.", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun initViews(view: View) {
        tvTitle = view.findViewById(R.id.tv_title)
        tvSubtitle = view.findViewById(R.id.tv_subtitle)
        dot1 = view.findViewById(R.id.dot_1)
        dot2 = view.findViewById(R.id.dot_2)
        btnSelesai = view.findViewById(R.id.btn_selesai)
        btnLewati = view.findViewById(R.id.btn_lewati)

        layoutStep1 = view.findViewById(R.id.layout_step1)
        tilNamaLengkap = view.findViewById(R.id.til_nama_lengkap)
        etNamaLengkap = view.findViewById(R.id.et_nama_lengkap)
        btnLanjutStep1 = view.findViewById(R.id.btn_lanjut)

        layoutStep2 = view.findViewById(R.id.layout_step2)
        spinnerWilayah = view.findViewById(R.id.spinner_wilayah)
        tilAlamatLengkap = view.findViewById(R.id.til_alamat_lengkap)
        etAlamatLengkap = view.findViewById(R.id.et_alamat_lengkap)
        tilLuasLahan = view.findViewById(R.id.til_luas_lahan)
        etLuasLahan = view.findViewById(R.id.et_luas_lahan)
        btnAmbilFoto1 = view.findViewById(R.id.btn_ambil_foto1)
        btnUploadGaleri1 = view.findViewById(R.id.btn_upload_galeri1)
        ivPhoto1 = view.findViewById(R.id.iv_photo1)
        ivPhoto2 = view.findViewById(R.id.iv_photo2)
    }

    private fun showStep1() {
        tvSubtitle.text = "Langkah 1/2: Data Diri"
        dot1.setBackgroundResource(R.drawable.dot_active)
        dot2.setBackgroundResource(R.drawable.dot_inactive)
        layoutStep1.visibility = View.VISIBLE
        layoutStep2.visibility = View.GONE
        btnSelesai.visibility = View.GONE
        btnLewati.visibility = View.GONE
        btnLanjutStep1.visibility = View.VISIBLE
    }

    private fun showStep2() {
        tvSubtitle.text = "Langkah 2/2: Wilayah & Lahan Awal"
        dot1.setBackgroundResource(R.drawable.dot_inactive)
        dot2.setBackgroundResource(R.drawable.dot_active)
        layoutStep1.visibility = View.GONE
        layoutStep2.visibility = View.VISIBLE
        btnSelesai.visibility = View.VISIBLE
        btnLewati.visibility = View.VISIBLE
        btnLanjutStep1.visibility = View.GONE
    }

    private fun validateInputsStep2(): Boolean {
        var isValid = true
        if (spinnerWilayah.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Silakan pilih wilayah", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (etAlamatLengkap.text.isNullOrBlank()) {
            tilAlamatLengkap.error = "Alamat tidak boleh kosong"
            isValid = false
        } else tilAlamatLengkap.error = null

        if (etLuasLahan.text.isNullOrBlank()) {
            tilLuasLahan.error = "Luas lahan tidak boleh kosong"
            isValid = false
        } else tilLuasLahan.error = null

        if (currentPhotoUri1 == null && currentPhotoUri2 == null) {
            Toast.makeText(requireContext(), "Minimal unggah satu foto lahan", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(requireContext(), "Tidak ada aplikasi kamera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchPickFromGalleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as? android.graphics.Bitmap
                    imageBitmap?.let {
                        val tempUri = saveBitmapAndGetUri(it)
                        if (currentPhotoUri1 == null) {
                            currentPhotoUri1 = tempUri
                            ivPhoto1.setImageBitmap(it)
                            ivPhoto1.visibility = View.VISIBLE
                        } else if (currentPhotoUri2 == null) {
                            currentPhotoUri2 = tempUri
                            ivPhoto2.setImageBitmap(it)
                            ivPhoto2.visibility = View.VISIBLE
                        }
                    }
                }
                REQUEST_IMAGE_GALLERY -> {
                    data?.data?.let { uri ->
                        if (currentPhotoUri1 == null) {
                            currentPhotoUri1 = uri
                            ivPhoto1.setImageURI(uri)
                            ivPhoto1.visibility = View.VISIBLE
                        } else if (currentPhotoUri2 == null) {
                            currentPhotoUri2 = uri
                            ivPhoto2.setImageURI(uri)
                            ivPhoto2.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun saveBitmapAndGetUri(bitmap: android.graphics.Bitmap): Uri? {
        val tempFile = java.io.File(requireContext().cacheDir, "${System.currentTimeMillis()}.png")
        tempFile.createNewFile()
        val bos = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bos)
        val bitmapdata = bos.toByteArray()
        return try {
            val fos = java.io.FileOutputStream(tempFile)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            androidx.core.content.FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                tempFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}