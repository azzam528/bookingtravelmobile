package com.travelku.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RiwayatStore {
    private const val PREF = "travelku"
    private const val KEY = "riwayat_v2"
    private val gson = Gson()

    fun load(ctx: Context): MutableList<Pemesanan> {
        val sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = sp.getString(KEY, "[]") ?: "[]"
        return try {
            val type = object : TypeToken<MutableList<Pemesanan>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }
    fun save(ctx: Context, list: List<Pemesanan>) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putString(KEY, gson.toJson(list)).apply()
    }
    fun add(ctx: Context, p: Pemesanan) {
        val list = load(ctx); list.add(0, p); save(ctx, list)
    }
    fun updateStatus(ctx: Context, id: String, status: String) {
        val list = load(ctx)
        list.forEachIndexed { i, p -> if (p.id == id) list[i] = p.copy(status = status) }
        save(ctx, list)
    }
}
