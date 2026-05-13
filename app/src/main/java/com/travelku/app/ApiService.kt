package com.travelku.app

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("kota")
    suspend fun getKota(
        @Header("x-api-key") apiKey: String
    ): List<KotaResponse>

    @GET("kota/search")
    suspend fun searchKota(@Query("nama") nama: String): List<KotaResponse>

    @GET("bus")
    suspend fun getBus(): List<BusResponse>

    @GET("rute")
    suspend fun getRute(): List<RuteResponse>

    @GET("rute/search")
    suspend fun searchRute(
        @Query("asal") asal: Int,
        @Query("tujuan") tujuan: Int
    ): List<RuteResponse>

    @GET("jadwal")
    suspend fun getJadwal(): List<JadwalResponse>

    @GET("jadwal/search")
    suspend fun searchJadwal(
        @Query("rute") rute: Int,
        @Query("tanggal") tanggal: String
    ): List<JadwalResponse>

    @POST("penumpang")
    suspend fun createPenumpang(@Body request: PenumpangRequest): PenumpangResponse

    @POST("pemesanan")
    suspend fun createPemesanan(@Body request: PemesananRequest): PemesananResponse

    @GET("pemesanan/riwayat")
    suspend fun getRiwayatPemesanan(): List<PemesananResponse>

    @PUT("pemesanan/{id}/batal")
    suspend fun batalPemesanan(@Path("id") id: Int): PemesananResponse
}