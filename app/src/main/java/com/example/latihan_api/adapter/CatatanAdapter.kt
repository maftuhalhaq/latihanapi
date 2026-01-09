package com.example.latihan_api.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.latihan_api.databinding.ItemCatatanBinding
import com.example.latihan_api.entities.Catatan

class CatatanAdapter(
    private val dataset: MutableList<Catatan>,
    private val events: CatatanItemevents
) : RecyclerView.Adapter<CatatanAdapter.CatatanViewHolder>() {

    interface CatatanItemevents {
        fun onEdit(catatan: Catatan)
        fun onDelete(catatan: Catatan) // --- BARU: Tambah fungsi ini
    }

    inner class CatatanViewHolder(
        val view: ItemCatatanBinding
    ) : RecyclerView.ViewHolder(view.root) {

        fun setDataKeUI(data: Catatan) {
            view.judul.text = data.judul
            view.isi.text = data.isi

            // Klik Singkat -> Edit
            view.root.setOnClickListener{
                events.onEdit(data)
            }

            // --- BARU: Tekan Tahan (Long Click) -> Hapus ---
            view.root.setOnLongClickListener {
                events.onDelete(data)
                true // true artinya event ini selesai disini (tidak lanjut ke onClick)
            }
        }
    }

    // ... (sisa kode onCreateViewHolder, getItemCount, onBindViewHolder, updateDataset TETAP SAMA, tidak perlu diubah) ...
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatatanViewHolder {
        val binding = ItemCatatanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatatanViewHolder(binding)
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: CatatanViewHolder, position: Int) {
        holder.setDataKeUI(dataset[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataset(dataBaru: List<Catatan>) {
        dataset.clear()
        dataset.addAll(dataBaru)
        notifyDataSetChanged()
    }
}