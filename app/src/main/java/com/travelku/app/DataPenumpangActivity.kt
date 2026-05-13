package com.travelku.app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.travelku.app.databinding.ActivityDataPenumpangBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataPenumpangActivity : AppCompatActivity() {
    private lateinit var b: ActivityDataPenumpangBinding
    private lateinit var travel: Travel
    private lateinit var asal: Kota
    private lateinit var tujuan: Kota
    private lateinit var tanggal: String
    private lateinit var kursi: ArrayList<Int>
    private val penumpangs = mutableListOf<Penumpang>()
    private val rows = mutableListOf<Map<String, EditText>>()

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityDataPenumpangBinding.inflate(layoutInflater); setContentView(b.root)
        travel = intent.getSerializableExtra("travel") as Travel
        asal = intent.getSerializableExtra("asal") as Kota
        tujuan = intent.getSerializableExtra("tujuan") as Kota
        tanggal = intent.getStringExtra("tanggal") ?: ""
        kursi = (intent.getSerializableExtra("kursi") as ArrayList<Int>)
        b.btnBack.setOnClickListener { finish() }

        b.tvRute.text = "${asal.nama} → ${tujuan.nama}"
        b.tvDetail.text = "${travel.nama} • $tanggal • ${travel.jam} • Kursi ${kursi.joinToString(", ")}"
        b.tvHarga.text = DataSource.rupiah(travel.harga * kursi.size)

        kursi.forEachIndexed { i, k ->
            penumpangs.add(Penumpang(kursi = k))
            val card = LayoutInflater.from(this).inflate(R.layout.item_penumpang_input, b.container, false) as LinearLayout
            card.findViewById<TextView>(R.id.tvLabel).text = "Penumpang ${i + 1}  •  Kursi $k"
            val nama = card.findViewById<EditText>(R.id.etNama)
            val umur = card.findViewById<EditText>(R.id.etUmur).apply { inputType = InputType.TYPE_CLASS_NUMBER }
            val hp = card.findViewById<EditText>(R.id.etHp).apply { inputType = InputType.TYPE_CLASS_PHONE }
            val email = card.findViewById<EditText>(R.id.etEmail).apply { inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS }
            rows.add(mapOf("nama" to nama, "umur" to umur, "hp" to hp, "email" to email))
            b.container.addView(card)
        }

        b.btnPesan.setOnClickListener { pesan() }
    }

    private fun pesan() {
        rows.forEachIndexed { i, m ->
            penumpangs[i].nama = m["nama"]!!.text.toString()
            penumpangs[i].umur = m["umur"]!!.text.toString()
            penumpangs[i].hp = m["hp"]!!.text.toString()
            penumpangs[i].email = m["email"]!!.text.toString()
        }
        if (penumpangs.any { it.nama.isBlank() || it.umur.isBlank() || it.hp.isBlank() || it.email.isBlank() }) {
            android.widget.Toast.makeText(this, "Lengkapi semua data penumpang", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val pemesanan = Pemesanan(
            id = "TRV" + System.currentTimeMillis().toString().takeLast(8),
            travelNama = travel.nama, asal = asal.nama, tujuan = tujuan.nama,
            tanggal = tanggal, jam = travel.jam, kursi = kursi,
            total = travel.harga * kursi.size, penumpang = penumpangs.toList(),
            status = "Berhasil", tanggalPesan = today
        )
        RiwayatStore.add(this, pemesanan)
        startActivity(Intent(this, DetailPemesananActivity::class.java).putExtra("pemesanan", pemesanan))
        finish()
    }
}
