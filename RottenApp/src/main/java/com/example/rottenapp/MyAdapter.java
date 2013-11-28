package com.example.rottenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by alberto on 27/11/13.
 */
public class MyAdapter extends BaseAdapter {

    Context context;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    private ImageLoader mImageLoader;

    public MyAdapter(Context context, ArrayList data) {
        this.context=context;
        this.data=data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = VolleySingleton.getInstance(context).getImageLoader();
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
        NetworkImageView laImage = (NetworkImageView) vi.findViewById(R.id.laList_image);

        Movie m = (Movie)data.get(i);
        laTitle.setText(m.getTitle());
        laYear.setText(m.getYear());
        laScore.setText(m.getRatings().getCritics_score());

        if (laImage!=null) {
            laImage.setImageUrl(m.getPosters().getThumbnail(),mImageLoader);
        }

        return vi;
    }
}
