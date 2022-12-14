package org.dzing.base;

import org.dzing.Main;

import java.io.*;

public class Trainer extends Thread {

    private static boolean BUFFER_MODE = true;

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
        try (PrintWriter writer = BUFFER_MODE ? new PrintWriter(new InMemoryStringBuffer(outputFile)) : new PrintWriter(outputFile);) {
            writer.print("iter;MAX;AVR;MIN;BEST");
//            if(solver instanceof TabuSolver)
//            {
//                writer.print(";BEST");
//            }
//            if(solver instanceof SASolver)
//            {
//                writer.print(";BEST;StatsBest");
//            }
            writer.println();
            solver.setDebugStream(writer);
            solver.init();
            for (int i = 0; i < numberOfIterations; i++) {
                if (Main.ECHO)
                    System.out.println("ITeration " + i + " goings");
                writer.print(i + ";");
                solver.step();
                writer.print(solver.getBestSolutionStep() + ";" + solver.getAverageSolutionScore() + ";" + solver.getWorstSolutionStep() + ";" + solver.getGlobalBest());
//                if (solver instanceof TabuSolver) {
//                    writer.print(";" + ((TabuSolver) solver).getBestInCurrent());
//                }
//                if (solver instanceof SASolver) {
//                    writer.print(";" + ((SASolver) solver).getGlobalBestSolution()+";"+((SASolver)solver).getStatsBest());
//                }
                writer.println();
            }
            writer.print("" + "FINAL" + ";" + solver.getBestSolutionStep() + ";" + solver.getAverageSolutionScore() + ";" + solver.getWorstSolutionStep() + ";" + solver.getGlobalBest());
//            if (solver instanceof TabuSolver) {
//                writer.print(";" + ((TabuSolver) solver).getBestInCurrent());
//            }
//            if (solver instanceof SASolver) {
//                writer.print(";" + ((SASolver) solver).getGlobalBestSolution()+";"+((SASolver)solver).getStatsBest());
//            }
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

    public class InMemoryStringBuffer extends Writer {
        private final File outputFile;
        StringBuffer string = new StringBuffer();

        public InMemoryStringBuffer(File outputFile) {
            this.outputFile = outputFile;
        }

        public void dumpStringToFile() throws IOException {
            System.out.println("Dumping to file...");
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                writer.print(string.toString());
            }
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            string.append(cbuf, off, len);
        }

        @Override
        public void flush() throws IOException {
            //nothign
        }

        @Override
        public void close() throws IOException {
            dumpStringToFile();
        }
    }
}
