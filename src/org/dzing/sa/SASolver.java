package org.dzing.sa;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;
import org.dzing.genetic.base.Mutate;
import org.dzing.tabu.TabuInitializer;

import java.util.Arrays;

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

    private double avarage;
    private TTP.ItemsResponse best, worst;

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
            int repeat = 0;
            while (Arrays.equals(best.cities, cities) && repeat++ < 100) {
                neighborGenerator.mutate(cities, mutationChance);
            }
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
        TTP.ItemsResponse best = null, worst = null;
        double sum = 0;
        for (TTP.ItemsResponse neighbour : neighbours) {
            sum += neighbour.getCurrentResult();
            if (best == null || best.getCurrentResult() < neighbour.getCurrentResult()) {
                best = neighbour;
            }
            if (worst == null || worst.getCurrentResult() > neighbour.getCurrentResult()) {
                worst = neighbour;
            }
        }
        this.best = best;
        this.worst = worst;
        this.avarage = sum / neighbours.length;

//        Arrays.sort(neighbours, Comparator.comparingDouble(TTP.ItemsResponse::getCurrentResult).reversed());
        TTP.ItemsResponse currentBest = best;
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
        return this.best.getCurrentResult();
    }

    @Override
    public double getAverageSolutionScore() {
        return this.avarage;
//        return Arrays.stream(neighbours).mapToDouble(TTP.ItemsResponse::getCurrentResult).average().orElseThrow();

    }

    @Override
    public double getWorstSolutionStep() {
        return this.worst.getCurrentResult();
    }

    @Override
    public double getGlobalBest() {
        return statsBest.currentResult;
    }

}
