package dev.zerochurn.math;

import net.minecraft.core.BlockPos;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class PooledBlockPosIterator implements Iterator<BlockPos> {
    private final int poolIndex;
    private final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    private int startX;
    private int startY;
    private int startZ;
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private int total;
    private int index;
    private boolean checkedOut;
    private boolean released;

    PooledBlockPosIterator(int poolIndex) {
        this.poolIndex = poolIndex;
    }

    int poolIndex() {
        return this.poolIndex;
    }

    boolean takeCheckout() {
        if (!this.checkedOut) {
            return false;
        }
        this.checkedOut = false;
        return true;
    }

    void reset(int startX, int startY, int startZ, int sizeX, int sizeY, int sizeZ, int total) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.total = total;
        this.index = 0;
        this.checkedOut = true;
        this.released = false;
        this.mutable.set(0, 0, 0);
    }

    @Override
    public boolean hasNext() {
        if (this.index < this.total) {
            return true;
        }
        this.release();
        return false;
    }

    @Override
    public BlockPos next() {
        if (this.index >= this.total) {
            this.release();
            throw new NoSuchElementException();
        }
        int cursor = this.index++;
        return this.mutable.set(
            BlockPosCursor.x(cursor, this.startX, this.sizeX),
            BlockPosCursor.y(cursor, this.startY, this.sizeX, this.sizeY),
            BlockPosCursor.z(cursor, this.startZ, this.sizeX, this.sizeY)
        );
    }

    private void release() {
        if (this.released) {
            return;
        }
        this.released = true;
        BlockPosPools.releaseIterator(this);
    }
}
