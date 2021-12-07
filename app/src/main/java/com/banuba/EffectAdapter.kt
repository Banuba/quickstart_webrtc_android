package com.banuba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.banuba.video.processor.EffectInfo
import com.banuba.video.processor.EffectType

class EffectAdapter : RecyclerView.Adapter<EffectAdapter.ViewHolder>() {
    var effectList: List<EffectInfo>? = null
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }
    private var selectedEffect: EffectInfo? = null
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
            val info = effectList?.get(position) ?: return
            when (info.type) {
                EffectType.VBG -> {
                    Glide.with(holder.itemView.context).load(info.filePath).into(holder.iconView)
                    iconTv.visibility = View.GONE
                    iconView.visibility = View.VISIBLE
                }
                EffectType.Blur -> {
                    iconTv.visibility = View.VISIBLE
                    iconTv.text = "Blur"
                    iconView.visibility = View.GONE
                }
                EffectType.OFF -> {
                    iconTv.visibility = View.VISIBLE
                    iconTv.text = "OFF"
                    iconView.visibility = View.GONE
                }
                EffectType.MP4 -> {
                    iconTv.visibility = View.VISIBLE
                    iconTv.text = "MP4"
                    iconView.visibility = View.GONE
                }
                EffectType.GIF -> {
                    iconTv.visibility = View.VISIBLE
                    iconTv.text = "GIF"
                    iconView.visibility = View.GONE
                }
                EffectType.Select -> {
                    iconTv.visibility = View.VISIBLE
                    iconTv.text = "Select"
                    iconView.visibility = View.GONE
                }
                EffectType.Makeup -> {
                    iconTv.visibility = View.VISIBLE
                    iconTv.text = "Makeup"
                    iconView.visibility = View.GONE
                }
            }

            selectedFrame.visibility = if (selectedEffect == info) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

            holder.itemView.setOnClickListener {
                selectedEffect = info
                onItemClickListener?.onItemClick(info)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(info: EffectInfo)
    }

}
