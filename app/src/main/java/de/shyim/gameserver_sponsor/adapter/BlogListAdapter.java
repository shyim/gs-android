package de.shyim.gameserver_sponsor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.struct.BlogItem;
import de.shyim.gameserver_sponsor.task.BlogImageDownloaderTask;

public class BlogListAdapter extends BaseAdapter {
    private ArrayList listData;
    private LayoutInflater layoutInflater;

    public BlogListAdapter(Context context, ArrayList listData) {
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
            convertView = layoutInflater.inflate(R.layout.blog_list_item, null);
            holder = new ViewHolder();
            holder.headlineView = (TextView) convertView.findViewById(R.id.blog_list_title);
            holder.reporterNameView = (TextView) convertView.findViewById(R.id.blog_list_author);
            holder.reportedDateView = (TextView) convertView.findViewById(R.id.blog_list_date);
            holder.imageView = (ImageView) convertView.findViewById(R.id.blog_list_thumb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BlogItem blogItem = (BlogItem) listData.get(position);
        holder.headlineView.setText(blogItem.getTitle());
        holder.reporterNameView.setText(blogItem.getAuthor());
        holder.reportedDateView.setText(blogItem.getDate());
        if (holder.imageView != null) {
            new BlogImageDownloaderTask(holder.imageView).execute(blogItem.getImage());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView headlineView;
        TextView reporterNameView;
        TextView reportedDateView;
        ImageView imageView;
    }
}
