package org.dzing.genetic.cross;

import org.dzing.base.City;
import org.dzing.genetic.base.Cross;

public class NoCrossing implements Cross {
    @Override
    public City[][] cross(City[] firstParent, City[] secondParent) {
        return new City[][]{firstParent, secondParent};
    }
}
