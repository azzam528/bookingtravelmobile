package com.travelku.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.travelku.app.databinding.ActivityPilihKursiBinding

class PilihKursiActivity : AppCompatActivity() {
    private lateinit var b: ActivityPilihKursiBinding
    private lateinit var travel: Travel
    private val selected = mutableListOf<Int>()
    private val taken = DataSource.KURSI_TERPAKAI
    private val seatButtons = mutableMapOf<Int, Button>()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityPilihKursiBinding.inflate(layoutInflater); setContentView(b.root)
        travel = intent.getSerializableExtra("travel") as Travel
        val asal = intent.getSerializableExtra("asal") as Kota
        val tujuan = intent.getSerializableExtra("tujuan") as Kota
        val tanggal = intent.getStringExtra("tanggal") ?: ""

        b.btnBack.setOnClickListener { finish() }
        b.tvHeader.text = "${travel.nama} • ${travel.jam}"

        // Layout sesuai mockup: 1 - 3 - 3 - 2 - 3
        val rows = listOf(
            listOf(1, 0, 0),
            listOf(2, 3, 4),
            listOf(5, 6, 7),
            listOf(0, 8, 9),
            listOf(10, 11, 12),
        )
        rows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }
            row.forEach { n ->
                val v = if (n == 0) emptySeat() else seatButton(n)
                rowLayout.addView(v)
            }
            b.cabin.addView(rowLayout)
        }
        updateBar()

        b.btnLanjut.setOnClickListener {
            if (selected.isEmpty()) return@setOnClickListener
            startActivity(Intent(this, DataPenumpangActivity::class.java)
                .putExtra("travel", travel).putExtra("asal", asal)
                .putExtra("tujuan", tujuan).putExtra("tanggal", tanggal)
                .putExtra("kursi", ArrayList(selected.sorted())))
        }
    }

    private fun emptySeat(): View {
        val v = View(this)
        v.layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply {
            setMargins(dp(6), dp(6), dp(6), dp(6))
        }
        return v
    }
    private fun seatButton(n: Int): Button {
        val btn = Button(this)
        btn.text = n.toString()
        btn.layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply {
            setMargins(dp(6), dp(6), dp(6), dp(6))
        }
        btn.setPadding(0, 0, 0, 0)
        styleSeat(btn, n)
        btn.setOnClickListener {
            if (taken.contains(n)) return@setOnClickListener
            if (selected.contains(n)) selected.remove(n) else selected.add(n)
            styleSeat(btn, n); updateBar()
        }
        seatButtons[n] = btn
        return btn
    }
    private fun styleSeat(btn: Button, n: Int) {
        when {
            taken.contains(n) -> { btn.setBackgroundColor(Color.parseColor("#E2E8F0")); btn.setTextColor(Color.parseColor("#94A3B8")) }
            selected.contains(n) -> { btn.setBackgroundColor(Color.parseColor("#F59E0B")); btn.setTextColor(Color.WHITE) }
            else -> { btn.setBackgroundColor(Color.WHITE); btn.setTextColor(Color.parseColor("#2563EB")) }
        }
    }
    private fun updateBar() {
        b.tvSelected.text = "${selected.size} kursi dipilih"
        b.tvTotal.text = DataSource.rupiah(travel.harga * selected.size)
        b.btnLanjut.isEnabled = selected.isNotEmpty()
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
