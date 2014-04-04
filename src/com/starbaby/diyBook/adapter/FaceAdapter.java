package com.starbaby.diyBook.adapter;


import java.util.List;

import com.starbaby.diyBook.R;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
 
 
public class FaceAdapter extends BaseAdapter {

    private List<Integer> data;

    private LayoutInflater inflater;

    private int size=0;

    public FaceAdapter(Context context, List<Integer> list) {
        this.inflater=LayoutInflater.from(context);
        this.data=list;
        this.size=list.size();
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // Emoji emoji=data.get(position);
    	//System.out.println(emoji);
        ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_face, null);
            viewHolder.iv_face=(ImageView)convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        int imgId = data.get(position);
        viewHolder.iv_face.setImageResource(imgId );
       /* if( imgId == R.drawable.face_del_icon) {
             //convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageResource(imgId );
        } else {
            viewHolder.iv_face.setTag(imgId );
            viewHolder.iv_face.setImageResource(imgId );
        }*/

        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }
}