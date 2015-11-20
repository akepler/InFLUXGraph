package main;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by andrke on 2.10.2015.
 */
public class InfluxDataCollector {

    private InfluxDB influxDB;
    private String dbName = "temps";

    public InfluxDataCollector() throws InterruptedException, IOException {

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


    public QueryResult series() {
        Query query = new Query("SHOW SERIES", this.dbName);
        QueryResult result = this.influxDB.query(query);
        return result;
    }

    public Iterator<List<Object>> sensorValues(String table, String sensor, String period) {
        Iterator<List<Object>> sValues = null;

        Query query = new Query(String.format("select  value from %s where sensor = '%s' and time > now() - '%s'", table, sensor, period), dbName);
        Iterator<QueryResult.Result> results = this.influxDB.query(query).getResults().iterator();
        while (results.hasNext()) {
            ListIterator<QueryResult.Series> series = results.next().getSeries().listIterator();
            while (series.hasNext()) {
                Iterator<List<Object>> values = series.next().getValues().iterator();

                sValues = values;


//                while (values.hasNext()) {
//                    List<Object> items =  values.next();
//                    Iterator<Object> item = items.iterator();
//                    Object date = item.next();
//                    Object temp = item.next();
//                    System.out.println(String.format("Kuup�ev on %s ja temp on %f", date,temp));
//                    }

            }
        }
        return sValues;

    }


}

