package org.dzing.hauristics;

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
    private int[] solution;
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
        solution = new int[ttp.cities.length];
        solution[0] = city++;
        if (city > ttp.cities.length) {
            city = 0;
        }
        debugStream.write(city + ";");
        List<Integer> lister = Arrays.stream(ttp.cities).map(it -> it.getId() - 1).collect(Collectors.toList());
        lister.remove((Integer) solution[0]);
        for (int i = 1; i < ttp.cities.length; i++) {
            int currentCity = solution[i - 1];
            int shortest = -1;
            double distance = 0;
            for (int j = 0; j < ttp.cities.length; j++) {
                if (lister.contains((Integer) j)) {
                    if (shortest == -1 || (currentCity != j && ttp.distanceMatrix[currentCity][j] < distance)) {
                        distance = ttp.distanceMatrix[currentCity][j];
                        shortest = j;
                    }
                }
            }
            if (shortest == -1 && lister.size() == 1) {
                shortest = lister.get(0);
            }
            solution[i] = shortest;
            lister.remove((Integer) shortest);
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
}
