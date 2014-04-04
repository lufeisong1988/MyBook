package com.starbaby.diyBook.clientui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.FaceAdapter;
import com.starbaby.diyBook.adapter.FacePagerAdapter;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.EmojiDate;
import com.starbaby.diyBook.clientbean.FansList;
import com.starbaby.diyBook.clientbean.FriendPost;
import com.starbaby.diyBook.clientbean.Picture;
import com.starbaby.diyBook.clientcommon.EmoticonUtil;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;
import com.starbaby.diyBook.clientcommon.FileUtils;
import com.starbaby.diyBook.clientcommon.ImageUtils;
import com.starbaby.diyBook.clientcommon.MediaUtils;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.main.FansListActivity;
import com.starbaby.diyBook.view.EnterActivityView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 发表帖子
 * 
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public class MessagePub extends BaseActivity implements OnItemClickListener { //
	private ImageView mBack;
	/* private TextView mReceiver; */
	private EditText mContent;
	private ImageView mPublish;
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private ImageView mFace;
	private TextView mName;
	private AppContext appContext;
	private int _uid;
	private String _pwd;
	private String _content;
	private int _curCatalog;

	private LinearLayout imgLinear;
	private final static int DELAY_TIMES = 30; // 延迟次数

	private boolean firstUpload = true;
	private boolean uploadinng = false;

	private File imgFile;
	private String theThumbnail;
	private String theLarge;
	private ImageView mImgIcon;
	private ImageView mAtIcon;
	private ImageView mFaceIcon;

	private ArrayList<Bitmap> smallPic = new ArrayList<Bitmap>(); // imageview视图
	private HashMap<String, String> picsUrl = new HashMap<String, String>(); // 上传成功后的图片地址
	private ArrayList<String> fileUrl = new ArrayList<String>(); // 临时文件
	private HashMap<String, File> uploadFailed = new HashMap<String, File>();// 上传失败的文件
	private ArrayList<String> delList = new ArrayList<String>(); // 删除的临时文件
	private ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0L,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	/** 显示表情页的viewpager */
	private ViewPager vpFace;
	/** 表情页界面集合 */
	private ArrayList<View> pageViews;
	/** 游标显示布局 */
	private LinearLayout layoutPoint;
	/** 游标点集合 */
	private ArrayList<ImageView> pointViews;
	/** 表情区域 */
	private View view;
	/** 表情数据填充器 */
	private List<FaceAdapter> faceAdapters;
	/** 当前表情页 */
	private int current = 0;

	public final static int POST_PUB_REQUEST = 0x01;// 请求标志

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_pub);
		this.initView();
		appContext = (AppContext) getApplication();
		// 初始化表情视图
		// this.initGridView();
		initViewPager();
		initPoint();
		initData();
	}

	// 初始化视图控件
	private void initView() {
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		_curCatalog = getIntent().getIntExtra("catalog", 1);

		vpFace = (ViewPager) findViewById(R.id.vp_contains);
		layoutPoint = (LinearLayout) findViewById(R.id.iv_image);
		view = findViewById(R.id.ll_facechoose);
		/* if(_uid > 0) tempMessageKey = AppConfig.TEMP_MESSAGE + "_" +_uid; */

		mBack = (ImageView) findViewById(R.id.message_pub_back);
		mPublish = (ImageView) findViewById(R.id.message_pub_publish);
		mContent = (EditText) findViewById(R.id.message_pub_content);
		mContent.clearFocus();

		// mReceiver = (TextView)findViewById(R.id.message_pub_receiver);
		mFace = (ImageView) findViewById(R.id.message_pub_face);
		mName = (TextView) findViewById(R.id.message_pub_username);

		imgLinear = (LinearLayout) findViewById(R.id.message_pub_img_ll);
		imgLinear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// hideFace();
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
		/*
		 * footLinear=
		 * (LinearLayout)findViewById(R.id.message_pub_footer_linear);
		 */

		mImgIcon = (ImageView) findViewById(R.id.message_pub_footbar_photo);
		mImgIcon.setOnClickListener(pickClickListener);

		mFaceIcon = (ImageView) findViewById(R.id.message_pub_footbar_face);
		mFaceIcon.setOnClickListener(faceClickListener);

		mAtIcon = (ImageView) findViewById(R.id.message_pub_footbar_at);
		 mAtIcon.setOnClickListener(atClickListener);

		/*
		 * mContent.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { showOrHideIMM(); } });
		 */
		/*
		 * footLinear.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * imm.hideSoftInputFromWindow(mContent.getWindowToken(), 0); } });
		 */

		mBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (smallPic.size() > 0
						|| !StringUtils.isEmpty(mContent.getText().toString())) {
					UIHelper.showFinishDialog(MessagePub.this);
				} else {
					finish();
					overridePendingTransition(R.anim.none,R.anim.workbook_out);
				}
			}
		});

		mPublish.setOnClickListener(publishClickListener);
		mPublish.setEnabled(true); // 可点击
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (appContext.isLogin()) {
			_uid = appContext.getLoginUid();
			_pwd = appContext.getLoginPwd();
			// String faceURL =((AppContext)getApplication()).getFaceUrl();搜索
			imageLoader.displayImage(appContext.getFaceUrl(), mFace);
			// UIHelper.showLoadImage(mFace, appContext.getFaceUrl(),
			// R.drawable.face_loading, "");
			mName.setText(appContext.getLoginName());
		}

	}

	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			showOrHideIMM();
		}
	};

	private void showFace() {
		mFaceIcon.setImageResource(R.drawable.widget_bar_keyboard);
		mFaceIcon.setTag(1);
		view.setVisibility(View.VISIBLE);
	}

	private void hideFace() {
		mFaceIcon.setImageResource(R.drawable.widget_bar_face);
		mFaceIcon.setTag(null);
		view.setVisibility(View.GONE);
	}

	private void showOrHideIMM() {
		if (mFaceIcon.getTag() == null) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(mContent.getWindowToken(), 0);
			// 显示表情
			showFace();
		} else {
			// 显示软键盘
			imm.showSoftInput(mContent, 0);
			// 隐藏表情
			hideFace();
		}
	}

	private void initViewPager() {
		pageViews = new ArrayList<View>();
		// 左侧加空白
		View nullView1 = new View(this);
		pageViews.add(nullView1);
		// 中间添加表情页
		faceAdapters = new ArrayList<FaceAdapter>();
		// System.out.println(emojis.size());
		int pageCount = (int) Math.ceil(EmojiDate.emojiImgArrray.size() / 20);
		for (int i = 0; i < pageCount; i++) {
			GridView view = new GridView(this);
			FaceAdapter adapter = new FaceAdapter(this, FaceConversionUtil
					.getInstace().getData(i));
			view.setAdapter(adapter);
			faceAdapters.add(adapter);
			view.setOnItemClickListener(this);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 0, 5, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			pageViews.add(view);
		}

		// 右侧添加空页面
		View nullView2 = new View(this);
		// 设置透明背景
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView2);
	}

	private void initPoint() {

		pointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(this);
			imageView.setBackgroundResource(R.drawable.d1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.width = 8;
			layoutParams.height = 8;
			layoutPoint.addView(imageView, layoutParams);
			if (i == 0 || i == pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.d2);
			}
			pointViews.add(imageView);

		}
	}

	/**
	 * 填充数据
	 */
	private void initData() {
		vpFace.setAdapter(new FacePagerAdapter(pageViews));
		vpFace.setCurrentItem(1);
		current = 0;
		vpFace.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				current = arg0 - 1;
				// 描绘分页点
				drawPoint(arg0);
				// 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
				if (arg0 == pointViews.size() - 1 || arg0 == 0) {
					if (arg0 == 0) {
						vpFace.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
						pointViews.get(1).setBackgroundResource(R.drawable.d2);
					} else {
						vpFace.setCurrentItem(arg0 - 1);// 倒数第二屏
						pointViews.get(arg0 - 1).setBackgroundResource(
								R.drawable.d2);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	/**
	 * 绘制游标背景
	 */
	public void drawPoint(int index) {
		for (int i = 1; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}

	/**
	 * 再把最后一项背景变为默认的 ， 先把删除的一项隐藏或删除一列,然后遍历组件设置图片背景
	 * 
	 * @param bitmaps
	 */
	private void initImgGone(List<Bitmap> bitmaps) {
		int imgLength = bitmaps.size();
		int position = 0;
		// System.out.println("initImgGone imLength==="+imgLength);
		int n = imgLength / 4; // 行数 0 1 2 (最多三行)
		int m = imgLength % 4; // 列数 1 2 3 0 (最多4列)
		// System.out.println("n="+n+",m="+m+"imgLinear.getChildCount()=="+imgLinear.getChildCount());
		if (m == 3 && n >= 0) { // 如果最后显示图片在第3列，说明应该移除一列
			if (imgLinear.getChildCount() > n + 1)
				imgLinear.removeViewAt(n + 1);
			// imgLinear.removeViewInLayout(imgLinear.getChildAt(n+1));
		} else {
			// 隐藏删除的一项
			ImageView imgView = (ImageView) ((LinearLayout) imgLinear
					.getChildAt(n)).getChildAt(m + 1);
			imgView.setImageResource(R.drawable.smiley_add_btn);
			imgView.setVisibility(View.GONE);
		}
		// 把最后一项背景变为默认的
		ImageView imgView = (ImageView) ((LinearLayout) imgLinear.getChildAt(n))
				.getChildAt(m);
		imgView.setImageResource(R.drawable.smiley_add_btn);
		imgView.setOnClickListener(pickClickListener);
		imgView.setVisibility(View.VISIBLE);

		// 替换背景图片
		for (int i = 0; i < n; i++) { // 填充满行的
			LinearLayout linearLayout = (LinearLayout) imgLinear.getChildAt(i);
			for (int j = 0; j < 4; j++) {
				((ImageView) linearLayout.getChildAt(j)).setImageBitmap(bitmaps
						.get(position));
				position++;
			}
		}

		// 填充不满行的
		LinearLayout linearLayout = (LinearLayout) imgLinear.getChildAt(n);
		for (int k = 0; k < m; k++) {
			((ImageView) linearLayout.getChildAt(k)).setImageBitmap(bitmaps
					.get(position));
			position++;
		}
	}

	private void initImgVisible(List<Bitmap> bitmaps) {
		int imgLength = bitmaps.size();
		int position = imgLength - 1;
		int n = imgLength / 4; // 行数 0 1 2 3
		int m = imgLength % 4; // 列数 1 2 3 0
		int x = m;
		int y = n;
		if ((n == 0 && m == 1 && imgLinear.getChildCount() == 0)
				|| (n > 0 && m == 0)) { // 添加图片行布局默认
			imgLinear.setVisibility(View.VISIBLE);
			LayoutInflater inflater = LayoutInflater.from(MessagePub.this);
			View layout = inflater.inflate(R.layout.message_pub_imagesline,
					null);
			imgLinear.addView(layout);
		}
		if (n > 0 && m == 0) { // 如果是换行
			y = n - 1;
			x = 4;
		}
		// System.out.println("n="+n+",m="+m+",x="+x+",y="+y+"position="+position);
		ImageView imageView = (ImageView) ((LinearLayout) imgLinear
				.getChildAt(y)).getChildAt(x - 1);
		imageView.setImageBitmap(bitmaps.get(position));
		imageView.setTag(position);
		imageView.setVisibility(View.VISIBLE);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Toast.makeText(MessagePub.this, "跳转图片放大！位置是："+view.getTag(),
				// 5).show();
				UIHelper.showImageUploadPager(MessagePub.this, fileUrl,
						(Integer) view.getTag());
			}
		});
		if (imgLength < 9) {
			ImageView imageView1 = (ImageView) ((LinearLayout) imgLinear
					.getChildAt(n)).getChildAt(m);
			imageView1.setVisibility(View.VISIBLE);
			imageView1.setOnClickListener(pickClickListener);
		}
	}

	
	private View.OnClickListener atClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) { // hideFace();
			if (!appContext.isLogin()) {
				UIHelper.showLogin(MessagePub.this);
				return;
			}
			imm.hideSoftInputFromWindow(mContent.getWindowToken(), 0);
			startActivityForResult(new Intent(MessagePub.this,FansListActivity.class), 3);
			overridePendingTransition(R.anim.workbook_in,R.anim.none);
//			startActivity(new Intent(MessagePub.this,FansListActivity.class));
		}
	};

	private View.OnClickListener pickClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			// 隐藏表情
			// hideFace();
			imm.hideSoftInputFromWindow(mContent.getWindowToken(), 0);

			if (!appContext.isLogin()) {
				UIHelper.showLogin(MessagePub.this);
				return;
			}

			if (appContext.getNetworkType() == 2 && smallPic.size() >= 1) {
				UIHelper.ToastMessage(MessagePub.this,
						"您当前使用的2G网络，为节省流量，您需要切换到wifi或3G网络下才能继续使用照片上传功能！", 5000);
				return;
			}

			if (smallPic.size() >= 9) {
				UIHelper.ToastMessage(MessagePub.this, "最多上传9张图片！");
				return;
			}
			CharSequence[] items = {
					MessagePub.this.getString(R.string.img_from_album),
					MessagePub.this.getString(R.string.img_from_camera) };
			imageChooseItem(items);
		}
	};

	/**
	 * 操作选择
	 * 
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items) {

		AlertDialog imageDialog = new AlertDialog.Builder(this)
				.setTitle("上传图片").setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 手机选图
						if (item == 0) {
							// 判断是否挂载了SD卡
							String storageState = Environment
									.getExternalStorageState();
							if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
								UIHelper.ToastMessage(MessagePub.this,
										"无法获取上传的头像，请检查SD卡是否挂载");
								return;
							}
							Intent intent = new Intent(
									Intent.ACTION_GET_CONTENT);
							intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/*");
							startActivityForResult(
									Intent.createChooser(intent, "选择图片"),
									ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
						}
						// 拍照
						else if (item == 1) {
							String savePath = "";
							// 判断是否挂载了SD卡
							String storageState = Environment
									.getExternalStorageState();
							if (storageState.equals(Environment.MEDIA_MOUNTED)) {
								savePath = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/Starbaby/Camera/";// 存放照片的文件夹
								File savedir = new File(savePath);
								if (!savedir.exists()) {
									savedir.mkdirs();
								}
							}

							// 没有挂载SD卡，无法保存文件
							if (StringUtils.isEmpty(savePath)) {
								UIHelper.ToastMessage(MessagePub.this,
										"无法保存照片，请检查SD卡是否挂载");
								return;
							}

							String timeStamp = new SimpleDateFormat(
									"yyyyMMddHHmmss").format(new Date());
							String fileName = "starbaby_" + timeStamp + ".jpg";// 照片命名
							File out = new File(savePath, fileName);
							Uri uri = Uri.fromFile(out);
							theLarge = savePath + fileName;// 该照片的绝对路径
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent,
									ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
						}
					}
				}).create();

		imageDialog.show();
	}

	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			// _content = mContent.getText().toString();
			_content = FaceConversionUtil.convertToMsg(
					mContent.getEditableText(), MessagePub.this);
			if (StringUtils.isEmpty(_content)) {
				UIHelper.ToastMessage(v.getContext(), "请输入留言内容");
				return;
			}

			if (!appContext.isLogin()) {
				UIHelper.showLogin(MessagePub.this);
				return;
			}
			sendPub();
		}
	};

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if(resultCode == 3){
			if(requestCode == 3){
				String name = data.getStringExtra("name");
				mContent.setText("@" + name + " ");
				CharSequence text = mContent.getText();
				 //Debug.asserts(text instanceof Spannable);
				 if (text instanceof Spannable) {
				     Spannable spanText = (Spannable)text;
				     Selection.setSelection(spanText, text.length());
				 }
			}
		}
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA
				|| requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						if (firstUpload) {
							firstUpload = false;
							// footLinear.setVisibility(View.GONE);
							imgLinear.setVisibility(View.VISIBLE);
						}
						// 显示图片
						smallPic.add((Bitmap) msg.obj);
						initImgVisible(smallPic);
						uploadNewPhoto(imgFile, theThumbnail); // 上传
					}
				}
			};

			new Thread() {
				public void run() {
					Bitmap bitmap = null;

					if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
						if (data == null)
							return;

						Uri thisUri = data.getData();
						String thePath = ImageUtils
								.getAbsolutePathFromNoStandardUri(thisUri);

						// 如果是标准Uri
						if (StringUtils.isEmpty(thePath)) {
							theLarge = ImageUtils.getAbsoluteImagePath(
									MessagePub.this, thisUri);
						} else {
							theLarge = thePath;
						}

						String attFormat = FileUtils.getFileFormat(theLarge);
						if (!"photo".equals(MediaUtils
								.getContentType(attFormat))) {
							Toast.makeText(MessagePub.this,
									getString(R.string.choose_image),
									Toast.LENGTH_SHORT).show();
							return;
						}

						// 获取图片缩略图 只有Android2.1以上版本支持
						if (AppContext
								.isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)) {
							String imgName = FileUtils.getFileName(theLarge);
							bitmap = ImageUtils.loadImgThumbnail(
									MessagePub.this, imgName,
									MediaStore.Images.Thumbnails.MICRO_KIND);
						}

						if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
							bitmap = ImageUtils.loadImgThumbnail(theLarge, 200,
									200);
						}
					}
					// 拍摄图片
					else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
						if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
							bitmap = ImageUtils.loadImgThumbnail(theLarge, 200,
									200);
						}
					}

					if (bitmap != null) {
						// 存放照片的文件夹
						String savePath = Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/Starbaby/Camera/";
						File savedir = new File(savePath);
						if (!savedir.exists()) {
							savedir.mkdirs();
						}

						String largeFileName = FileUtils.getFileName(theLarge);
						String largeFilePath = savePath + largeFileName;
						// 判断是否已存在缩略图
						if (largeFileName.startsWith("thumb_")
								&& new File(largeFilePath).exists()) {
							theThumbnail = largeFilePath;
							imgFile = new File(theThumbnail);
						} else {
							// 生成上传的800宽度图片
							String thumbFileName = "thumb_" + largeFileName;
							theThumbnail = savePath + thumbFileName;
							if (new File(theThumbnail).exists()) {
								imgFile = new File(theThumbnail);
							} else {
								try {
									// 压缩上传的图片
									ImageUtils.createImageThumbnail(
											MessagePub.this, theLarge,
											theThumbnail, 800, 70);
									imgFile = new File(theThumbnail);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						fileUrl.add(theThumbnail); // 把保存的临时文件添加到文件列表中
						// System.out.println("fileUrl size=="+fileUrl.size());
						// 保存动弹临时图片
						Message msg = new Message();
						msg.what = 1;
						msg.obj = bitmap;
						handler.sendMessage(msg);
					}
				};
			}.start();
		} else if (requestCode == UIHelper.REQUEST_CODE_FOR_VIEW) { // 删除上传图片后的返回处理
			if (data != null && data.getExtras().getBoolean("isDel")) {
				int position = data.getExtras().getInt("position", 0);
				// System.out.println("position============"+position);
				if (smallPic.size() > position && fileUrl.size() > position) {

					smallPic.remove(position);
					delList.add(fileUrl.get(position));
					fileUrl.remove(position);
					/*
					 * if(picsUrl.containsKey(tempPath)){
					 * picsUrl.remove(tempPath); }
					 * if(uploadFailed.containsKey(tempPath)){
					 * uploadFailed.remove(tempPath); }
					 */
				}
				// System.out.println("smallPic.size===="+smallPic.size()+";fileUrl.size==="+fileUrl.size()+";picsUrl.size===="+picsUrl.size()+"uploadFailed.size()==="+uploadFailed.size());
				initImgGone(smallPic);
			}
		} else if (requestCode == UIHelper.REQUEST_CODE_FOR_FINISH) { // 是否保存编辑信息
			if (data.getExtras().getBoolean("isFinish")) {
				pool.shutdownNow();
				finish();
			}
		} else if (requestCode == UIHelper.REQUEST_CODE_FOR_DIALOGIN) {
			if (_uid > 0) {
				String faceURL = ((AppContext) getApplication()).getFaceUrl();
				// UIHelper.showUserFace(mFace,faceURL);
				imageLoader.displayImage(faceURL, mFace);
				mName.setText(appContext.getLoginName());
			}
		}/*
		 * else if(requestCode==UIHelper.REQUEST_CODE_FOR_ATME){ String atme =
		 * "@"+data.getStringExtra("name")+" ";
		 * mContent.getText().insert(mContent.getSelectionStart(), atme); }
		 */
	}

	private void sendPub() {
		mPublish.setEnabled(false); // 设置不可点击
		// pool.shutdown(); // 不可提交新任务
		mProgress = ProgressDialog.show(MessagePub.this, null, "发送中···", true,
				false);
		// System.out.println(",picsUrl.size()"+picsUrl.size()
		// +",pool.getPoolSize()==="+pool.getPoolSize()+",pool.getQueue=="+pool.getQueue().size()+"pool.getCompletedTaskCount=="+pool.getCompletedTaskCount()+"uploadFailed.size()==="+uploadFailed.size());
		delUploadFile();// 去掉被删除的文件
		againUploadFailed(); // 如果有上传失败的照片则重新上传

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (mProgress != null)
					mProgress.dismiss();
				mPublish.setEnabled(true); // 可点击
				if (msg.what == 1) {
					UIHelper.sendBroadPubMsg(MessagePub.this, 1, _curCatalog);
					finish();
				} else if (msg.what == -14) {
					UIHelper.ToastMessage(MessagePub.this,
							R.string.network_not_connected);
				} else if (msg.what == -1) {
					((AppException) msg.obj).makeToast(MessagePub.this);
				} else if (msg.what == -11) {
					UIHelper.ToastMessage(MessagePub.this,
							R.string.msg_opt_post_network_busy);
				} else {
					UIHelper.ToastMessage(MessagePub.this,
							getErrorInfo(msg.what));
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (appContext.isNetworkConnected()) {
						// 等待图片上传结束，如果DELAY_TIMES秒不结束让用户重发
						int times = 0;
						while (pool.getQueue().size() != 0 && uploadinng
								&& times <= DELAY_TIMES) {
							sleep(500);
							times++;
						}
						if (times > DELAY_TIMES) {
							msg.what = -11;
						} else {
							FriendPost res = appContext.pubPost(_uid, _pwd,
									_content, picsUrl, _curCatalog);
							msg.what = res.getMsg();
							msg.obj = res;
						}
					} else {
						msg.what = -14;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				} catch (InterruptedException e) {
					e.printStackTrace();
					msg.what = -8;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private void delUploadFile() {
		for (int i = 0; i < delList.size(); i++) {
			String tempPath = delList.get(i);
			if (picsUrl.containsKey(tempPath)) {
				picsUrl.remove(tempPath);
			}
			if (uploadFailed.containsKey(tempPath)) {
				uploadFailed.remove(tempPath);
			}
		}
	}

	// 重新上传失败的图片
	private void againUploadFailed() {
		if (uploadFailed.size() > 0) {
			Iterator<Entry<String, File>> iterator = uploadFailed.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, File> item = iterator.next();
				if (!picsUrl.containsKey(item.getKey())) {
					uploadNewPhoto(item.getValue(), item.getKey());
				}
			}
		}
	}

	// 删除多余图片
	/*
	 * private void checkUploadPic(){ if(picsUrl.size()>fileUrl.size()){
	 * Iterator<Entry<String, String>> iterator = picsUrl.entrySet().iterator();
	 * Toast.makeText(context, text, duration); while(iterator.hasNext()) {
	 * Entry<String, String> item = iterator.next();
	 * if(!fileUrl.contains(item.getKey())){ picsUrl.remove(item.getKey()); } }
	 * }
	 * 
	 * }
	 */
	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto(final File file, final String tempPath) {
		/*
		 * synchronized (this){ uploadCount++; };
		 */
		uploadinng = true;
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				uploadinng = false;
				if (msg.what == 1 && msg.obj != null) {
					Picture picture = (Picture) msg.obj;
					// 提示信息
					if (picture.getResult() == 1) {
						System.out.println("temPath===" + tempPath
								+ ",picture====" + picture.getImgUrl());
						picsUrl.put(tempPath, picture.getImgUrl());
					}
				} else if (msg.what == -1) {
					uploadFailed.put(tempPath, file);
				} else if (msg.what == -4) {
					uploadFailed.put(tempPath, file);
					// pool.shutdownNow();
					// mPublish.setEnabled(true); //可点击
					UIHelper.ToastMessage(MessagePub.this,
							R.string.network_not_connected);
				}
			}
		};
		// UIHelper.ToastMessage(MessagePub.this,
		// "pool==="+pool+";pool.isShutdown()==="+pool.isShutdown());
		/*
		 * if(pool.isShutdown()) {
		 */
		pool.execute(new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (((AppContext) getApplication()).isNetworkConnected()) {
						Picture picture = ((AppContext) getApplication())
								.updatePortrait(file);
						msg.what = picture.getResult();
						msg.obj = picture;
					} else {
						msg.what = -4;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			};
		});
		System.out.println("当前线程数pool.getPoolSize" + pool.getPoolSize());
		// }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& (smallPic.size() > 0 || !StringUtils.isEmpty(mContent
						.getText().toString()))) {
			UIHelper.showFinishDialog(MessagePub.this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private String getErrorInfo(final int msg) {
		String result = "";
		switch (msg) {
		case -2:
			result = this.getString(R.string.msg_opt_user_pwd_error);
			break;
		case -3:
			result = this.getString(R.string.msg_opt_user_uid_is_null);
			break;
		case -4:
			result = this.getString(R.string.msg_opt_user_info_no_exist);
			break;
		case -5:
			result = this.getString(R.string.msg_login_error_five);
			break;
		case -6:
			result = this.getString(R.string.msg_opt_post_name_is_null);
			break;
		case -7:
			result = this.getString(R.string.msg_opt_post_content_is_null);
			break;
		case -8:
			result = this.getString(R.string.msg_opt_post_ing);
			break;
		case -9:
			result = this.getString(R.string.msg_opt_post_unknow);
			break;
		case -10:
			result = this.getString(R.string.msg_opt_post_system_busy);
			break;
		case -12:
			result = "分类不能为空!";
			break;
		}
		return result;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Integer emojiId = (Integer) faceAdapters.get(current).getItem(arg2);
		int selection = mContent.getSelectionStart();
		if (emojiId == R.drawable.face_del_icon) {
			String text = mContent.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if (">".equals(text2)) {
					int start = text.lastIndexOf("<");
					int end = selection;
					mContent.getText().delete(start, end);
					return;
				}
				mContent.getText().delete(selection - 1, selection);
			}
		} else {
			mContent.getText().insert(
					selection,
					Html.fromHtml(EmoticonUtil
							.formatFaces(EmojiDate.emojiImgToNumArray.get(
									emojiId).toString()), EmoticonUtil
							.getImageGetter(MessagePub.this), null));
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try{
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MessagePub.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		}catch(Exception e){
			e.printStackTrace();
			Log.i("EnterActivity exception",e.toString());
		}
		return super.onTouchEvent(event);
	}
	
}
