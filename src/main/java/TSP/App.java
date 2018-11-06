package TSP;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App extends javafx.application.Application {

    Stage primaryStage;

    @FXML
    Label filePathLabel;
    @FXML
    TextField populationSizeField;
    @FXML
    TextField numberOfGenerationsField;
    @FXML
    TextField mutationRateField;
    @FXML
    TextField extinctionPeriodField;
    @FXML
    TextField survivorsFractionField;
    @FXML
    Button findSolutionButton;
    @FXML
    ProgressBar progressBar;
    @FXML
    Button cancelButton;
    @FXML
    Label shortestPathLengthLabel;
    @FXML
    Button showReportButton;
    @FXML
    Button showSolutionButton;

    static double[][] citiesCoordinates;
    static GeneticAlgorithm algorithm;
    static Solution solution;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        AnchorPane dynamicPane = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/Layout.fxml"));
            dynamicPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Решение проблемы коммивояжера");
        Scene scene = new Scene(dynamicPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void chooseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Текстовые файлы (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extensionFilter);


        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);

        File tspData = fileChooser.showOpenDialog(null);
        if (tspData != null) {
            try {
                citiesCoordinates = readCitiesCoordinates(tspData);
                filePathLabel.setText(tspData.getPath());
                findSolutionButton.setDisable(false);
            } catch (FileNotFoundException e) {
                showErrorAlert("Ошибка", "При попытке открыть файл c координатами возникла ошибка!");
            } catch (NoSuchElementException e) {
                showErrorAlert("Ошибка", "Неверный формат файла с координатами!");
            }
        } else {
            showErrorAlert("Ошибка", "При попытке открыть файл c координатами возникла ошибка!");
        }
    }

    private void showErrorAlert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private double[][] readCitiesCoordinates(File citiesCoordinates) throws FileNotFoundException {
        Scanner sc = new Scanner(citiesCoordinates);
        sc.useLocale(Locale.US);
        int size = sc.nextInt();
        double[][] coordinates = new double[size][2];
        for (int i = 0; i < size; i++) {
            int index = sc.nextInt() - 1;
            coordinates[i][0] = sc.nextDouble();
            coordinates[i][1] = sc.nextDouble();
        }
        return coordinates;
    }

    public void showTemplate(ActionEvent actionEvent) {
        File template = new File("template.txt");
        try (FileWriter fw = new FileWriter(template)) {
            fw.write("<N - число всех городов>");
            fw.write("\r\n<1> <Координата х 1-ого города> <Координата y 1-ого города>");
            fw.write("\r\n<2> <Координата х 2-ого города> <Координата y 2-ого города>");
            fw.write("\r\n...");
            fw.write("\r\n<N> <Координата х N-ого города> <Координата y N-ого города>");
            Desktop.getDesktop().open(template);
        } catch (IOException ex) {
            showErrorAlert("Ошибка", "При попытке открыть файл c шаблоном возникла ошибка!");
        }
    }

    public void findSolution(ActionEvent actionEvent) {
        populationSizeField.setStyle("-fx-control-inner-background: white;");
        numberOfGenerationsField.setStyle("-fx-control-inner-background: white;");
        mutationRateField.setStyle("-fx-control-inner-background: white;");
        extinctionPeriodField.setStyle("-fx-control-inner-background: white;");
        survivorsFractionField.setStyle("-fx-control-inner-background: white;");
        boolean correctFormat = true;
        int populationSize = 0;
        try {
            populationSize = Integer.parseInt(populationSizeField.getText());
        } catch (NumberFormatException ex) {
            correctFormat = false;
            populationSizeField.setStyle("-fx-control-inner-background: #ff695f;");
        }
        int numberOfGenerations = 0;
        try {
            numberOfGenerations = Integer.parseInt(numberOfGenerationsField.getText());
        } catch (NumberFormatException ex) {
            correctFormat = false;
            numberOfGenerationsField.setStyle("-fx-control-inner-background: #ff695f;");
        }
        double mutationRate = 0;
        try {
            mutationRate = Double.parseDouble(mutationRateField.getText());
            if (mutationRate < 0 || mutationRate > 1) {
                correctFormat = false;
                mutationRateField.setStyle("-fx-control-inner-background: #ff695f;");
            }
        } catch (NumberFormatException ex) {
            correctFormat = false;
            mutationRateField.setStyle("-fx-control-inner-background: #ff695f;");
        }
        int extinctionPeriod = numberOfGenerations;
        try {
            extinctionPeriod = Integer.parseInt(extinctionPeriodField.getText());
        } catch (NumberFormatException ex) {
            correctFormat = false;
            extinctionPeriodField.setStyle("-fx-control-inner-background: #ff695f;");
        }
        double survivorsFraction = 1;
        try {
            survivorsFraction = Double.parseDouble(survivorsFractionField.getText());
            if (mutationRate < 0 || mutationRate > 1) {
                correctFormat = false;
                survivorsFractionField.setStyle("-fx-control-inner-background: #ff695f;");
            }
        } catch (NumberFormatException ex) {
            correctFormat = false;
            survivorsFractionField.setStyle("-fx-control-inner-background: #ff695f;");
        }
        if (correctFormat) {
            cancelButton.setDisable(false);
            algorithm = new GeneticAlgorithm(citiesCoordinates, populationSize,
                    mutationRate, numberOfGenerations,extinctionPeriod, survivorsFraction);
            Thread findingSolution = new Thread(algorithm);
            findingSolution.setDaemon(true);
            findingSolution.start();
            progressBar.setVisible(true);
            progressBar.progressProperty().bind(algorithm.progressProperty());
            algorithm.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, (WorkerStateEvent event) -> {
                solution = algorithm.getValue();
                DecimalFormat formatter = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
                shortestPathLengthLabel.setText("Длина найденного решения: " +
                        formatter.format(solution.getShortestPathLength()));
                cancelButton.setDisable(true);
                showReportButton.setDisable(false);
                showSolutionButton.setDisable(false);
            });
            algorithm.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED,
                    (WorkerStateEvent event) -> cancelButton.setDisable(true));
        } else {
            showErrorAlert("Ошибка", "Неверный формат введенных параметров!");
        }
    }

    private int getTickUnit(double value) {
        if (value <= 0) return 0;
        return (int) Math.pow(10, (int) (Math.log10(value)) - 1);
    }

    private int roundDown(double value, int tickUnit) {
        return (int) value - ((int) value % tickUnit);
    }

    private int roundUp(double value, int tickUnit) {
        return (int) value - ((int) value % tickUnit) + tickUnit;
    }

    public void cancel(ActionEvent actionEvent) {
        algorithm.cancel();
    }

    public void showReport(ActionEvent actionEvent) {
        try {
            Stage chartStage = new Stage();
            double yMax = (double) solution.getAvgPathLengthSeries().getData().get(0).getYValue();
            double yMin = solution.getShortestPathLength();
            int tickUnit = getTickUnit(yMax);
            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis(roundDown(yMin, tickUnit),
                    roundUp(yMax, tickUnit), tickUnit);
            xAxis.setLabel("Поколение");
            xAxis.setUpperBound(algorithm.getNumberOfGenerations());
            yAxis.setLabel("Длина пути");

            LineChart<Number, Number> avgPathLengthChart;avgPathLengthChart = new LineChart<>(xAxis, yAxis);
            avgPathLengthChart.setTitle("Изменение длины пути в течение работы алгоритма");
            avgPathLengthChart.getData().addAll(solution.getAvgPathLengthSeries(),
                    solution.getShortestPathLengthSeries());
            avgPathLengthChart.setCreateSymbols(false);

            Scene scene = new Scene(avgPathLengthChart, 800, 600);
            chartStage.setScene(scene);
            chartStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Ошибка", "Во время формирования отчёта возникла ошибка");
        }
    }

    public void showSolution(ActionEvent actionEvent) {
        File bestSolution = new File("Решение задачи коммивояжера.txt");
        try (FileWriter fw = new FileWriter(bestSolution)) {
            for (int vertex : solution.getShortestPath()){
                fw.write((vertex+1) + " ");
            }
            Desktop.getDesktop().open(bestSolution);
        } catch (IOException ex) {
            showErrorAlert("Ошибка", "При попытке открыть файл с решением возникла ошибка!");
        }
    }

}

