package com.example.latihan_api

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.latihan_api.adapter.CatatanAdapter
import com.example.latihan_api.databinding.ActivityMainBinding
import com.example.latihan_api.entities.Catatan
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CatatanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupEvents()
    }

    fun setupEvents() {
        // Update bagian inisialisasi Adapter
        adapter = CatatanAdapter(mutableListOf(), object : CatatanAdapter.CatatanItemevents {

            // Event Edit (Tetap sama)
            override fun onEdit(catatan: Catatan) {
                val intent = Intent(this@MainActivity, EditCatatanActivity::class.java)
                intent.putExtra("id_catatan", catatan.id)
                startActivity(intent)
            }

            // --- BARU: Event Delete ---
            override fun onDelete(catatan: Catatan) {
                // Panggil fungsi untuk menampilkan Pop-Up
                tampilkanDialogHapus(catatan)
            }
        })

        binding.container.adapter = adapter
        binding.container.layoutManager = LinearLayoutManager(this)

        // Tombol Tambah (Tetap sama)
        binding.btnNavigate.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateCatatan::class.java)
            startActivity(intent)
        }
    }

    private fun tampilkanDialogHapus(catatan: Catatan) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Hapus Catatan")
        builder.setMessage("Apakah Anda yakin ingin menghapus catatan '${catatan.judul}'?")

        // Tombol Ya (Hapus)
        builder.setPositiveButton("Hapus") { dialog, which ->
            hapusCatatanDariServer(catatan.id ?: 0)
        }

        // Tombol Tidak (Batal)
        builder.setNegativeButton("Batal") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun hapusCatatanDariServer(id: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.catatanRepository.deleteCatatan(id)

                if (response.isSuccessful) {
                    displayMessage("Berhasil dihapus!")
                    loadData() // Refresh list agar data yang dihapus hilang dari layar
                } else {
                    displayMessage("Gagal menghapus: ${response.message()}")
                }
            } catch (e: Exception) {
                displayMessage("Error Koneksi: ${e.message}")
            }
        }
    }


    override fun onStart() {
        super.onStart()
        loadData()
    }

    fun loadData() {
        // JEJAK 1: Mulai proses
        Log.d("CekData", "Fungsi loadData() dipanggil! Sedang menghubungi server...")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.catatanRepository.getCatatan()

                // JEJAK 2: Server merespon
                Log.d("CekData", "Respon diterima. Code: ${response.code()}")

                if (!response.isSuccessful) {
                    Log.e("CekData", "Gagal Server: ${response.message()}")
                    displayMessage("Gagal : ${response.message()}")
                    return@launch
                }

                val data = response.body()

                // JEJAK 3: Cek isi data
                Log.d("CekData", "Isi Data: $data")

                if (data == null) {
                    Log.e("CekData", "Data Kosong (Null)")
                    displayMessage("Tidak ada data")
                    return@launch
                }

                // JEJAK 4: Masukkan ke Adapter
                Log.d("CekData", "Menampilkan ${data.size} catatan ke layar")
                adapter.updateDataset(data)

            } catch (e: Exception) {
                // JEJAK 5: Error Koneksi (Paling sering terjadi)
                Log.e("CekData", "ERROR KONEKSI FATAL: ${e.message}")
                e.printStackTrace()
                displayMessage("Error Koneksi: ${e.message}")
            }
        }
    }

    fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}