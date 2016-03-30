package nl.group11.planplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

//TODO comments

/**
 * Created by s132054 on 15-3-2016.
 */
public class ImageCache {

    LruCache<String, Bitmap> cache;
    Bitmap fallback;

    ImageCache(Context context) {
        int ram = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int maxSize = ram / 32;
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        fallback = BitmapFactory.decodeResource(context.getResources(), R.drawable.imgnotfound);
    }

    public Bitmap setImageFromURL(final String url, final APIHandler.Callback<Bitmap> callback) {
        if (url == null) {
            return fallback;
        } else if (cache.get(url) != null) { //use bitmap from cache
            return cache.get(url);
        } else { //request bitmap from URL and set in callback
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        InputStream in = new URL(url).openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        return bitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return fallback;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    cache.put(url, bitmap);
                    callback.onItem(bitmap);
                }
            }.execute();
            return fallback;
        }
    }
}
