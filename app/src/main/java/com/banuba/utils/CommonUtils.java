package com.banuba.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class CommonUtils {
    public void save(@NonNull Context context, @NonNull ByteBuffer buffer, @NonNull String prefix, int w, int h) {
        Bitmap image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        buffer.rewind();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                final int value = buffer.get() & 0xFF;
                image.setPixel(x, y, Color.argb(255, value, value, value));
            }
        }

//        try (FileOutputStream out = new FileOutputStream(new File(context.getFilesDir(), prefix + ".jpg"))) {
//            image.compress(Bitmap.CompressFormat.JPEG, 70, out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
