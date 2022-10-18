package org.dzing.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Trainer extends Thread {

    private final File outputFile;
    private Solver solver;
    private int numberOfIterations;

    public Trainer(Solver solver, int numberOfIterations, File outputFile) {
        this.solver = solver;
        this.numberOfIterations = numberOfIterations;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(outputFile)) {

            solver.setDebugStream(writer);
            solver.init();
            for (int i = 0; i < numberOfIterations; i++) {
                System.out.println("ITeration " + i + " goings");
                writer.print(i + ";");
                solver.step();
                writer.println(solver.getBestSolutionStep() + ";" + solver.getAverageSolutionScore() + ";" + solver.getWorstSolutionStep());
            }
            writer.println("" + "FINAL" + ";" + solver.getBestSolutionStep() + ";" + solver.getAverageSolutionScore() + ";" + solver.getWorstSolutionStep() + ";");
            System.out.println("FINISHED " + outputFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
