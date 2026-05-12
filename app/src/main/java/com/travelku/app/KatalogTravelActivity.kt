package com.travelku.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.travelku.app.databinding.ActivityKatalogBinding

class KatalogTravelActivity : AppCompatActivity() {
    private lateinit var b: ActivityKatalogBinding
    private lateinit var asal: Kota
    private lateinit var tujuan: Kota
    private lateinit var tanggal: String

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityKatalogBinding.inflate(layoutInflater); setContentView(b.root)
        asal = intent.getSerializableExtra("asal") as Kota
        tujuan = intent.getSerializableExtra("tujuan") as Kota
        tanggal = intent.getStringExtra("tanggal") ?: ""
        b.tvRoute.text = "${asal.nama} → ${tujuan.nama} • $tanggal"
        b.btnBack.setOnClickListener { finish() }

        val adapter = TravelAdapter(DataSource.TRAVELS.toMutableList()) { t ->
            startActivity(Intent(this, PilihKursiActivity::class.java)
                .putExtra("travel", t).putExtra("asal", asal)
                .putExtra("tujuan", tujuan).putExtra("tanggal", tanggal))
        }
        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { adapter.applyFilter(s?.toString().orEmpty()) }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        val sortOptions = arrayOf("Termurah", "Termahal", "Pagi", "Malam")
        b.spSort.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)
        b.spSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                adapter.applySort(sortOptions[pos])
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }
}

class TravelAdapter(private val all: MutableList<Travel>, val onClick: (Travel) -> Unit)
    : RecyclerView.Adapter<TravelAdapter.VH>() {
    private var query = ""
    private var sort = "Termurah"
    private var data = recompute()

    fun applyFilter(q: String) { query = q; data = recompute(); notifyDataSetChanged() }
    fun applySort(s: String) { sort = s; data = recompute(); notifyDataSetChanged() }
    private fun recompute(): MutableList<Travel> {
        val f = all.filter { it.nama.contains(query, true) }
        return when (sort) {
            "Termahal" -> f.sortedByDescending { it.harga }
            "Pagi" -> f.sortedBy { it.jam }
            "Malam" -> f.sortedByDescending { it.jam }
            else -> f.sortedBy { it.harga }
        }.toMutableList()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val nama: TextView = v.findViewById(R.id.tvNama)
        val jam: TextView = v.findViewById(R.id.tvJam)
        val harga: TextView = v.findViewById(R.id.tvHarga)
    }
    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_travel, p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val t = data[pos]
        h.nama.text = t.nama
        h.jam.text = "${t.jam} • ${t.durasi}"
        h.harga.text = DataSource.rupiah(t.harga)
        h.itemView.setOnClickListener { onClick(t) }
    }
    override fun getItemCount() = data.size
}
