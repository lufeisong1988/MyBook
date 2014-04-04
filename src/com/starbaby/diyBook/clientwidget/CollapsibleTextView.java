package com.starbaby.diyBook.clientwidget;

import java.util.HashMap;

import com.starbaby.diyBook.R;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;


public class CollapsibleTextView extends LinearLayout //implements  OnClickListener
  {

    /** default text show max lines */
    private static final int DEFAULT_MAX_LINE_COUNT = 6;

    private Context  context;
    private TextView desc;
    private TextView descOp;

    private String shrinkup;
    private String spread;
  //   private int mState;
    private boolean flag;
    private HashMap mHashMap;
    private int     position;
    
    public CollapsibleTextView(Context context1, AttributeSet attrs) {
        super(context1, attrs);
        context = context1;
        initView();
    }

    public CollapsibleTextView(Context context1) {
        super(context1);
        context = context1;
        initView();
    }

    static TextView getDescView(CollapsibleTextView collapsibletextview){
    	return collapsibletextview.desc;
    }
    
    static TextView gteOpView(CollapsibleTextView collapsibletextview){
    	return collapsibletextview.descOp;
    }
    
    public  static String getDisplyStr(CollapsibleTextView collapsibletextview){
    	return   collapsibletextview.spread;
    }
    
    public  void setDesc(CharSequence charSequence, BufferType bufferType,HashMap hashMap,int i) {
    	mHashMap = hashMap;
    	position = i;
        desc.setText(charSequence, bufferType);
        if(hashMap.get(i)!=null) {
        	flag = true;
        	switch(((Integer)hashMap.get(Integer.valueOf(i))).intValue()){
        	case 0:
        		descOp.setVisibility(View.GONE);
        		break;
        	case 1:
        		desc.setMaxLines(DEFAULT_MAX_LINE_COUNT);
        		descOp.setVisibility(View.VISIBLE);
        		descOp.setText(spread);
        		break;
        	case 2:
        		desc.setMaxLines(Integer.MAX_VALUE);
        		descOp.setVisibility(View.VISIBLE);
        		descOp.setText(shrinkup);
        		break;
        	}
        }else{
        	flag = false;
           	desc.setMaxLines(DEFAULT_MAX_LINE_COUNT);
        	descOp.setVisibility(View.GONE);
        	requestLayout();
        }
        	
    }

    private void initView(){
    	   shrinkup = context.getString(R.string.desc_shrinkup);
           spread = context.getString(R.string.desc_spread);
           View view = inflate(context, R.layout.collapsible_textview, this);
           view.setPadding(0, -3, 0, 0);
           desc = (TextView) view.findViewById(R.id.desc_tv);
           descOp = (TextView) view.findViewById(R.id.desc_op_tv);
    }
    
    public void onOpClick(View.OnClickListener onClickListener) {
       if(descOp!=null)
    	   descOp.setOnClickListener(onClickListener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!flag) {
        	flag = true;
            if (desc.getLineCount() <= DEFAULT_MAX_LINE_COUNT) {
            	mHashMap.put(Integer.valueOf(position),Integer.valueOf(0));
            } else {
            	mHashMap.put(Integer.valueOf(position),Integer.valueOf(1));
            	(new Handler()).post(new InnerRunnable(this));
            }
        }
    }

   class InnerRunnable implements Runnable {
	   private CollapsibleTextView ct;
	   public InnerRunnable(CollapsibleTextView collapsibletextview){
		   super();
		   ct = collapsibletextview;
	   }
	   
        @Override
        public  void run() {
        		getDescView(ct).setMaxLines(DEFAULT_MAX_LINE_COUNT);
        		gteOpView(ct).setVisibility(View.VISIBLE);
        		gteOpView(ct).setText(getDisplyStr(ct));
        }
    }
}
