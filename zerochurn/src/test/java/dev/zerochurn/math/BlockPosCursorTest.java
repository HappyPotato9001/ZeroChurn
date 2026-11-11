package dev.zerochurn.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockPosCursorTest {
    @Test
    void walksXThenYThenZ() {
        int[][] expected = {
            {10, 20, 30},
            {11, 20, 30},
            {10, 21, 30},
            {11, 21, 30},
            {10, 20, 31},
            {11, 20, 31},
            {10, 21, 31},
            {11, 21, 31}
        };
        for (int index = 0; index < expected.length; index++) {
            assertEquals(expected[index][0], BlockPosCursor.x(index, 10, 2));
            assertEquals(expected[index][1], BlockPosCursor.y(index, 20, 2, 2));
            assertEquals(expected[index][2], BlockPosCursor.z(index, 30, 2, 2));
        }
    }
}
