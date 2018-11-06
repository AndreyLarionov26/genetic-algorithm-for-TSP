package TSP;


import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;

public class GeneticAlgorithm extends Task<Solution> {

    private double[][] citiesMatrix;

    private Population population;

    private int numberOfGenerations;

    private int extinctionPeriod;

    public GeneticAlgorithm(double[][] citiesCoordinates, int populationSize, double mutationRate, int numberOfGenerations,
                            int extinctionPeriod, double surviversFraction) {
        citiesMatrix = readCitiesMatrix(citiesCoordinates);
        population = new Population(populationSize, mutationRate, citiesMatrix, surviversFraction);
        this.numberOfGenerations = numberOfGenerations;
        this.extinctionPeriod = extinctionPeriod;
    }

    @Override
    protected Solution call() {
        XYChart.Series<Number, Number> avgPathLengthSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> shortestPathLengthSeries = new XYChart.Series<>();
        avgPathLengthSeries.setName("Средняя длина пути ");
        shortestPathLengthSeries.setName("Длина кратчайшего пути");
        while (population.getGenerationCounter() <= numberOfGenerations) {
            if (isCancelled()) {
                updateProgress(1, 1);
                break;
            }
            updateProgress(population.getGenerationCounter(), numberOfGenerations);
            avgPathLengthSeries.getData().add(new XYChart.Data<>(population.getGenerationCounter(),
                    -population.getGenerationAvgFitness()));
            shortestPathLengthSeries.getData().add(new XYChart.Data<>(population.getGenerationCounter(),
                    -population.getFittest().getFitness()));
            if (population.getGenerationCounter() % extinctionPeriod == 0) population.shakeUp();
            population.evolve();
            population.mutate();
        }
        return new Solution(avgPathLengthSeries, shortestPathLengthSeries,
                population.getBestSolution().chromosome, -population.getBestSolution().getFitness());
    }

    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    private double[][] readCitiesMatrix(double[][] coordinates) {
        int size = coordinates.length;
        double[][] citiesMatrix = new double[size][size];
        for (int cityIndex = 0; cityIndex < size; cityIndex++) {
            for (int neighbourIndex = cityIndex; neighbourIndex < size; neighbourIndex++) {
                citiesMatrix[cityIndex][neighbourIndex] = findDistance(
                        coordinates[cityIndex][0], coordinates[cityIndex][1],
                        coordinates[neighbourIndex][0], coordinates[neighbourIndex][1]);
                citiesMatrix[neighbourIndex][cityIndex] = citiesMatrix[cityIndex][neighbourIndex];
            }
        }
        return citiesMatrix;
    }

    private double findDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
