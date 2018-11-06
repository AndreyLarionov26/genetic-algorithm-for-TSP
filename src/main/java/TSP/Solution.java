package TSP;

import javafx.scene.chart.XYChart;

public class Solution {

    private XYChart.Series<Number, Number> avgPathLengthSeries;
    private XYChart.Series<Number, Number> shortestPathLengthSeries;

    private int[] shortestPath;
    private double shortestPathLength;

    public Solution(XYChart.Series<Number, Number> avgPathLengthSeries,
                    XYChart.Series<Number, Number> shortestPathLengthSeries,
                    int[] shortestPath, double shortestPathLength) {
        this.avgPathLengthSeries = avgPathLengthSeries;
        this.shortestPathLengthSeries = shortestPathLengthSeries;
        this.shortestPath = shortestPath;
        this.shortestPathLength = shortestPathLength;
    }

    public XYChart.Series<Number, Number> getAvgPathLengthSeries() {
        return avgPathLengthSeries;
    }

    public XYChart.Series<Number, Number> getShortestPathLengthSeries() {
        return shortestPathLengthSeries;
    }

    public int[] getShortestPath() {
        return shortestPath;
    }

    public double getShortestPathLength() {
        return shortestPathLength;
    }
}
