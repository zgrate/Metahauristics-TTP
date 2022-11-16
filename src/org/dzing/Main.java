package org.dzing;

import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;
import org.dzing.base.Trainer;
import org.dzing.genetic.GeneticEvolutionSolver;
import org.dzing.genetic.cross.CXCross;
import org.dzing.genetic.cross.OXCross;
import org.dzing.genetic.mutate.InverseMutate;
import org.dzing.genetic.mutate.SwapMutate;
import org.dzing.genetic.selections.RouletteSelect;
import org.dzing.genetic.selections.TournamentSelect;
import org.dzing.hauristics.GreedySolver;
import org.dzing.hauristics.RandomSolver;
import org.dzing.itemchoicealgorithms.GreedyPriceOverWeight;
import org.dzing.sa.SASolver;
import org.dzing.sa.StandardTempAlgo;
import org.dzing.tabu.TabuSolver;
import org.dzing.tabu.initializers.GreedyBestCity;
import org.dzing.tabu.initializers.RandomTabuInit;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static String BASE_DIR = "D:\\Metahauristics_Results\\";

    public static boolean ECHO = false;

    static int minPopulation = 50, maxPopulation = 450, populationStep = 100;
    static double minMutationProb = 0.1, maxMutationProb = 0.3, mutationStep = 0.05;
    static double minCrossProb = 0, maxCrossProb = 0.8, crossProbStep = 0.2;
    static int minGeneration = 50, maxGeneration = 200, generationStep = 50;
    static int minTournament = 1, maxTournament = 11, tournamentStep = 2;

    static int randomAmountRepeat = 100000;

    static int minNumberOfIteration = 200, maxNumberOfIterations = 1000, iterationStep = 200;
    static int minNumberOfNeighbours = 10, maxNumberOfNeighbours = 110, neighbourStep = 20;
    static int minTabuSteps = 100, maxTabuSteps = 500, tabuStep = 100;
    static double minTabuMutationProb = 0.05, maxTabuMutationProb = 0.6, tabuMutationStep = 0.1;


    private static void performTestForTournament(String folder, String file, boolean block, ItemChoiceAlgorithm algo) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        File folderNew = Path.of(BASE_DIR, file + "_directory_" + System.currentTimeMillis() + "_tour").toFile();
        folderNew.mkdirs();
        TTP ttp = TTP.loadTTP(Path.of(folder, file).toString());
        assert ttp != null;
//        service.submit(new Trainer(new GreedySolver(ttp, algo), ttp.cities.length, new File(folderNew, "output_greedy.csv")));
//        service.submit(new Trainer(new RandomSolver(ttp, algo), randomAmountRepeat, new File(folderNew, "output_random.csv")));

        int i = 0;
        for (int generation = minGeneration; generation < maxGeneration; generation += generationStep) {
            for (int population = minPopulation; population < maxPopulation; population += populationStep) {
                for (double mutationProb = minMutationProb; mutationProb < maxMutationProb; mutationProb += mutationStep) {
                    for (double crossProb = minCrossProb; crossProb < maxCrossProb; crossProb += crossProbStep) {
                        for (int tournament = minTournament; tournament < maxTournament; tournament += tournamentStep) {

                            service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new TournamentSelect(tournament), new InverseMutate(), new CXCross(), algo, crossProb, mutationProb * 2), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb + "_tour_inverse_cx_" + algo.getClass().getName() + "_" + tournament + "_" + generation + ".csv")));
                            service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new TournamentSelect(tournament), new SwapMutate(), new CXCross(), algo, crossProb, mutationProb), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb * 2 + "_tour_swap_cx_" + algo.getClass().getName() + "_" + tournament + "_" + generation + ".csv")));
                            service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new TournamentSelect(tournament), new InverseMutate(), new OXCross(), algo, crossProb, mutationProb * 2), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb + "_tour_inverse_ox_" + algo.getClass().getName() + "_" + tournament + "_" + generation + ".csv")));
                            service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new TournamentSelect(tournament), new SwapMutate(), new OXCross(), algo, crossProb, mutationProb), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb * 2 + "_tour_swap_ox_" + algo.getClass().getName() + "_" + tournament + "_" + generation + ".csv")));
                            i += 4;
                        }
//
                    }
                }
            }
        }
        System.out.println("STARTED " + i);
        service.shutdown();
        if (block) {
            service.awaitTermination(100, TimeUnit.DAYS);
        }

    }

    private static void performTestForRoulette(String folder, String file, boolean block, ItemChoiceAlgorithm algo) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        File folderNew = Path.of(BASE_DIR, file + "_directory_" + System.currentTimeMillis() + "_roul").toFile();
        folderNew.mkdirs();
        TTP ttp = TTP.loadTTP(Path.of(folder, file).toString());
        assert ttp != null;
