package com.travelku.app.api
import java.io.Serializable

data class KotaResponse(
    val id_kota: Int,
    val nama_kota: String,
    val provinsi: String,
    val status_aktif: Boolean
): Serializable

data class BusResponse(
    val id_bus: Int,
    val nama_travel: String,
    val plat_nomor: String,
    val kapasitas: Int,
    val tipe_kendaraan: String
)

data class RuteResponse(
    val id_rute: Int,
    val id_kota_asal: Int,
    val id_kota_tujuan: Int,
    val estimasi_waktu: String
)

data class JadwalResponse(
    val id_jadwal: Int,
    val id_bus: Int,
    val id_rute: Int,
    val tanggal_berangkat: String,
    val jam_berangkat: String,
    val harga_tiket: Double
)

data class PenumpangRequest(
    val nama_lengkap: String,
    val umur: Int,
    val no_hp: String,
    val email: String
)

data class PenumpangResponse(
    val id_penumpang: Int,
    val nama_lengkap: String,
    val umur: Int,
    val no_hp: String,
    val email: String
)

data class PemesananRequest(
    val id_user: Int,
    val id_penumpang: Int,
    val id_jadwal: Int,
    val nomor_kursi: String,
    val jumlah_kursi: Int,
    val total_harga: Double
)

data class PemesananResponse(
    val id_pemesanan: Int,
    val id_penumpang: Int,
    val id_jadwal: Int,
    val nomor_kursi: String,
    val jumlah_kursi: Int,
    val total_harga: Double,
    val status_pemesanan: String,
    val tanggal_pemesanan: String,

    val nama_travel: String? = null,
    val asal: String? = null,
    val tujuan: String? = null,
    val tanggal_berangkat: String? = null,
    val jam_berangkat: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val id_user: Int,
    val access_token: String,
    val token_type: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val message: String
)