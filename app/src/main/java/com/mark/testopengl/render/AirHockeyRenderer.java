package com.mark.testopengl.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Looper;
import android.text.Html;

import com.mark.testopengl.R;
import com.mark.testopengl.utils.LoggerConfig;
import com.mark.testopengl.utils.ShaderHelper;
import com.mark.testopengl.utils.TextResourceReader;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private Context mContext;
    private int program;
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    private final float[] modelMatrix = new float[16];


    public AirHockeyRenderer(Context context) {
        float[] tableVertices = {
                0f, 0f,
                0f, 14f,
                9f, 14f,
                9f, 0f
        };

        /**
         * 定义三角形时,要逆时针制定三角形的位置.优化性能.
         */
        float[] tableVerticesWithTriangles1 = {
                //第一个三角形
                0f, 0f,
                9f, 14f,
                0f, 14f,
                //第二个三角形
                0f, 0f,
                9f, 0f,
                9f, 14f,

                //line1
                0f, 7f,
                9f, 7f,

                //两个点
                4.5f, 2f,
                4.5f, 12f,
        };

        float[] tableVerticesWithTriangles2 = {
                //第一个三角形
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,
                //第二个三角形
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,

                //line1
                -0.5f, 0f,
                0.5f, 0f,

                //两个点
                0f, -0.25f,
                0f, 0.25f,
        };

        float[] tableVerticesWithTriangles3 = {
                //三角形扇
                0, 0, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                //line1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                //两个点
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f,
        };
        float[] tableVerticesWithTriangles = {
                //三角形扇
                   0f,    0f, 0f, 1.5f,   1f,   1f,   1f,
                -0.5f, -0.8f, 0f,   1f, 0.7f, 0.7f, 0.7f,
                 0.5f, -0.8f, 0f,   1f, 0.7f, 0.7f, 0.7f,
                 0.5f,  0.8f, 0f,   2f, 0.7f, 0.7f, 0.7f,
                -0.5f,  0.8f, 0f,   2f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0f,   1f, 0.7f, 0.7f, 0.7f,

                //line1
                -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                 0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

                //两个点
                0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
                0f,  0.4f, 0f, 1.75f, 1f, 0f, 0f,
        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);

        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        GLES20.glUseProgram(program);

//        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);


        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
/*        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            //横屏
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            //竖屏
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f);
        }*/

/*        Matrix.perspectiveM(projectionMatrix, 0, 45,
                (float) width / (float) height,
                1f, 10f);*/
        perspectiveM(projectionMatrix, 45,
                (float) width / (float) height, 0f, 10f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);
        Matrix.rotateM(modelMatrix, 0, -40f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        //矩形
//        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
//        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
        //中间横线
//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
        //画第一个蓝点
//        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
        //画第二个红点
//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);

    }

    public void perspectiveM(float[] m, float yFovInDegrees, float aspect,
                             float n, float f) {
        final float angleInRadians = (float) ((yFovInDegrees) * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;

    }
}
