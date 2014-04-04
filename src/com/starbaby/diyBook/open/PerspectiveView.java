package com.starbaby.diyBook.open;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.starbaby.diyBook.helper.BookMusicHelper;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-6
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
@SuppressLint("NewApi")
public class PerspectiveView extends GLSurfaceView implements GLSurfaceView.Renderer {
	/*
	 * open parameters
	 */
    private float[] projectionMatrix = new float[16];//投影矩阵
    private float[] viewMatrix = new float[16]; //视图矩阵
    private float[] modelMatrix = new float[16];//模型矩阵
    private float[] mvpMatrix = new float[16];//模型视图投影矩阵
    private TextureMesh coverMesh, contentMesh;
    private float distance = 10.5f;
    private float width, height, ratio, factor;
    private long duration = 800;
    private Bitmap coverTexture, contentTexture,backCover;
    private int flag;
    public ManimatorListener animatorListener;
    float left = -ratio;
    float right = ratio;
    float top = 1;
    float bottom = -1;
    float origin_left = -ratio;
    float origin_right = ratio;
    float origin_top = 1;
    float origin_bottom = -1;
    float x_scale = 1;
    float y_scale = 1;
    float newleft = 0;
	float newright = 0;
	float newtop = 0;
	float newbottom = 0;
	private boolean hasTexture;
	private int coverWidth,coverHeight;
	private BookMusicHelper mBookMusicHelper;
	Context context;
	boolean bLong;
    
    public void setManimatorListener(ManimatorListener animatorListener) {
		this.animatorListener = animatorListener;
	}

	public interface ManimatorListener{
    	public void onAnimationEnd();
    }
	
    public PerspectiveView(Context context) {
        super(context);
        this.init();
        this.context = context;
    }

    /**
     * 
     * @param coverTexture 打开封面
     * @param contentTexture 打开第一页
     * @param left 左上角X
     * @param right 右下角X
     * @param top 左上角Y
     * @param bottom 右下角Y
     */
    public void setTextures(Bitmap coverTexture, Bitmap contentTexture,Bitmap backCover,float left,float right,float top,float bottom,int flag,int width,int height,boolean bLong) {
    	this.bLong = bLong;
    	hasTexture = false;
    	mBookMusicHelper = new BookMusicHelper(context);
    	this.flag = flag;
		this.coverTexture = coverTexture;
		this.backCover = backCover;
		this.contentTexture = contentTexture;
        this.origin_left = left;
        this.origin_right = right;
        this.origin_top = top;
        this.origin_bottom = bottom;
        this.coverWidth = width;
        this.coverHeight = height;
        this.left = BookUtils.toGLX(origin_left, ratio,this.width);
        this.right = BookUtils.toGLX(origin_right, ratio,this.width);
        this.bottom = BookUtils.toGLY(origin_bottom,this.height);
        this.top = BookUtils.toGLY(origin_top,this.height);
        
    }

    private void init() {
        this.setEGLContextClientVersion(2);
        //设置背景透明
        this.setZOrderOnTop(true);
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    	Log.i("surfaceview = ", "create");
        glClearColor(0f, 0f, 0f, 0f);

        //照相机位置
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = distance;

        //照相机拍照方向
        float lookX = 0.0f;
        float lookY = 0.0f;
        float lookZ = -1.0f;

        //照相机的垂直方向
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        coverMesh = new TextureMesh(getContext(),gl10);
        contentMesh = new TextureMesh(getContext(),gl10);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);
        ratio = (float) width / height;
        
