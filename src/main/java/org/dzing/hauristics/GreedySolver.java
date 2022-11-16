package org.dzing.hauristics;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.Solver;
import org.dzing.base.TTP;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GreedySolver extends Solver {

    private final TTP ttp;
    private final Random random = new Random();
    int city = 0;
    private ItemChoiceAlgorithm choice;
    private City[] solution;
    private TTP.ItemsResponse score;

    public GreedySolver(TTP ttp, ItemChoiceAlgorithm choice) {
        this.ttp = ttp;
        this.choice = choice;
    }

    @Override
    public void init() {

    }

    @Override
    public void step() {
        solution = new City[ttp.cities.length];
        solution[0] = ttp.cities[city];
        city++;
        if (city > ttp.cities.length) {
            city = 0;
        }
        List<City> lister = Arrays.stream(ttp.cities).collect(Collectors.toList());
        lister.remove(solution[0]);
        for (int i = 1; i < ttp.cities.length; i++) {
            City currentCity = solution[i - 1];
            City shortest = null;
            double distance = 0;
            for (int j = 0; j < ttp.cities.length; j++) {
                if (lister.contains(ttp.cities[j])) {
                    if (shortest == null || (currentCity != ttp.cities[j] && ttp.distanceMatrix[currentCity.getId() - 1][j] < distance)) {
                        distance = ttp.distanceMatrix[currentCity.getId() - 1][j];
                        shortest = ttp.cities[j];
                    }
                }
            }
            if (shortest == null && lister.size() == 1) {
                shortest = lister.get(0);
            }
            solution[i] = shortest;
            lister.remove(shortest);
        }
        score = choice.selectItemsAndScore(ttp, solution);
        assert Arrays.stream(solution).distinct().count() == solution.length;
    }

    @Override

    public double getBestSolutionStep() {
        return score.getCurrentResult();
    }

    @Override
    public double getAverageSolutionScore() {
        return score.getCurrentResult();
    }

    @Override
    public double getWorstSolutionStep() {
        return score.getCurrentResult();
    }

    @Override
    public double getGlobalBest() {
        return 0;
    }

    public TTP.ItemsResponse getStepResponse() {
        return score;
    }
}
