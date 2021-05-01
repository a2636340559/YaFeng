package com.experiment.yafeng.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.experiment.yafeng.Modal.Poetry;
import com.experiment.yafeng.R;

import java.util.ArrayList;
import java.util.List;

public class PoemListAdapter extends BaseAdapter {
    private List<Poetry> poetryList = new ArrayList<Poetry>();
    private Context context;

    public PoemListAdapter(List<Poetry> poetryList, Context context) {

        this.poetryList = poetryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return poetryList.size();
    }

    @Override
    public Object getItem(int position) {
        return poetryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Log.d("ListViewLoading", "--------------------------------------------");
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.poem_item, null);
            viewHolder.author = convertView.findViewById(R.id.poem_author);
            viewHolder.title = convertView.findViewById(R.id.poem_title);
            viewHolder.contentView = convertView.findViewById(R.id.poem_content);
            viewHolder.spiltLine = convertView.findViewById(R.id.spilt_line);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.title.setText(poetryList.get(position).getName());
        String author = poetryList.get(position).getDynasty() + "." + poetryList.get(position).getAuthor();
        viewHolder.author.setText(author);
        viewHolder.contentView.setText(poetryList.get(position).getContent());
        return convertView;
    }

    public class ViewHolder {
        public TextView title;
        public TextView author;
        public TextView contentView;
        public TextView spiltLine;
    }
}
