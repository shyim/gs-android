package de.shyim.gameserver_sponsor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.struct.GPItem;

public class GPListAdapter extends BaseAdapter {
    private ArrayList listData;
    private LayoutInflater layoutInflater;

    public GPListAdapter(Context context, ArrayList listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.gp_list_item, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.gp_list_title);
            holder.valueView = (TextView) convertView.findViewById(R.id.gp_list_value);
            holder.dateView = (TextView) convertView.findViewById(R.id.gp_list_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GPItem gpItem = (GPItem) listData.get(position);
        holder.titleView.setText(gpItem.getName());
        holder.valueView.setText(gpItem.getValue().toString() + " GP");
        holder.dateView.setText(gpItem.getTimestamp());

        return convertView;
    }

    static class ViewHolder {
        TextView titleView;
        TextView valueView;
        TextView dateView;
    }
}
