package org.example.service;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class LogService extends OutputStream {

    private final OutputStream out1;
    private final OutputStream out2;

    private static final String LOGS_DIR_FILE_NAME = "logs/";
    private static final Integer MAX_NUMBER_OF_LOGS = 10;
    private static final Integer NUMBER_OF_LOGS_TO_DELETE = 5;

    public static void init(){

        createLogsDir();
        clearLogs();
        setUpLog();
    }

    private static void createLogsDir() throws IllegalStateException{

        File logsDirFile = new File(LOGS_DIR_FILE_NAME);

        if(logsDirFile.exists()){
            return;
        }

        boolean wasCreated = logsDirFile.mkdir();

        if(!wasCreated){

            throw new IllegalStateException("Logs dir could not be created");
        }
    }

    private static void clearLogs() throws IllegalStateException{

        File logsDirFile = new File(LOGS_DIR_FILE_NAME);

        File[] logsFiles = logsDirFile.listFiles();

        if(logsFiles == null || logsFiles.length < MAX_NUMBER_OF_LOGS){

            return;
        }

        for(int i = 0; i < NUMBER_OF_LOGS_TO_DELETE; i++){

            File logFile = logsFiles[i];

            boolean wasDeleted = logFile.delete();

            if(!wasDeleted){

                throw new IllegalStateException("Could not remove log file " + logFile.getName());
            }
        }
    }

    private static void setUpLog(){

        PrintStream originalOut = System.out;

        LocalDateTime actualDate = LocalDateTime.now();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");

        String formattedDate = dateTimeFormatter.format(actualDate);

        FileOutputStream fileOutputStream = null;

        try {

            fileOutputStream = new FileOutputStream(LOGS_DIR_FILE_NAME + "loggs-" + formattedDate + ".log", true);
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        OutputStream outputStream = new LogService(fileOutputStream, originalOut);
        PrintStream dualOut = new PrintStream(outputStream, true);

        System.setOut(dualOut);
    }

    @Override
    public void write(int b) throws IOException {

        out1.write(b);
        out2.write(b);
    }

    @Override
    public void flush() throws IOException {

        out1.flush();
        out2.flush();
    }

    @Override
    public void close() throws IOException {

        out1.close();
        out2.close();
    }

}
