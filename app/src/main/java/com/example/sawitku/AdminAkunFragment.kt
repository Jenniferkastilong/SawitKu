package com.example.sawitku

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.adapter.UserAdminAdapter
import com.example.sawitku.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sawitku.R

class AdminAkunFragment : Fragment(), UserAdminAdapter.OnUserActionListener {

    private lateinit var rvUsers: RecyclerView
    private lateinit var adapter: UserAdminAdapter
    private var userList = ArrayList<UserModel>()
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 3. Inflate layout disimpan ke variabel 'view'
        val view = inflater.inflate(R.layout.fragment_admin_akun, container, false)

        db = FirebaseFirestore.getInstance()

        // 4. Gunakan 'view.findViewById', bukan langsung findViewById
        rvUsers = view.findViewById(R.id.recyclerViewUsers)

        rvUsers.layoutManager = LinearLayoutManager(context)

        adapter = UserAdminAdapter(userList, this)
        rvUsers.adapter = adapter

        fetchUsers()

        return view
    }

    private fun fetchUsers() {
        db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Gagal ambil data", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                userList.clear()
                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val user = doc.toObject(UserModel::class.java)
                        user?.uid = doc.id
                        // Filter: Jangan tampilkan admin sendiri
                        if (user != null && user.role != "admin") {
                            userList.add(user)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    // Implementasi Interface dari Adapter
    override fun onToggleStatus(user: UserModel, currentStatus: Boolean) {
        // Gunakan 'currentStatus' di sini
        db.collection("users").document(user.uid)
            .update("aktif", currentStatus)
            .addOnSuccessListener {
                val msg = if (currentStatus) "User Diaktifkan" else "User Dinonaktifkan"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal update status", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onDeleteUser(user: UserModel) {
        AlertDialog.Builder(context)
            .setTitle("Hapus User")
            .setMessage("Hapus data ${user.name}? User tidak akan bisa login lagi.")
            .setPositiveButton("Hapus") { _, _ ->
                db.collection("users").document(user.uid)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Data user dihapus", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}