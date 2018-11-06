package TSP;

import java.util.*;

public class Individual {

    double[][] matrix;

    private double fitness;

    int[] chromosome;

    public Individual(int[] chromosome, double[][] matrix) {
        this.chromosome = chromosome;
        this.matrix = matrix;
        fitness = calculateFitness();
    }

    public Individual(Individual cloneSource) {
        this.matrix = cloneSource.matrix;
        this.fitness = cloneSource.fitness;
        this.chromosome = cloneSource.chromosome;
    }

    public double getFitness() {
        fitness = calculateFitness();
        return fitness;
    }

    public int[] crossover(Individual mate) {
        int[] childChromosome = new int[chromosome.length];
        List<Set<Integer>> edgeMap = new ArrayList<>(chromosome.length);
        for (int i = 0; i < chromosome.length; i++) {
            edgeMap.add(new HashSet<>());
        }
        for (int geneIndex = 0; geneIndex < chromosome.length; geneIndex++) {
            if (geneIndex > 0) {
                edgeMap.get(this.chromosome[geneIndex]).add(this.chromosome[geneIndex - 1]);
                edgeMap.get(mate.chromosome[geneIndex]).add(mate.chromosome[geneIndex - 1]);
            } else {
                edgeMap.get(this.chromosome[geneIndex]).add(this.chromosome[this.chromosome.length - 1]);
                edgeMap.get(mate.chromosome[geneIndex]).add(mate.chromosome[mate.chromosome.length - 1]);
            }
            if (geneIndex < chromosome.length - 1) {
                edgeMap.get(this.chromosome[geneIndex]).add(this.chromosome[geneIndex + 1]);
                edgeMap.get(mate.chromosome[geneIndex]).add(mate.chromosome[geneIndex + 1]);
            } else {
                edgeMap.get(this.chromosome[geneIndex]).add(this.chromosome[0]);
                edgeMap.get(mate.chromosome[geneIndex]).add(mate.chromosome[0]);
            }
        }
        Set<Integer> selectedGenes = new TreeSet<>();
        childChromosome[0] = 0;
        selectedGenes.add(0);
        for (int nextGeneIndex = 1; nextGeneIndex < childChromosome.length; nextGeneIndex++) {
            for (int i = 0; i < edgeMap.size(); i++) {
                edgeMap.get(i).remove(childChromosome[nextGeneIndex - 1]);
            }
            List<Integer> possibleNextGenes = new ArrayList<>(edgeMap.get(childChromosome[nextGeneIndex - 1]));
            Collections.sort(possibleNextGenes,
                    (Integer gene1, Integer gene2) -> edgeMap.get(gene1).size() - edgeMap.get(gene2).size());
            int nextGene;
            if (possibleNextGenes.size() > 0) {
                nextGene = possibleNextGenes.get(0);
            } else {
                nextGene = 0;
                for (int i = 0; i < edgeMap.size(); i++){
                    if (!selectedGenes.contains(i)) {
                        nextGene = i;
                        break;
                    }
                }
                for (int i = nextGene; i < edgeMap.size(); i++) {
                    if (edgeMap.get(i).size() > 0 && edgeMap.size() < edgeMap.get(nextGene).size()) {
                        nextGene = i;
                    }
                }
            }
            childChromosome[nextGeneIndex] = nextGene;
            selectedGenes.add(nextGene);
        }
        return childChromosome;
    }

    public void mutate() {
        int firstIndex = 1 + (int) (Math.random() * chromosome.length - 1);
        int secondIndex = firstIndex;
        while (firstIndex == secondIndex) {
            secondIndex = 1 + (int) (Math.random() * chromosome.length - 1);
        }
        int temp = chromosome[firstIndex];
        chromosome[firstIndex] = chromosome[secondIndex];
        chromosome[secondIndex] = temp;
    }

    public Individual(double[][] matrix) {
        this.matrix = matrix;
        chromosome = new int[matrix.length];
        for (int geneIndex = 0; geneIndex < chromosome.length; geneIndex++) {
            chromosome[geneIndex] = geneIndex;
        }
        fitness = calculateFitness();
    }

    private double calculateFitness() {
        double routeLength = 0;
        for (int geneIndex = 0; geneIndex < chromosome.length - 1; geneIndex++) {
            routeLength += matrix[chromosome[geneIndex]][chromosome[geneIndex + 1]];
        }
        routeLength += matrix[chromosome[chromosome.length - 1]][0];
        return -routeLength;
    }
}

