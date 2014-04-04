package com.starbaby.diyBook.open;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


import com.starbaby.diyBook.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;



/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class TextureMesh {

    private int[] textureId;
    
    private Bitmap texture,backCover;

    private Vertex[] vertexes;

    private Shader shader;

    private FloatBuffer vertexBuffer, textureCoordBuffer;
    GL10 gl;
    public TextureMesh(Context context ,GL10 gl) {
        shader = new Shader();
        this.gl = gl;
        shader.setProgram(context, R.raw.demo_vertex_shader, R.raw.demo_fragment_shader);

        //一个四边形所需顶点的空间: 4个点（x,y,z,w），float是4字节
        vertexBuffer = ByteBuffer.allocateDirect(4 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        //纹理坐标，针对四边形，4个点（x,y），float是4字节
        textureCoordBuffer = ByteBuffer.allocateDirect(2 * 4 * 4) .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordBuffer.put(new float[]{
                0, 0,
                0, 1,
                1, 0,
                1, 1
        });
    }

    public void draw(float[] projectionMatrix,float degrees) {
        if (vertexes == null) {
            return;
        }

        initBuffer();//初始化

        this.shader.useProgram();
        if (textureId != null) {
			textureId = new int[1];
			glGenTextures(1, textureId, 0);
			glBindTexture(GL_TEXTURE_2D, textureId[0]);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
					GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
					GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
					GL_CLAMP_TO_EDGE);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
					GL_CLAMP_TO_EDGE);
		} else {
			textureId = new int[1];
			glBindTexture(GL_TEXTURE_2D, textureId[0]);
		}
        
		if (degrees < 90f) {
			if(texture != null){
				glEnable(GL_TEXTURE_2D);
				glBindTexture(GL_TEXTURE_2D, textureId[0]);
				GLUtils.texImage2D(GL_TEXTURE_2D, 0, texture, 0);
				glDisable(GL_TEXTURE_2D);
			}else{
				
			}
			
		} else {
			if(backCover != null){
				glEnable(GL_TEXTURE_2D);
				glBindTexture(GL_TEXTURE_2D, textureId[0]);
				GLUtils.texImage2D(GL_TEXTURE_2D, 0, backCover, 0);
				glDisable(GL_TEXTURE_2D);
			}else{

			}
		}
        int aTextureCoord = this.shader.getHandle("aTextureCoord");
        glVertexAttribPointer(aTextureCoord, 2, GL_FLOAT, false,0, textureCoordBuffer);
        glEnableVertexAttribArray(aTextureCoord);
        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);
        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 4, GL_FLOAT, false,  4 * 4, vertexBuffer);
        glEnableVertexAttribArray(aPosition);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        
        //unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setTexture(Bitmap bitmap,Bitmap backCover) {//设置翻页后显示的第一张图片
        this.texture = bitmap;
        this.backCover = backCover;
    }

    public boolean hasTexture() {
        return this.texture != null;
    }

    public void setVertexes(Vertex[] vertexes) {
        this.vertexes = vertexes;
    }

    public void clear() {
        vertexes = null;
    }

    public boolean isClear() {
        return vertexes == null;
    }

    private void initBuffer() {
        this.vertexBuffer.clear();

        for (Vertex v : vertexes) {
            this.vertexBuffer.put(v.getPosition());
        }

        this.vertexBuffer.position(0);

        textureCoordBuffer.position(0);
    }
}
