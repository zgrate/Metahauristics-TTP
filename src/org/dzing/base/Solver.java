package org.dzing.base;

import java.io.Writer;

public abstract class Solver {


    public abstract void init(TTP ttp, Writer debugStream);

    public abstract void step(TTP ttp, Writer debugStream, ItemChoiceAlgorithm itemChoiceAlgorithm);

    public abstract TTP.ItemsResponse getBestSolutionStep();

    public abstract TTP.ItemsResponse getAverageSolutionStop();

    public abstract TTP.ItemsResponse getWorstSolutionStep();

}
