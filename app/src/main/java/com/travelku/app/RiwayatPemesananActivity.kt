package com.travelku.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.travelku.app.databinding.ActivityRiwayatBinding

class RiwayatPemesananActivity : AppCompatActivity() {
    private lateinit var b: ActivityRiwayatBinding
    private lateinit var adapter: RiwayatAdapter
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityRiwayatBinding.inflate(layoutInflater); setContentView(b.root)
        b.btnBack.setOnClickListener { finish() }
        adapter = RiwayatAdapter(
            mutableListOf(),
            onOpen = { p ->
                startActivity(Intent(this, DetailPemesananActivity::class.java).putExtra("pemesanan", p))
            },
            onCancel = { p ->
                AlertDialog.Builder(this).setTitle("Batalkan pesanan?")
                    .setPositiveButton("Ya") { _, _ ->
                        RiwayatStore.updateStatus(this, p.id, "Dibatalkan")
                        reload()
                    }
                    .setNegativeButton("Tidak", null).show()
            }
        )
        b.rv.layoutManager = LinearLayoutManager(this); b.rv.adapter = adapter
    }
    override fun onResume() { super.onResume(); reload() }
    private fun reload() {
        val list = RiwayatStore.load(this)
        adapter.setData(list)
        b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }
}

class RiwayatAdapter(
    private var data: MutableList<Pemesanan>,
    val onOpen: (Pemesanan) -> Unit,
    val onCancel: (Pemesanan) -> Unit
) : RecyclerView.Adapter<RiwayatAdapter.VH>() {
    fun setData(list: List<Pemesanan>) { data = list.toMutableList(); notifyDataSetChanged() }
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val travel: TextView = v.findViewById(R.id.tvTravel)
        val rute: TextView = v.findViewById(R.id.tvRute)
        val info: TextView = v.findViewById(R.id.tvInfo)
        val total: TextView = v.findViewById(R.id.tvTotal)
        val status: TextView = v.findViewById(R.id.tvStatus)
        val btnBatal: Button = v.findViewById(R.id.btnBatal)
    }
    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_riwayat, p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val p = data[pos]
        h.travel.text = p.travelNama
        h.rute.text = "${p.asal} → ${p.tujuan}"
        h.info.text = "${p.tanggal} • ${p.jam} • ${p.penumpang.size} penumpang"
        h.total.text = DataSource.rupiah(p.total)
        h.status.text = p.status
        val cancelled = p.status == "Dibatalkan"
        h.status.setTextColor(if (cancelled) Color.parseColor("#DC2626") else Color.parseColor("#16A34A"))
        h.btnBatal.visibility = if (cancelled) View.GONE else View.VISIBLE
        h.itemView.setOnClickListener { onOpen(p) }
        h.btnBatal.setOnClickListener { onCancel(p) }
    }
    override fun getItemCount() = data.size
}
