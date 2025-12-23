package com.example.sawitku

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

object SharedData {

    data class Lokasi(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val alamat: String = "-"
    )

    data class Laporan(
        val id: String = UUID.randomUUID().toString(),
        val uidPetani: String,
        val namaPetani: String,
        var namaKebun: String,
        var luas: Double,
        var tanggal: String,
        var lokasi: Lokasi,
        var verified: Boolean = false,
        var status: String = "Dalam Proses",
        var catatanRevisi: String? = null,
        var fotoPath: String? = null
    )

    data class User(
        val uid: String = UUID.randomUUID().toString(),
        val nama: String,
        val username: String,
        val role: String,
        val fotoUrl: String = "",
        var aktif: Boolean = true,
        var profileCompleted: Boolean = true
    )

    val laporanList = mutableListOf<Laporan>()
    val laporanKonsultan = mutableListOf<Laporan>()
    val userList = mutableListOf<User>()
    val laporanAdmin = mutableListOf<Laporan>()


    fun initDummyData() {
        if (userList.isEmpty()) {
            userList.addAll(
                listOf(
                    User(nama = "Budi", username = "budi123", role = "petani"),
                    User(nama = "Siti", username = "siti456", role = "petani"),
                    User(nama = "Andi", username = "andi789", role = "konsultan"),
                    User(nama = "Admin", username = "admin", role = "admin")
                )
            )
        }

        if (laporanKonsultan.isEmpty()) {
            laporanKonsultan.addAll(
                listOf(
                    Laporan(
                        uidPetani = userList[0].uid,
                        namaPetani = "Budi",
                        namaKebun = "Kebun Budi",
                        luas = 2.0,
                        tanggal = "21-10-2025",
                        lokasi = Lokasi(-6.2, 106.8, "Bogor")
                    ),
                    Laporan(
                        uidPetani = userList[1].uid,
                        namaPetani = "Siti",
                        namaKebun = "Kebun Siti",
                        luas = 1.5,
                        tanggal = "20-10-2025",
                        lokasi = Lokasi(-6.3, 106.9, "Depok")
                    )
                )
            )
        }
    }

    fun getRiwayatLaporan(uid: String): List<Laporan> {
        return laporanList.filter { it.uidPetani == uid } +
                laporanKonsultan.filter { it.uidPetani == uid }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun hasSubmittedThisMonth(uid: String): Boolean {
        val now = java.time.LocalDate.now()
        return getRiwayatLaporan(uid).any {
            val dateParts = it.tanggal.split("-").map { part -> part.toIntOrNull() ?: 0 }
            if (dateParts.size != 3) return@any false
            dateParts[1] == now.monthValue && dateParts[2] == now.year
        }
    }
}