package org.dzing.tabu;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;
import org.dzing.genetic.base.Mutate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabuSolver extends Solver {


/*
sBest ← s0
bestCandidate ← s0
tabuList ← []
tabuList.push(s0)
while (not stoppingCondition())
    sNeighborhood ← getNeighbors(bestCandidate)
    bestCandidate ← sNeighborhood[0]
    for (sCandidate in sNeighborhood)
        if ( (not tabuList.contains(sCandidate)) and (fitness(sCandidate) > fitness(bestCandidate)) )
            bestCandidate ← sCandidate
        end
    end
    if (fitness(bestCandidate) > fitness(sBest))
        sBest ← bestCandidate
    end
    tabuList.push(bestCandidate)
    if (tabuList.size > maxTabuSize)
        tabuList.removeFirst()
    end
end
return sBest


 */

    List<TTP.ItemsResponse> tabuList = new ArrayList<>();

    TabuInitializer initializer;
    TTP ttp;
    int numberOfNeighbours = 10;
    Mutate neighborGenerator;
    double mutationChance;
    ItemChoiceAlgorithm itemChoiceAlgorithm;
    int maxTabuSize = 100;

    TTP.ItemsResponse[] neighbours;
    TTP.ItemsResponse bestInIteration;

    TTP.ItemsResponse globalBest;
    private TTP.ItemsResponse best;
    private TTP.ItemsResponse worst;
    private double avarage;

    public TabuSolver(TabuInitializer initializer, TTP ttp, int numberOfNeighbours, Mutate neighborGenerator, double mutationChance, ItemChoiceAlgorithm itemChoiceAlgorithm, int maxTabuSize) {
        this.initializer = initializer;
        this.ttp = ttp;
        this.numberOfNeighbours = numberOfNeighbours;
        this.neighborGenerator = neighborGenerator;
        this.mutationChance = mutationChance;
        this.itemChoiceAlgorithm = itemChoiceAlgorithm;
        this.maxTabuSize = maxTabuSize;
    }

    @Override
    public void init() {
//        neighbours = new TTP.ItemsResponse[1];
        globalBest = initializer.initialize(ttp, itemChoiceAlgorithm);
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
    public void step() {
        neighbours = generateNeighbours();
//        Arrays.sort(neighbours, Comparator.comparingDouble(TTP.ItemsResponse::getCurrentResult).reversed());
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


        TTP.ItemsResponse currentBest = best;

//        for (int i = 0; i < neighbours.length; i++) {
//            if(!tabuList.contains(neighbours[i]) && currentBest.getCurrentResult() < neighbours[i].getCurrentResult()){
//                currentBest = neighbours[i];
//            }
//        }
        if (!tabuList.contains(currentBest)) {
            tabuList.add(currentBest);
            if (currentBest.getCurrentResult() > globalBest.getCurrentResult()) {
                globalBest = currentBest;
            }
        }


//        if (tabuList.contains(currentBest)) {
//            tabuList.add(currentBest);
//            bestInIteration = currentBest;
//        } else {
//            tabuList.add(neighbours[1]);
//            bestInIteration = neighbours[1];
//        }
        if (tabuList.size() > maxTabuSize) {
            tabuList.remove(0);
        }
    }

    @Override
    public double getBestSolutionStep() {
        return this.best.getCurrentResult();
    }

    @Override
    public double getAverageSolutionScore() {
        return this.avarage;
    }

    @Override
    public double getWorstSolutionStep() {
        return this.worst.getCurrentResult();
    }

    @Override
    public double getGlobalBest() {
        return globalBest.getCurrentResult();
    }

}
