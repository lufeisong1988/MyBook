package com.starbaby.diyBook.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.starbaby.diyBook.utils.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
/**
 * 从服务器下载url图片
 * @author Administrator
 *
 */
public class ImageGetFromHttp {
    /**
     * 通过URL获得网上图片。如:http://www.xxxxxx.com/xx.jpg
     * */
    public static Bitmap downloadBitmap(String url)  {
        Bitmap bmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        
        InputStream stream = null;
		try {
			stream = new URL(url).openStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(stream == null)
			return null;
        byte[] bytes = getBytes(stream);
        //这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, Utils.DMWidth * Utils.DMHeight);
        //end
        opts.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        return bmp;
    }
    /**
     * 数据流转成btyle[]数组
     * */
    private static byte[] getBytes(InputStream is) {
    	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        try {
            while ((len = is.read(b)) != -1) {
                baos.write(b, 0, len);
                baos.flush();
            }
        } catch (IOException e) {
                e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
    /****
    *    处理图片bitmap size exceeds VM budget （Out Of Memory 内存溢出）
    */
    private static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        Log.i("opts.inSampleSize",roundedSize+"");
        return roundedSize;
    }
    
    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}