package dev.zerochurn.math;

/** Vanilla {@code BlockPos.betweenClosed} order: X fastest, then Y, then Z. */
public final class BlockPosCursor {
    private BlockPosCursor() {
    }

    public static int x(int index, int startX, int sizeX) {
        return startX + index % sizeX;
    }

    public static int y(int index, int startY, int sizeX, int sizeY) {
        return startY + (index / sizeX) % sizeY;
    }

    public static int z(int index, int startZ, int sizeX, int sizeY) {
        return startZ + (index / sizeX) / sizeY;
    }
}
