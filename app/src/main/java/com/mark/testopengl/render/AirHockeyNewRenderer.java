package com.mark.testopengl.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.mark.testopengl.R;
import com.mark.testopengl.objects.Mallet;
import com.mark.testopengl.objects.Puck;
import com.mark.testopengl.objects.Table;
import com.mark.testopengl.programs.ColorShaderProgram;
import com.mark.testopengl.programs.TextureShaderProgram;
import com.mark.testopengl.utils.Geometry;
import com.mark.testopengl.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyNewRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] invertedViewProjectionMatrix = new float[16];

    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;


    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;
    private int texture;

    public AirHockeyNewRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2, 0.4f);
        redMalletPosition = new Geometry.Point(0f, mallet.height / 2, -0.4f);
        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        perspectiveM(projectionMatrix, 45,
                (float) width / (float) height, 1f, 10f);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 2f, 2f, 0f, 0f, 0f, 0f, 1f, 0f);
//        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);
//        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
//
//        final float[] temp = new float[16];
//        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        puckPosition = puckPosition.translate(puckVector);
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            //碰撞减小速度系数,注释掉就不会减速了,永动机
//            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            //碰撞减小速度系数,注释掉就不会减速了,永动机
//            puckVector = puckVector.scale(0.9f);
        }


        puckPosition = new Geometry.Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));
        //碰撞减小速度系数,注释掉就不会减速了,永动机
//        puckVector = puckVector.scale(0.99f);

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);


//        textureShaderProgram.useProgram();
//        textureShaderProgram.setUniforms(projectionMatrix, texture);
//        table.bindData(textureShaderProgram);
//        table.draw();
//
//        colorShaderProgram.useProgram();
//        colorShaderProgram.setUniforms(projectionMatrix);
//        mallet.bindData(colorShaderProgram);
//        mallet.draw();
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

//        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        positionObjectInScene(redMalletPosition.x, redMalletPosition.y, redMalletPosition.z);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

//        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

//        positionObjectInScene(0f, puck.height / 2f, 0f);
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorShaderProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
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

    private boolean blueMalletPressed = false;
    private boolean redMalletPressed = false;
    private Geometry.Point blueMalletPosition;
    private Geometry.Point redMalletPosition;
    private Geometry.Point previousBlueMalletPosition;
    private Geometry.Point previousRedMalletPosition;
    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(
                blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height / 2);
        blueMalletPressed = Geometry.intersects(malletBoundingSphere, ray);

        Geometry.Sphere redMalletBoundingSphere = new Geometry.Sphere(new Geometry.Point(
                redMalletPosition.x, redMalletPosition.y, redMalletPosition.z), mallet.height / 2);
        redMalletPressed = Geometry.intersects(redMalletBoundingSphere, ray);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if (blueMalletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            previousBlueMalletPosition = blueMalletPosition;
//            blueMalletPosition = new Geometry.Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z);
            blueMalletPosition = new Geometry.Point(
                    clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z, 0f + mallet.radius, nearBound - mallet.radius));
            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();

            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }

        if (redMalletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            previousRedMalletPosition = redMalletPosition;
//            blueMalletPosition = new Geometry.Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z);
            redMalletPosition = new Geometry.Point(
                    clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z, farBound + mallet.radius,0f - mallet.radius));

            float distance = Geometry.vectorBetween(redMalletPosition, puckPosition).length();

            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetween(previousRedMalletPosition, redMalletPosition);
            }
        }
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};


        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));

    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }
}
