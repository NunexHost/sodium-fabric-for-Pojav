package me.jellysquid.mods.sodium.client.render.immediate.model;

import net.minecraft.util.math.Direction;
import org.joml.*;

import java.util.BitSet;

public class ModelCuboid {
    private final float x1, y1, z1;
    private final float x2, y2, z2;

    private final float u0, u1, u2, u3, u4, u5;
    private final float v0, v1, v2;

    private final BitSet faces;

    private final boolean mirror;

    public ModelCuboid(int u, int v,
                       float x1, float y1, float z1,
                       float sizeX, float sizeY, float sizeZ,
                       float extraX, float extraY, float extraZ,
                       boolean mirror,
                       float textureWidth, float textureHeight,
                       BitSet renderDirections) {
        this.x1 = x1 / 16.0f;
        this.y1 = y1 / 16.0f;
        this.z1 = z1 / 16.0f;

        this.x2 = (x1 + sizeX) / 16.0f;
        this.y2 = (y1 + sizeY) / 16.0f;
        this.z2 = (z1 + sizeZ) / 16.0f;

        float scaleU = 1.0f / textureWidth;
        float scaleV = 1.0f / textureHeight;

        this.u0 = scaleU * u;
        this.u1 = scaleU * (u + sizeZ);
        this.u2 = scaleU * (u + sizeZ + sizeX);
        this.u3 = scaleU * (u + sizeZ + sizeX + sizeX);
        this.u4 = scaleU * (u + sizeZ + sizeX + sizeZ);
        this.u5 = scaleU * (u + sizeZ + sizeX + sizeZ + sizeX);

        this.v0 = scaleV * v;
        this.v1 = scaleV * (v + sizeZ);
        this.v2 = scaleV * (v + sizeZ + sizeY);

        this.mirror = mirror;

        this.faces = renderDirections;
    }

    public boolean shouldDrawFace(int quadIndex) {
        return faces.get(quadIndex);
    }
}
