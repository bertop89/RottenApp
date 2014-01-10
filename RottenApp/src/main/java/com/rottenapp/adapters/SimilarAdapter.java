package com.rottenapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.rottenapp.R;
import com.rottenapp.helpers.VolleySingleton;
import com.rottenapp.models.Movie;

import java.util.ArrayList;

/**
 * Created by Alberto Polidura on 16/12/13.
 */
public class SimilarAdapter extends BaseAdapter {

    Context context;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    private ImageLoader mImageLoader;

    public SimilarAdapter(Context context, ArrayList data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.similar_item, null);

        TextView tvSimilar = (TextView) convertView.findViewById(R.id.tvSimilar);
        NetworkImageView ivSimilar = (NetworkImageView) convertView.findViewById(R.id.ivSimilar);

        Movie m = (Movie)data.get(position);
        tvSimilar.setText(m.getTitle());
        ivSimilar.setImageResource(R.drawable.ic_launcher);

        if (ivSimilar!=null) {
            ivSimilar.setImageUrl(m.getPosters().getProfile(),mImageLoader);
        }
        return convertView;
    }
}
