package org.dzing.genetic.cross;

import org.dzing.genetic.base.Cross;

public class NoCrossing implements Cross {
    @Override
    public int[][] cross(int[] firstParent, int[] secondParent) {
        return new int[][]{firstParent, secondParent};
    }
}