//        service.submit(new Trainer(new GreedySolver(ttp, algo), ttp.cities.length, new File(folderNew, "output_greedy.csv")));
//        service.submit(new Trainer(new RandomSolver(ttp, algo), randomAmountRepeat, new File(folderNew, "output_random.csv")));

        int i = 0;
        for (int generation = minGeneration; generation < maxGeneration; generation += generationStep) {
            for (int population = minPopulation; population < maxPopulation; population += populationStep) {
                for (double mutationProb = minMutationProb; mutationProb < maxMutationProb; mutationProb += mutationStep) {
                    for (double crossProb = minCrossProb; crossProb < maxCrossProb; crossProb += crossProbStep) {

                        service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new RouletteSelect(1), new InverseMutate(), new CXCross(), algo, crossProb, mutationProb * 2), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb + "_roul_inverse_cx_" + algo.getClass().getName() + "_" + generation + ".csv")));
                        service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new RouletteSelect(1), new SwapMutate(), new CXCross(), algo, crossProb, mutationProb), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb * 2 + "_roul_swap_cx_" + algo.getClass().getName() + "_" + generation + ".csv")));
                        service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new RouletteSelect(1), new InverseMutate(), new OXCross(), algo, crossProb, mutationProb * 2), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb + "_roul_inverse_ox_" + algo.getClass().getName() + "_" + generation + ".csv")));
                        service.submit(new Trainer(new GeneticEvolutionSolver(ttp, population, new RouletteSelect(1), new SwapMutate(), new OXCross(), algo, crossProb, mutationProb), generation, new File(folderNew, population + "_" + crossProb + "_" + mutationProb * 2 + "_roul_swap_ox_" + algo.getClass().getName() + "_" + generation + ".csv")));
                        i += 4;
                    }
                }
            }
        }
        System.out.println("STARTED " + i);
        service.shutdown();
        if (block) {
            service.awaitTermination(100, TimeUnit.DAYS);
        }

    }

    private static void performTestForTabu(String folder, String file, boolean block, ItemChoiceAlgorithm item) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        File folderNew = Path.of(BASE_DIR, file + "_directory_" + System.currentTimeMillis() + "_tabu").toFile();
        folderNew.mkdirs();
        TTP ttp = TTP.loadTTP(Path.of(folder, file).toString());
        assert ttp != null;
        int i = 0;
        for (int iterations = minNumberOfIteration; iterations < maxNumberOfIterations; iterations += iterationStep) {
            for (int neighbours = minNumberOfNeighbours; neighbours < maxNumberOfNeighbours; neighbours += neighbourStep) {
                for (int tabu = minTabuSteps; tabu < maxTabuSteps; tabu += tabuStep) {
                    for (double prob = minTabuMutationProb; prob < maxTabuMutationProb; prob += tabuMutationStep) {
                        service.submit(new Trainer(new TabuSolver(new RandomTabuInit(), ttp, neighbours, new SwapMutate(), prob, item, tabu), iterations, new File(folderNew, neighbours + "_" + prob + "_" + tabu + "_random_swap_" + item.getClass().getName() + "_" + iterations + ".csv")));
                        service.submit(new Trainer(new TabuSolver(new GreedyBestCity(), ttp, neighbours, new SwapMutate(), prob, item, tabu), iterations, new File(folderNew, neighbours + "_" + prob + "_" + tabu + "_greedy_swap_" + item.getClass().getName() + "_" + iterations + ".csv")));
                        i += 2;
                    }
                    service.submit(new Trainer(new TabuSolver(new RandomTabuInit(), ttp, neighbours, new InverseMutate(), 1, item, tabu), iterations, new File(folderNew, neighbours + "_" + 1 + "_" + tabu + "_random_inverse_" + item.getClass().getName() + "_" + iterations + ".csv")));
                    service.submit(new Trainer(new TabuSolver(new GreedyBestCity(), ttp, neighbours, new InverseMutate(), 1, item, tabu), iterations, new File(folderNew, neighbours + "_" + 1 + "_" + tabu + "_greedy_inverse_" + item.getClass().getName() + "_" + iterations + ".csv")));
                    i += 2;
                }
            }
        }
        System.out.println("STARTED " + i);
        service.shutdown();
        if (block) {
            service.awaitTermination(100, TimeUnit.DAYS);
        }
