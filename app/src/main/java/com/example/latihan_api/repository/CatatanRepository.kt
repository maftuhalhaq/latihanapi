package com.example.latihan_api.repository

import com.example.latihan_api.entities.Catatan
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CatatanRepository {
    // Untuk Membuat Catatan Baru
    @POST("catatan")
    suspend fun createCatatan(@Body catatan: Catatan): Response<Catatan>

    // Untuk Mengambil Semua Data (Dipakai di MainActivity - loadData)
    @GET("catatan")
    suspend fun getCatatan(): Response<List<Catatan>>

    // Untuk Mengambil 1 Data (Dipakai di EditCatatan)
    @GET("catatan/{id}")
    suspend fun getCatatan(@Path("id") id: Int): Response<Catatan>

    // Untuk Mengedit Data
    @PUT("catatan/{id}")
    suspend fun editCatatan(@Path("id") id: Int, @Body catatan: Catatan): Response<Catatan>

    // Untuk Menghapus Data (Dipakai di MainActivity - hapusCatatanDariServer)
    @DELETE("catatan/{id}")
    suspend fun deleteCatatan(@Path("id") id: Int): Response<Void>
}