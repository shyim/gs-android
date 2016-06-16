package de.shyim.gameserver_sponsor.cache;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ImageCache {
    public static ArrayList<String> keys;
    public static ArrayList<Bitmap> images;

    public static boolean exists(String name) {
        return keys.contains(name);
    }

    public static void put(String name, Bitmap image) {
        keys.add(name);
        images.add(image);
    }

    public static Bitmap get(String name) {
        return images.get(keys.indexOf(name));
    }
}
