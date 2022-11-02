package org.dzing.base;

import org.dzing.Main;
import org.dzing.tabu.TabuSolver;

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
                if (Main.ECHO)
                    System.out.println("ITeration " + i + " goings");
                writer.print(i + ";");
                solver.step();
                writer.print(solver.getBestSolutionStep() + ";" + solver.getAverageSolutionScore() + ";" + solver.getWorstSolutionStep());
                if (solver instanceof TabuSolver) {
                    writer.print(";" + ((TabuSolver) solver).getBestInCurrent());
                }
                writer.println();
            }
            writer.print("" + "FINAL" + ";" + solver.getBestSolutionStep() + ";" + solver.getAverageSolutionScore() + ";" + solver.getWorstSolutionStep());
            if (solver instanceof TabuSolver) {
                writer.print(";" + ((TabuSolver) solver).getBestInCurrent());
            }
            writer.println();
            writer.flush();

            System.out.println("FINISHED " + outputFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }


    }
}
