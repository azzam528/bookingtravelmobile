package com.travelku.app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.travelku.app.api.ApiClient
import com.travelku.app.api.PemesananRequest
import com.travelku.app.api.PenumpangRequest
import com.travelku.app.databinding.ActivityDataPenumpangBinding
import kotlinx.coroutines.launch

class DataPenumpangActivity : AppCompatActivity() {

    private lateinit var b: ActivityDataPenumpangBinding

    private var idJadwal = 0
    private var namaTravel = "-"
    private var asal = ""
    private var tujuan = ""
    private var tanggal = ""
    private var jamBerangkat = ""
    private var hargaTiket = 0.0
    private lateinit var kursi: ArrayList<Int>

    private val rows = mutableListOf<Map<String, EditText>>()

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        b = ActivityDataPenumpangBinding.inflate(layoutInflater)
        setContentView(b.root)

        idJadwal = intent.getIntExtra("id_jadwal", 0)
        namaTravel = intent.getStringExtra("nama_travel") ?: "-"
        asal = intent.getStringExtra("asal") ?: ""
        tujuan = intent.getStringExtra("tujuan") ?: ""
        tanggal = intent.getStringExtra("tanggal") ?: ""
        jamBerangkat = intent.getStringExtra("jam_berangkat") ?: ""
        hargaTiket = intent.getDoubleExtra("harga_tiket", 0.0)
        kursi = intent.getSerializableExtra("kursi") as ArrayList<Int>

        b.btnBack.setOnClickListener { finish() }

        b.tvRute.text = "$asal → $tujuan"
        b.tvDetail.text = "$namaTravel • $tanggal • $jamBerangkat • Kursi ${kursi.joinToString(", ")}"
        b.tvHarga.text = DataSource.rupiah((hargaTiket * kursi.size).toInt())

        kursi.forEachIndexed { i, k ->
            val card = LayoutInflater.from(this)
                .inflate(R.layout.item_penumpang_input, b.container, false) as LinearLayout

            card.findViewById<TextView>(R.id.tvLabel).text =
                "Penumpang ${i + 1}  •  Kursi $k"

            val nama = card.findViewById<EditText>(R.id.etNama)
            val umur = card.findViewById<EditText>(R.id.etUmur).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
            }
            val hp = card.findViewById<EditText>(R.id.etHp).apply {
                inputType = InputType.TYPE_CLASS_PHONE
            }
            val email = card.findViewById<EditText>(R.id.etEmail).apply {
                inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

            rows.add(
                mapOf(
                    "nama" to nama,
                    "umur" to umur,
                    "hp" to hp,
                    "email" to email
                )
            )

            b.container.addView(card)
        }

        b.btnPesan.setOnClickListener {
            pesan()
        }
    }

    private fun pesan() {
        val nama = rows[0]["nama"]!!.text.toString()
        val umurText = rows[0]["umur"]!!.text.toString()
        val hp = rows[0]["hp"]!!.text.toString()
        val email = rows[0]["email"]!!.text.toString()

        if (nama.isBlank() || umurText.isBlank() || hp.isBlank() || email.isBlank()) {
            Toast.makeText(this, "Lengkapi semua data penumpang", Toast.LENGTH_SHORT).show()
            return
        }

        val umur = umurText.toIntOrNull()
        if (umur == null) {
            Toast.makeText(this, "Umur harus berupa angka", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val penumpang = ApiClient.instance.createPenumpang(
                    PenumpangRequest(
                        nama_lengkap = nama,
                        umur = umur,
                        no_hp = hp,
                        email = email
                    )
                )

                val pemesanan = ApiClient.instance.createPemesanan(
                    PemesananRequest(
                        id_penumpang = penumpang.id_penumpang,
                        id_jadwal = idJadwal,
                        nomor_kursi = kursi.joinToString(","),
                        jumlah_kursi = kursi.size,
                        total_harga = hargaTiket * kursi.size
                    )
                )

                Toast.makeText(
                    this@DataPenumpangActivity,
                    "Pemesanan berhasil",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(this@DataPenumpangActivity, DetailPemesananActivity::class.java)
                        .putExtra("id_pemesanan", pemesanan.id_pemesanan)
                        .putExtra("nama_travel", namaTravel)
                        .putExtra("asal", asal)
                        .putExtra("tujuan", tujuan)
                        .putExtra("jam_berangkat", jamBerangkat)
                )

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@DataPenumpangActivity,
                    "Pemesanan gagal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}