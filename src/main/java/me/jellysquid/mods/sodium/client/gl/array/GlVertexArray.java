package me.jellysquid.mods.sodium.client.gl.array;

import me.jellysquid.mods.sodium.client.gl.GlObject;
import org.lwjgl.opengl.GL11;

/**
 * Provides Vertex Array functionality emulation on OpenGL 2.0
 */
public class GlVertexArray extends GlObject {
    public static final int NULL_ARRAY_ID = 0;

    private int currentVbo = -1; // Track the currently bound VBO

    public GlVertexArray() {
        // Not required for emulating VAOs in OpenGL 2.0
    }

    @Override
    public void bind() {
        // No binding needed for emulated VAOs
    }

    @Override
    public void unbind() {
        // No unbinding needed for emulated VAOs
    }

    @Override
    public void delete() {
        // No object to delete for emulated VAOs
    }

    /**
     * Binds a vertex buffer to this emulated vertex array.
     * 
     * @param vboId The ID of the vertex buffer object to bind
     */
    public void bindVertexBuffer(int vboId) {
        if (currentVbo != vboId) {
            GL11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vboId);
            currentVbo = vboId;
        }
    }

    // You can add similar methods for binding other vertex attribute types (e.g., element arrays)
}
