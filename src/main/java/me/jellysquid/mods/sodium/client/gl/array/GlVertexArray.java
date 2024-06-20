package me.jellysquid.mods.sodium.client.gl.array;

import me.jellysquid.mods.sodium.client.gl.GlObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * Provides Vertex Array functionality on supported platforms (OpenGL 2.0).
 */
public class GlVertexArray extends GlObject {
    private int vboId;
    public static final int NULL_ARRAY_ID = 0;

    public GlVertexArray() {
        // Generate a Vertex Buffer Object (VBO) for vertex data
        this.vboId = GL15.glGenBuffers();
    }

    public void bind() {
        // Bind the VBO when needed (before drawing)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboId);
    }

    public void unbind() {
        // Unbind the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void setData(float[] data) {
        // Upload vertex data to the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void delete() {
        // Delete the VBO when no longer needed
        GL15.glDeleteBuffers(this.vboId);
    }
}
