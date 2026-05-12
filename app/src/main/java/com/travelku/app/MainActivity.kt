package com.travelku.app

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.travelku.app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private var asal: Kota? = null
    private var tujuan: Kota? = null
    private var tanggal: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private val pickAsal = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            asal = it.data?.getSerializableExtra("kota") as? Kota; render()
        }
    }
    private val pickTujuan = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            tujuan = it.data?.getSerializableExtra("kota") as? Kota; render()
        }
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityMainBinding.inflate(layoutInflater); setContentView(b.root)

        b.btnAsal.setOnClickListener {
            pickAsal.launch(Intent(this, PilihKotaActivity::class.java).putExtra("title", "Pilih Kota Asal"))
        }
        b.btnTujuan.setOnClickListener {
            pickTujuan.launch(Intent(this, PilihKotaActivity::class.java).putExtra("title", "Pilih Kota Tujuan"))
        }
        b.btnTanggal.setOnClickListener { showDate() }
        b.btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatPemesananActivity::class.java))
        }
        b.btnCari.setOnClickListener {
            if (asal != null && tujuan != null && asal!!.id != tujuan!!.id) {
                startActivity(Intent(this, KatalogTravelActivity::class.java)
                    .putExtra("asal", asal).putExtra("tujuan", tujuan).putExtra("tanggal", tanggal))
            }
        }
        render()
    }

    private fun showDate() {
        val cal = Calendar.getInstance()
        val dlg = DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d); tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time); render()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        // Batasi hanya hari ini ke depan
        dlg.datePicker.minDate = System.currentTimeMillis() - 1000
        dlg.show()
    }

    private fun render() {
        b.tvAsal.text = asal?.nama ?: "Pilih kota asal"
        b.tvTujuan.text = tujuan?.nama ?: "Pilih kota tujuan"
        b.tvTanggal.text = tanggal
        b.btnCari.isEnabled = asal != null && tujuan != null && asal?.id != tujuan?.id
    }
}
