package timeCalc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * static class for translation time in date format to seconds
 */
public class TimeCalc {

    /**
     * returns the difference between {@code departureDateTimeString} and {@code arrivalDateTimeString}
     * in seconds
     *
     * @param departureDateTimeString departure time
     * @param arrivalDateTimeString arrival time
     * @return {@link Long} primitive of duration in seconds
     */
    public static long getTime(String departureDateTimeString, String arrivalDateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd.HH.mm");
        departureDateTimeString = normalizeTokens(departureDateTimeString);
        arrivalDateTimeString = normalizeTokens(arrivalDateTimeString);

        if (!checkTime(departureDateTimeString) || !checkTime(arrivalDateTimeString)) {
            return -1;
        }

        LocalDateTime departureDateTime = LocalDateTime.parse(departureDateTimeString, formatter);
        LocalDateTime arrivalDateTime = LocalDateTime.parse(arrivalDateTimeString, formatter);
        return ChronoUnit.SECONDS.between(departureDateTime, arrivalDateTime);
    }

    /**
     * checks the format of the {@link String}
     * for the Date format matching
     *
     * @param dateTime {@link String} to check
     * @return result of checking
     */
    public static boolean checkTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd.HH.mm");
        dateTime = normalizeTokens(dateTime);

        try {
            LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
    
    private static String normalizeTokens(String dateTime) {
        String[] tokens = dateTime.split("\\.");
        List<String> normalizedTokens = Arrays.stream(tokens)
                .map(t -> t.length() < 2
                        ? String.join("", Collections.nCopies(2 - t.length(), "0")) + t
                        : t)
                .limit(5)
                .collect(Collectors.toList());
        return String.join(".", normalizedTokens);
    }
}
