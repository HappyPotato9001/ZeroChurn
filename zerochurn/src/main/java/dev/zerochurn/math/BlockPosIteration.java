package dev.zerochurn.math;

import net.minecraft.core.BlockPos;

import java.util.Iterator;

final class BlockPosIteration implements Iterable<BlockPos> {
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int total;
    private boolean borrowed;

    BlockPosIteration(int startX, int startY, int startZ, int sizeX, int sizeY, int sizeZ, int total) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.total = total;
    }

    @Override
    public Iterator<BlockPos> iterator() {
        if (!this.borrowed) {
            this.borrowed = true;
            Iterator<BlockPos> pooled = BlockPosPools.borrowIterator(
                this.startX, this.startY, this.startZ, this.sizeX, this.sizeY, this.sizeZ, this.total
            );
            if (pooled != null) {
                return pooled;
            }
        }
        return new BlockPosRangeIterator(
            this.startX, this.startY, this.startZ, this.sizeX, this.sizeY, this.sizeZ, this.total
        );
    }
}
