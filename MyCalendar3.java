//Jakob Karhinen
//2/1/2022
//CS 141
//Assignment 3
//thir program prompts the user to enter a command which allows them to
//enter a date and print a corresponding calendar, display today's date,
//view the next or previous month, create an event, or print a calendar
//to a file
//it took about 8 hours to complete this

import java.util.*;
import java.io.*;

public class MyCalendar3 {
    static String[][] events = new String[12][];
    static final int SIZE = 10;
    static PrintStream ps;
    static String outputType;
    static final String OUT_CONSOLE = "C";
    static final String OUT_FILE = "F";
    public static void main(String args[]) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        for (int i = 0; i < 12; i++) {
            events[i] = new String[findDaysInMonth(i + 1)];
        }
        
        File holidays = new File("calendarEvents.txt");
        if (holidays.exists()) {
            Scanner readEvents = new Scanner(holidays);
            while (readEvents.hasNextLine()) {
                String event = readEvents.nextLine();
                addEvent(event);
            }
        }
        
        System.out.println("");
        System.out.println("Welcome to my calendar program!");
        String response = menu(console);
        String date = "0";
        String incorrect = "0";
        while (!response.equalsIgnoreCase(
                "q")) {
            outputType = OUT_CONSOLE;
            if (response.equalsIgnoreCase("e")) {
                date = enterDate();
                response = menu(console);
            } else if (response.equalsIgnoreCase("t")) {
                date = currentDate();
                response = menu(console);
            } else if (response.equalsIgnoreCase("n")) {
                if (date.equals(incorrect)) {
                    System.out.println("You need to display a calendar first.");
                    response = menu(console);
                } else {
                    int day = dayFromDate(date);
                    int month = monthFromDate(date) + 1;
                    if (month == 13) {
                        month = 1;
                    }
                    drawMonth(day, month, findDaysInMonth(month), findEmptyBoxes(month));
                    date = month + "/" + day;
                    response = menu(console);
                }
            } else if (response.equalsIgnoreCase("p")) {
                if (date.equals(incorrect)) {
                    System.out.println("You need to display a calendar first.");
                    response = menu(console);
                } else {
                    int day = dayFromDate(date);
                    int month = monthFromDate(date) - 1;
                    if (month == 0) {
                        month = 12;
                    }
                    drawMonth(day, month, findDaysInMonth(month), findEmptyBoxes(month));
                    date = month + "/" + day;
                    response = menu(console);
                }
            } else if (response.equalsIgnoreCase("ev")) {
                addEvent(createEvent(console));
                response = menu(console);
            } else if (response.equalsIgnoreCase("fp")) {
                outputType = OUT_FILE;
                System.out.println("Enter the name of the file you wish to print the calendar to");
                ps = new PrintStream(console.nextLine());
                date = enterDate();
                System.out.println("File has been created.\n");
                response = menu(console);
            } else {
                System.out.println("Please enter a valid command.");
                response = menu(console);
            }
        }
    }

