package com.example.reflect;

import java.util.Date;

/**
 * utility class containing recurring values used throughout the project
 */
public class Utils {
    public static final String DATABASE_NAME = "reflectDatabase"; // the name of the database
    public static final String JOURNAL_TABLE = "journalTable"; // name of the journal table
    public static final String MORNING_REFLECTION_TABLE = "morningReflectionTable"; // the name of the morning reflection tables

    /**
     * method which takes in a date and converts it into the following format: DAYOFWEEK, XX MONTH
     * @param date the date we want to convert into a string
     * @return the date in string format
     */
    public static String getDateString(Date date){
        // get a list of days of the week
        String[] dayString = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        String[] monthString = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

        // calculate the date of the month, adding a 0 if necessary to get it to two digits
        String dateOfMonth = "" + date.getDate();
        if (dateOfMonth.length() == 1){
            dateOfMonth = "0" + dateOfMonth;
        }

        // return that date converted into a string
        return dayString[date.getDay()] + ", " + dateOfMonth + " " + monthString[date.getMonth()];
    }
}
