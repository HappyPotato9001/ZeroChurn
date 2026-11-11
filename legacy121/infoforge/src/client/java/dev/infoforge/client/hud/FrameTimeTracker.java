package dev.infoforge.client.hud;

/**
 * Ring buffer of recent frame times for a cheap 1% low FPS estimate.
 * Scratch sort array is reused; call only from the render thread.
 */
public final class FrameTimeTracker {
    private static final int CAPACITY = 100;

    private final long[] samplesNs = new long[CAPACITY];
    private final long[] sortScratch = new long[CAPACITY];
    private int index;
    private int filled;

    public void record(long frameTimeNs) {
        if (frameTimeNs <= 0L) {
            return;
        }
        this.samplesNs[this.index] = frameTimeNs;
        this.index++;
        if (this.index >= CAPACITY) {
            this.index = 0;
        }
        if (this.filled < CAPACITY) {
            this.filled++;
        }
    }

    /**
     * Approximates 1% lows: average of the slowest 1% of recent frames, as FPS.
     * Returns 0 when not enough samples.
     */
    public int onePercentLowFps() {
        int count = this.filled;
        if (count < 10) {
            return 0;
        }
        System.arraycopy(this.samplesNs, 0, this.sortScratch, 0, count);
        insertionSort(this.sortScratch, count);
        int slowCount = count / 100;
        if (slowCount < 1) {
            slowCount = 1;
        }
        long sum = 0L;
        for (int i = count - slowCount; i < count; i++) {
            sum += this.sortScratch[i];
        }
        long avgNs = sum / slowCount;
        if (avgNs <= 0L) {
            return 0;
        }
        return (int) (1_000_000_000L / avgNs);
    }

    private static void insertionSort(long[] values, int count) {
        for (int i = 1; i < count; i++) {
            long key = values[i];
            int j = i - 1;
            while (j >= 0 && values[j] > key) {
                values[j + 1] = values[j];
                j--;
            }
            values[j + 1] = key;
        }
    }
}
