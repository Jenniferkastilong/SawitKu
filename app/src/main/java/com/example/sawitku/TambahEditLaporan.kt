package com.example.sawitku

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class LaporanLahanEdit : Fragment(), OnMapReadyCallback {

    private lateinit var etNamaKebun: EditText
    private lateinit var etLuas: EditText
    private lateinit var etTahun: EditText
    private lateinit var btnAmbilGPS: Button
    private lateinit var btnAmbilFoto: Button
    private lateinit var btnSubmit: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivFoto: ImageView
    private lateinit var adapter: AdapterLaporan
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var selectedLatLng: LatLng? = null
    private var savedPhotoPath: String? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var laporanListener: ListenerRegistration? = null
    private val laporanList = mutableListOf<Laporan>()

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
            bmp?.let {
                ivFoto.setImageBitmap(it)
                saveBitmapToCache(it)
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) takePhoto.launch(null)
            else Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_laporan_lahan, container, false)

        etNamaKebun = v.findViewById(R.id.etNamaKebun)
        etLuas = v.findViewById(R.id.etLuas)
        etTahun = v.findViewById(R.id.etTahunTanam)
        btnAmbilGPS = v.findViewById(R.id.btnAmbilGPS)
        btnAmbilFoto = v.findViewById(R.id.btnAmbilFoto)
        btnSubmit = v.findViewById(R.id.btnSubmit)
        recyclerView = v.findViewById(R.id.recyclerView)
        ivFoto = v.findViewById(R.id.ivFoto)
        mapView = v.findViewById(R.id.mapView)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdapterLaporan(laporanList)
        recyclerView.adapter = adapter

        btnAmbilGPS.setOnClickListener { requestLocationPermission() }
        btnAmbilFoto.setOnClickListener { requestCameraPermission.launch(Manifest.permission.CAMERA) }
        btnSubmit.setOnClickListener { submitReport() }

        listenUserLaporan()

        return v
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 200)
        } else {
            googleMap?.let { map ->
                map.isMyLocationEnabled = true
                map.setOnMapClickListener { latLng ->
                    selectedLatLng = latLng
                    map.clear()
                    map.addMarker(com.google.android.gms.maps.model.MarkerOptions().position(latLng))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    Toast.makeText(requireContext(), "Lokasi dipilih: ${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(requireContext(), "Klik pada peta untuk memilih lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBitmapToCache(bmp: Bitmap) {
        try {
            val file = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            fos.flush()
            fos.close()
            savedPhotoPath = file.absolutePath
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal simpan foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitReport() {
        val namaK = etNamaKebun.text.toString().trim()
        val luasDouble = etLuas.text.toString().toDoubleOrNull()
        val lokasi = selectedLatLng
        val currentUser = auth.currentUser ?: return

        if (namaK.isEmpty() || luasDouble == null || lokasi == null) {
            Toast.makeText(requireContext(), "Isi nama kebun, luas, dan pilih lokasi dulu.", Toast.LENGTH_SHORT).show()
            return
        }

        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        val laporanBaru = hashMapOf(
            "uidPetani" to currentUser.uid,
            "namaPetani" to (currentUser.displayName ?: "UserDemo"),
            "namaKebun" to namaK,
            "luas" to luasDouble,
            "tanggal" to tanggal,
            "lokasi" to hashMapOf(
                "latitude" to lokasi.latitude,
                "longitude" to lokasi.longitude,
                "alamat" to "-"
            ),
            "fotoPath" to savedPhotoPath,
            "status" to "pending" // initial status
        )

        firestore.collection("laporan").add(laporanBaru).addOnSuccessListener {
            Toast.makeText(requireContext(), "Laporan tersimpan!", Toast.LENGTH_SHORT).show()
            resetForm()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal kirim laporan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetForm() {
        etNamaKebun.text.clear()
        etLuas.text.clear()
        etTahun.text.clear()
        ivFoto.setImageResource(android.R.color.darker_gray)
        savedPhotoPath = null
        selectedLatLng = null
        googleMap?.clear()
    }

    private fun listenUserLaporan() {
        val uid = auth.currentUser?.uid ?: return
        laporanListener = firestore.collection("laporan")
            .whereEqualTo("uidPetani", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                laporanList.clear()
                snapshot?.forEach { doc ->
                    val lokasiMap = doc.get("lokasi") as? Map<String, Any>
                    val lokasi = LatLng(
                        (lokasiMap?.get("latitude") as? Double) ?: 0.0,
                        (lokasiMap?.get("longitude") as? Double) ?: 0.0
                    )
                    laporanList.add(
                        Laporan(
                            id = doc.id,
                            uidPetani = doc.getString("uidPetani") ?: "",
                            namaPetani = doc.getString("namaPetani") ?: "",
                            namaKebun = doc.getString("namaKebun") ?: "",
                            luas = (doc.getDouble("luas") ?: 0.0),
                            tanggal = doc.getString("tanggal") ?: "",
                            lokasi = lokasi,
                            fotoPath = doc.getString("fotoPath"),
                            status = doc.getString("status") ?: "pending"
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission()
            } else {
                Toast.makeText(requireContext(), "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy(); laporanListener?.remove() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }

    data class Laporan(
        val id: String,
        val uidPetani: String,
        val namaPetani: String,
        val namaKebun: String,
        val luas: Double,
        val tanggal: String,
        val lokasi: LatLng,
        val fotoPath: String?,
        val status: String // pending, revisi, verified
    )

    inner class AdapterLaporan(private val items: MutableList<Laporan>) :
        RecyclerView.Adapter<AdapterLaporan.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvJudulLaporan: TextView = v.findViewById(R.id.tvJudulLaporan)
            val tvTanggal: TextView = v.findViewById(R.id.tvTanggal)
            val tvLuas: TextView = v.findViewById(R.id.tvLuas)
            val tvStatus: TextView = v.findViewById(R.id.tvStatus)
            val ivFotoLaporan: ImageView = v.findViewById(R.id.ivFotoLaporan)
            val btnEditLaporan: Button = v.findViewById(R.id.btnEditLaporan)
            val btnHapusLaporan: Button = v.findViewById(R.id.btnHapusLaporan)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_laporan_user, parent, false) // layout khusus user
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val laporan = items[position]
            holder.tvJudulLaporan.text = laporan.namaKebun
            holder.tvTanggal.text = laporan.tanggal
            holder.tvLuas.text = "Luas: ${laporan.luas} Ha"
            holder.tvStatus.text = "Status: ${laporan.status}"

            if (!laporan.fotoPath.isNullOrBlank()) {
                holder.ivFotoLaporan.setImageURI(Uri.fromFile(File(laporan.fotoPath)))
            } else {
                holder.ivFotoLaporan.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            // Atur visibilitas button edit & hapus
            when (laporan.status) {
                "pending", "revisi" -> {
                    holder.btnEditLaporan.visibility = View.VISIBLE
                    holder.btnHapusLaporan.visibility = if (laporan.status == "pending") View.VISIBLE else View.GONE
                }
                "verified" -> {
                    holder.btnEditLaporan.visibility = View.GONE
                    holder.btnHapusLaporan.visibility = View.GONE
                }
            }

            // Edit laporan
            holder.btnEditLaporan.setOnClickListener {
                showEditDialog(laporan)
            }

            // Hapus laporan
            holder.btnHapusLaporan.setOnClickListener {
                firestore.collection("laporan").document(laporan.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Laporan dihapus", Toast.LENGTH_SHORT).show()
                        items.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Gagal hapus laporan", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        override fun getItemCount(): Int = items.size
    }

    private fun showEditDialog(laporan: Laporan) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_laporan, null)
        val etNama = dialogView.findViewById<EditText>(R.id.etNamaKebunDialog)
        val etLuas = dialogView.findViewById<EditText>(R.id.etLuasDialog)

        etNama.setText(laporan.namaKebun)
        etLuas.setText(laporan.luas.toString())

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Edit Laporan")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val namaBaru = etNama.text.toString().trim()
                val luasBaru = etLuas.text.toString().toDoubleOrNull() ?: laporan.luas

                firestore.collection("laporan").document(laporan.id)
                    .update(
                        mapOf(
                            "namaKebun" to namaBaru,
                            "luas" to luasBaru,
                            "status" to if (laporan.status == "revisi") "pending" else laporan.status
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Laporan diperbarui", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Gagal update laporan", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Batal", null)
            .create()
        dialog.show()
    }
}