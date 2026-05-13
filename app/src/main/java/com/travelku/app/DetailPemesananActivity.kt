package com.travelku.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.travelku.app.databinding.ActivityDetailBinding

class DetailPemesananActivity : AppCompatActivity() {
    private lateinit var b: ActivityDetailBinding
    private lateinit var p: Pemesanan
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityDetailBinding.inflate(layoutInflater); setContentView(b.root)
        p = intent.getSerializableExtra("pemesanan") as Pemesanan
        b.btnBack.setOnClickListener { goHome() }

        b.tvTravel.text = p.travelNama
        b.tvRute.text = "${p.asal} → ${p.tujuan}"
        b.tvTanggal.text = p.tanggal
        b.tvJam.text = p.jam
        b.tvStatus.text = p.status
        b.tvStatus.setTextColor(if (p.status == "Dibatalkan") Color.parseColor("#DC2626") else Color.parseColor("#16A34A"))

        p.penumpang.forEachIndexed { i, pn ->
            val card = LayoutInflater.from(this).inflate(R.layout.item_penumpang_detail, b.penumpangContainer, false) as LinearLayout
            card.findViewById<TextView>(R.id.tvLabel).text = "PENUMPANG ${i + 1}  •  KURSI ${pn.kursi}"
            card.findViewById<TextView>(R.id.tvNama).text = pn.nama
            card.findViewById<TextView>(R.id.tvUmur).text = pn.umur
            card.findViewById<TextView>(R.id.tvHp).text = pn.hp
            card.findViewById<TextView>(R.id.tvEmail).text = pn.email
            b.penumpangContainer.addView(card)
        }

        b.tvKursiCount.text = "${p.kursi.size} kursi"
        b.tvTotal.text = DataSource.rupiah(p.total)
        b.tvTotalAtas.text = DataSource.rupiah(p.total)

        if (p.status == "Dibatalkan") b.btnBatal.visibility = android.view.View.GONE
        b.btnBatal.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Batalkan pesanan?")
                .setPositiveButton("Ya") { _, _ ->
                    RiwayatStore.updateStatus(this, p.id, "Dibatalkan")
                    goHome()
                }
                .setNegativeButton("Tidak", null).show()
        }
        b.btnSelesai.setOnClickListener { goHome() }
    }

    private fun goHome() {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i); finishAffinity()
    }
}
