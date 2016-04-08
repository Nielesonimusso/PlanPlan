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
 *
 * Singleton Cache for images; makes displaying images that come from the internet more efficient
 */
abstract public class ImageCache {

    //internal image cache
    LruCache<String, Bitmap> cache;
    //fallback image, used when an image cannot be found in the cache
    Bitmap fallback;

    //singleton instance
    static ImageCache instance;

    /**
     * singleton instance initializer; should be called before {@link #getInstance()} is called
     *
     * @param context context used to initialize singleton instance
     */
    static void initInstance(Context context) {
        if (instance == null) {
            instance = new ImageCache(context) {
            };
        }
    }

    static ImageCache getInstance() {
        return instance;
    }

    ImageCache(Context context) {
        int ram = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int maxSize = ram / 8;
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        fallback = BitmapFactory.decodeResource(context.getResources(), R.drawable.imgnotfound);
    }

    /**
     * returns an image, given an image url. Images are cached, so that future requests do not
     * generate more internet requests
     *
     * @param url the url of the image to be fetched
     * @param callback callback that can be used to notify the image requestor that their image is
     *                 ready
     * @return the image belonging to {@code url} when it is in cache, otherwise {@link #fallback}
     */
    public Bitmap getImageFromURL(final String url, final APIHandler.Callback<Bitmap> callback) {
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
