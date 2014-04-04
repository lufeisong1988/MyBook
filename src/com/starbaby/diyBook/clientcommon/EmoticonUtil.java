package com.starbaby.diyBook.clientcommon;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

/**
 * 表情工具类
 * 
 * @author dqjk
 * 
 */
public class EmoticonUtil {

	/**
	 * 根据Emoji表情unicode编码获取获取本地Emoji表情文件名
	 * 
	 * @param emojiUnicode
	 * @param c
	 * @return
	 */
	public static Integer getEmoticonResId(String emojiUnicode, Context c) {
		return c.getResources().getIdentifier("emoji_" + emojiUnicode,
				"drawable", c.getPackageName());
	}

	/**
	 * 组装emoji表情标签，在空间中使用Html格式显示
	 * 
	 * @param emojiName
	 * @return
	 */
	public static String formatFaces(String emojiName) {
		//System.out.println("emojiName=="+emojiName);
		StringBuffer sb = new StringBuffer();
		sb.append("<img src=\"emoji_");
		sb.append(emojiName);
		sb.append("\">");
		return sb.toString();
	}

	/**
	 * Html中<img>标签图片获取工具
	 * @param c
	 * @return
	 */
	public static ImageGetter getImageGetter(final Context c) {
		return new ImageGetter() {
			public Drawable getDrawable(String source) {
				Integer resID=c.getResources().getIdentifier(source,"drawable", c.getPackageName());
				Drawable d = c.getResources().getDrawable(resID);
				d.setBounds(0, 0, 24, 24);
				return d;
			}
		};
	}

}
