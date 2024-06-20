package me.jellysquid.mods.sodium.client.util.color;

import net.minecraft.util.math.MathHelper;

public class BoxBlur {

    public static void blur(ColorBuffer buf, ColorBuffer tmp, int radius) {
        if (buf.width != tmp.width || buf.height != tmp.height) {
            throw new IllegalArgumentException("Color buffers must have same dimensions");
        }

        if (isHomogeneous(buf.data)) {
            return;
        }

        blurImpl(buf.data, tmp.data, buf.width, buf.height, radius); // X-axis
        blurImpl(tmp.data, buf.data, buf.width, buf.height, radius); // Y-axis
    }

    private static void blurImpl(int[] src, int[] dst, int width, int height, int radius) {
        int multiplier = getAveragingMultiplier((radius * 2) + 1);
        int offset, srcRowOffset;

        for (int y = 0; y < height; y++) {
            srcRowOffset = y * width;

            int red = 0, green = 0, blue = 0;

            // Initial sum for the radius
            for (int i = -radius; i <= radius; i++) {
                offset = MathHelper.clamp(i, -width, width - 1);
                int color = src[srcRowOffset + offset];
                red += (color >> 16) & 0xFF; // unpackRed equivalent
                green += (color >> 8) & 0xFF; // unpackGreen equivalent
                blue += color & 0xFF; // unpackBlue equivalent
            }

            // Blur along the row
            for (int x = 0; x < width; x++) {
                dst[srcRowOffset + x] = averageRGB(red, green, blue, multiplier);

                if (x + radius + 1 < width) {
                    offset = MathHelper.clamp(x + radius + 1, -width, width - 1);
                    int colorToAdd = src[srcRowOffset + offset];
                    red += (colorToAdd >> 16) & 0xFF; // unpackRed equivalent
                    green += (colorToAdd >> 8) & 0xFF; // unpackGreen equivalent
                    blue += colorToAdd & 0xFF; // unpackBlue equivalent
                }

                if (x - radius >= 0) {
                    offset = MathHelper.clamp(x - radius, -width, width - 1);
                    int colorToRemove = src[srcRowOffset + offset];
                    red -= (colorToRemove >> 16) & 0xFF; // unpackRed equivalent
                    green -= (colorToRemove >> 8) & 0xFF; // unpackGreen equivalent
                    blue -= colorToRemove & 0xFF; // unpackBlue equivalent
                }
            }
        }
    }

    /**
     * Pre-computes a multiplier that can be used to avoid costly division when averaging the color data in the
     * sliding window.
     */
    private static int getAveragingMultiplier(int size) {
        return MathHelper.ceil((1L << 24) / (double) size);
    }

    /**
     * Calculates the average color within the sliding window using the pre-computed constant.
     */
    private static int averageRGB(int red, int green, int blue, int multiplier) {
        int value = 0xFF << 24; // Alpha is constant (fully opaque)
        value |= ((blue * multiplier) >>> 24) << 0;
        value |= ((green * multiplier) >>> 24) << 8;
        value |= ((red * multiplier) >>> 24) << 16;

        return value;
    }

    private static boolean isHomogeneous(int[] array) {
        int first = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] != first) {
                return false;
            }
        }

        return true;
    }

    public static class ColorBuffer {
        protected final int[] data;
        protected final int width, height;

        public ColorBuffer(int width, int height) {
            this.data = new int[width * height];
            this.width = width;
            this.height = height;
        }

        public void set(int x, int y, int color) {
            this.data[getIndex(x, y, this.width)] = color;
        }

        public int get(int x, int y) {
            return this.data[getIndex(x, y, this.width)];
        }

        public static int getIndex(int x, int y, int width) {
            return (y * width) + x;
        }
    }
}
