package com.rottenapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rottenapp.R;
import com.rottenapp.models.Cast;

import java.util.ArrayList;

/**
 * Created by Alberto Polidura on 14/12/13.
 */
public class CastAdapter extends BaseAdapter {

    Context context;
    ArrayList<Cast> data;
    private static LayoutInflater inflater = null;

    public CastAdapter(Context context,ArrayList<Cast> data) {
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
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.actor, null);
        }

        TextView tvActor = (TextView) convertView.findViewById(R.id.tvActor);
        TextView tvRole = (TextView) convertView.findViewById(R.id.tvRole);

        tvActor.setText(data.get(position).getName());
        try {
            tvRole.setText(data.get(position).getCharacters().get(0));
        } catch (IndexOutOfBoundsException e) {
            tvRole.setText("");
        }

        return convertView;
    }
}
