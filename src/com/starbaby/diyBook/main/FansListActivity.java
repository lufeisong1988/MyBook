package com.starbaby.diyBook.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.SortAdapter;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.net.AsyncHttpGet;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.CharacterParser;
import com.starbaby.diyBook.utils.PinyinComparator;
import com.starbaby.diyBook.utils.SortModel;
import com.starbaby.diyBook.view.ClearEditText;
import com.starbaby.diyBook.view.SideBar;
import com.starbaby.diyBook.view.SideBar.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FansListActivity extends Activity {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private ImageButton back,refresh;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case -1:
				Toast.makeText(FansListActivity.this, "获取好友列表失败...", Toast.LENGTH_LONG).show();
				break;
			case 1:
				SourceDateList = filledData(uidList,avatarList,usernameList);
				
				// 根据a-z进行排序源数据
				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new SortAdapter(FansListActivity.this, SourceDateList);
				sortListView.setAdapter(adapter);
				
				
				mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
				
				//根据输入框输入值的改变来过滤搜索
				mClearEditText.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
						filterData(s.toString());
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
					}
				});
				//设置右侧触摸监听
				sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
					
					@Override
					public void onTouchingLetterChanged(String s) {
						//该字母首次出现的位置
						int position = adapter.getPositionForSection(s.charAt(0));
						if(position != -1){
							sortListView.setSelection(position);
						}
						
					}
				});
				
				
				sortListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("name",((SortModel)adapter.getItem(position)).getName());
						intent.putExtras(bundle);
						FansListActivity.this.setResult(3,intent);
						FansListActivity.this.finish();
						//这里要利用adapter.getItem(position)来获取当前position所对应的对象
						Toast.makeText(getApplication(), ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
						
					}
				});
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}
	ArrayList<String> avatarList;
	ArrayList<Integer> uidList;
	ArrayList<String> usernameList;
	private void initViews() {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		
		pinyinComparator = new PinyinComparator();
		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		back = (ImageButton) findViewById(R.id.fans_back);
		refresh = (ImageButton) findViewById(R.id.fans_refresh);
		final SharedPreferences sp = getSharedPreferences("diyBook", MODE_WORLD_READABLE);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FansListActivity.this.finish();
				overridePendingTransition(R.anim.none,R.anim.workbook_out);
			}
		});
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getFansList(sp.getInt("uid", 0));
			}
		});
		getFansList(sp.getInt("uid", 0));
	}


	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(ArrayList<Integer> uidList,ArrayList<String> avatarList,ArrayList<String> usernameList){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<uidList.size(); i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(usernameList.get(i));
			sortModel.setAvater(avatarList.get(i));
			sortModel.setUid(uidList.get(i));
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(usernameList.get(i));
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			
			mSortList.add(sortModel);
		}
		return mSortList;
		
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
	void getFansList(int uid) {
		AsyncHttpGet get = new AsyncHttpGet(null, "http://api.starbaby.cn/familyHelper/quanzi/at_user_list/" + uid, null, new RequestResultCallback() {
			
			@Override
			public void onSuccess(Object o) {
				String result = (String)o;
				Log.i("result",result);
				if(result != null && !result.equals("")){
					avatarList = new ArrayList<String>();
					uidList = new ArrayList<Integer>();
					usernameList = new ArrayList<String>();
					try {
						JSONObject object = new JSONObject(result);
						JSONArray atuserlist = object.getJSONArray("atuserlist");
						for(int i = 0;i < atuserlist.length();i++){
							int uid = atuserlist.getJSONObject(i).getInt("uid");
							String username = atuserlist.getJSONObject(i).getString("username");
							String avatar = atuserlist.getJSONObject(i).getString("avatar");
							uidList.add(uid);
							avatarList.add(avatar);
							usernameList.add(username);
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Message msg = new Message();
						msg.what = -1;
						mHandler.sendMessage(msg);
					}
				}
			}
			
			@Override
			public void onFail(Exception e) {
				
			}
		});
		DefaultThreadPool.getInstance().execute(get);
	}
}
