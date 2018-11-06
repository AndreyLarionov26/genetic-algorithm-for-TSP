package TSP;

import java.util.*;

public class Population {

    private Individual bestSolution;

    private int generationCounter;

    private double generationAvgFitness;

    private double[][] adjacencyMatrix;

    private int size;

    private double mutationRate;

    private List<Individual> generation;

    private double surviversFraction;

    public Population(int size, double mutationRate, double[][] adjacencyMatrix, double surviversFraction) {
        this.size = size;
        this.mutationRate = mutationRate;
        this.adjacencyMatrix = adjacencyMatrix;
        this.surviversFraction = surviversFraction;
        generation = new ArrayList<>(size);
        int totalFitness = 0;
        for (int i = 0; i < size; i++) {
            Individual individual = new Individual(adjacencyMatrix);
            individual.mutate();
            totalFitness += individual.getFitness();
            generation.add(individual);
        }
        generationCounter = 1;
        generationAvgFitness = ((double) totalFitness) / size;
    }

    public void mutate() {
        for (Individual i : generation) {
            if (Math.random() <= mutationRate) i.mutate();
        }
    }

    public void evolve() {
        sort();
        Individual fittestInGeneration = generation.get(generation.size() - 1);
        if (bestSolution != null) {
            if (bestSolution.getFitness() < fittestInGeneration.getFitness())
                bestSolution = new Individual(fittestInGeneration);
        } else {
            bestSolution = new Individual(fittestInGeneration);
        }
        List<Individual> nextGeneration = new ArrayList<>(size);
        nextGeneration.add(fittestInGeneration);
        for (int i = 0; i < size - 1; i++) {
            Individual firstParent = generation.get(size - size / 10 + (int) (Math.random() * size / 10));
            Individual secondParent = generation.get(size - size / 10 + (int) (Math.random() * size / 10));
            Individual child = new Individual(firstParent.crossover(secondParent), adjacencyMatrix);
            nextGeneration.add(child);
        }
        generation = nextGeneration;
        generationCounter++;
    }

    public void shakeUp(){
        int numberOfSurvivors = (int) (size* surviversFraction);
        Set<Individual> survivors = new HashSet<>(numberOfSurvivors);
        for (int i = 0; i < numberOfSurvivors; i++){
            survivors.add(generation.remove((int) (generation.size() * Math.random())));
        }
        generation = new ArrayList<>(size);
        generation.addAll(survivors);
        for (int i = 0; i < size-survivors.size(); i++){
            Individual individual = new Individual(adjacencyMatrix);
            generation.add(individual);
        }
    }

    public Individual getFittest() {
        sort();
        return generation.get(generation.size() - 1);
    }

    public Individual getBestSolution() {

        if (bestSolution != null) return bestSolution;
        else {
            bestSolution = getFittest();
            return bestSolution;
        }
    }

    public int getGenerationCounter() {
        return generationCounter;
    }

    public double getGenerationAvgFitness() {
        double totalFitness = 0;
        for (int i = 0; i < generation.size(); i++) {
            totalFitness += generation.get(i).getFitness();
        }
        generationAvgFitness = (totalFitness) / size;
        return generationAvgFitness;
    }

    private void sort() {
        Collections.sort(generation, (Individual o1, Individual o2) -> Double.compare(o1.getFitness(), o2.getFitness()));
    }


}
