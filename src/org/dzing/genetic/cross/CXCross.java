package org.dzing.genetic.cross;

import org.dzing.genetic.base.Cross;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CXCross implements Cross {

    private int[] crosser(int[] firstParent, int[] secondParent) {
        int[] newArray = Arrays.copyOf(firstParent, firstParent.length);
        for (int i = 0; i < newArray.length; i++) {
            if (firstParent[i] == secondParent[i] || (i < newArray.length - 1 && firstParent[i + 1] == secondParent[i])) {
                newArray[i] = secondParent[i];
            }

        }
        List<Integer> newGene = Arrays.stream(newArray).distinct().boxed().collect(Collectors.toList());
        for (int j : secondParent) {
            if (!newGene.contains(j)) {
                newGene.add(j);
            }
        }
        assert newGene.stream().distinct().count() == newGene.size();

        return newGene.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public int[][] cross(int[] firstParent, int[] secondParent) {
        int[][] test = new int[][]{
                crosser(firstParent, secondParent), crosser(secondParent, firstParent)
        };
        return test;


    }
}
