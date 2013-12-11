package com.example.rottenapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rottenapp.models.NavDrawerItem;
import com.example.rottenapp.R;

import java.util.ArrayList;

/**
 * Created by alberto on 9/12/13.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    Context context;
    private ArrayList<NavDrawerItem> data;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> data) {
        this.context = context;
        this.data = data;
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
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.drawer_list_item, null);
        }
        ImageView imgIcon = (ImageView) view.findViewById(R.id.icon);
        TextView txtTitle = (TextView) view.findViewById(R.id.title);

        imgIcon.setImageResource(data.get(i).getIcon());
        txtTitle.setText(data.get(i).getTitle());

        return view;
    }
}