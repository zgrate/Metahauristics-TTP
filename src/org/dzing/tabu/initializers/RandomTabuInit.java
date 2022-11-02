package org.dzing.tabu.initializers;

import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;
import org.dzing.hauristics.RandomSolver;
import org.dzing.tabu.TabuInitializer;

public class RandomTabuInit implements TabuInitializer {

    @Override
    public TTP.ItemsResponse initialize(TTP ttp, ItemChoiceAlgorithm algo) {
        return algo.selectItemsAndScore(ttp, RandomSolver.generateRandomSolution(ttp));
    }
}
