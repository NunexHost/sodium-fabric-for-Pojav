package me.jellysquid.mods.sodium.client.render.immediate.model;

import net.minecraft.util.math.Direction;
import org.joml.*;

import java.util.Set;

public class ModelCuboid {
    public final int x1, y1, z1;
    public final int x2, y2, z2;

    public final int u0, u1, u2, u3, u4, u5;
    public final int v0, v1, v2;

    private final int faces;

    public final boolean mirror;

    public ModelCuboid(int u, int v,
                       float x1, float y1, float z1,
                       float sizeX, float sizeY, float sizeZ,
                       float extraX, float extraY, float extraZ,
                       boolean mirror,
                       int textureWidth, int textureHeight,
                       Direction... renderDirections) {
        int sizeXInt = Math.round(sizeX); // Arredonde para valores inteiros
        int sizeYInt = Math.round(sizeY);
        int sizeZInt = Math.round(sizeZ);

        int x2 = x1 + sizeXInt;
        int y2 = y1 + sizeYInt;
        int z2 = z1 + sizeZInt;

        x1 -= Math.round(extraX);
        y1 -= Math.round(extraY);
        z1 -= Math.round(extraZ);

        x2 += Math.round(extraX);
        y2 += Math.round(extraY);
        z2 += Math.round(extraZ);

        if (mirror) {
            int tmp = x2;
            x2 = x1;
            x1 = tmp;
        }

        this.x1 = x1 >> 4; // Dividir por 16 como operação bitwise
        this.y1 = y1 >> 4;
        this.z1 = z1 >> 4;

        this.x2 = x2 >> 4;
        this.y2 = y2 >> 4;
        this.z2 = z2 >> 4;

        int scaleU = textureWidth >> 16; // Evitar divisão por float
        int scaleV = textureHeight >> 16;

        this.u0 = u * scaleU;
        this.u1 = (u + sizeZInt) * scaleU;
        this.u2 = (u + sizeZInt + sizeXInt) * scaleU;
        this.u3 = (u + sizeZInt + sizeXInt + sizeXInt) * scaleU;
        this.u4 = (u + sizeZInt + sizeXInt + sizeZInt) * scaleU;
        this.u5 = (u + sizeZInt + sizeXInt + sizeZInt + sizeXInt) * scaleU;

        this.v0 = v * scaleV;
        this.v1 = (v + sizeZInt) * scaleV;
        this.v2 = (v + sizeZInt + sizeYInt) * scaleV;

        this.mirror = mirror;

        int faces = 0;
        for (Direction dir : renderDirections) {
            faces |= 1 << dir.ordinal();
        }

        this.faces = faces;
    }

    public boolean shouldDrawFace(int quadIndex) {
        return (this.faces & (1 << quadIndex)) != 0;
    }
}
