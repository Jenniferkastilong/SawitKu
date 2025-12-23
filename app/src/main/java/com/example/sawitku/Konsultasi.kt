package com.example.sawitku

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// ===== MODEL DATA =====
data class Konsultasi(
    val id: String,
    val namaPetani: String,
    val pesan: String,
    val tanggal: String
)

// ===== DUMMY DATA GLOBAL =====
object DummyData {
    val daftarKonsultasi = mutableListOf<Konsultasi>()
}

// ===== ADAPTER UNTUK KONSULTAN =====
class KonsultasiAdapter(private val data: List<Konsultasi>) :
    RecyclerView.Adapter<KonsultasiAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nama: TextView = v.findViewById(R.id.itemNamaPetani)
        val pesan: TextView = v.findViewById(R.id.itemPesan)
        val tanggal: TextView = v.findViewById(R.id.itemTanggal)
    }

    // Keep this one
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_konsultasi, parent, false)
        return ViewHolder(v)
    }

    // The conflicting function has been removed

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val k = data[position]
        holder.nama.text = k.namaPetani
        holder.pesan.text = k.pesan
        holder.tanggal.text = k.tanggal
    }

    override fun getItemCount() = data.size
}

// ===== ACTIVITY UTAMA =====
class KonsultasiActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editNama: EditText
    private lateinit var editPesan: EditText
    private lateinit var btnKirim: Button
    private lateinit var formLayout: LinearLayout
    private lateinit var listLayout: LinearLayout

    private var userRole: String = "petani" // nanti disesuaikan dari login Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konsultasi)

        recyclerView = findViewById(R.id.recyclerViewKonsultasi)
        editNama = findViewById(R.id.editNama)
        editPesan = findViewById(R.id.editPesan)
        btnKirim = findViewById(R.id.btnKirim)
        formLayout = findViewById(R.id.layoutForm)
        listLayout = findViewById(R.id.layoutList)

        // Simulasi: ambil role dari intent (nanti dihubungkan login)
        userRole = intent.getStringExtra("role") ?: "petani"

        if (userRole == "petani") {
            tampilkanFormPetani()
        } else {
            tampilkanDashboardKonsultan()
        }
    }

    private fun tampilkanFormPetani() {
        formLayout.visibility = View.VISIBLE
        listLayout.visibility = View.GONE

        btnKirim.setOnClickListener {
            val nama = editNama.text.toString().trim()
            val pesan = editPesan.text.toString().trim()

            if (nama.isEmpty() || pesan.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val konsultasi = Konsultasi(
                id = System.currentTimeMillis().toString(),
                namaPetani = nama,
                pesan = pesan,
                tanggal = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date())
            )

            DummyData.daftarKonsultasi.add(konsultasi)
            Toast.makeText(this, "Konsultasi berhasil dikirim!", Toast.LENGTH_SHORT).show()
            editPesan.text.clear()
        }
    }

    private fun tampilkanDashboardKonsultan() {
        formLayout.visibility = View.GONE
        listLayout.visibility = View.VISIBLE

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = KonsultasiAdapter(DummyData.daftarKonsultasi)
    }

    override fun onResume() {
        super.onResume()
        if (userRole != "petani") {
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}