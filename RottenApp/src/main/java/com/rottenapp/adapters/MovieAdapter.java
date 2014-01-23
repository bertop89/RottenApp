package com.rottenapp.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.rottenapp.models.Movie;
import com.rottenapp.R;
import com.rottenapp.helpers.VolleySingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Alberto Polidura on 27/11/13.
 */
public class MovieAdapter extends BaseAdapter {

    Context context;
    private ArrayList data;
    private int type;
    private static LayoutInflater inflater = null;
    private ImageLoader mImageLoader;

    public MovieAdapter(Context context, ArrayList data, int type) {
        this.context=context;
        this.data=data;
        this.type=type;
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


        final ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.movie_row, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.laTitle = (TextView) view.findViewById(R.id.laTitle);
            viewHolder.laYear = (TextView) view.findViewById(R.id.laYear);
            viewHolder.laScore = (TextView) view.findViewById(R.id.laScore);
            viewHolder.laImage = (NetworkImageView) view.findViewById(R.id.laList_image);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Movie m = (Movie)data.get(i);

        viewHolder.laTitle.setText(m.getTitle());

        SimpleDateFormat iso = new SimpleDateFormat("dd/MM/yyyy");

        switch (type) {
            case 0:
                viewHolder.laYear.setText(iso.format(m.getRelease_dates().getTheater()));
                break;
            case 1:
                viewHolder.laYear.setText(iso.format(m.getRelease_dates().getDvd()));
                break;
            case 2:
                viewHolder.laYear.setText(m.getYear());
                break;
            default:
                break;
        }


        String score = m.getRatings().getCritics_score();
        if (score.equals("-1") || score.equals("")) {
            viewHolder.laScore.setText("——");
        } else {
            viewHolder.laScore.setText(score+"%");
        }


        if (viewHolder.laImage!=null) {
            viewHolder.laImage.setImageUrl(m.getPosters().getThumbnail(),mImageLoader);
        }

        return view;
    }

    static class ViewHolder {
        TextView laTitle;
        TextView laYear;
        TextView laScore;
        NetworkImageView laImage;
    }


}
