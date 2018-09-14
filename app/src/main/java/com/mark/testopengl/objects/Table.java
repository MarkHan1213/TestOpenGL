package com.mark.testopengl.objects;

import android.opengl.GLES20;

import com.mark.testopengl.data.VertexArray;
import com.mark.testopengl.programs.TextureShaderProgram;

public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_CONT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_CONT) * 4;


    private final VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    private static final float[] VERTEX_DATA = {
            //X,Y,S,T
               0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.8f,   0f, 0.9f,
             0.5f, -0.8f,   1f, 0.9f,
             0.5f,  0.8f,   1f, 0.1f,
            -0.5f,  0.8f,   0f, 0.1f,
            -0.5f, -0.8f,   0f, 0.9f
    };

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureShaderProgram.getaPositionLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureShaderProgram.getaTextureCoordinatesLocation(),
                TEXTURE_COORDINATES_COMPONENT_CONT,
                STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }


}
