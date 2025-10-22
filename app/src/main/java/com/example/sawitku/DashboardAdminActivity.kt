package com.example.sawitku

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class User(
    val uid: String = "",
    val nama: String = "",
    val email: String = "",
    val role: String = "",
    var aktif: Boolean = true
)

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        recyclerView = findViewById(R.id.recyclerViewAkun)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(userList, this)
        recyclerView.adapter = adapter

        // Listen real-time changes di collection users
        db.collection("users")
            .addSnapshotListener(EventListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Gagal ambil data: ${e.message}", Toast.LENGTH_LONG).show()
                    return@EventListener
                }

                if (snapshots != null) {
                    userList.clear()
                    for (doc in snapshots.documents) {
                        val user = User(
                            uid = doc.id,
                            nama = doc.getString("nama") ?: "",
                            email = doc.getString("email") ?: "",
                            role = doc.getString("role") ?: "",
                            aktif = doc.getBoolean("aktif") ?: true
                        )
                        userList.add(user)
                    }
                    adapter.notifyDataSetChanged()
                }
            })
    }

    inner class UserAdapter(
        private val users: MutableList<User>,
        private val context: Context
    ) : RecyclerView.Adapter<UserAdapter.VH>() {

        inner class VH(v: android.view.View) : RecyclerView.ViewHolder(v) {
            val tvName = v.findViewById<TextView>(R.id.tvNamaUser)
            val tvRole = v.findViewById<TextView>(R.id.tvRoleUser)
            val btnToggle = v.findViewById<android.widget.Button>(R.id.btnToggleAkun)
            val btnDelete = v.findViewById<android.widget.Button>(R.id.btnHapusUser)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
            val v = layoutInflater.inflate(R.layout.item_user_admin, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val user = users[position]
            holder.tvName.text = if (user.nama.isNotEmpty()) user.nama else user.email
            holder.tvRole.text = user.role
            holder.btnToggle.text = if (user.aktif) "Nonaktifkan" else "Aktifkan"

            holder.btnToggle.setOnClickListener {
                // Update field "aktif" di Firestore
                db.collection("users").document(user.uid)
                    .update("aktif", !user.aktif)
                    .addOnSuccessListener {
                        user.aktif = !user.aktif
                        notifyItemChanged(position)
                        Toast.makeText(
                            context,
                            "${user.email} sekarang ${if (user.aktif) "aktif" else "nonaktif"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Gagal update status: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }

            holder.btnDelete.setOnClickListener {
                // Hapus user di Firestore
                db.collection("users").document(user.uid)
                    .delete()
                    .addOnSuccessListener {
                        users.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "${user.email} dihapus", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Gagal hapus user: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }

        override fun getItemCount(): Int = users.size
    }
}