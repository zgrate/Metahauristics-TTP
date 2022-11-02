package org.dzing.tabu;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;
import org.dzing.genetic.base.Mutate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
        neighbours = new TTP.ItemsResponse[1];
        neighbours[0] = initializer.initialize(ttp, itemChoiceAlgorithm);
    }

    private TTP.ItemsResponse[] generateNeighbours() {
        TTP.ItemsResponse[] neight = new TTP.ItemsResponse[numberOfNeighbours];
        TTP.ItemsResponse best = neighbours[0];
        neight[0] = best;
        for (int i = 1; i < neight.length; i++) {
//            int[] current = Arrays.stream(best.cities).mapToInt(it -> it.getId()-1).toArray();
            City[] cities = Arrays.copyOfRange(best.cities, 0, best.cities.length);
            neighborGenerator.mutate(cities, mutationChance);
            neight[i] = itemChoiceAlgorithm.selectItemsAndScore(ttp, cities);
        }
        return neight;
    }

    @Override
    public void step() {
        neighbours = generateNeighbours();
        Arrays.sort(neighbours, Comparator.comparingDouble(TTP.ItemsResponse::getCurrentResult).reversed());
        TTP.ItemsResponse currentBest = neighbours[0];

//        for (int i = 0; i < neighbours.length; i++) {
//            if(!tabuList.contains(neighbours[i]) && currentBest.getCurrentResult() < neighbours[i].getCurrentResult()){
//                currentBest = neighbours[i];
//            }
//        }
        if (tabuList.contains(currentBest)) {
            tabuList.add(currentBest);
            bestInIteration = currentBest;
        } else {
            tabuList.add(neighbours[1]);
            bestInIteration = neighbours[1];
        }
        if (tabuList.size() > maxTabuSize) {
            tabuList.remove(0);
        }
    }

    @Override
    public double getBestSolutionStep() {
        return neighbours[0].getCurrentResult();
    }

    @Override
    public double getAverageSolutionScore() {
        return Arrays.stream(neighbours).mapToDouble(TTP.ItemsResponse::getCurrentResult).average().orElseThrow();
    }

    @Override
    public double getWorstSolutionStep() {
        return neighbours[neighbours.length - 1].getCurrentResult();
    }

    public double getBestInCurrent() {
        return bestInIteration.currentResult;
    }
}
