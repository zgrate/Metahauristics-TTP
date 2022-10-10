package org.dzing;

import org.dzing.base.TTP;
import org.dzing.base.Trainer;
import org.dzing.hauristics.RandomSolver;
import org.dzing.itemchoicealgorithms.GreedyWithTravelCostNextCity;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        TTP ttp = TTP.loadTTP("dane\\easy_0.ttp");
        assert ttp != null;


        for (int index = 0; index < 1; index++) {
//                TTP.ItemsResponse response = ttp.calculateFunctionValueWithGreedyItemSelection(cities);s
            Trainer trainer = new Trainer(new RandomSolver(), new GreedyWithTravelCostNextCity(0.5), ttp, 1, "output" + System.currentTimeMillis() + ".csv");
            trainer.start();
        }

    }


}
