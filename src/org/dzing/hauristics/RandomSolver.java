package org.dzing.hauristics;

import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;

import java.io.Writer;
import java.util.Random;

public class RandomSolver extends Solver {

    private int[] solution;
    private TTP.ItemsResponse response;

    public static int[] generateRandomSolution(TTP ttp) {
        int[] cities = new int[ttp.getDimension()];
        for (int i = 0; i < cities.length; i++) {
            cities[i] = i;
        }
        Random random = new Random();
        for (int i = 0; i < cities.length; i++) {
            int randomIndexToSwap = random.nextInt(cities.length);
            int temp = cities[randomIndexToSwap];
            cities[randomIndexToSwap] = cities[i];
            cities[i] = temp;
        }
        return cities;
    }

    @Override
    public void init(TTP ttp, Writer debugStream) {
        solution = generateRandomSolution(ttp);
    }

    @Override
    public void step(TTP ttp, Writer debugStream, ItemChoiceAlgorithm itemChoiceAlgorithm) {
        response = itemChoiceAlgorithm.selectItems(ttp, solution);
    }

    @Override
    public TTP.ItemsResponse getBestSolutionStep() {
        return response;
    }

    @Override
    public TTP.ItemsResponse getAverageSolutionStop() {
        return response;
    }

    @Override
    public TTP.ItemsResponse getWorstSolutionStep() {
        return response;
    }

}
