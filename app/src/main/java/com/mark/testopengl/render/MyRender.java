package com.mark.testopengl.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.mark.testopengl.shape.Rect;
import com.mark.testopengl.shape.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRender implements GLSurfaceView.Renderer {

    private Context context;

    public MyRender(Context context){
        this.context = context;
    }

    //定义三角形对象
    private Triangle triangle;

    private Rect rect;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f,0.0f,1.0f,0.8f);
        triangle = new Triangle(context);
        rect = new Rect(context);
        Log.e("--Main--","onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        Log.e("--Main--","onSurfaceChanged");

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        triangle.draw();
//        rect.draw();
        Log.e("--Main--","onDrawFrame");
    }
}
