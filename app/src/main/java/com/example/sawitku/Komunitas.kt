package com.example.sawitku.ui.komunitas

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class Komunitas : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddPost: FloatingActionButton
    private lateinit var adapter: PostAdapter

    companion object {
        val postList = mutableListOf<Post>()

        val currentUsername: String
            get() {
                val user = com.example.sawitku.SharedData.userList.firstOrNull { it.aktif }
                return user?.nama ?: "User"
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_komunitas, container, false)

        recyclerView = root.findViewById(R.id.recyclerViewPosts)
        fabAddPost = root.findViewById(R.id.btnAddPost)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostAdapter(postList)
        recyclerView.adapter = adapter

        if (postList.isEmpty()) addDummyPosts()

        fabAddPost.setOnClickListener { showAddPostDialog() }

        return root
    }

    private fun addDummyPosts() {
        val dummy = listOf(
            Post("Pak Budi", "Panen sawit bulan ini bagus sekali!", "08:15"),
            Post("Bu Sari", "Harga CPO naik minggu ini.", "09:20"),
            Post("Pak Andi", "Apakah ada yang tahu pupuk organik terbaik?", "10:05")
        )
        postList.addAll(dummy)
        adapter.notifyDataSetChanged()
    }

    private fun showAddPostDialog() {
        val inputContent = EditText(requireContext()).apply {
            hint = "Tulis sesuatu..."
            minLines = 2
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Buat Postingan Baru")
            .setView(inputContent)
            .setPositiveButton("Posting") { dialog, _ ->
                val content = inputContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    postList.add(0, Post(currentUsername, content, timestamp))
                    adapter.notifyItemInserted(0)
                    recyclerView.scrollToPosition(0)
                } else {
                    Toast.makeText(requireContext(), "Isi postingan tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    data class Post(val username: String, val content: String, val time: String)

    inner class PostAdapter(private val posts: List<Post>) :
        RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        inner class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val textUsername = view.findViewById<android.widget.TextView>(R.id.textUsername)
            val textContent = view.findViewById<android.widget.TextView>(R.id.textContent)
            val textTime = view.findViewById<android.widget.TextView>(R.id.textTime)
            val avatar = view.findViewById<android.widget.ImageView>(R.id.avatar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
            return PostViewHolder(view)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            val post = posts[position]
            holder.textUsername.text = post.username
            holder.textContent.text = post.content
            holder.textTime.text = post.time
            holder.avatar.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        override fun getItemCount(): Int = posts.size
    }
}