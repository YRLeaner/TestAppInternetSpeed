package com.example.tyr.showyourspeed.list;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tyr.showyourspeed.R;

import java.util.List;

/**
 * Created by tyr on 2017/2/11.
 */
public class SpeedMsgAdapter extends BaseAdapter {

    private List<ShowMessage> mDatas;
    private LayoutInflater mInflater;
    private Context context;
    public SpeedMsgAdapter(Context context,List<ShowMessage> mDatas) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size()<5?mDatas.size():5;
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowMessage showMessage = mDatas.get(position);
        ViewHolder viewHolder = null;
        if (convertView==null){
            convertView = mInflater.inflate(R.layout.list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mtitle = (TextView)convertView.findViewById(R.id.title);
            viewHolder.mspeed = (TextView)convertView.findViewById(R.id.speed);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        viewHolder.mtitle.setText(showMessage.getTitle());
        viewHolder.mspeed.setText(Formatter.formatFileSize(context,showMessage.getSpeed())+"/s");
        return convertView;
    }

    private final class ViewHolder{
        TextView mtitle;
        TextView mspeed;
    }
}
