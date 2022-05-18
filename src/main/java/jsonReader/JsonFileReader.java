package jsonReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import ticketException.TicketException;
import timeCalc.TimeCalc;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

/**
 * Class for reading {@code json} files
 */
public class JsonFileReader {
    private final JsonReader reader;

    public JsonFileReader(String path) throws IOException {
        reader = new JsonReader(new FileReader(path, StandardCharsets.UTF_8));
    }

    /**
     * returns {@code long} array of flights duration in seconds
     * of correct tickets
     * @param origin code of origin place
     * @param destination code of destination place
     * @return {@link Long} primitive
     * @throws {@link TicketException} if no tickets in file
     */
    public long[] getFlightTime(final String origin, final String destination) throws TicketException {
        JsonElement element = JsonParser.parseReader(reader).getAsJsonObject().get("tickets");
        if (element == null) {
            throw new TicketException("No \"ticket\" element in json file");
        }

        JsonArray tickets = element.getAsJsonArray();

        return IntStream.range(0, tickets.size())
                .mapToLong(i -> {
                    JsonElement jsonElement = tickets.get(i);
                    if (!checkTicket(jsonElement, origin, destination)) {
                        System.err.println("Ticket number " + (i + 1) + " is wrong");
                        return -1;
                    }

                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    String departureDate = jsonObject.get("departure_date").getAsString();
                    String departureTime = jsonObject.get("departure_time").getAsString();
                    String arrivalDate = jsonObject.get("arrival_date").getAsString();
                    String arrivalTime = jsonObject.get("arrival_time").getAsString();

                    return TimeCalc.getTime(
                            departureDate + "." + departureTime.replace(":", "."),
                            arrivalDate + "." + arrivalTime.replace(":", "."));
                })
                .filter(l -> l >= 0)
                .toArray();
    }

    private boolean checkTicket(JsonElement jsonElement, String origin, String destination) {
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject(); //
            String jsonOrigin = jsonObject.get("origin").getAsString();
            String jsonDestination = jsonObject.get("destination").getAsString();
            if (jsonOrigin == null || jsonDestination == null
                    || !jsonOrigin.equals(origin) || !jsonDestination.equals(destination)) {
                return false;
            }

            String departureDate = jsonObject.get("departure_date").getAsString();
            String departureTime = jsonObject.get("departure_time").getAsString();
            String arrivalDate = jsonObject.get("arrival_date").getAsString();
            String arrivalTime = jsonObject.get("arrival_time").getAsString();

            if (departureDate == null || departureTime == null || arrivalDate == null || arrivalTime == null) {
                return false;
            }

            if (!TimeCalc.checkTime(departureDate + "." + departureTime.replace(":", ".")) ||
                    !TimeCalc.checkTime(arrivalDate + "." + arrivalTime.replace(":", "."))) {
                return false;
            }
        } catch (ClassCastException | IllegalStateException | NullPointerException e) {
            return false;
        }

        return true;
    }
}
