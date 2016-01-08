package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
/*
This example programe used from https://docs.oracle.com/javafx/2/charts/area-chart.htm#CIHCFGBA
DatePicker example:
http://www.java2s.com/Tutorials/Java/JavaFX/0540__JavaFX_DatePicker.htm
*/

public class InFluxGraph extends Application {

    @Override
    public void start(Stage stage) throws Exception {


        final NumberAxis xAxis = new NumberAxis(0,23,1);
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> ac =
                new LineChart<>(xAxis, yAxis);
        ac.setTitle("Temperature Monitoring (in Degrees C)");
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "mean",
                        "max",
                        "min"
                );
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue("mean");


        VBox vbox = new VBox(2);
        DatePicker datePicker = new DatePicker();

        //Kui muudetakse datePicker sisu

        datePicker.setOnAction(event -> {
            LocalDate date = datePicker.getValue();
            System.out.println("Selected date: " + date);
            try {
                PlotGraph(ac, date.toString(), comboBox.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        //Kui muudetakse compoBoxi sisu

        comboBox.setOnAction(event -> {
            LocalDate date = datePicker.getValue();
            System.out.println("Selected date: " + date);
            try {
                PlotGraph(ac, date.toString(), comboBox.getValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        });


        vbox.getChildren().add(datePicker);
        vbox.getChildren().add(comboBox);
        vbox.getChildren().add(ac);
        Scene scene = new Scene(vbox, 1200, 400);
            stage.setScene(scene);
            stage.show();


    }

    private void PlotGraph(LineChart<Number, Number> ac, String sensorDate, String queryType) throws InterruptedException, IOException, ParseException {

        InfluxDataCollector data = new InfluxDataCollector();
        Iterator<List<Object>> sensors = data.getSensorList();

        ac.getData().clear();

        while (sensors.hasNext()) {
            List<Object> items = sensors.next();
            Iterator<Object> item = items.iterator();
            Object desc = item.next();
            String sensor_name = (String) item.next();

            XYChart.Series sensorOject = new XYChart.Series();
            String[] descParts = desc.toString().split("=");
            String descName = descParts[1];
            sensorOject.setName(descName);
            Iterator<List<Object>> values = data.sensorValues("rosma_temps", sensor_name, sensorDate, queryType);

            int i = 0;
            while (values.hasNext()) {
                List<Object> sensorItems = values.next();
                Iterator<Object> sensorItem = sensorItems.iterator();
                Object oDate = sensorItem.next();
                Object temp = sensorItem.next();

                Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")).parse(oDate.toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int hours = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);
                sensorOject.getData().add(new XYChart.Data(hours, temp));
                System.out.println(String.format("Sensor: %s, Date: %s, temp on %f i=%d", sensor_name, date.toString(), temp, i));
                i++;
            }

            ac.getData().add(sensorOject);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
