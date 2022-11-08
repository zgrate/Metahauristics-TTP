package org.dzing.sa;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;
import org.dzing.genetic.base.Mutate;
import org.dzing.tabu.TabuInitializer;

import java.util.Arrays;
import java.util.Comparator;

public class SASolver extends Solver {


    private TTP ttp;
    private ItemChoiceAlgorithm itemChoiceAlgorithm;
    private int numberOfNeighbours;
    private TabuInitializer initializer;
    private TTP.ItemsResponse[] neighbours;
    private Mutate neighborGenerator;
    private double mutationChance;
    private TTP.ItemsResponse globalBest;
    private TTP.ItemsResponse statsBest;
    private TemperatureAlgo temperatureAlgo;

    public SASolver(TTP ttp, ItemChoiceAlgorithm itemChoiceAlgorithm, int numberOfNeighbours, TabuInitializer initializer, Mutate neighborGenerator, double mutationChance, TemperatureAlgo temperatureAlgo) {

        this.ttp = ttp;
        this.itemChoiceAlgorithm = itemChoiceAlgorithm;
        this.numberOfNeighbours = numberOfNeighbours;


        this.initializer = initializer;
        this.neighborGenerator = neighborGenerator;
        this.mutationChance = mutationChance;
        this.temperatureAlgo = temperatureAlgo;
    }

    private TTP.ItemsResponse[] generateNeighbours() {
        TTP.ItemsResponse[] neight = new TTP.ItemsResponse[numberOfNeighbours];
        TTP.ItemsResponse best = globalBest;
        for (int i = 0; i < neight.length; i++) {
//            int[] current = Arrays.stream(best.cities).mapToInt(it -> it.getId()-1).toArray();
            City[] cities = Arrays.copyOfRange(best.cities, 0, best.cities.length);
//            while(Arrays.equals(best.cities, cities)) {
            neighborGenerator.mutate(cities, mutationChance);
//            }
            neight[i] = itemChoiceAlgorithm.selectItemsAndScore(ttp, cities);
        }
        return neight;
    }

    @Override
    public void init() {
        this.globalBest = initializer.initialize(ttp, itemChoiceAlgorithm);
    }

    @Override
    public void step() {
        neighbours = generateNeighbours();
        Arrays.sort(neighbours, Comparator.comparingDouble(TTP.ItemsResponse::getCurrentResult).reversed());
        TTP.ItemsResponse currentBest = neighbours[0];
        if (statsBest == null || currentBest.currentResult > statsBest.currentResult) {
            statsBest = currentBest;
        }
        if (globalBest == null || currentBest.currentResult >= globalBest.currentResult) {
//            System.out.println("new best " + currentBest.getCurrentResult());
            globalBest = currentBest;
        } else if (temperatureAlgo.temperatureChance(ttp, globalBest, currentBest)) {
//            System.out.println("SEtting " + currentBest.getCurrentResult() + " from "+ globalBest.getCurrentResult() );
            globalBest = currentBest;
        }
//        else{
//            System.out.println("no new best");
//        }
    }

    @Override
    public double getBestSolutionStep() {
        return globalBest.getCurrentResult();
    }

    @Override
    public double getAverageSolutionScore() {
        return Arrays.stream(neighbours).mapToDouble(TTP.ItemsResponse::getCurrentResult).average().orElseThrow();
    }

    @Override
    public double getWorstSolutionStep() {
        return neighbours[neighbours.length - 1].getCurrentResult();
    }

    @Override
    public double getGlobalBest() {
        return statsBest.currentResult;
    }


    public double getStatsBest() {
        return statsBest.currentResult;
    }

    public double getGlobalBestSolution() {
        return globalBest.currentResult;
    }
}
