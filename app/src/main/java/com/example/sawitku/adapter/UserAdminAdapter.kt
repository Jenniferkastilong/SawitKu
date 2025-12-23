package com.example.sawitku.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R
import com.example.sawitku.model.UserModel

class UserAdminAdapter(
    private val userList: ArrayList<UserModel>,
    private val listener: OnUserActionListener
) : RecyclerView.Adapter<UserAdminAdapter.UserViewHolder>() {

    interface OnUserActionListener {
        fun onToggleStatus(user: UserModel, currentStatus: Boolean)
        fun onDeleteUser(user: UserModel)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Sesuaikan ID dengan XML kamu
        val tvName: TextView = itemView.findViewById(R.id.tvNamaUser)
        val tvRole: TextView = itemView.findViewById(R.id.tvRoleUser)
        val btnToggle: Button = itemView.findViewById(R.id.btnToggleAkun)
        val btnDelete: Button = itemView.findViewById(R.id.btnHapusUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_admin, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.tvName.text = user.name
        holder.tvRole.text = "Role: ${user.role}" // Tambah label biar jelas

        // --- LOGIKA TOMBOL DINAMIS ---
        if (user.aktif) {
            // Jika user AKTIF -> Tampilkan tombol untuk MEMATIKAN
            holder.btnToggle.text = "Nonaktifkan"
            holder.btnToggle.setBackgroundColor(Color.parseColor("#E53935")) // Merah (Bahaya)
        } else {
            // Jika user NONAKTIF -> Tampilkan tombol untuk MENGHIDUPKAN
            holder.btnToggle.text = "Aktifkan"
            holder.btnToggle.setBackgroundColor(Color.parseColor("#43A047")) // Hijau (Aman)
        }

        // Klik Tombol Toggle
        holder.btnToggle.setOnClickListener {
            // Kirim status yang BARU (kebalikan status sekarang)
            listener.onToggleStatus(user, !user.aktif)
        }

        // Klik Tombol Hapus
        holder.btnDelete.setOnClickListener {
            listener.onDeleteUser(user)
        }
    }

    override fun getItemCount(): Int = userList.size
}