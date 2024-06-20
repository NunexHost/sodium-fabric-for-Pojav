package me.jellysquid.mods.sodium.client.gl.array;

import org.lwjgl.opengl.GL11;

/**
 * Provides Vertex Array functionality using OpenGL 2.0 mechanisms.
 */
public class GlVertexArray {
    // No handle needed for OpenGL 2.0, as VAOs aren't available

    public GlVertexArray() {
        // No need to generate an ID, as VAOs aren't used in OpenGL 2.0
    }

    /**
     * Binds a vertex buffer to a specific attribute index.
     *
     * @param attribute the attribute index (e.g., GL_VERTEX_ATTRIB_ARRAY_0)
     * @param vbo the vertex buffer object containing the data
     * @param stride the number of bytes between consecutive vertex elements (or 0 for tightly packed data)
     * @param offset the byte offset within the buffer to start reading from
     */
    public static void bindVertexBuffer(int attribute, long vbo, int stride, int offset) {
        GL11.glBindBuffer(GL11.GL_ARRAY_BUFFER, (int) vbo);
        GL11.glVertexAttribPointer(attribute, /* num components */, GL11.GL_FLOAT, /* normalized? */, stride, offset);
    }

    /**
     * Enables or disables a vertex attribute array.
     *
     * @param attribute the attribute index (e.g., GL_VERTEX_ATTRIB_ARRAY_0)
     * @param enabled whether to enable (true) or disable (false) the attribute
     */
    public static void enableAttribute(int attribute, boolean enabled) {
        if (enabled) {
            GL11.glEnableVertexAttribArray(attribute);
        } else {
            GL11.glDisableVertexAttribArray(attribute);
        }
    }
}
