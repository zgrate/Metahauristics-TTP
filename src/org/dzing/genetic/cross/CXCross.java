package org.dzing.genetic.cross;

import org.dzing.base.City;
import org.dzing.genetic.base.Cross;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CXCross implements Cross {

    private City[] crosser(City[] firstParent, City[] secondParent) {
        City[] newArray = Arrays.copyOf(firstParent, firstParent.length);
        for (int i = 0; i < newArray.length; i++) {
            if (firstParent[i] == secondParent[i] || (i < newArray.length - 1 && firstParent[i + 1] == secondParent[i])) {
                newArray[i] = secondParent[i];
            }

        }
        List<City> newGene = Arrays.stream(newArray).distinct().collect(Collectors.toList());
        for (City j : secondParent) {
            if (!newGene.contains(j)) {
                newGene.add(j);
            }
        }
        assert newGene.stream().distinct().count() == newGene.size();

        return newGene.toArray(City[]::new);
    }

    @Override
    public City[][] cross(City[] firstParent, City[] secondParent) {
        City[][] test = new City[][]{
                crosser(firstParent, secondParent), crosser(secondParent, firstParent)
        };
        return test;


    }
}
