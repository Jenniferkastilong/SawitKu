package com.example.sawitku

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.sawitku.ui.berita.BeritaActivity
import com.example.sawitku.ui.harga.HargaActivity
import com.example.sawitku.ui.kalkulator.KalkulatorActivity
import com.example.sawitku.ui.profile.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class Home : Fragment() {

    private lateinit var tvWilayah: TextView
    private lateinit var tvTotalLahan: TextView
    private lateinit var tvTotalPetani: TextView
    private lateinit var llNotifikasi: LinearLayout
    private lateinit var tvWeatherTemp: TextView
    private lateinit var ivWeatherIcon: ImageView
    private lateinit var ivProfile: ImageView

    private lateinit var btnBerita: ImageButton
    private lateinit var btnHarga: ImageButton
    private lateinit var btnKonsultasi: ImageButton
    private lateinit var btnKalkulator: ImageButton

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        // Bind views
        tvWilayah = v.findViewById(R.id.tv_wilayah_nama)
        tvTotalLahan = v.findViewById(R.id.tv_total_lahan)
        tvTotalPetani = v.findViewById(R.id.tv_total_petani)
        llNotifikasi = v.findViewById(R.id.ll_notifikasi)
        tvWeatherTemp = v.findViewById(R.id.tv_weather_temp)
        ivWeatherIcon = v.findViewById(R.id.iv_weather_icon)
        ivProfile = v.findViewById(R.id.iv_profile_pic)

        btnBerita = v.findViewById(R.id.btnBerita)
        btnHarga = v.findViewById(R.id.btnHarga)
        btnKonsultasi = v.findViewById(R.id.btnKonsultasi)
        btnKalkulator = v.findViewById(R.id.btnKalkulator)

        fetchUserDataAndWilayah()
        setupNotifikasi()
        setupButtonActions()

        return v
    }

    private fun fetchUserDataAndWilayah() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!isAdded) return@addOnSuccessListener
                if (doc != null && doc.exists()) {
                    val wilayah = doc.getString("wilayahId") ?: "-"
                    val luasLahan = doc.getLong("luas_lahan") ?: 0
                    val fotoUrl = doc.getString("foto_url")
                    val nama = doc.getString("nama")

                    tvWilayah.text = wilayah
                    tvTotalLahan.text = "$luasLahan Ha"

                    firestore.collection("users")
                        .whereEqualTo("wilayahId", wilayah)
                        .whereEqualTo("role", "Petani")
                        .get()
                        .addOnSuccessListener { snapshot ->
                            if (!isAdded) return@addOnSuccessListener
                            tvTotalPetani.text = snapshot.size().toString()
                        }

                    if (!fotoUrl.isNullOrBlank() && isAdded) {
                        Glide.with(this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.ic_profile)
                            .error(generateInitialPlaceholder(nama))
                            .into(ivProfile)
                    } else if (isAdded) {
                        ivProfile.setImageBitmap(generateInitialPlaceholder(nama))
                    }

                    ivProfile.setOnClickListener {
                        if (!isAdded) return@setOnClickListener
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, Profile())
                            .addToBackStack(null)
                            .commit()
                    }

                    setupCuacaApi(wilayah)
                }
            }
            .addOnFailureListener {
                if (!isAdded) return@addOnFailureListener
                Toast.makeText(requireContext(), "Gagal memuat data user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupCuacaApi(wilayah: String) {
        if (!isAdded) return
        val apiKey = "deb4b6298cf64cf28c88e7c64fdd84d0"
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$wilayah&appid=$apiKey&units=metric"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                if (!isAdded) return@StringRequest
                try {
                    val json = JSONObject(response)
                    val temp = json.getJSONObject("main").getDouble("temp")
                    val iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon")

                    tvWeatherTemp.text = "${temp.toInt()}°C"

                    val resId = resources.getIdentifier(
                        "ic_weather_$iconCode",
                        "drawable",
                        requireContext().packageName
                    )
                    if (resId != 0) ivWeatherIcon.setImageResource(resId)
                } catch (e: Exception) {
                    tvWeatherTemp.text = "-"
                    Toast.makeText(requireContext(), "Error parsing cuaca", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                if (!isAdded) return@StringRequest
                tvWeatherTemp.text = "-"
                Toast.makeText(requireContext(), "Gagal mengambil cuaca: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun generateInitialPlaceholder(nama: String?): Bitmap? {
        if (!isAdded) return null
        val initial = nama?.firstOrNull()?.uppercaseChar() ?: 'U'
        val size = 200
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        val paint = Paint().apply {
            color = ContextCompat.getColor(requireContext(), android.R.color.white)
            textSize = 100f
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
        }
        val textWidth = paint.measureText(initial.toString())
        val x = (size - textWidth) / 2
        val y = size / 2 - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(initial.toString(), x, y, paint)
        return bmp
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupNotifikasi() {
        val uid = auth.currentUser?.uid ?: return
        val tx = llNotifikasi.findViewById<TextView>(R.id.text_notif)
        if (!isAdded) return
        tx.text = if (!SharedData.hasSubmittedThisMonth(uid)) {
            "Anda belum submit laporan bulan ini!"
        } else {
            "Tidak ada notifikasi"
        }
    }

    private fun setupButtonActions() {
        btnBerita.setOnClickListener {
            if (!isAdded) return@setOnClickListener
            val intent = Intent(requireContext(), BeritaActivity::class.java)
            startActivity(intent)
        }

        btnHarga.setOnClickListener {
            if (!isAdded) return@setOnClickListener
            val intent = Intent(requireContext(), HargaActivity::class.java)
            startActivity(intent)
        }

        btnKonsultasi.setOnClickListener {
            if (!isAdded) return@setOnClickListener
            val intent = Intent(requireContext(), KonsultasiActivity::class.java)
            startActivity(intent)
        }

        btnKalkulator.setOnClickListener {
            if (!isAdded) return@setOnClickListener
            val intent = Intent(requireContext(), KalkulatorActivity::class.java)
            startActivity(intent)
        }
    }
}