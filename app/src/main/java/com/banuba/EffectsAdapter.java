package com.banuba;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class EffectsAdapter extends RecyclerView.Adapter<EffectsAdapter.EffectInfoViewHolder> {
    @NonNull
    private List<String> mEffectsList;
    @NonNull
    private OnEffectClickListener mCallback;

    public EffectsAdapter(@NonNull List<String> effectsList) {
        mEffectsList = effectsList;
    }

    public void setOnItemClickListener(OnEffectClickListener callback) {
        mCallback = callback;
    }

    public interface OnEffectClickListener {
        void onEffectClick(String effect);
    }

    @NonNull
    @Override
    public EffectInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.banuba_vbg_item_view, null);
        return new EffectInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EffectInfoViewHolder holder, int position) {
        String effectName = mEffectsList.get(position);
        holder.bind(effectName);
        holder.itemView.setOnClickListener($ -> mCallback.onEffectClick(effectName));
    }

    @Override
    public int getItemCount() {
        return mEffectsList.size();
    }

    class EffectInfoViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        void bind(String effectName) {
            if (effectName == "off()") {
                mImageView.setImageResource(R.mipmap.unload);
            } else {
                File preview = new File(effectName + "/preview.png");
                if (preview.exists()) {
                    mImageView.setImageBitmap(BitmapFactory.decodeFile(preview.getPath()));
                }
            }
        }

        EffectInfoViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iconView);
        }
    }
}
