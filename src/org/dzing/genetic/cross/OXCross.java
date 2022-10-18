package org.dzing.genetic.cross;

import org.dzing.genetic.base.Cross;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OXCross implements Cross {
    private final Random random = new Random();

    private int[] generateOXCrossover(int[] first, int[] second) {
        int x1 = random.nextInt(first.length);
        int x2 = random.nextInt(first.length - x1) + x1;
        int[] subset = Arrays.copyOfRange(first, x1, x2);
        List<Integer> newOne = new ArrayList<>();
        assert first.length == second.length;
        for (int i = 0; i < second.length; i++) {
            if (i >= x1 && i < x2) {
                newOne.add(second[i]);
            } else {
                int result = first[i];
                if (IntStream.of(subset).noneMatch(x -> x == result)) {
                    newOne.add(result);
                }
            }
        }
        List<Integer> newGene = newOne.stream().distinct().collect(Collectors.toList());
        for (int j : first) {
            if (!newGene.contains(j)) {
                newGene.add(j);
            }
        }
        assert newGene.size() == first.length;
        assert newGene.stream().distinct().count() == newGene.size();
        return newGene.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public int[][] cross(int[] firstParent, int[] secondParent) {
        assert firstParent.length == secondParent.length;
        int[][] newCross = new int[2][];
        newCross[0] = generateOXCrossover(firstParent, secondParent);
        newCross[1] = generateOXCrossover(firstParent, secondParent);
        return newCross;
    }
}