//        Trainer t = new Trainer(new TabuSolver(new RandomTabuInit(), ttp, 200, new InverseMutate(), 0.3, item, 10000), 300, new File("output_tabu_test.csv"));
//        t.start();
//        t.join();
    }

    private static void performTestForSA(String folder, String file, boolean block, ItemChoiceAlgorithm itemChoiceAlgorithm) throws InterruptedException {
        TTP ttp = TTP.loadTTP(Path.of(folder, file).toString());

//        Trainer t = new Trainer(new SASolver(ttp, itemChoiceAlgorithm, 100, new RandomTabuInit(), new InverseMutate(), 1, new StandardTempAlgo(40, 400, 0.02)), 1500, new File("test.csv"));
//        t.start();
//        t.join();
//        System.exit(0);

        ExecutorService service = Executors.newCachedThreadPool();
        File folderNew = Path.of(BASE_DIR, file + "_directory_" + System.currentTimeMillis() + "_sa").toFile();
        folderNew.mkdirs();
        assert ttp != null;
        int i = 0;
        for (int generations = 100; generations < 301; generations += 100) {
            for (int neighbours = 50; neighbours < 300; neighbours += 50) {
                for (int startTemp = 1; startTemp < 50; startTemp += 10) {
                    for (double mutChance = 0.0; mutChance < 0.5; mutChance += 0.1) {
                        service.submit(new Trainer(new SASolver(ttp, itemChoiceAlgorithm, neighbours, new RandomTabuInit(), new SwapMutate(), mutChance, new StandardTempAlgo(startTemp, 1, 0.05)), generations, new File(folderNew, neighbours + "_" + mutChance + "_" + startTemp + "_random_swap_" + itemChoiceAlgorithm.getClass().getName() + "_" + generations + ".csv")));
                        service.submit(new Trainer(new SASolver(ttp, itemChoiceAlgorithm, neighbours, new GreedyBestCity(), new SwapMutate(), mutChance, new StandardTempAlgo(startTemp, 1, 0.05)), generations, new File(folderNew, neighbours + "_" + mutChance + "_" + startTemp + "_greedy_swap_" + itemChoiceAlgorithm.getClass().getName() + "_" + generations + ".csv")));

                    }
                    service.submit(new Trainer(new SASolver(ttp, itemChoiceAlgorithm, neighbours, new RandomTabuInit(), new InverseMutate(), 1, new StandardTempAlgo(startTemp, 1, 0.05)), generations, new File(folderNew, neighbours + "_" + 1 + "_" + startTemp + "_random_inverse_" + itemChoiceAlgorithm.getClass().getName() + "_" + generations + ".csv")));
                    service.submit(new Trainer(new SASolver(ttp, itemChoiceAlgorithm, neighbours, new GreedyBestCity(), new InverseMutate(), 1, new StandardTempAlgo(startTemp, 1, 0.05)), generations, new File(folderNew, neighbours + "_" + 1 + "_" + startTemp + "_greedy_inverse_" + itemChoiceAlgorithm.getClass().getName() + "_" + generations + ".csv")));

                }
            }
        }


//        service.submit(new Trainer(new SASolver(ttp, itemChoiceAlgorithm, 200, new RandomTabuInit(), new SwapMutate(), 0.2, new StandardTempAlgo(1)), 1000, new File("test.csv")));

        service.shutdown();
        if (block) {
            service.awaitTermination(100, TimeUnit.DAYS);
        }

    }

    private static void random(String file) throws InterruptedException {
        TTP ttp = TTP.loadTTP("dane\\" + file);
        Trainer t = new Trainer(new RandomSolver(ttp, new GreedyPriceOverWeight()), randomAmountRepeat, new File(file.replace(".ttp", "").replace("_", "") + "_output_random.csv"));
        t.start();
        t.join();
    }

    public static void executeAllTestsForFile(String file) throws InterruptedException {
        TTP ttp = TTP.loadTTP(Path.of("dane", file).toString());

        System.out.println("Starting " + file);
        Trainer t = new Trainer(new GreedySolver(ttp, new GreedyPriceOverWeight()), ttp.cities.length, new File(BASE_DIR, file.replace(".ttp", "").replace("_", "") + "_output_greedy.csv"));
        Trainer t2 = new Trainer(new RandomSolver(ttp, new GreedyPriceOverWeight()), randomAmountRepeat, new File(BASE_DIR, file.replace(".ttp", "").replace("_", "") + "_output_random.csv"));
        System.out.println("Generating randoms and greedy...");
        t.start();
        t2.start();
        t.join();
        t2.join();
        System.out.println("Starting SA...");

        for (int i = 0; i < 10; i++) {
            performTestForSA("dane", file, true, new GreedyPriceOverWeight());
        }
        System.out.println("Starting TABU");
        for (int i = 0; i < 10; i++) {
            performTestForTabu("dane", file, true, new GreedyPriceOverWeight());
        }
        System.out.println("Starting roulette");
        for (int i = 0; i < 10; i++) {
            performTestForRoulette("dane", file, true, new GreedyPriceOverWeight());
        }
        System.out.println("Starting tournament");
        for (int i = 0; i < 10; i++) {
            performTestForTournament("dane", file, true, new GreedyPriceOverWeight());
        }
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        executeAllTestsForFile("easy_0.ttp");
        executeAllTestsForFile("medium_0.ttp");
        executeAllTestsForFile("medium_1.ttp");
        executeAllTestsForFile("medium_2.ttp");

    }


}
