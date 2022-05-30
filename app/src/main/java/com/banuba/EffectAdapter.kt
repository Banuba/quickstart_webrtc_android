package com.banuba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class EffectAdapter : RecyclerView.Adapter<EffectAdapter.ViewHolder>() {
    var effectList: List<String>? = null
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }
    private var selectedEffect: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var onItemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int = effectList?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.banuba_vbg_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(holder, position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val selectedFrame: ImageView = itemView.findViewById(R.id.selectedFrameView)
        private val iconView: ImageView = itemView.findViewById(R.id.iconView)
        private val iconTv: TextView = itemView.findViewById(R.id.iconTv)

        fun bindViewHolder(holder: ViewHolder, position: Int) {
            val effectDir = effectList?.get(position) ?: return

            if (effectDir == "off()") {
                iconTv.visibility = View.VISIBLE
                iconTv.text = "off()"
                iconView.visibility = View.GONE
            } else {
                if (File(effectDir).exists()) {
                    Glide.with(holder.itemView.context).load(effectDir + "/preview.png").into(holder.iconView)
                    iconTv.visibility = View.GONE
                    iconTv.text = ""
                    iconView.visibility = View.VISIBLE
                } else {
                    iconTv.visibility = View.VISIBLE
                    iconTv.text = "Effect"
                    iconView.visibility = View.GONE
                }
            }

            selectedFrame.visibility = if (selectedEffect == effectDir) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

            holder.itemView.setOnClickListener {
                selectedEffect = effectDir
                onItemClickListener?.onItemClick(effectDir)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(info: String)
    }

}
