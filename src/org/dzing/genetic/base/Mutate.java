package org.dzing.genetic.base;

import org.dzing.base.City;

public interface Mutate {

    void mutate(City[] toMutate, double mutationChance);

}
