package com.mark.testopengl.programs;

import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLES20;

import com.mark.testopengl.R;

public class TextureShaderProgram extends ShaderProgram {

    //uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    //Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader,
                R.raw.texture_fragment_shader);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getaPositionLocation() {
        return aPositionLocation;
    }

    public int getaTextureCoordinatesLocation() {
        return aTextureCoordinatesLocation;
    }
}
