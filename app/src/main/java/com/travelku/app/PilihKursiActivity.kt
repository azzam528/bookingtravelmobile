package com.travelku.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.travelku.app.databinding.ActivityPilihKursiBinding

class PilihKursiActivity : AppCompatActivity() {

    private lateinit var b: ActivityPilihKursiBinding

    private var idJadwal = 0
    private var asal = ""
    private var tujuan = ""
    private var tanggal = ""
    private var jamBerangkat = ""
    private var hargaTiket = 0.0

    private val selected = mutableListOf<Int>()
    private val taken = DataSource.KURSI_TERPAKAI

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        b = ActivityPilihKursiBinding.inflate(layoutInflater)
        setContentView(b.root)

        idJadwal = intent.getIntExtra("id_jadwal", 0)
        asal = intent.getStringExtra("asal") ?: ""
        tujuan = intent.getStringExtra("tujuan") ?: ""
        tanggal = intent.getStringExtra("tanggal") ?: ""
        jamBerangkat = intent.getStringExtra("jam_berangkat") ?: ""
        hargaTiket = intent.getDoubleExtra("harga_tiket", 0.0)

        b.btnBack.setOnClickListener { finish() }
        b.tvHeader.text = "$asal → $tujuan • $jamBerangkat"

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
                val view = if (n == 0) emptySeat() else seatButton(n)
                rowLayout.addView(view)
            }

            b.cabin.addView(rowLayout)
        }

        updateBar()

        b.btnLanjut.setOnClickListener {
            if (selected.isEmpty()) return@setOnClickListener

            startActivity(
                Intent(this, DataPenumpangActivity::class.java)
                    .putExtra("id_jadwal", idJadwal)
                    .putExtra("asal", asal)
                    .putExtra("tujuan", tujuan)
                    .putExtra("tanggal", tanggal)
                    .putExtra("jam_berangkat", jamBerangkat)
                    .putExtra("harga_tiket", hargaTiket)
                    .putExtra("kursi", ArrayList(selected.sorted()))
            )
        }
    }

    private fun emptySeat(): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply {
                setMargins(dp(6), dp(6), dp(6), dp(6))
            }
        }
    }

    private fun seatButton(n: Int): Button {
        return Button(this).apply {
            text = n.toString()
            layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply {
                setMargins(dp(6), dp(6), dp(6), dp(6))
            }

            setPadding(0, 0, 0, 0)
            styleSeat(this, n)

            setOnClickListener {
                if (taken.contains(n)) return@setOnClickListener

                if (!selected.contains(n) && selected.size >= 4) {
                    Toast.makeText(
                        this@PilihKursiActivity,
                        "Maksimal 4 kursi",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (selected.contains(n)) {
                    selected.remove(n)
                } else {
                    selected.add(n)
                }

                styleSeat(this, n)
                updateBar()
            }
        }
    }

    private fun styleSeat(btn: Button, n: Int) {
        when {
            taken.contains(n) -> {
                btn.setBackgroundColor(Color.parseColor("#E2E8F0"))
                btn.setTextColor(Color.parseColor("#94A3B8"))
            }

            selected.contains(n) -> {
                btn.setBackgroundColor(Color.parseColor("#F59E0B"))
                btn.setTextColor(Color.WHITE)
            }

            else -> {
                btn.setBackgroundColor(Color.WHITE)
                btn.setTextColor(Color.parseColor("#2563EB"))
            }
        }
    }

    private fun updateBar() {
        b.tvSelected.text = "${selected.size} kursi dipilih"
        b.tvTotal.text = DataSource.rupiah((hargaTiket * selected.size).toInt())
        b.btnLanjut.isEnabled = selected.isNotEmpty()
    }

    private fun dp(v: Int): Int {
        return (v * resources.displayMetrics.density).toInt()
    }
}