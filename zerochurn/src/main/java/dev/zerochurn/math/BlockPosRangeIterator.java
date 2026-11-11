package dev.zerochurn.math;

import net.minecraft.core.BlockPos;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class BlockPosRangeIterator implements Iterator<BlockPos> {
    private final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int total;
    private int index;

    BlockPosRangeIterator(int startX, int startY, int startZ, int sizeX, int sizeY, int sizeZ, int total) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.total = total;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.total;
    }

    @Override
    public BlockPos next() {
        if (this.index >= this.total) {
            throw new NoSuchElementException();
        }
        int cursor = this.index++;
        return this.mutable.set(
            BlockPosCursor.x(cursor, this.startX, this.sizeX),
            BlockPosCursor.y(cursor, this.startY, this.sizeX, this.sizeY),
            BlockPosCursor.z(cursor, this.startZ, this.sizeX, this.sizeY)
        );
    }
}
