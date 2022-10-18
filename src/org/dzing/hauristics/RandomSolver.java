package org.dzing.hauristics;

import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;

import java.util.Random;

public class RandomSolver extends Solver {

    private int[] solution;
    private TTP.ItemsResponse response;
    private TTP ttp;
    private ItemChoiceAlgorithm itemChoiceAlgorithm;

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

    public RandomSolver(TTP ttp, ItemChoiceAlgorithm itemChoiceAlgorithm) {

        this.ttp = ttp;
        this.itemChoiceAlgorithm = itemChoiceAlgorithm;
    }

    @Override
    public void init() {

    }

    @Override
    public void step() {
        solution = generateRandomSolution(ttp);
        response = itemChoiceAlgorithm.selectItemsAndScore(ttp, solution);
    }

    @Override
    public double getBestSolutionStep() {
        return response.getCurrentResult();
    }

    @Override
    public double getAverageSolutionScore() {
        return response.getCurrentResult();
    }

    @Override
    public double getWorstSolutionStep() {
        return response.currentResult;
    }

}
