package org.dzing.genetic.cross;

import org.dzing.base.City;
import org.dzing.genetic.base.Cross;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class OXCross implements Cross {
    private final Random random = new Random();

    private City[] generateOXCrossover(City[] first, City[] second) {
        int x1 = random.nextInt(first.length);
        int x2 = random.nextInt(first.length - x1) + x1;
        City[] subset = Arrays.copyOfRange(first, x1, x2);
        List<City> newOne = new ArrayList<>();
        assert first.length == second.length;
        for (int i = 0; i < second.length; i++) {
            if (i >= x1 && i < x2) {
                newOne.add(second[i]);
            } else {
                City result = first[i];
                if (Arrays.stream(subset).noneMatch(x -> x == result)) {
                    newOne.add(result);
                }
            }
        }
        List<City> newGene = newOne.stream().distinct().collect(Collectors.toList());
        for (City j : first) {
            if (!newGene.contains(j)) {
                newGene.add(j);
            }
        }
        assert newGene.size() == first.length;
        assert newGene.stream().distinct().count() == newGene.size();
        return newGene.toArray(City[]::new);
    }

    @Override
    public City[][] cross(City[] firstParent, City[] secondParent) {
        assert firstParent.length == secondParent.length;
        City[][] newCross = new City[2][];
        newCross[0] = generateOXCrossover(firstParent, secondParent);
        newCross[1] = generateOXCrossover(firstParent, secondParent);
        return newCross;
    }
}
