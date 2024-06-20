package me.jellysquid.mods.sodium.client.gl.array;

import javax.microedition.khronos.opengles.GL10;

/**
 * Provides Vertex Array functionality on supported platforms.
 */
public class GlVertexArray {
    public static final int NULL_ARRAY_ID = 0;
    private int handle;

    public GlVertexArray() {
        handle = createVertexArray();
    }

    public int getHandle() {
        return handle;
    }

    private int createVertexArray() {
        int[] array = new int[1];
        GLES20.glGenVertexArrays(1, array, 0);
        return array[0];
    }

    public void bind() {
        GLES20.glBindVertexArray(handle);
    }

    public void unbind() {
        GLES20.glBindVertexArray(NULL_ARRAY_ID);
    }

    public void delete() {
        GLES20.glDeleteVertexArrays(1, new int[] { handle }, 0);
    }
}
