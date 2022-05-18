import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class Tester {
    private Tickets tickets;
    private String inputFile;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setTickets() {
        tickets = new Tickets();
        inputFile = "";
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void test01_empty() {
        inputFile += "test01_empty.json";
        tickets.run(inputFile);
        assertEquals("", outContent.toString());
        assertEquals("Wrong syntax in input file: Not a JSON Object: null",
                errContent.toString().substring(0, Math.max(0, errContent.toString().length() - 2)));
    }

    @Test
    public void test02_noTickets() {
        inputFile += "test02_noTickets.json";
        tickets.run(inputFile);
        assertEquals("", outContent.toString());
        assertEquals("Error while reading input file: No \"ticket\" element in json file",
                errContent.toString().substring(0, Math.max(0, errContent.toString().length() - 2)));
    }

    @Test
    public void test03_emptyTickets() {
        inputFile += "test03_emptyTickets.json";
        tickets.run(inputFile);
        assertEquals("No tickets",
                outContent.toString().substring(0, Math.max(0, outContent.toString().length() - 2)));
        assertEquals("",
                errContent.toString().substring(0, Math.max(0, errContent.toString().length() - 2)));
    }

    @Test
    public void test04_correctTicket() {
        inputFile += "test04_correctTicket.json";
        tickets.run(inputFile);
        String[] strings = outContent.toString()
                .substring(0, Math.max(0, outContent.toString().length() - 2))
                .split("\\r?\\n");
        assertEquals("Average time of flight = 00:00:00", strings[0]);
        assertEquals("90th percentile of flight time = 00:00:00", strings[1]);
        assertEquals("",
                errContent.toString().substring(0, Math.max(0, errContent.toString().length() - 2)));
    }

    @Test
    public void test05_correctTicket() {
        inputFile += "test05_correctTicket.json";
        tickets.run(inputFile);
        String[] strings = outContent.toString()
                .substring(0, Math.max(0, outContent.toString().length() - 2))
                .split("\\r?\\n");
        assertEquals("Average time of flight = 22:59:00", strings[0]);
        assertEquals("90th percentile of flight time = 22:59:00", strings[1]);
        assertEquals("",
                errContent.toString().substring(0, Math.max(0, errContent.toString().length() - 2)));
    }

    @Test
    public void test06_correctTicket() {
        inputFile += "test06_correctTicket.json";
        tickets.run(inputFile);
        String[] strings = outContent.toString()
                .substring(0, Math.max(0, outContent.toString().length() - 2))
                .split("\\r?\\n");
        assertEquals("Average time of flight = 03:02:16:00", strings[0]);
        assertEquals("90th percentile of flight time = 03:02:16:00", strings[1]);
        assertEquals("",
                errContent.toString().substring(0, Math.max(0, errContent.toString().length() - 2)));
    }

    @Test
    public void test07_manyCorrectTickets() {
        inputFile += "test07_manyCorrectTickets.json";
        tickets.run(inputFile);
        String[] strings = outContent.toString()
                .substring(0, Math.max(0, outContent.toString().length() - 2))
                .split("\\r?\\n");
        assertEquals("Average time of flight = 02:07:01:45", strings[0]);
        assertEquals("90th percentile of flight time = 12:28:00", strings[1]);
        assertEquals("",
                errContent.toString().substring(0, Math.max(0, errContent.toString().length() - 2)));
    }

    @Test
    public void test08_manyIncorrectTickets() {
        inputFile += "test08_manyIncorrectTickets.json";
        tickets.run(inputFile);
        String[] strings = outContent.toString()
                .substring(0, Math.max(0, outContent.toString().length() - 2))
                .split("\\r?\\n");
        assertEquals("Average time of flight = 23:32:00", strings[0]);
        assertEquals("90th percentile of flight time = 23:32:00", strings[1]);
        assertEquals("Ticket number 1 is wrongTicket number 2 is wrongTicket number 3 is wrong",
                errContent.toString().replaceAll("\\r?\\n", ""));
    }

    @Test
    public void test09_manyTickets() {
        inputFile += "test09_manyTickets.json";
        tickets.run(inputFile);
        String[] strings = outContent.toString()
                .substring(0, Math.max(0, outContent.toString().length() - 2))
                .split("\\r?\\n");
        assertEquals("Average time of flight = 01:00:00:00", strings[0]);
        assertEquals("90th percentile of flight time = 01:00:00:00", strings[1]);
        assertEquals("", errContent.toString().replaceAll("\\r?\\n", ""));
    }
}