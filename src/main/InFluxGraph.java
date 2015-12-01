package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
/*
This example programe used from https://docs.oracle.com/javafx/2/charts/area-chart.htm#CIHCFGBA
*/

public class InFluxGraph extends Application {

    @Override
    public void start(Stage stage) throws Exception {


        final NumberAxis xAxis = new NumberAxis(0,23,1);
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> ac =
                new LineChart<Number, Number>(xAxis, yAxis);
        ac.setTitle("Temperature Monitoring (in Degrees C)");


        InfluxDataCollector data = new InfluxDataCollector();
        Iterator<List<Object>> sensors = data.getSensorList();

        while (sensors.hasNext()) {
            List<Object> items = sensors.next();
            Iterator<Object> item = items.iterator();
            Object desc = item.next();
            String sensor_name = (String) item.next();
            System.out.println(sensor_name);


            XYChart.Series sensorOject = new XYChart.Series();
            sensorOject.setName(sensor_name);
            Iterator<List<Object>> values = data.sensorValues("rosma_temps", sensor_name, "1d");

            int i = 0;
            while (values.hasNext()) {
                System.out.println(i);
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
                System.out.println(String.format("Sensor: %s Kell %s:%s ja temp on %f",sensor_name, hours, minutes, temp));
                i++;
            }

            ac.getData().add(sensorOject);
        }


            Scene scene = new Scene(ac, 800, 600);
            stage.setScene(scene);
            stage.show();

    }





    public static void main(String[] args) {
        launch(args);
    }
}