//prompts the user to enter a command that displays a calendar
//returns the user's input
    public static String menu(Scanner console) {
        System.out.println("Please type a command");
        System.out.println("\t\"e\" to enter a date and display the corresponding calendar");
        System.out.println("\t\"t\" to get today's date and display today's calendar");
        System.out.println("\t\"n\" to display the next month");
        System.out.println("\t\"p\" to display the previous month");
        System.out.println("\t\"ev\" to create an event");
        System.out.println("\t\"fp\" to print a calendar to a file");
        System.out.println("\t\"q\" to quit the program");
        String response = console.nextLine();
        return response;
    }
    
    //prompts the user to enter the date and name of an event they wish to add to the calendar
    //returns their response
    public static String createEvent(Scanner console) {
        System.out.println("Please type an event. (MM/DD event_title)");
        String response = console.nextLine();
        return response;
    }

    //takes in the event and date and adds them to the events array
    public static void addEvent(String eventAndDate) {
        String date = eventAndDate.substring(0, eventAndDate.indexOf(" "));
        int day = dayFromDate(date);
        int month = monthFromDate(date);
        String event = eventAndDate.substring(eventAndDate.indexOf(" ") + 1);
        events[month - 1][day - 1] = event;
    }
    
    //finds and returns day from given date
    public static int dayFromDate(String date) {
        String d = date.substring(date.indexOf("/") + 1);
        int day = Integer.parseInt(d);
        return day;
    }

    //takes the month from the date that was typed in
    public static int monthFromDate(String date) {
        String m = date.substring(0, date.indexOf("/"));
        int month = Integer.parseInt(m);
        return month;
    }

    //displays the month and day that were given
    public static void displayDate(int month, int day) {
        printString("Month:" + month + "\n");
        printString("Day:" + day + "\n");
    }

    //given the day, month, number of days in the month, and empty boxes in the first row,
    //it prints the calendar and the tree picture on top of it.
    public static void drawMonth(int day, int month, int daysInMonth, int emptyBoxes) {
        int eventLength = 0;
        int adjustSpacing = 0;
        for (int i = 0; i < daysInMonth; i++) {
            String event = events[month - 1][i];
            if (event != null) {
                eventLength = event.length();
            }

            if (eventLength > adjustSpacing) {
                adjustSpacing = eventLength;
            }

        }
        if (adjustSpacing >= SIZE - 1) {
            adjustSpacing -= SIZE - 1;
        } else {
            adjustSpacing = 0;
        }

        drawTree(adjustSpacing);
        printString("\n");
        for (int space = 1; space <= (7 * (SIZE - 1 + adjustSpacing) + 8) / 2 - 1; space++) {
            printString(" ");
        }
        printString(month + "\n");
        for (int space = 1; space <= 2; space++) {
            printString(" ");
        }
        printString("SUN");
        days("MON", adjustSpacing);
        days("TUE", adjustSpacing);
        days("WED", adjustSpacing);
        days("THU", adjustSpacing);
        days("FRI", adjustSpacing);
        days("SAT", adjustSpacing);
        printString("\n");
        int startingNumber = 1;
        int lastNumber = 0;
        int row = 1;
        while (lastNumber < daysInMonth) {
            lastNumber = drawRow(startingNumber, daysInMonth, emptyBoxes, day, month, adjustSpacing);
            emptyBoxes = 0;
            startingNumber = lastNumber + 1;
        }
        drawEqualSigns(adjustSpacing);
        displayDate(month, day);
    }

    //prints the correct number of spaces and the day
    //above each column on the calendar
    public static void days(String day, int adjustSpacing) {
        for (int space = 1; space <= SIZE - 3 + adjustSpacing; space++) {
            printString(" ");
        }
        printString(day);
    }

    //prints a single row given the starting number, days in month, empty boxes, selected day, month, and adjustSpacing number.
    //returns last number in row
    public static int drawRow(int startingNumber, int daysInMonth, int emptyBoxes, int selectedDay, int month, int adjustSpacing) {
        int lastNumber = 0;
        int day = startingNumber;
        int correctspacing = 0;
        int boxes = 0;
        int numberedBoxes = 0;
        int recentLength = 0;
        if (startingNumber + 6 > daysInMonth) {
            numberedBoxes = (daysInMonth - startingNumber) + 1;
        } else {

            numberedBoxes = 7 - emptyBoxes;
        }

        drawEqualSigns(adjustSpacing);
        emptyBoxes(emptyBoxes, 1, adjustSpacing);
        for (int topOfRows = 0; topOfRows < numberedBoxes; topOfRows++) {
            correctspacing = Integer.toString(day).length() - 1;
            printString("| " + day);
            if (day == selectedDay) {
                printString(" *");
                for (int space = 1; space <= (SIZE - 5) - correctspacing + adjustSpacing; space++) {
                    printString(" ");
                }
            } else {
                for (int space = 1; space <= (SIZE - 3) - correctspacing + adjustSpacing; space++) {
                    printString(" ");
                }
            }
            boxes++;
            day++;
        }
        
        if (day - 1 == daysInMonth) {
            emptyBoxes(7 - boxes, 1, adjustSpacing);
        }
        
        printString("|\n");
        if (emptyBoxes > 0) {
            lastNumber = 7 - emptyBoxes;
        } else if (startingNumber >= daysInMonth - 6) {
            lastNumber = daysInMonth;
        } else {
            lastNumber = startingNumber + 6;
            if (lastNumber == daysInMonth) {
                drawEqualSigns(adjustSpacing);
            }
        }

        if (lastNumber < 7) {
            emptyBoxes(7 - lastNumber, 1, adjustSpacing);
        }

        for (int start = startingNumber - 1; start < lastNumber; start++) {
            if (events[month - 1][start] != null) {
                drawEventBox(start, month, adjustSpacing);
            } else {
                emptyBoxes(1, 1, adjustSpacing);
            }
        }
        
        if (lastNumber == daysInMonth) {
            emptyBoxes(7 - (lastNumber + 1 - startingNumber), 1, adjustSpacing);
        }

        printString("|\n");
        emptyBoxes(7, (3 * SIZE) / 5 - 3, adjustSpacing);
        if (SIZE < 9) {
            printString("|\n");
        }
        return lastNumber;
    }
    
    //draws line of equals signs. Uses adjustspacing value to adjust number of equals signs printed
    public static void drawEqualSigns(int adjustSpacing) {
        for (int equalsign = 1; equalsign <= 7 * (SIZE - 1 + adjustSpacing) + 8; equalsign++) {
            printString("=");
        }
        printString("\n");
    }

    //draws a picture of a tree given the adjustspacing number
    public static void drawTree(int adjustSpacing) {
        printString("\n");
        topOfTree(5, -2, -3, adjustSpacing);
        topOfTree(4, 4, 9, adjustSpacing);
        topOfTree(4, 6, 13, adjustSpacing);
        treeStump(adjustSpacing);
    }

    //Draws the top sections of the tree picture. It takes in numbers
    //so that it can print the right number of spaces and stars.
    public static void topOfTree(int repetitions, int spaces, int stars, int adjustSpacing) {
        int middle = (7 * (SIZE - 1 + adjustSpacing) + 8) / 2 - 1;
        for (int line = 1; line <= (repetitions); line++) {
            for (int space = 1; space <= (middle - (line * 2 + spaces)); space++) {
                printString(" ");
            }
            for (int star = 1; star <= 4 * line + stars; star++) {
                printString("*");
            }
            printString("\n");
        }
    }

    //draws the stump in the tree picture, takes in adjustspacing number so it can print the correct number
    //of spaces
    public static void treeStump(int adjustSpacing) {
        int middle = (7 * (SIZE - 1 + adjustSpacing) + 8) / 2 - 1;
        for (int line = 1; line <= 4; line++) {
            for (int space = 1; space <= middle - 2; space++) {
                printString(" ");
            }
            for (int star = 1; star <= 5; star++) {
                printString("*");
            }
            printString("\n");
        }
    }
    
    //draws a box with an event in it given the day, month, and number for adjusting the spaces
    public static void drawEventBox(int dayel, int month, int adjustSpacing) {
        int monthel = month - 1;
        int day = dayel + 1;
        printString("|" + events[monthel][dayel]);
        if (events[monthel][dayel].length() < adjustSpacing + (SIZE - 1)) {
            for (int i = 0; i < (SIZE - 1) - events[monthel][dayel].length() + adjustSpacing; i++) {
                printString(" ");
            }
        }
    }

    //prints empty boxes with the length and height that are given
    public static void emptyBoxes(int horizontal, int vertical, int adjustSpacing) {
        for (int numberAcross = 1; numberAcross <= vertical; numberAcross++) {
            for (int column = 1; column <= horizontal; column++) {
                printString("|");
                for (int space = 1; space <= SIZE - 1 + adjustSpacing; space++) {
                    printString(" ");
                }
            }
            if (vertical > 1) {
                printString("|\n");
            }
        }
    }

    //prints a calendar for the current month and day and returns the date
    public static String currentDate() {
        Calendar currentdate = Calendar.getInstance();
        drawMonth(currentdate.get(Calendar.DATE), currentdate.get(Calendar.MONTH) + 1,
                findDaysInMonth(currentdate.get(Calendar.MONTH) + 1),
                findEmptyBoxes(currentdate.get(Calendar.MONTH) + 1));
        String date = currentdate.get(Calendar.MONTH) + 1 + "/"
                + currentdate.get(Calendar.DATE);
        return date;
    }

    //prompts the user to enter a date then prints a calendar for that date
    //and returns the date
    public static String enterDate() {
        Scanner console = new Scanner(System.in);
        System.out.println("What date would you like to look at?(mm/dd) ");
        String date = console.next();
        int month = monthFromDate(date);
        int day = dayFromDate(date);
        drawMonth(day, month, findDaysInMonth(month), findEmptyBoxes(month));
        return date;
    }

    //finds and returns the correct number of empty boxes in the
    //first row of the given month
    public static int findEmptyBoxes(int selectedMonth) {
        int month = 1;
        int days = 1;
        int emptyBoxes = 0;
        while (selectedMonth >= month + 1) {
            days += findDaysInMonth(month);
            month++;
        }
        if ((days) % 7 == 0) {
            emptyBoxes = 5;
        } else if ((days - 1) % 7 == 0) {
            emptyBoxes = 6;
        } else if ((days - 2) % 7 == 0) {
            emptyBoxes = 0;
        } else if ((days - 3) % 7 == 0) {
            emptyBoxes = 1;
        } else if ((days - 4) % 7 == 0) {
            emptyBoxes = 2;
        } else if ((days - 5) % 7 == 0) {
            emptyBoxes = 3;
        } else {
            emptyBoxes = 4;
        }
        return emptyBoxes;
    }

    //calculates and returns the number of days in the month that is given
    public static int findDaysInMonth(int selectedMonth) {
        if (selectedMonth == 1 || (selectedMonth <= 7 && selectedMonth % 2 != 0)
                || (selectedMonth > 7 && selectedMonth % 2 == 0)) {
            return 31;
        } else if (selectedMonth == 2) {
            return 28;
        } else {
            return 30;
        }
    }
    
    //This method takes in and prints a string to either the console or the output file
    public static void printString(String string) {
        if (outputType.equals(OUT_CONSOLE)) {
            System.out.print(string);
        } else if (outputType.equals(OUT_FILE)) {
            ps.print(string);
        }
    }
}
