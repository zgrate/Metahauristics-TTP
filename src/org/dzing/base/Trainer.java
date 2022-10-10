package org.dzing.base;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Trainer extends Thread {

    private final String outputFile;
    private Solver solver;
    private ItemChoiceAlgorithm itemChooser;
    private TTP ttp;
    private int numberOfIterations;

    public Trainer(Solver solver, ItemChoiceAlgorithm itemChooser, TTP ttp, int numberOfIterations, String outputFile) {
        this.solver = solver;
        this.itemChooser = itemChooser;
        this.ttp = ttp;
        this.numberOfIterations = numberOfIterations;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            solver.init(ttp, writer);
            for (int i = 0; i < numberOfIterations; i++) {
                solver.step(ttp, writer, this.itemChooser);
                writer.println("" + i + ";" + solver.getBestSolutionStep().getCSVString() + ";" + solver.getAverageSolutionStop().getCSVString() + ";" + solver.getWorstSolutionStep().getCSVString());
            }
            writer.println("" + "FINAL" + ";" + solver.getBestSolutionStep().getCSVString() + ";" + solver.getAverageSolutionStop().getCSVString() + ";" + solver.getWorstSolutionStep().getCSVString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
