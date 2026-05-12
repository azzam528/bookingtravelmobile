package com.travelku.app

import java.io.Serializable

data class Kota(val id: Int, val nama: String, val provinsi: String) : Serializable
data class Travel(val id: Int, val nama: String, val jam: String, val harga: Int, val durasi: String, val kursiTotal: Int = 12) : Serializable
data class Penumpang(var nama: String = "", var umur: String = "", var hp: String = "", var email: String = "", var kursi: Int = 0) : Serializable
data class Pemesanan(
    val id: String,
    val travelNama: String,
    val asal: String,
    val tujuan: String,
    val tanggal: String,
    val jam: String,
    val kursi: List<Int>,
    val total: Int,
    val penumpang: List<Penumpang>,
    var status: String,
    val tanggalPesan: String
) : Serializable

object DataSource {
    val KOTA = listOf(
        Kota(1, "Jakarta", "DKI Jakarta"),
        Kota(2, "Bandung", "Jawa Barat"),
        Kota(3, "Bogor", "Jawa Barat"),
        Kota(4, "Cirebon", "Jawa Barat"),
        Kota(5, "Semarang", "Jawa Tengah"),
        Kota(6, "Yogyakarta", "DI Yogyakarta"),
        Kota(7, "Surabaya", "Jawa Timur"),
        Kota(8, "Malang", "Jawa Timur"),
        Kota(9, "Denpasar", "Bali"),
        Kota(10, "Medan", "Sumatera Utara"),
    )
    val TRAVELS = listOf(
        Travel(1, "Xtrans", "06:00", 150000, "3j 30m"),
        Travel(2, "Cititrans", "08:30", 175000, "3j 15m"),
        Travel(3, "DayTrans", "10:00", 130000, "4j"),
        Travel(4, "Baraya Travel", "13:00", 120000, "4j 15m"),
        Travel(5, "Jackal Holidays", "15:30", 165000, "3j 30m"),
        Travel(6, "Bhinneka Travel", "19:00", 140000, "3j 45m"),
    )
    val KURSI_TERPAKAI = listOf(3, 7)
    fun rupiah(n: Int) = "Rp " + java.text.NumberFormat.getNumberInstance(java.util.Locale("in", "ID")).format(n)
}
