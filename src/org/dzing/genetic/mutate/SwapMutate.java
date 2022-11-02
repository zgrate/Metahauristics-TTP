package org.dzing.genetic.mutate;

import org.dzing.base.City;
import org.dzing.genetic.base.Mutate;

import java.util.Random;

public class SwapMutate implements Mutate {

    private final Random random = new Random();

    @Override
    public void mutate(City[] toMutate, double mutationChance) {
        for (int i = 0; i < toMutate.length; i++) {
            if (random.nextFloat() < mutationChance) {
                int swapperId = random.nextInt(toMutate.length);
                City val = toMutate[i];
                toMutate[i] = toMutate[swapperId];
                toMutate[swapperId] = val;
            }
        }
    }
}
