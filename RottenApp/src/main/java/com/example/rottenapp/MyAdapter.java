package com.example.rottenapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by alberto on 27/11/13.
 */
public class MyAdapter extends BaseAdapter {

    Context context;
    private ArrayList data;
    private static LayoutInflater inflater = null;

    public MyAdapter(Context context, ArrayList data) {
        this.context=context;
        this.data=data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);

        TextView laTitle = (TextView) vi.findViewById(R.id.laTitle);
        TextView laYear = (TextView) vi.findViewById(R.id.laYear);
        TextView laScore = (TextView) vi.findViewById(R.id.laScore);
        ImageView laImage = (ImageView) vi.findViewById(R.id.laList_image);

        Movie m = (Movie)data.get(i);
        laTitle.setText(m.getTitle());
        laYear.setText(m.getYear());
        laScore.setText(m.getRatings().getCritics_score());

        if (laImage!=null) {
            new ImageDownloaderTask(laImage).execute(m.getPosters().getThumbnail());
        }

        return vi;
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference(imageView);
        }

        @Override
        // Actual download method, run in the task thread
        protected Bitmap doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            return downloadBitmap(params[0]);
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = (ImageView)imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageDrawable(imageView.getContext().getResources()
                                .getDrawable(R.drawable.ic_launcher));
                    }
                }

            }
        }

    }

    static Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
}
