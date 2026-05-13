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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.travelku.app.api.ApiClient
import com.travelku.app.api.JadwalResponse
import com.travelku.app.databinding.ActivityKatalogBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class KatalogTravelActivity : AppCompatActivity() {

    private lateinit var b: ActivityKatalogBinding
    private lateinit var adapter: TravelAdapter

    private var idRute: Int = 0
    private var asal: String = ""
    private var tujuan: String = ""
    private var tanggal: String = ""

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        b = ActivityKatalogBinding.inflate(layoutInflater)
        setContentView(b.root)

        idRute = intent.getIntExtra("id_rute", 0)
        asal = intent.getStringExtra("asal") ?: ""
        tujuan = intent.getStringExtra("tujuan") ?: ""
        tanggal = intent.getStringExtra("tanggal") ?: ""

        b.tvRoute.text = "$asal → $tujuan • $tanggal"
        b.btnBack.setOnClickListener { finish() }

        adapter = TravelAdapter(mutableListOf()) { jadwal ->
            val namaTravel = getNamaTravel(jadwal.id_bus)

            startActivity(
                Intent(this, PilihKursiActivity::class.java)
                    .putExtra("id_jadwal", jadwal.id_jadwal)
                    .putExtra("nama_travel", namaTravel)
                    .putExtra("asal", asal)
                    .putExtra("tujuan", tujuan)
                    .putExtra("tanggal", tanggal)
                    .putExtra("jam_berangkat", jadwal.jam_berangkat)
                    .putExtra("harga_tiket", jadwal.harga_tiket)
            )
        }

        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.applyFilter(s?.toString().orEmpty())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        val sortOptions = arrayOf("Termurah", "Termahal", "Pagi", "Malam")

        b.spSort.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            sortOptions
        )

        b.spSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                adapter.applySort(sortOptions[pos])
            }

            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        loadJadwal()
    }

    private fun loadJadwal() {
        lifecycleScope.launch {
            try {
                val result = ApiClient.instance.searchJadwal(
                    rute = idRute,
                    tanggal = tanggal
                )

                if (result.isEmpty()) {
                    Toast.makeText(
                        this@KatalogTravelActivity,
                        "Jadwal tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                adapter.updateData(result)

            } catch (e: Exception) {
                Toast.makeText(
                    this@KatalogTravelActivity,
                    "Gagal load jadwal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getNamaTravel(idBus: Int): String {
        return when (idBus) {
            1 -> "TravelKu Express"
            2 -> "TravelKu Premium"
            3 -> "TravelKu Executive"
            else -> "TravelKu"
        }
    }
}

class TravelAdapter(
    private val all: MutableList<JadwalResponse>,
    val onClick: (JadwalResponse) -> Unit
) : RecyclerView.Adapter<TravelAdapter.VH>() {

    private var query = ""
    private var sort = "Termurah"
    private var data = all.toMutableList()

    fun updateData(newData: List<JadwalResponse>) {
        all.clear()
        all.addAll(newData)
        data = recompute()
        notifyDataSetChanged()
    }

    fun applyFilter(q: String) {
        query = q
        data = recompute()
        notifyDataSetChanged()
    }

    fun applySort(s: String) {
        sort = s
        data = recompute()
        notifyDataSetChanged()
    }

    private fun recompute(): MutableList<JadwalResponse> {
        val filtered = all.filter {
            it.jam_berangkat.contains(query, true) ||
                    it.harga_tiket.toString().contains(query, true)
        }

        return when (sort) {
            "Termahal" -> filtered.sortedByDescending { it.harga_tiket }
            "Pagi" -> filtered.sortedBy { it.jam_berangkat }
            "Malam" -> filtered.sortedByDescending { it.jam_berangkat }
            else -> filtered.sortedBy { it.harga_tiket }
        }.toMutableList()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val nama: TextView = v.findViewById(R.id.tvNama)
        val jam: TextView = v.findViewById(R.id.tvJam)
        val harga: TextView = v.findViewById(R.id.tvHarga)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        return VH(
            LayoutInflater.from(p.context)
                .inflate(R.layout.item_travel, p, false)
        )
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val t = data[pos]

        val namaTravel = when (t.id_bus) {
            1 -> "TravelKu Express"
            2 -> "TravelKu Premium"
            3 -> "TravelKu Executive"
            else -> "TravelKu"
        }

        h.nama.text = namaTravel
        h.jam.text = "Berangkat ${t.jam_berangkat}"
        h.harga.text = rupiah(t.harga_tiket)

        h.itemView.setOnClickListener {
            onClick(t)
        }
    }

    override fun getItemCount(): Int = data.size

    private fun rupiah(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(value)
    }
}