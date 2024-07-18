package com.example.reflect;

import java.util.Date;

/**
 * model class which stores all values from the morning reflection table
 */
public class MorningReflectionModel {
    // all field values from the morning reflection table
    private int id; // unique identifier for each individual entry
    private Date date; // the date the given entry is for
    private int sleepScore; // the level of sleep the user received between 1-5
    private int motivationScore; // the motivated the user was between 1-5


    /**
     * getter method to retrieve the id for the given entry
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * setter method to set the id for a particular entry
     * @param id the id we want the entry to have
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter method to retrieve the date for the given entry
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * setter method to set the date for a particular entry
     * @param date the date we want the entry to have
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * getter method to retrieve the sleep score for the given entry
     * @return the sleep score
     */
    public int getSleepScore() {
        return sleepScore;
    }

    /**
     * setter method to set the sleep score for a particular entry
     * @param sleepScore the sleep score we want the entry to have
     */
    public void setSleepScore(int sleepScore) {
        this.sleepScore = sleepScore;
    }

    /**
     * getter method to retrieve the motivation score for the given entry
     * @return the motivation score
     */
    public int getMotivationScore() {
        return motivationScore;
    }

    /**
     * setter method to set the motivation score for a particular entry
     * @param motivationScore the motivation score we want the entry to have
     */
    public void setMotivationScore(int motivationScore) {
        this.motivationScore = motivationScore;
    }

    /**
     * a toString method which takes a record and displays it in string format
     * @return a String representative to the contents of a given entry
     */
    @Override
    public String toString() {
        return "MorningReflectionModel{" +
                "id=" + id +
                ", date=" + date.toString() +
                ", sleepScore=" + sleepScore +
                ", motivationScore=" + motivationScore +
                '}';
    }
}
