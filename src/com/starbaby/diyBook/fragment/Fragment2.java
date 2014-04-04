package com.starbaby.diyBook.fragment;

/**
 * 关于我们
 */
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.Update;
/**
 * 关于我们
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class Fragment2 extends Fragment{
	Context mContext;
	public Fragment2() {
	}
	public Fragment2(Context mContext) {
		this.mContext = mContext;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		if (container == null) 
		{
            return null;
        }
		LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    View layout = myInflater.inflate(R.layout.fragment_3, container, false); 
	    TextView tv =(TextView)layout.findViewById(R.id.fragment3_tv1);
	    TextView tv2 =(TextView)layout.findViewById(R.id.fragment3_tv2);
	    TextView tv3 =(TextView)layout.findViewById(R.id.fragment3_tv3);
	    TextView tv4 =(TextView)layout.findViewById(R.id.fragment3_tv4);
	    TextView tv5 =(TextView)layout.findViewById(R.id.fragment3_tv5);
	    tv.setText("        “星宝宝童书”是星宝宝育教网倾心打造的个性化有声读物，为0-12岁孩子专业定制个性化的精品童书，每本童书封面和插图都可由自家宝宝的照片制作。图书内容由中国知名出版社提供专业支持和中国一线绘本画家提供精品画作。");
	    tv2.setText("          联系电话： 021-56717254（总机）"); 
	    tv3.setText("          问题反馈：10000@188ab.com"); 
//	    tv4.setText("当前版本号:" + new Update().getVerName(mContext)); 
	    tv5.setText("          官方网站： www.starbaby.cn"); 
		return layout;
	}
}