        Log.v("ARC", "调用onSurfaceChanged");

    }

    float nr_x,nr_y, nd, barW;
    @Override
    public void onDrawFrame(GL10 gl10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		if (coverTexture == null) {
			return;
		}
        float near = distance;//视点到屏幕的距离
        float far = 250;//ratio*2*near/(right-left);//视点到原图的距离
        nd = (near*ratio*2)/(right-left)-near;//原图到屏幕的距离 ，Z轴
		newleft = left*(nd+near)/near;//平移后再屏幕上的左边距离
		newright = right*(nd+near)/near;//平移后再屏幕上的右边距离
		newtop = top*(nd+near)/near;//平移后再屏幕上的上边距离
		newbottom = bottom*(nd+near)/near;//平移后再屏幕上的下边距离
		
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);//书本缩略图的矩阵(初始化的效果，立刻执行)

        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        setMeshTexture();//调用
        if(flag == 1){
        	 if(!bLong){
        	//放大平移
        	coverMesh.setVertexes(getTextureVertexes());//调用
             contentMesh.setVertexes(getTextureVertexes());//调用
             Matrix.setIdentityM(modelMatrix, 0);
             Matrix.translateM(modelMatrix, 0, (newleft+Math.abs(newright-newleft)/2)*(1-factor),(newtop-Math.abs(newbottom-newtop)/2)*(1-factor),-nd*(1-factor));
             
             Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
             Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
             contentMesh.draw(mvpMatrix,0);
            
            	 //旋转平移 
            	 
                 Matrix.setIdentityM(modelMatrix, 0);
                 Matrix.translateM(modelMatrix, 0, (newleft+Math.abs(newright-newleft)/2)*(1-factor), (newtop-Math.abs(newbottom-newtop)/2)*(1-factor),-nd*(1-factor));

         		Matrix.rotateM(modelMatrix, 0, -180 * factor, 0, 1, 0);
         		float degrees = 180 * factor;
                 Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
                 Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

                 coverMesh.draw(mvpMatrix,degrees);
         	}else{
         		Utils.currentPage = 0;
         		coverMesh.setVertexes(getTextureVertexes());//调用
         		coverMesh.setVertexes(getTextureVertexes());//调用
                 Matrix.setIdentityM(modelMatrix, 0);
                 Matrix.translateM(modelMatrix, 0, (newleft+Math.abs(newright-newleft)/2)*(1-factor),(newtop-Math.abs(newbottom-newtop)/2)*(1-factor),-nd*(1-factor));
                 
                 Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
                 Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
                 coverMesh.draw(mvpMatrix,0);
         	}
        }else if(flag == 2){
        	
        	
            contentMesh.setVertexes(getTextureVertexes());//调用
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, (newleft+Math.abs(newright-newleft)/2)*(factor),(newtop-Math.abs(newbottom-newtop)/2)*(factor),-nd*(factor));
            
            Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
            contentMesh.draw(mvpMatrix,0);
            
            if(Utils.currentPage != 0){
            	coverMesh.setVertexes(getTextureVertexes());//调用
            	 Matrix.setIdentityM(modelMatrix, 0);
                 Matrix.translateM(modelMatrix, 0, (newleft+Math.abs(newright-newleft)/2)*(factor), (newtop-Math.abs(newbottom-newtop)/2)*(factor),-nd*(factor));

         		Matrix.rotateM(modelMatrix, 0, -180 *(1- factor), 0, 1, 0);
         		float degrees = 180 * (1- factor);
                 Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
                 Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

                 coverMesh.draw(mvpMatrix,degrees);
            }
        }
    }

    
    /*
     * 绘制书页图片
     */
	private void setMeshTexture() {
		if (!hasTexture && coverTexture != null) {
			coverMesh.setTexture(coverTexture, backCover);
			contentMesh.setTexture(contentTexture, backCover);
			hasTexture = true;
		}
	}
    //设置打开的书页在屏幕上的显示矩阵（-1到1之间，（0,0,0）为屏幕中心点 ；（-1,1）为左上角；（-1，-1）为左下角；（1,1）为右上角；（1，-1）为右下角）
    private Vertex[] getTextureVertexes() {
		float W = coverWidth;
		float H = coverHeight;
		float scaleBit = ((2 * W) / H);
		float scaleScreen = ((float)Utils.DMWidth / (float)Utils.DMHeight);
		if(scaleBit < scaleScreen){//竖版
			 Vertex[] vertexes = new Vertex[]{
		                new Vertex(0f, 1f,0f, 1f),
		                new Vertex(0f, -1f,0f, 1f),
		                new Vertex(((float)W *2 / H), 1f, 0f, 1f),
		                new Vertex(((float)W *2 / H), -1f, 0f, 1f)
		        };
			 return vertexes;
		}else{//横版
			 Vertex[] vertexes = new Vertex[]{
		                new Vertex(0f, (float)(Utils.DMWidth * H) / (Utils.DMHeight * W * 2),0f, 1f),
		                new Vertex(0f, -(float)(Utils.DMWidth * H) / (Utils.DMHeight * W * 2),0f, 1f),
		                new Vertex((float)(Utils.DMWidth) / Utils.DMHeight, (float)(Utils.DMWidth * H) / (Utils.DMHeight * W * 2) , 0f, 1f),
		                new Vertex((float)(Utils.DMWidth) / Utils.DMHeight, -(float)(Utils.DMWidth * H) / (Utils.DMHeight * W * 2), 0f, 1f)
		        };
			 return vertexes;
		}
    }
    
    public void startAnimation() {
    	new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
//				if(contentTexture != null){
//					contentTexture.recycle();
//				}
//				if(coverTexture != null){
//					coverTexture.recycle();
//				}
//				if(backCover != null){
//					backCover.recycle();
//				}
				System.gc();
			}
		}, 2000);
    	ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        if(flag == 1){
        	mBookMusicHelper.openBook();
        }else if(flag == 2){
        	mBookMusicHelper.closeBook();
        }
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final float f = (Float) valueAnimator.getAnimatedValue();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        factor = f;
                        requestRender();
                    }
                });
            }
        });

        animator.start();
        animator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				if(animatorListener!=null){
					animatorListener.onAnimationEnd();
				}
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});
    }
}
