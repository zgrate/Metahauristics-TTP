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
import java.util.Random;

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
    private final TTP.ItemsResponse[] allPopulations;
    //    private City[][] allPopulations;
    private Writer debugStream;
    //    private Double[] sortedScores;
    private TTP.ItemsResponse bestScore, worstScore;
    private double average;
    ;


    private TTP.ItemsResponse globalBest;

    /*
        Selekcja osobnika -> Czy krzyżujemy? Jak tak, to bierzemy kolejnego, i krzyżujemy, jeżeli nie, to mutacja i prawdopodobnieństwo, powtarzamy, aż mamy całą listę
     */

    public GeneticEvolutionSolver(TTP ttp, int populationSize, Select selector, Mutate mutator, Cross crosser, ItemChoiceAlgorithm itemChoiceAlgorithm, double crossoverChance, double mutationChance) {
        this.ttp = ttp;
        this.populationSize = populationSize;
        this.selector = selector;
        this.mutator = mutator;
        this.crosser = crosser;
        this.allPopulations = new TTP.ItemsResponse[populationSize];
//        this.currentGenScores = new TTP.ItemsResponse[populationSize];
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

        City[][] cities = new City[allPopulations.length][];
        for (int i = 0; i < allPopulations.length; i++) {
            cities[i] = RandomSolver.generateRandomSolution(ttp);
        }

        generateScores(cities);

    }

    private void generateScores(City[][] scores) {
        TTP.ItemsResponse best = null, worst = null;
        double sum = 0;
        for (int i = 0; i < scores.length; i++) {
            TTP.ItemsResponse score = itemChoiceAlgorithm.selectItemsAndScore(ttp, scores[i]);
            this.allPopulations[i] = score;
            if (best == null || score.getCurrentResult() > best.getCurrentResult()) {
                best = score;
            } else if (worst == null || score.getCurrentResult() < worst.getCurrentResult()) {
                worst = score;
            }
            sum += score.getCurrentResult();
        }
        this.average = sum / scores.length;
        this.bestScore = best;
        this.worstScore = worst;
        if (this.globalBest == null || this.bestScore.getCurrentResult() > this.globalBest.getCurrentResult()) {
            this.globalBest = this.bestScore;
        }

    }

//    private void invalid_scoreAll() {
//        for (int i = 0; i < allPopulations.length; i++) {
//            this.currentGenScores[i] = itemChoiceAlgorithm.selectItemsAndScore(ttp, allPopulations[i]);
//        }
//        sortedScores = Arrays.stream(currentGenScores).map(TTP.ItemsResponse::getCurrentResult).sorted(Comparator.reverseOrder()).toArray(Double[]::new);
//        globalStatistic = sortedScores[0];
////        sortedScores = Arrays.stream(currentGenScores).map(TTP.ItemsResponse::getCurrentResult).sorted(Comparator.reverseOrder()).toArray(Double[]::new);
//
//    }


    @Override
    public void step() {
        int selected = 0;
        City[][] newPopulation = new City[populationSize][];
        while (selected < this.populationSize) {
            City[][] selectedPopulation;
            if (this.populationSize - selected == 1) {
                selectedPopulation = Solver.deepArrayCopy(selector.select(allPopulations, 1));
            } else if (random.nextFloat() < crossoverChance) {
                selectedPopulation = selector.select(allPopulations, 2);
                selectedPopulation = crosser.cross(selectedPopulation[0], selectedPopulation[1]);//CROSSOVERs
            } else {
                selectedPopulation = Solver.deepArrayCopy(selector.select(allPopulations, 1));
            }

            for (City[] element : selectedPopulation) {
                mutator.mutate(element, this.mutationChance);
                newPopulation[selected] = element;
                selected++;
            }

        }
        generateScores(newPopulation);
//        scoreAll();
    }

    @Override
    public double getBestSolutionStep() {
        return this.bestScore.getCurrentResult();
    }

    @Override
    public double getAverageSolutionScore() {
        return this.average;
    }

    @Override
    public double getWorstSolutionStep() {
        return this.worstScore.getCurrentResult();
    }

    @Override
    public double getGlobalBest() {
        return this.globalBest.getCurrentResult();
    }
}
