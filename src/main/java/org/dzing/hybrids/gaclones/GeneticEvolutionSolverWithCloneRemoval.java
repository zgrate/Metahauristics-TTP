package org.dzing.hybrids.gaclones;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;
import org.dzing.genetic.base.Cross;
import org.dzing.genetic.base.Mutate;
import org.dzing.genetic.base.Select;
import org.dzing.hauristics.RandomSolver;
import org.dzing.tabu.initializers.GreedyBestCity;

import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GeneticEvolutionSolverWithCloneRemoval extends Solver {


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
    List<ArrayWrapper> acceptedCities = new LinkedList();
    //    private City[][] allPopulations;
    private Writer debugStream;
    //    private Double[] sortedScores;
    private TTP.ItemsResponse bestScore, worstScore;
    private double average;
    private int cloneRemovalTrials;
    private boolean greedyStart = false;

    /*
        Selekcja osobnika -> Czy krzyżujemy? Jak tak, to bierzemy kolejnego, i krzyżujemy, jeżeli nie, to mutacja i prawdopodobnieństwo, powtarzamy, aż mamy całą listę
     */
    private TTP.ItemsResponse globalBest;

    public GeneticEvolutionSolverWithCloneRemoval(TTP ttp, int populationSize, Select selector, Mutate mutator, Cross crosser, ItemChoiceAlgorithm itemChoiceAlgorithm, double crossoverChance, double mutationChance, int cloneRemovalTrials) {
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
        this.cloneRemovalTrials = cloneRemovalTrials;
        this.random = new Random();
    }

    public void setDebugStream(Writer debugStream) {
        this.debugStream = debugStream;
    }

    @Override
    public void init() {

        City[][] cities = new City[allPopulations.length][];
        if (greedyStart) {
            TTP.ItemsResponse res = new GreedyBestCity().initialize(this.ttp, this.itemChoiceAlgorithm);
            for (int i = 0; i < allPopulations.length; i++) {
                cities[i] = res.getCities();
            }
        } else {
            for (int i = 0; i < allPopulations.length; i++) {
                cities[i] = RandomSolver.generateRandomSolution(ttp);
            }
        }
        generateScores(Arrays.stream(cities).toList());
    }

    private void generateScores(List<City[]> scores) {
        TTP.ItemsResponse best = null, worst = null;
        double sum = 0;
        for (int i = 0; i < scores.size(); i++) {
            TTP.ItemsResponse score = itemChoiceAlgorithm.selectItemsAndScore(ttp, scores.get(i));
            this.allPopulations[i] = score;
            if (best == null || score.getCurrentResult() > best.getCurrentResult()) {
                best = score;
            } else if (worst == null || score.getCurrentResult() < worst.getCurrentResult()) {
                worst = score;
            }
            sum += score.getCurrentResult();
        }
        this.average = sum / scores.size();
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

        acceptedCities.clear();
//            int selected = acceptedCities.size();
        while (acceptedCities.size() < populationSize) {
            City[][] selectedPopulation;
            if (this.populationSize - acceptedCities.size() == 1) {
                selectedPopulation = Solver.deepArrayCopy(selector.select(allPopulations, 1));
            } else if (random.nextFloat() < crossoverChance) {
                selectedPopulation = selector.select(allPopulations, 2);
                selectedPopulation = crosser.cross(selectedPopulation[0], selectedPopulation[1]);//CROSSOVERs
            } else {
                selectedPopulation = Solver.deepArrayCopy(selector.select(allPopulations, 1));
            }
            for (City[] element : selectedPopulation) {
                ArrayWrapper wrapper = new ArrayWrapper(element);
                mutator.mutate(element, this.mutationChance);
                if (acceptedCities.contains(wrapper)) {
                    int repeat = 0;
                    while (acceptedCities.contains(wrapper) && repeat < 3) {
                        mutator.mutate(element, this.mutationChance);
                        repeat++;
                    }
                    if (acceptedCities.contains(wrapper)) {
                        acceptedCities.add(new ArrayWrapper(RandomSolver.generateRandomSolution(ttp)));
                    } else {
                        acceptedCities.add(wrapper);
                    }
                } else {
                    acceptedCities.add(wrapper);
                }

            }
//            acceptedCities = acceptedCities.stream().distinct().collect(Collectors.toList());
        }

        generateScores(acceptedCities.stream().map(ArrayWrapper::getWrapped).collect(Collectors.toList()));
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

    static class ArrayWrapper {
        private City[] wrapped;

        public ArrayWrapper(City[] wrapped) {
            this.wrapped = wrapped;
        }

        public City[] getWrapped() {
            return wrapped;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrayWrapper that = (ArrayWrapper) o;
            return Arrays.equals(wrapped, that.wrapped);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(wrapped);
        }
    }

    class Wrapper {
        City[] cities;

        public Wrapper(City[] cities) {
            this.cities = cities;
        }

        public City[] getCities() {
            return cities;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Wrapper wrapper = (Wrapper) o;
            return Arrays.equals(cities, wrapper.cities);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(cities);
        }
    }
}
