package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class TLog {

    /** Logs an info message into logging.txt */
    public static void info(String msg){

        try {

            String date = LocalDateTime.now().toString();
            FileWriter fileWriter = new FileWriter("src\\main\\resources\\logging.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(date + " [INFO] - " + msg + "\n");
            bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("ERROR: Problem writing to logging file");
            e.printStackTrace();
        }
    }

    /** Logs an error message into logging.txt */
    public static void error(String msg){

        try {

            String date = LocalDateTime.now().toString();
            FileWriter fileWriter = new FileWriter("src\\main\\resources\\logging.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(date + " [ERROR] - " + msg + "\n");
            bufferedWriter.close();

        } catch (IOException e) {
            System.err.println("ERROR: Problem writing to logging file");
            e.printStackTrace();
        }
    } 
}
