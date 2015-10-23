package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
/*
This example programe used from https://docs.oracle.com/javafx/2/charts/area-chart.htm#CIHCFGBA
 */

public class InFluxGraph extends Application {

    private double timeInHours = 0;
    final NumberAxis yAxis = new NumberAxis(0,100,10);
    final NumberAxis xAxis = new NumberAxis(0,24,3);
    final StackedAreaChart<Number, Number> sac =
            new StackedAreaChart<Number, Number>(xAxis, yAxis);


    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        final NumberAxis xAxis = new NumberAxis(0,24,3);
        final NumberAxis yAxis = new NumberAxis(0,50,10);
        final AreaChart<Number,Number> ac =
                new AreaChart<Number,Number>(xAxis,yAxis);
        ac.setTitle("Temperature Monitoring (in Degrees C)");

        InfluxDataCollector data = new InfluxDataCollector();
        Iterator<List<Object>> values = data.sensorValues("rosma_temps", "28FFE320131400D5", "1d");

        XYChart.Series series28FFE320131400D5 = new XYChart.Series();
        series28FFE320131400D5.setName("28FFE320131400D5");

        int i=0;
        while (values.hasNext()) {
            List<Object> items =  values.next();
            Iterator<Object> item = items.iterator();
            Object oDate = item.next();
            Object temp = item.next();

            Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")).parse(oDate.toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int hours = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);

            series28FFE320131400D5.getData().add(new XYChart.Data(hours, temp));
            System.out.println(String.format("Kuupäev on %s ja temp on %f",hours ,temp));
            i++;
        }


        Scene scene  = new Scene(ac,800,600);
        scene.getStylesheets().add("main/Chart.css");
        ac.getData().addAll(series28FFE320131400D5);
        stage.setScene(scene);
        stage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
