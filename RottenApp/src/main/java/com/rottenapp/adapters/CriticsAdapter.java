package com.rottenapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rottenapp.models.Critic;
import com.rottenapp.R;

import java.util.ArrayList;

/**
 * Created by Alberto Polidura on 10/12/13.
 */
public class CriticsAdapter extends BaseAdapter {

    Context context;
    private ArrayList<Critic> data;
    private static LayoutInflater inflater = null;


    public CriticsAdapter(Context context, ArrayList<Critic> data) {
        this.context = context;
        this.data = data;
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
        if (view == null)
            view = inflater.inflate(R.layout.critic_row, null);

        ImageView ivIcon = (ImageView) view.findViewById(R.id.critic_icon);
        TextView tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
        TextView tvPublication = (TextView) view.findViewById(R.id.tvPublication);
        TextView tvQuote = (TextView) view.findViewById(R.id.critic_quote);

        if (data.get(i).getFreshness().equals("fresh")) {
            ivIcon.setImageResource(R.drawable.ic_launcher);
        } else {
            ivIcon.setImageResource(R.drawable.rotten);
        }
        tvAuthor.setText(data.get(i).getCritic());
        tvPublication.setText(data.get(i).getPublication());
        tvQuote.setText(data.get(i).getQuote());

        return view;

    }
}
