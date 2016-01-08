package main;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import retrofit.RetrofitError;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andrke on 2.12.2015.
 *
 * Example from https://github.com/influxdb/influxdb-java
 */


public class InfluxDataCollector {

    private InfluxDB influxDB;
    private String dbName = "temps";

    public InfluxDataCollector() throws InterruptedException, IOException, RetrofitError {

        String ip = "217.159.228.173";
        this.influxDB = InfluxDBFactory.connect("http://" + ip + ":8086", "temps", "temps");
        boolean influxDBstarted = false;
        do {
            Pong response;
            try {
                response = this.influxDB.ping();
                System.out.println(response);
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                // NOOP intentional
                e.printStackTrace();
            }
            Thread.sleep(100L);
        } while (!influxDBstarted);
        this.influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
        // String logs = CharStreams.toString(new InputStreamReader(containerLogsStream,
        // Charsets.UTF_8));
        System.out.println("##################################################################################");
        // System.out.println("Container Logs: \n" + logs);
        System.out.println("#  Connected to InfluxDB Version: " + this.influxDB.version() + " #");
        System.out.println("##################################################################################");

    }


    public Iterator<List<Object>> getSensorList() {
        Iterator<List<Object>> sValues = null;

        Query query = new Query("SHOW SERIES", this.dbName);
        for (QueryResult.Result result : this.influxDB.query(query).getResults())
            for (QueryResult.Series series1 : result.getSeries()) {

                sValues = series1.getValues().iterator();

            }
        return sValues;
    }

    public Iterator<List<Object>> sensorValues(String table, String sensor, String day, String type) {
        Iterator<List<Object>> sValues = null;

        //Query query = new Query(String.format("select  mean(value) from %s where sensor = '%s' and time > '2015-01-01 00:00:00' - %s group by time(1h) fill(0)", table, sensor, period), dbName);
        Query query = new Query(String.format("select  %s(value) from %s where sensor = '%s' and " +
                "time >= '%sT00:00:00Z' AND time <= '%sT23:59:00Z' group by time(1h) fill(0)", type, table, sensor, day, day), dbName);
        for (QueryResult.Result result : this.influxDB.query(query).getResults()) {
            for (QueryResult.Series series1 : result.getSeries()) {
                sValues = series1.getValues().iterator();


            }
        }
        return sValues;

    }


}

