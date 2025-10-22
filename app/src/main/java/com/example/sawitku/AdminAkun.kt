package com.example.sawitku

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminAkunFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_admin_akun, container, false)
        val rv = v.findViewById<RecyclerView>(R.id.recyclerViewAkun)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = UserAdapter(SharedData.userList)
        return v
    }

    inner class UserAdapter(private val users: MutableList<SharedData.User>) :
        RecyclerView.Adapter<UserAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvNama: TextView = view.findViewById(R.id.tvNamaUser)
            val tvRole: TextView = view.findViewById(R.id.tvRoleUser)
            val btnToggle: Button = view.findViewById(R.id.btnToggleAkun)
            val btnHapus: Button = view.findViewById(R.id.btnHapusUser)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user_admin, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val u = users[pos]
            holder.tvNama.text = u.nama
            holder.tvRole.text = "Peran: ${u.role}"
            holder.btnToggle.text = if (u.aktif) "Nonaktifkan" else "Aktifkan"
            holder.btnToggle.setOnClickListener {
                u.aktif = !u.aktif
                notifyItemChanged(pos)
                Toast.makeText(requireContext(), "${u.nama} ${if (u.aktif) "diaktifkan" else "dinonaktifkan"}", Toast.LENGTH_SHORT).show()
            }
            holder.btnHapus.setOnClickListener {
                users.removeAt(pos)
                notifyItemRemoved(pos)
                Toast.makeText(requireContext(), "${u.nama} dihapus", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount() = users.size
    }
}