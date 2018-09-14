package com.mark.testopengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mark.testopengl.render.AirHockeyNewRenderer;
import com.mark.testopengl.render.AirHockeyRenderer;
import com.mark.testopengl.render.MyRender;
import com.mark.testopengl.render.MyRenderer;

public class MainActivity extends AppCompatActivity {

    /**
     * Hold a reference to our GLSurfaceView
     */
    private GLSurfaceView mGLSurfaceView;

    final AirHockeyNewRenderer airHockeyNewRenderer = new AirHockeyNewRenderer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        if (detectOpenGLES20()) {
            // Request an OpenGL ES 2.0 compatible context.
            // Use 2 for OpenGL ES 2.0
            mGLSurfaceView.setEGLContextClientVersion(2);

            // Set the renderer to our demo renderer, defined below.
//            mGLSurfaceView.setRenderer(new LessonOneRenderer());
            mGLSurfaceView.setRenderer(airHockeyNewRenderer);
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }
        setContentView(mGLSurfaceView);

        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    final float normalizedX =
                            (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY =
                            -((event.getY() / (float) v.getHeight()) * 2 - 1);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private boolean detectOpenGLES20() {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }
}
