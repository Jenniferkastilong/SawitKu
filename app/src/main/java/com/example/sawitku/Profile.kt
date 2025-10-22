package com.example.sawitku.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sawitku.MainActivity
import com.example.sawitku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Profile : Fragment() {

    private lateinit var ivProfile: ImageView
    private lateinit var textNama: TextView
    private lateinit var textMail: TextView
    private lateinit var textPhone: TextView
    private lateinit var textWilayah: TextView
    private lateinit var textRole: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnSimpan: Button
    private lateinit var btnLogout: Button
    private lateinit var btnDeleteAccount: Button
    private lateinit var rvHistory: RecyclerView

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var isEditing = false
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        ivProfile = view.findViewById(R.id.iv_profile)
        textNama = view.findViewById(R.id.text_nama)
        textMail = view.findViewById(R.id.text_mail)
        textPhone = view.findViewById(R.id.text_phone)
        textWilayah = view.findViewById(R.id.text_wilayah)
        textRole = view.findViewById(R.id.text_role)
        btnEdit = view.findViewById(R.id.btn_edit_profile)
        btnLogout = view.findViewById(R.id.btn_logout)
        btnDeleteAccount = view.findViewById(R.id.btn_delete_account)
//        rvHistory = view.findViewById(R.id.rv_history)

        btnSimpan = Button(requireContext()).apply {
            text = "Simpan"
            visibility = View.GONE
            setBackgroundColor(resources.getColor(R.color.dgreen))
            setTextColor(resources.getColor(R.color.white))
        }

        val mainLayout = view.findViewById<LinearLayout>(R.id.mainLayout)
        mainLayout.addView(btnSimpan)

        loadUserData()

        ivProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnEdit.setOnClickListener {
            if (!isEditing) enableEditing(true)
        }

        btnSimpan.setOnClickListener {
            saveUserData()
            enableEditing(false)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        btnDeleteAccount.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()?.addOnSuccessListener {
                firestore.collection("users").document(user.uid).delete()
                Toast.makeText(requireContext(), "Akun dihapus", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }?.addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal hapus akun", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    textNama.text = doc.getString("nama") ?: "-"
                    textMail.text = "Email : ${doc.getString("email") ?: "-"}"
                    textPhone.text = "No. HP : ${doc.getString("no_hp") ?: "-"}"
                    textWilayah.text = "Wilayah : ${doc.getString("wilayahId") ?: "-"}"
                    textRole.text = "Peran: ${doc.getString("role") ?: "-"}"

                    val photoUrl = doc.getString("photoUrl")
                    if (!photoUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_person) // ✅ fix
                            .error(R.drawable.ic_person)       // ✅ fix
                            .into(ivProfile)
                    } else {
                        ivProfile.setImageResource(R.drawable.ic_person)
                    }
                }
                enableEditing(false)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal memuat data profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserData() {
        val uid = auth.currentUser?.uid ?: return

        val namaBaru = (textNama as? EditText)?.text?.toString() ?: textNama.text.toString()
        val emailBaru = (textMail as? EditText)?.text?.toString() ?: textMail.text.toString()
        val phoneBaru = (textPhone as? EditText)?.text?.toString() ?: textPhone.text.toString()
        val wilayahBaru = (textWilayah as? EditText)?.text?.toString() ?: textWilayah.text.toString()

        val data = mapOf(
            "nama" to namaBaru,
            "email" to emailBaru.replace("Email : ", ""),
            "no_hp" to phoneBaru.replace("No. HP : ", ""),
            "wilayahId" to wilayahBaru.replace("Wilayah : ", "")
        )

        firestore.collection("users").document(uid)
            .update(data)
            .addOnSuccessListener {
                if (selectedImageUri != null) uploadProfilePhoto(uid)
                Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                loadUserData()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menyimpan perubahan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfilePhoto(uid: String) {
        val imageRef = storage.reference.child("profile_photos/$uid.jpg")
        val uri = selectedImageUri ?: return
        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    firestore.collection("users").document(uid)
                        .update("photoUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            Glide.with(this)
                                .load(downloadUri)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(ivProfile)
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal upload foto profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enableEditing(enabled: Boolean) {
        if (enabled) {
            isEditing = true
            btnSimpan.visibility = View.VISIBLE

            textNama = convertToEditText(textNama)
            textMail = convertToEditText(textMail)
            textPhone = convertToEditText(textPhone)
            textWilayah = convertToEditText(textWilayah)

            btnEdit.isEnabled = false
        } else {
            isEditing = false
            btnSimpan.visibility = View.GONE
            btnEdit.isEnabled = true
        }
    }

    private fun convertToEditText(tv: TextView): EditText {
        val parent = tv.parent as ViewGroup
        val index = parent.indexOfChild(tv)
        parent.removeView(tv)

        val et = EditText(requireContext()).apply {
            setText(tv.text.toString().replace("Email : ", "")
                .replace("No. HP : ", "")
                .replace("Wilayah : ", ""))
            textSize = 16f
            setPadding(10, 5, 10, 5)
            setTextColor(resources.getColor(R.color.black))
            background = resources.getDrawable(android.R.drawable.edit_text)
        }

        parent.addView(et, index)
        return et
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivProfile)
            }
        }
    }
}