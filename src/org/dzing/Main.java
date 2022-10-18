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
import org.dzing.hauristics.RandomSolver;
import org.dzing.itemchoicealgorithms.GreedyPriceOverWeight;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    static int minPopulation = 50, maxPopulation = 450, populationStep = 100;
    static double minMutationProb = 0.1, maxMutationProb = 0.3, mutationStep = 0.05;
    static double minCrossProb = 0, maxCrossProb = 0.8, crossProbStep = 0.2;
    static int minGeneration = 50, maxGeneration = 200, generationStep = 50;
    static int minTournament = 1, maxTournament = 11, tournamentStep = 2;

    static int randomAmountRepeat = 100000;

    private static void performTest(String folder, String file, boolean block, ItemChoiceAlgorithm algo) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(6);
        File folderNew = new File("D:\\Metahauristics_Results\\" + file + "_directory_" + System.currentTimeMillis() + "_tour");
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
        ExecutorService service = Executors.newFixedThreadPool(4);
        File folderNew = new File("D:\\Metahauristics_Results\\" + file + "_directory_" + System.currentTimeMillis() + "_roul");
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

    private static void random(String file) throws InterruptedException {
        TTP ttp = TTP.loadTTP("dane\\" + file);
        Trainer t = new Trainer(new RandomSolver(ttp, new GreedyPriceOverWeight()), randomAmountRepeat, new File(file.replace(".ttp", "").replace("_", "") + "_output_random.csv"));
        t.start();
        t.join();
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        TTP ttp = TTP.loadTTP("dane\\medium_0.ttp");
//        random("easy_0.ttp");
//        random("easy_1.ttp");
//        random("easy_2.ttp");
//        random("hard_0.ttp");
//        random("medium_0.ttp");
//        random("trivial_0.ttp");
//        System.exit(0);
//        for(int i = 0; i < 10; i++) {
//            performTestForRoulette("dane", "trivial_0.ttp", true, new GreedyPriceOverWeight());
//        }
//        for(int i = 0; i < 10; i++) {
//            performTestForRoulette("dane", "easy_1.ttp", true, new GreedyPriceOverWeight());
//        }
//        for(int i = 0; i < 10; i++) {
//            performTestForRoulette("dane", "medium_0.ttp", true, new GreedyPriceOverWeight());
//        }
//        for(int i = 0; i < 10; i++) {
//            performTestForRoulette("dane", "easy_0.ttp", true, new GreedyPriceOverWeight());
//        }
        for (int i = 0; i < 8; i++) {
            performTest("dane", "easy_0.ttp", true, new GreedyPriceOverWeight());
        }
        System.exit(0);
        assert ttp != null;

//        Trainer trainer = new Trainer (new GeneticEvolutionSolver(ttp, 100, new RouletteSelect(), new SwapMutate(), new CXCross(), new GreedyPriceOverWeight(), 0.7, 0.1), 100, "output_genetic.csv");
        Trainer trainer2 = new Trainer(new GeneticEvolutionSolver(ttp, 100, new TournamentSelect(2), new InverseMutate(), new CXCross(), new GreedyPriceOverWeight(), 0.7, 0.6), 1000, new File("output_genetic_no_items_tournament.csv"));
//        Trainer trainer = new Trainer(new GeneticEvolutionSolver(ttp, 100, new RouletteSelect(10), new SwapMutate(), new CXCross(), new GreedyPriceOverWeight(), 0.7, 0.05), 100, new File("output_genetic_no_items_roulette.csv"));
//        Trainer trainer = new Trainer(new GreedySolver(ttp, new GreedyPriceOverWeight()), 100, "output_greedy.csv");
//        trainer.start();
//        trainer2.start();
//        trainer.join();
//        trainer2.join();
        System.exit(0);

//
//        for (int index = 0; index < 1; index++) {
////                TTP.ItemsResponse response = ttp.calculateFunctionValueWithGreedyItemSelection(cities);s
//            Trainer trainer = new Trainer(new RandomSolver(ttp, new GreedyWithTravelCostNextCity(0.5)), 1, "output" + System.currentTimeMillis() + ".csv");
//            trainer.start();
//        }

    }


}
