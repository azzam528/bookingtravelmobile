package com.travelku.app

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.travelku.app.api.ApiClient
import com.travelku.app.api.KotaResponse
import com.travelku.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    private var asal: KotaResponse? = null
    private var tujuan: KotaResponse? = null

    private var tanggal: String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private val pickAsal =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                asal = it.data?.getSerializableExtra("kota") as? KotaResponse
                render()
            }
        }

    private val pickTujuan =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                tujuan = it.data?.getSerializableExtra("kota") as? KotaResponse
                render()
            }
        }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnAsal.setOnClickListener {
            pickAsal.launch(
                Intent(this, PilihKotaActivity::class.java)
                    .putExtra("title", "Pilih Kota Asal")
            )
        }

        b.btnTujuan.setOnClickListener {
            pickTujuan.launch(
                Intent(this, PilihKotaActivity::class.java)
                    .putExtra("title", "Pilih Kota Tujuan")
            )
        }

        b.btnSwap.setOnClickListener {
            val temp = asal
            asal = tujuan
            tujuan = temp
            render()
        }

        b.btnTanggal.setOnClickListener {
            showDate()
        }

        b.btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatPemesananActivity::class.java))
        }

        b.btnCari.setOnClickListener {
            cariRuteDanJadwal()
        }

        render()
    }

    private fun cariRuteDanJadwal() {
        val kotaAsal = asal
        val kotaTujuan = tujuan

        if (kotaAsal == null || kotaTujuan == null) {
            Toast.makeText(this, "Pilih kota asal dan tujuan", Toast.LENGTH_SHORT).show()
            return
        }

        if (kotaAsal.id_kota == kotaTujuan.id_kota) {
            Toast.makeText(this, "Kota asal dan tujuan tidak boleh sama", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val ruteList = ApiClient.instance.searchRute(
                    asal = kotaAsal.id_kota,
                    tujuan = kotaTujuan.id_kota
                )

                if (ruteList.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Rute tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val rute = ruteList[0]

                val intent = Intent(this@MainActivity, KatalogTravelActivity::class.java)
                intent.putExtra("id_rute", rute.id_rute)
                intent.putExtra("asal", kotaAsal.nama_kota)
                intent.putExtra("tujuan", kotaTujuan.nama_kota)
                intent.putExtra("tanggal", tanggal)

                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Gagal mencari rute: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showDate() {
        val cal = Calendar.getInstance()

        val dlg = DatePickerDialog(
            this,
            { _, y, m, d ->
                cal.set(y, m, d)
                tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                render()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        dlg.datePicker.minDate = System.currentTimeMillis() - 1000
        dlg.show()
    }

    private fun render() {
        b.tvAsal.text = asal?.nama_kota ?: "Pilih kota asal"
        b.tvTujuan.text = tujuan?.nama_kota ?: "Pilih kota tujuan"
        b.tvTanggal.text = tanggal

        b.btnCari.isEnabled =
            asal != null &&
                    tujuan != null &&
                    asal?.id_kota != tujuan?.id_kota
    }
}