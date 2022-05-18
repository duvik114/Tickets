import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import jsonReader.JsonFileReader;
import ticketException.TicketException;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Main class for reading data from input file and calculating output
 *
 * @author Nikita Beliaev
 */
public class Tickets {
    private static final String origin = "VVO";
    private static final String departure = "TLV";
    private static final String inputFile = "resources/tickets.json";
    private static final int percentileNum = 90;

    /**
     * main method to get the average time of flights and {@value percentileNum}th percentile
     * from correct tickets
     * @param inputFileName input json file with tickets
     */
    public void run(String inputFileName) {
        JsonFileReader jsonFileReader;
        try {
            jsonFileReader = new JsonFileReader(inputFileName);
        } catch (IOException e) {
            System.err.println("Cannot open input file: " + e.getMessage());
            return;
        }

        try {
            long[] flightTime = jsonFileReader.getFlightTime(origin, departure);

            if (flightTime.length == 0) {
                System.out.println("No tickets");
                return;
            }

            double average = Arrays.stream(flightTime).average().orElse(0);
            DateTimeFormatter formatterAverage = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("UTC"));
            if (average >= 86400) {
                formatterAverage = DateTimeFormatter.ofPattern("dd:HH:mm:ss").withZone(ZoneId.of("UTC"));
                average -= 86400;
            }
            Instant averageInstant = Instant.ofEpochMilli((long) (average * 1000));
            System.out.println("Average time of flight = " + formatterAverage.format(averageInstant));

            Arrays.sort(flightTime);
            int percentileIndex = (percentileNum * flightTime.length / 100) > 0
                    ? (percentileNum * flightTime.length / 100) - 1
                    : (percentileNum * flightTime.length / 100);
            long percentile = flightTime[percentileIndex];
            DateTimeFormatter formatterPercentile = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("UTC"));
            if (percentile >= 86400) {
                formatterPercentile = DateTimeFormatter.ofPattern("dd:HH:mm:ss").withZone(ZoneId.of("UTC"));
                percentile -= 86400;
            }
            Instant percentileInstant = Instant.ofEpochMilli(percentile * 1000);
            System.out.println(percentileNum + "th percentile of flight time = " + formatterPercentile.format(percentileInstant));
        } catch (TicketException e) {
            System.err.println("Error while reading input file: " + e.getMessage());
        } catch (JsonIOException e) {
            System.err.println("Cannot parse input file: " + e.getMessage());
        } catch (JsonSyntaxException | IllegalStateException e) {
            System.err.println("Wrong syntax in input file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String inputFileName = inputFile;
        if (args.length == 1 && args[0] != null) {
            inputFileName = args[0];
        }
        new Tickets().run(inputFileName);
    }
}
