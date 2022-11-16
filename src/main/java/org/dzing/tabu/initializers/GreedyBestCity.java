package org.dzing.tabu.initializers;

import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;
import org.dzing.hauristics.GreedySolver;
import org.dzing.tabu.TabuInitializer;

public class GreedyBestCity implements TabuInitializer {


    @Override
    public TTP.ItemsResponse initialize(TTP ttp, ItemChoiceAlgorithm algo) {
        GreedySolver solver = new GreedySolver(ttp, algo);
        solver.init();
        TTP.ItemsResponse bestScore = null;
        for (int i = 0; i < ttp.cities.length; i++) {
            solver.step();
            TTP.ItemsResponse score = solver.getStepResponse();
            if (bestScore == null || bestScore.getCurrentResult() < score.getCurrentResult()) {
                bestScore = score;
            }
        }
        return bestScore;

    }
}
