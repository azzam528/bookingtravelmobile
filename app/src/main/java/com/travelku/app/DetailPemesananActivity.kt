package com.travelku.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.travelku.app.api.ApiClient
import com.travelku.app.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailPemesananActivity : AppCompatActivity() {

    private lateinit var b: ActivityDetailBinding

    private var idPemesanan = 0
    private var namaTravel = "-"
    private var asal = "-"
    private var tujuan = "-"
    private var jamBerangkat = "-"

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        b = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(b.root)

        idPemesanan = intent.getIntExtra("id_pemesanan", 0)
        namaTravel = intent.getStringExtra("nama_travel") ?: "-"
        asal = intent.getStringExtra("asal") ?: "-"
        tujuan = intent.getStringExtra("tujuan") ?: "-"
        jamBerangkat = intent.getStringExtra("jam_berangkat") ?: "-"

        b.btnBack.setOnClickListener { goHome() }
        b.btnSelesai.setOnClickListener { goHome() }

        b.btnBatal.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Batalkan pesanan?")
                .setPositiveButton("Ya") { _, _ ->
                    batalPemesanan()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        loadDetail()
    }

    private fun loadDetail() {
        lifecycleScope.launch {
            try {
                val p = ApiClient.instance.getDetailPemesanan(idPemesanan)

                b.tvTravel.text = namaTravel
                b.tvRute.text = "$asal → $tujuan"
                b.tvTanggal.text = p.tanggal_pemesanan.take(10)
                b.tvJam.text = jamBerangkat
                b.tvStatus.text = p.status_pemesanan
                b.tvKursiCount.text = "${p.jumlah_kursi} kursi"
                b.tvTotal.text = DataSource.rupiah(p.total_harga.toInt())
                b.tvTotalAtas.text = DataSource.rupiah(p.total_harga.toInt())

                b.tvStatus.setTextColor(
                    if (p.status_pemesanan == "Dibatalkan")
                        Color.parseColor("#DC2626")
                    else
                        Color.parseColor("#16A34A")
                )

                b.penumpangContainer.removeAllViews()

                b.btnBatal.visibility =
                    if (p.status_pemesanan == "Dibatalkan") View.GONE else View.VISIBLE

            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailPemesananActivity,
                    "Gagal load detail: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun batalPemesanan() {
        lifecycleScope.launch {
            try {
                ApiClient.instance.batalPemesanan(idPemesanan)

                Toast.makeText(
                    this@DetailPemesananActivity,
                    "Pesanan dibatalkan",
                    Toast.LENGTH_SHORT
                ).show()

                loadDetail()

            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailPemesananActivity,
                    "Gagal batal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun goHome() {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        finishAffinity()
    }
}