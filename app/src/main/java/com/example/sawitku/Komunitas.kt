package com.example.sawitku

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.ui.komunitas.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class Komunitas : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private val fullDataPost = mutableListOf<Post>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_komunitas, container, false)

        val rvDiskusi = view.findViewById<RecyclerView>(R.id.rvDiskusiPilihan)
        val rvPost = view.findViewById<RecyclerView>(R.id.recyclerViewPosts)
        val searchBar = view.findViewById<EditText>(R.id.searchBar)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupFilter)
        val btnAdd = view.findViewById<ExtendedFloatingActionButton>(R.id.btnAddPost)

        // 1. Carousel Kotak Hijau
        rvDiskusi.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvDiskusi.adapter = DiskusiPilihanAdapter()
        val snapHelper = PagerSnapHelper()
        if (rvDiskusi.onFlingListener == null) snapHelper.attachToRecyclerView(rvDiskusi)

        // 2. PERBAIKAN ERROR: Menggunakan Tanda Kutip ("") agar dianggap String
        if (fullDataPost.isEmpty()) {
            fullDataPost.add(Post("1", "Admin", "Petani", "Baru saja", "#tanaman", "Pupuk bagus", "rekomendasi pupuk yang bagus apa?", "", 0, 0))
            fullDataPost.add(Post("2", "Budi Santoso", "Petani Plasma", "2 jam lalu", "#Hama", "Daun Kuning Bintik", "Mohon bantuannya lur, ini penyakit apa?", "", 124, 45))
        }

        // 3. Setup Post Adapter
        postAdapter = PostAdapter(fullDataPost) { post -> showRevisiBottomSheet(post) }
        rvPost.layoutManager = LinearLayoutManager(requireContext())
        rvPost.adapter = postAdapter

        // 4. Fitur Filter Chip
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val chipId = checkedIds.firstOrNull()
            if (chipId != null) {
                val selectedCategory = view.findViewById<Chip>(chipId).text.toString()
                val filtered = if (selectedCategory == "Semua") fullDataPost
                else fullDataPost.filter { it.tag.contains(selectedCategory, ignoreCase = true) }
                postAdapter.updateList(filtered)
            }
        }

        // 5. Fitur Search
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                postAdapter.updateList(fullDataPost.filter { it.title.lowercase().contains(query) })
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // 6. Tambah Postingan Baru
        btnAdd.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_post, null)
            AlertDialog.Builder(requireContext())
                .setTitle("Posting Baru")
                .setView(dialogView)
                .setPositiveButton("Posting") { _, _ ->
                    val etTitle = dialogView.findViewById<EditText>(R.id.etInputTitle)
                    val etContent = dialogView.findViewById<EditText>(R.id.etInputContent)

                    if (etTitle.text.toString().isNotEmpty()) {
                        // PERBAIKAN ERROR: ID menggunakan String
                        val p = Post("${fullDataPost.size + 1}", "Admin", "Petani", "Baru saja", "#umum",
                            etTitle.text.toString(), etContent.text.toString(), "", 0, 0)
                        fullDataPost.add(0, p)
                        postAdapter.updateList(fullDataPost)
                    }
                }.setNegativeButton("Batal", null).show()
        }

        return view
    }

    private fun showRevisiBottomSheet(post: Post) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.layout_revisi_postingan, null)
        dialog.setContentView(sheetView)

        val tvJudul = sheetView.findViewById<TextView>(R.id.tvJudulLaporan)
        val etRevisi = sheetView.findViewById<EditText>(R.id.etCatatanRevisi)
        val btnKirim = sheetView.findViewById<Button>(R.id.btnKirimRevisi)

        tvJudul.text = post.title
        etRevisi.setText(post.content)

        btnKirim.setOnClickListener {
            dialog.dismiss()
            showSuccessDialog()
        }
        dialog.show()
    }

    private fun showSuccessDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_revisi, null)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(view).create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.btnOkSuccess).setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }
}