package nl.group11.planplan;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by s132054 on 15-3-2016.
 */
public class ImageCache {

    Map<String, Bitmap> cache = new HashMap<>();

    public Bitmap setImageFromURL(final Context context, final String url, final APIHandler.Callback<Bitmap> callback) {
        if (cache.containsKey(url)) { //use bitmap from cache
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
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.imgnotfound);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    cache.put(url, bitmap);
                    callback.onItem(bitmap);
                }
            }.execute();
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.imgnotfound);
        }
    }
}
