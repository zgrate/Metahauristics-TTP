package org.dzing.genetic.mutate;

import org.dzing.genetic.base.Mutate;

import java.util.Random;

public class InverseMutate implements Mutate {

    private final Random random = new Random();

    private void swap(int[] array, int i1, int i2) {
        int a = array[i1];
        array[i1] = array[i2];
        array[i2] = a;
    }

    @Override
    public void mutate(int[] toMutate, double mutationChance) {
        if (random.nextFloat() < mutationChance) {
            int x1 = random.nextInt(toMutate.length);
            int x2 = random.nextInt(toMutate.length - x1) + x1;
            for (int i = 0; i < (x2 - x1) / 2 + 1; i++) {
                swap(toMutate, x1 + i, x2 - i);
            }
        }
    }
}
