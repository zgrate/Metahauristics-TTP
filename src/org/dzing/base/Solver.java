package org.dzing.base;

import java.io.PrintWriter;
import java.util.Arrays;

public abstract class Solver {


    protected PrintWriter debugStream;

    public static int[][] deepArrayCopy(int[][] input) {
        int[][] newCopy = new int[input.length][];
        for (int i = 0; i < input.length; i++) {
            newCopy[i] = Arrays.copyOfRange(input[i], 0, input[i].length);
        }
        return newCopy;
    }

    public abstract void init();

    public abstract void step();

    public abstract double getBestSolutionStep();

    public abstract double getAverageSolutionScore();

    public abstract double getWorstSolutionStep();

    public void setDebugStream(PrintWriter debugStream) {
        this.debugStream = debugStream;
    }

}
