package org.dzing.genetic.selections;

import org.dzing.base.TTP;
import org.dzing.genetic.base.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TournamentSelect implements Select {

    private final int tournamentSize;
    private Random random = new Random();

    public TournamentSelect(int tournament) {
        this.tournamentSize = tournament;
    }

    public static <T> boolean contains(final T[] array, final T v) {
        for (final T e : array)
            if (e == v || v.equals(e))
                return true;
        return false;
    }

    private int[] selectOne(int[][] population, TTP.ItemsResponse[] scores) {
        int number = 0;
        double chance = 1.0 / (double) population.length;
        int[] best = null;
        double score = 0;


        List<Integer> shuffledValues = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            int index = random.nextInt(population.length);
            while (shuffledValues.contains(index)) {
                index = random.nextInt(population.length);
            }
            shuffledValues.add(index);
            if (best == null || scores[index].getCurrentResult() >= score) {
                score = scores[index].getCurrentResult();
                best = population[index];
            }
        }
        return best;
    }

    @Override
    public int[][] select(int[][] population, TTP.ItemsResponse[] scores, int selectNumber) {
        int[][] response = new int[selectNumber][];
//        response[0] = selectOne(population, scores);
        for (int i = 0; i < selectNumber; i++) {
//            while(contains(response, sel)){
//                sel = selectOne(population, scores);
//            }
            response[i] = selectOne(population, scores);
        }
        return response;
    }
}
