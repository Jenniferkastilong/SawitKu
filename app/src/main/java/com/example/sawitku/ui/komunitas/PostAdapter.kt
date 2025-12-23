package com.example.sawitku.ui.komunitas

import android.net.Uri
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R
import com.google.firebase.database.FirebaseDatabase

class PostAdapter(
    private var list: List<Post>,
    private val onRevisiClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.VH>() {

    fun updateList(newList: List<Post>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.textUsername)
        val tvRole: TextView = v.findViewById(R.id.textRole)
        val tvTag: TextView = v.findViewById(R.id.textTag)
        val tvTitle: TextView = v.findViewById(R.id.textTitle)
        val tvContent: TextView = v.findViewById(R.id.textContent)
        val tvLikes: TextView = v.findViewById(R.id.textLikes)
        val tvComments: TextView = v.findViewById(R.id.textComments)
        val imagePost: ImageView = v.findViewById(R.id.imagePost)
        val btnRevisi: View = v.findViewById(R.id.btnMenuMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val post = list[position]
        val dbRef = FirebaseDatabase.getInstance().getReference("posts")

        holder.tvName.text = post.authorName
        holder.tvRole.text = "${post.authorRole} • ${post.time}"
        holder.tvTag.text = post.tag
        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content
        holder.tvLikes.text = "${post.likes} Suka"
        holder.tvComments.text = "${post.comments} Komentar"

        // Logika Gambar (Gunakan sample_daun.xml yang telah dibuat)
        if (post.imageUri.isNotEmpty()) {
            holder.imagePost.visibility = View.VISIBLE
            holder.imagePost.setImageURI(Uri.parse(post.imageUri))
        } else if (post.title.contains("Daun", true)) {
            holder.imagePost.visibility = View.VISIBLE
            holder.imagePost.setImageResource(R.drawable.sample_daun)
        } else {
            holder.imagePost.visibility = View.VISIBLE
            holder.imagePost.setImageResource(R.color.gray)
        }

        // Like Real-time
        holder.tvLikes.setOnClickListener {
            if (post.key.isNotEmpty()) {
                dbRef.child(post.key).child("likes").setValue(post.likes + 1)
            }
        }

        // Komentar Real-time
        holder.tvComments.setOnClickListener {
            val et = EditText(holder.itemView.context)
            et.hint = "Tulis komentar..."
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Komentar")
                .setView(et)
                .setPositiveButton("Kirim") { _, _ ->
                    if (post.key.isNotEmpty()) {
                        dbRef.child(post.key).child("comments").setValue(post.comments + 1)
                    }
                }.show()
        }

        holder.btnRevisi.setOnClickListener { onRevisiClick(post) }
    }

    override fun getItemCount(): Int = list.size
}