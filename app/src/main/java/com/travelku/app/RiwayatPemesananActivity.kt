package com.travelku.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.travelku.app.api.ApiClient
import com.travelku.app.api.PemesananResponse
import com.travelku.app.databinding.ActivityRiwayatBinding
import kotlinx.coroutines.launch

class RiwayatPemesananActivity : AppCompatActivity() {

    private lateinit var b: ActivityRiwayatBinding
    private lateinit var adapter: RiwayatAdapter

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        b = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnBack.setOnClickListener { finish() }

        adapter = RiwayatAdapter(
            mutableListOf(),
            onOpen = { p ->
                startActivity(
                    Intent(this, DetailPemesananActivity::class.java)
                        .putExtra("id_pemesanan", p.id_pemesanan)
                        .putExtra("nama_travel", getNamaTravel(p.id_jadwal))
                        .putExtra("asal", "Asal")
                        .putExtra("tujuan", "Tujuan")
                        .putExtra("jam_berangkat", "-")
                )
            },
            onCancel = { p ->
                AlertDialog.Builder(this)
                    .setTitle("Batalkan pesanan?")
                    .setPositiveButton("Ya") { _, _ ->
                        batalPesanan(p.id_pemesanan)
                    }
                    .setNegativeButton("Tidak", null)
                    .show()
            }
        )

        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        lifecycleScope.launch {
            try {
                val list = ApiClient.instance.getRiwayatPemesanan()
                adapter.setData(list)
                b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(
                    this@RiwayatPemesananActivity,
                    "Gagal load riwayat: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun batalPesanan(id: Int) {
        lifecycleScope.launch {
            try {
                ApiClient.instance.batalPemesanan(id)
                Toast.makeText(this@RiwayatPemesananActivity, "Pesanan dibatalkan", Toast.LENGTH_SHORT).show()
                reload()
            } catch (e: Exception) {
                Toast.makeText(this@RiwayatPemesananActivity, "Gagal batal: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getNamaTravel(idJadwal: Int): String {
        return when (idJadwal) {
            5 -> "TravelKu Express"
            6 -> "TravelKu Premium"
            7 -> "TravelKu Executive"
            else -> "TravelKu"
        }
    }
}

class RiwayatAdapter(
    private var data: MutableList<PemesananResponse>,
    val onOpen: (PemesananResponse) -> Unit,
    val onCancel: (PemesananResponse) -> Unit
) : RecyclerView.Adapter<RiwayatAdapter.VH>() {

    fun setData(list: List<PemesananResponse>) {
        data = list.toMutableList()
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val travel: TextView = v.findViewById(R.id.tvTravel)
        val rute: TextView = v.findViewById(R.id.tvRute)
        val info: TextView = v.findViewById(R.id.tvInfo)
        val total: TextView = v.findViewById(R.id.tvTotal)
        val status: TextView = v.findViewById(R.id.tvStatus)
        val btnBatal: Button = v.findViewById(R.id.btnBatal)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        return VH(LayoutInflater.from(p.context).inflate(R.layout.item_riwayat, p, false))
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val p = data[pos]

        h.travel.text = "Pemesanan #${p.id_pemesanan}"
        h.rute.text = "${p.asal ?: "-"} → ${p.tujuan ?: "-"}"
        h.info.text =
            "${p.tanggal_berangkat ?: p.tanggal_pemesanan.take(10)} • ${p.jam_berangkat ?: "-"} • ${p.jumlah_kursi} kursi"
        h.total.text = DataSource.rupiah(p.total_harga.toInt())
        h.status.text = p.status_pemesanan

        val cancelled = p.status_pemesanan == "Dibatalkan"
        h.status.setTextColor(if (cancelled) Color.parseColor("#DC2626") else Color.parseColor("#16A34A"))
        h.btnBatal.visibility = if (cancelled) View.GONE else View.VISIBLE
        h.itemView.setOnClickListener { onOpen(p) }
        h.btnBatal.setOnClickListener { onCancel(p) }
    }

    override fun getItemCount(): Int = data.size
}