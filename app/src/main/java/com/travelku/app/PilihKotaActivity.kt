package com.travelku.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.travelku.app.databinding.ActivityPilihKotaBinding

class PilihKotaActivity : AppCompatActivity() {
    private lateinit var b: ActivityPilihKotaBinding
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityPilihKotaBinding.inflate(layoutInflater); setContentView(b.root)
        b.tvTitle.text = intent.getStringExtra("title") ?: "Pilih Kota"
        b.btnBack.setOnClickListener { finish() }

        val adapter = KotaAdapter(DataSource.KOTA.toMutableList()) { k ->
            setResult(Activity.RESULT_OK, Intent().putExtra("kota", k)); finish()
        }
        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter
        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s?.toString().orEmpty())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }
}

class KotaAdapter(private val all: MutableList<Kota>, val onClick: (Kota) -> Unit)
    : RecyclerView.Adapter<KotaAdapter.VH>() {
    private var data = all.toMutableList()
    fun filter(q: String) {
        data = if (q.isBlank()) all.toMutableList()
            else all.filter { it.nama.contains(q, true) }.toMutableList()
        notifyDataSetChanged()
    }
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val nama: TextView = v.findViewById(R.id.tvNama)
        val prov: TextView = v.findViewById(R.id.tvProv)
    }
    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_kota, p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val k = data[pos]
        h.nama.text = k.nama; h.prov.text = k.provinsi
        h.itemView.setOnClickListener { onClick(k) }
    }
    override fun getItemCount() = data.size
}
