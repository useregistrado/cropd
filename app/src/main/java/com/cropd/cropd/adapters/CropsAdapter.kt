package com.cropd.cropd.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cropd.cropd.R
import com.cropd.cropd.SamplingsActivity
import com.cropd.cropd.db.Crop
import io.realm.kotlin.query.RealmResults

class CropsAdapter(val crops: RealmResults<Crop>) : RecyclerView.Adapter<CropsAdapter.CropViewHolder>(){
    class CropViewHolder(itemView: View, val adapter: CropsAdapter) : RecyclerView.ViewHolder(itemView)  {
        var viewTitle: TextView
        var viewLastModified: TextView
        var viewCreationDate: TextView

        init {
            viewTitle = itemView.findViewById(R.id.titleCrop)
            viewLastModified = itemView.findViewById(R.id.lastModificationCrop)
            viewCreationDate = itemView.findViewById(R.id.creationCrop)

            itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(itemView.context, SamplingsActivity::class.java)
                itemView.context.startActivity(intent)
                val id = adapter.crops.get(adapterPosition)._id.toHexString()
                println("%&/&%$%&$#$%&//#")
                println(id)
                intent.putExtra("ID", id)
                itemView.context.startActivity(intent)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.card_crop, parent, false)
        return CropViewHolder(view, this)
    }

    override fun getItemCount(): Int {
        return crops.size

    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        holder.viewTitle.text = crops[position].name
        holder.viewLastModified.text = crops[position].lastModification
        holder.viewCreationDate.text = crops[position].creationDate
    }
}