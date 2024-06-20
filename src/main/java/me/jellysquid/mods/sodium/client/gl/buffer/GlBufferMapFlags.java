package me.jellysquid.mods.sodium.client.gl.buffer;

import me.jellysquid.mods.sodium.client.gl.util.EnumBit;
import org.lwjgl.opengl.GL11;

public enum GlBufferMapFlags implements EnumBit {
    READ(GL11.GL_MAP_READ_BIT),
    WRITE(GL11.GL_MAP_WRITE_BIT),
    INVALIDATE_BUFFER(GL11.GL_MAP_INVALIDATE_BUFFER_BIT),
    INVALIDATE_RANGE(GL11.GL_MAP_INVALIDATE_RANGE_BIT);

    private final int bit;

    GlBufferMapFlags(int bit) {
        this.bit = bit;
    }

    @Override
    public int getBits() {
        return this.bit;
    }
}
