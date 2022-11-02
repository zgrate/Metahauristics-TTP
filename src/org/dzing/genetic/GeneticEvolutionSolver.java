package org.dzing.genetic;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;
import org.dzing.genetic.base.Cross;
import org.dzing.genetic.base.Mutate;
import org.dzing.genetic.base.Select;
import org.dzing.hauristics.RandomSolver;

import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

public class GeneticEvolutionSolver extends Solver {


    private final Random random;
    private final ItemChoiceAlgorithm itemChoiceAlgorithm;
    private final double crossoverChance;
    private final double mutationChance;
    private final TTP ttp;
    private final int populationSize;
    private final Select selector;
    private final Mutate mutator;
    private final Cross crosser;
    private final TTP.ItemsResponse[] currentGenScores;
    private City[][] allPopulations;
    private Writer debugStream;
    private Double[] sortedScores;

    /*
        Selekcja osobnika -> Czy krzyżujemy? Jak tak, to bierzemy kolejnego, i krzyżujemy, jeżeli nie, to mutacja i prawdopodobnieństwo, powtarzamy, aż mamy całą listę
     */

    public GeneticEvolutionSolver(TTP ttp, int populationSize, Select selector, Mutate mutator, Cross crosser, ItemChoiceAlgorithm itemChoiceAlgorithm, double crossoverChance, double mutationChance) {
        this.ttp = ttp;
        this.populationSize = populationSize;
        this.selector = selector;
        this.mutator = mutator;
        this.crosser = crosser;
        this.allPopulations = new City[populationSize][];
        this.currentGenScores = new TTP.ItemsResponse[populationSize];
        this.itemChoiceAlgorithm = itemChoiceAlgorithm;
        this.crossoverChance = crossoverChance;
        this.mutationChance = mutationChance;
        this.random = new Random();
    }

    public void setDebugStream(Writer debugStream) {
        this.debugStream = debugStream;
    }

    @Override
    public void init() {
        for (int i = 0; i < allPopulations.length; i++) {
            allPopulations[i] = RandomSolver.generateRandomSolution(ttp);
        }
        scoreAll();
    }


    private void scoreAll() {
        for (int i = 0; i < allPopulations.length; i++) {
            this.currentGenScores[i] = itemChoiceAlgorithm.selectItemsAndScore(ttp, allPopulations[i]);
        }
        sortedScores = Arrays.stream(currentGenScores).map(TTP.ItemsResponse::getCurrentResult).sorted(Comparator.reverseOrder()).toArray(Double[]::new);
//        sortedScores = Arrays.stream(currentGenScores).map(TTP.ItemsResponse::getCurrentResult).sorted(Comparator.reverseOrder()).toArray(Double[]::new);

    }


    @Override
    public void step() {
        int selected = 0;
        City[][] newPopulation = new City[populationSize][];
        while (selected < this.populationSize) {
            City[][] selectedPopulation;
            if (this.populationSize - selected == 1) {
                selectedPopulation = Solver.deepArrayCopy(selector.select(allPopulations, currentGenScores, 1));
            } else if (random.nextFloat() < crossoverChance) {
                selectedPopulation = selector.select(allPopulations, currentGenScores, 2);
                selectedPopulation = crosser.cross(selectedPopulation[0], selectedPopulation[1]);//CROSSOVERs
            } else {
                selectedPopulation = Solver.deepArrayCopy(selector.select(allPopulations, currentGenScores, 1));
            }

            for (City[] element : selectedPopulation) {
                mutator.mutate(element, this.mutationChance);
                newPopulation[selected] = element;
                selected++;
            }

        }
        this.allPopulations = newPopulation;
        scoreAll();
    }

    @Override
    public double getBestSolutionStep() {
        return this.sortedScores[0];
    }

    @Override
    public double getAverageSolutionScore() {
        return Arrays.stream(this.sortedScores).collect(Collectors.averagingDouble(Double::doubleValue));
    }

    @Override
    public double getWorstSolutionStep() {
        return this.sortedScores[sortedScores.length - 1];
    }
}
