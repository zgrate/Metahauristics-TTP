package org.dzing.hybrids.gaclones;

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
import java.util.Random;

public class GeneticEvolutionSolverWithStagnationIncrease extends Solver {


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
    private int generationStagnationLength;
    private double stagnationMargin;
    private double randomChance;

    private int generation = 0;
    private int index = 0;
    //    private Queue<Double> lastScores;
    private double[] lastScores;

    private TTP.ItemsResponse globalBest;


    /*
        Selekcja osobnika -> Czy krzyżujemy? Jak tak, to bierzemy kolejnego, i krzyżujemy, jeżeli nie, to mutacja i prawdopodobnieństwo, powtarzamy, aż mamy całą listę
     */

    public GeneticEvolutionSolverWithStagnationIncrease(TTP ttp, int populationSize, Select selector, Mutate mutator, Cross crosser, ItemChoiceAlgorithm itemChoiceAlgorithm, double crossoverChance, double mutationChance, int generationStagnationLength, double stagnationMargin, double randomChance) {
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
        this.generationStagnationLength = generationStagnationLength;
        this.stagnationMargin = stagnationMargin;
        this.randomChance = randomChance;
        this.random = new Random();
        this.lastScores = new double[this.generationStagnationLength];
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
        this.lastScores[index] = best.getCurrentResult();
        index++;
        if (index >= this.lastScores.length) {
            index = 0;
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
        this.generation++;

        boolean stagnation = false;
        double lastAvarage = Arrays.stream(this.lastScores).average().orElse(0);
        if (this.generation > 100 && this.bestScore != null && Math.abs(lastAvarage - this.bestScore.getCurrentResult()) < this.stagnationMargin) {
            stagnation = true;
        }
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
                mutator.mutate(element, stagnation ? this.mutationChance * 2 : this.mutationChance);
                newPopulation[selected] = element;
                selected++;
            }
            if (stagnation && selected < this.populationSize && random.nextFloat() < randomChance) {
                newPopulation[selected] = RandomSolver.generateRandomSolution(ttp);
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
