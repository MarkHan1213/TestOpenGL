package com.mark.testopengl.shape;

import android.content.Context;
import android.opengl.GLES20;

import com.mark.testopengl.R;
import com.mark.testopengl.utils.ShaderHelper;
import com.mark.testopengl.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private Context context;

    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexBuffer;

    // 数组中每个顶点的坐标数
    public static final int COORDS_PER_VERTEX = 2;
    // 每个顶点的坐标数  	X ,  Y
    public static float triangleCoords[] = {0.0f, 0.5f, 0.5f,   // top
            -0.5f, -0.5f, 0f,   // bottom left
            0.5f, -0.5f, 0.5f};   // bottom right


    //------------第一步 : 定义两个标签，分别于着色器代码中的变量名相同,
    //------------第一个是顶点着色器的变量名，第二个是片段着色器的变量名
    private static final String A_POSITION = "a_Position";
    private static final String U_COLOR = "u_Color";

    //------------第二步: 定义两个ID,我们就是通ID来实现数据的传递的,这个与前面
    //------------获得program的ID的含义类似的
    private int uColorLocation;
    private int aPositionLocation;

    //---------第四步:定义坐标元素的个数，这里有三个顶点
    private static final int POSITION_COMPONENT_COUNT = 3;


    public Triangle(Context context) {
        this.context = context;

        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        initProgram();
        //----------第三步: 获取这两个ID ，是通过前面定义的标签获得的
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);


        //---------第五步: 传入数据
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }

    private int program;

    private void initProgram() {
        //获取顶点着色器文本
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        //获取片段着色器文本
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);
        //获取program的id
        program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(program);
    }

    //----------第七步:绘制
    public void draw(){
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, POSITION_COMPONENT_COUNT);

    }

}
