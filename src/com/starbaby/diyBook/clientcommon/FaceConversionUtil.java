package com.starbaby.diyBook.clientcommon;


import java.util.ArrayList;
import java.util.List;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientbean.EmojiDate;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
 
/**
 * 
 ****************************************** 
 * @文件名称 : FaceConversionUtil.java
 * @创建时间 : 2013-1-27 下午02:34:09
 * @文件描述 : 表情轉換工具
 ****************************************** 
 */
public class FaceConversionUtil {
	/** 每一页表情的个数 */
	private int pageSize = 20;

	private static FaceConversionUtil mFaceConversionUtil;

	private static final Pattern  pattern  = Pattern.compile("\\[e\\](.*?)\\[/e\\]");

/*	private static HashMap<Integer,Integer>    parseMap = new HashMap<Integer,Integer>();
	
	private static ExecutorService threadPool = Executors.newCachedThreadPool();  */
	 
	private FaceConversionUtil() {

	}

	public static FaceConversionUtil getInstace() {
		if (mFaceConversionUtil == null) {
			mFaceConversionUtil = new FaceConversionUtil();
		}
		return mFaceConversionUtil;
	}
	
	/**
	 * 添加表情
	 * 
	 * @param context
	 * @param imgId
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, int imgId,
			String spannableString) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				imgId);
		bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
		ImageSpan imageSpan = new ImageSpan(context, bitmap);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	
	/**
	 * 将本地显示内容转换成。替换所有<img>标签，转换成unicode编码,供发送到服务器
	 * 
	 * @param cs ：原始内容串，包含各种格式化。不用转换成纯String，如若为EditText中的内容，则直接取EditText.getEditableText()输入，
	 * 	不必使用Html.toHtml()来转换。
	 * @param mContext
	 * @return
	 */
	public static String convertToMsg(CharSequence cs, Context mContext) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(cs);
		ImageSpan[] spans = ssb.getSpans(0, cs.length(), ImageSpan.class);// 获取所有<img>标签
		for (int i = 0; i < spans.length; i++) {
			ImageSpan span = spans[i];
			String c = span.getSource();
			//System.out.println("c========"+span.getSource()+",converUnicode(c)=="+convertUnicode(c));
			int a = ssb.getSpanStart(span);
			int b = ssb.getSpanEnd(span);
			if (c.contains("emoji")) {  //如果是Emoji表情，即sic=emoji_2600的形式
				//String emoji = c.substring(c.indexOf("_") + 1);
				ssb.replace(a, b, convertUnicode(c)); // 替换<img>标签  convertUnicode(c)
			}
		}
		ssb.clearSpans(); // 去除所有标签
		return ssb.toString();
	}

	
	/**
	 * 将EmojiParser解析出的"[e]xxxxx[/e]"代码，渲染成图片
	 * @param content
	 * @param mContext
	 * @return
	 */
	public static SpannableStringBuilder convetToHtml(final String content,final Context mContext) {
		//Future<SpannableStringBuilder> futrue = threadPool.submit(new Callable<SpannableStringBuilder>(){
			//@Override
		//	public SpannableStringBuilder call() throws Exception {
	/*	
	            boolean flag = true;
				if(parseMap.containsKey(tid) && parseMap.get(tid)==2){
					flag = false;
				}
				
				if(flag){*/
					Resources resources = mContext.getResources();
					String unicode = parseEmoji(content,mContext);
					Matcher matcher = pattern.matcher(unicode);
					SpannableStringBuilder sBuilder = new SpannableStringBuilder(unicode);
					while (matcher.find()) {
						/*Drawable drawable = null;
						ImageSpan span = null;*/
						String emo = matcher.group();
						//System.out.println("emo======"+emo+",imgId==="+emo.substring(emo.indexOf("]") + 1,emo.lastIndexOf("[")));
						try {
							int id = Integer.valueOf(emo.substring(emo.indexOf("]") + 1,emo.lastIndexOf("[")));resources.getIdentifier(
									"emoji_"+ emo.substring(emo.indexOf("]") + 1,emo.lastIndexOf("[")), "drawable",
									mContext.getPackageName());
							if (id != 0) {
								Drawable  drawable = resources.getDrawable(id);
								drawable.setBounds(0, 0, 24, 24);
								ImageSpan 	span = new ImageSpan(drawable);
								sBuilder.setSpan(span, matcher.start(), matcher.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
						} catch (Exception e) {
							break;
						}
					}
					return sBuilder;
			/* }else{
				 if(!parseMap.containsKey(tid))
					 	parseMap.put(tid,2);
			 }
				return new SpannableStringBuilder(content);*/
			
	//	});
	
	/*	try {  
            return futrue.get();
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        } catch (ExecutionException e) {  
            e.printStackTrace();  
        }  */
		//return  new SpannableStringBuilder(content);
	}
	
	/**
	 * 解析字符串，将Emoji表情编码转换成"[e]xxxxx[/e]"的形式，供下一步解析使用
	 * @param input
	 * @return
	 */
	private static String parseEmoji(String input,Context context) {
		//System.out.println("=========+parseEmoji(input, context)");
		if (input == null || input.length() <= 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		int[] codePoints = toCodePointArray(input);
		List<Integer> key = null;
		for (int i = 0; i < codePoints.length; i++) {
			key = new ArrayList<Integer>();
			//尝试检查emoji表情是否为两个unicode码组成的
			if (i + 1 < codePoints.length) {
				key.add(codePoints[i]);
				key.add(codePoints[i + 1]);	
				if (EmojiDate.emojiNumToImgArray.containsKey(key)) {  
					Integer value = EmojiDate.emojiNumToImgArray.get(key);
					//System.out.println("codePoints==="+codePoints[i]);
					 if (value != 0) {
						result.append("[e]" + value + "[/e]");
					 }
					i++;
					continue;
				}
			} 
			
			//如果emoji表情是单个unicode组成的
		    //key.clear();
			//key.add(codePoints[i]);
			if (EmojiDate.emojiNumToImgArray.containsKey(codePoints[i])) {
				//System.out.println("codePoints==="+codePoints[i]);
				Integer value = EmojiDate.emojiNumToImgArray.get(codePoints[i]);
				 if (value != 0) {
						 result.append("[e]" + value + "[/e]");
					 	//result.append(("<img src='"+value+"'/>", imageGetter, null));
					 }
				continue;
			}
			
			result.append(Character.toChars(codePoints[i])); //如果不是表情，则直接转换成字符加入
		}
		return result.toString();
	}
	
	
	/**
	 * 获取 str 转换成 Unicode code 格式编码集,java中使用UTF-16来保存unicode编码
	 * 
	 * @param str
	 * @return
	 */
	private static int[] toCodePointArray(String str) {
		char[] ach = str.toCharArray();
		int len = ach.length;
		int[] acp = new int[Character.codePointCount(ach, 0, len)];//计算char数组中unicode码数量，1个unicode代码点可包含1-2个代码单元
		int j = 0;
		for (int i = 0, cp; i < len; i += Character.charCount(cp)) { //Character.charCount(cp)根据cp的unicode值来确定需要多少个char来表示
			cp = Character.codePointAt(ach, i); //获取 每个代码点 的值
			acp[j++] = cp;
		}
		return acp;
	}
	
	/**
	 * 将emo(本地表情文件名)转换成对应Emoji表情unicode编码,每个UTF-16码占两个字节 
	 * 
	 * @param emo 本地表情文件名 例如:emoji_1f1e8_1f1f3.png
	 * @return
	 */
	private static String convertUnicode(String emo) {
		emo = emo.substring(emo.indexOf("_") + 1);
		if (emo.length() < 6) {
			return new String(Character.toChars(Integer.parseInt(emo,16)));
		}
		String[] emos = emo.split("_");
		char[] char0 = Character.toChars(Integer.parseInt(emos[0], 16)); //字符串("2600") -> 整型 -> UTF-16 encoded char sequence(java使用UTF-16格式存储Unicode编码)
		char[] char1 = Character.toChars(Integer.parseInt(emos[1], 16));
		char[] emoji = new char[char0.length + char1.length];
		for (int i = 0; i < char0.length; i++) {
			emoji[i] = char0[i];
		}
		for (int i = char0.length; i < emoji.length; i++) {
			emoji[i] = char1[i - char0.length];
		}
		return new String(emoji);
	}

	/**
	 * 获取分页数据
	 * 
	 * @param page
	 * @return
	 */
	public List<Integer> getData(int page) {
		int startIndex = page * pageSize;
		int endIndex = startIndex + pageSize;

		if (endIndex > EmojiDate.emojiImgArrray.size()) {
			endIndex =  EmojiDate.emojiImgArrray.size();
		}
		// 不这么写，会在viewpager加载中报集合操作异常，我也不知道为什么
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(EmojiDate.emojiImgArrray.subList(startIndex, endIndex));
		if (list.size() == pageSize) {
			list.add(R.drawable.face_del_icon);
		}
		return list;
	}
	
}