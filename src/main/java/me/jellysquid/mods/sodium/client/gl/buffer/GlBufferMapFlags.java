package me.jellysquid.mods.sodium.client.gl.buffer;

import me.jellysquid.mods.sodium.client.gl.util.EnumBit;

public enum GlBufferMapFlags implements EnumBit {
    READ(0x0001), // GL_READ_ONLY_ARB
    WRITE(0x0002), // GL_WRITE_ONLY_ARB
    INVALIDATE_BUFFER(0x0008), // GL_INVALIDATE_BUFFER
    INVALIDATE_RANGE(0x0004), // GL_INVALIDATE_RANGE
    UNSYNCHRONIZED(0x0020); // GL_MAP_UNSYNCHRONIZED_BIT is not available in OpenGL 2.0

    private final int bit;

    GlBufferMapFlags(int bit) {
        this.bit = bit;
    }

    @Override
    public int getBits() {
        return this.bit;
    }
}
