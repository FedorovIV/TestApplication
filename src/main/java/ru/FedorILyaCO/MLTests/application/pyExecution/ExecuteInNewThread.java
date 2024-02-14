package ru.FedorILyaCO.MLTests.application.pyExecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteInNewThread implements Runnable {
    private Process process;

    public int number;

    public ExecuteInNewThread(int number){
        this.number = number;
    }
    @Override
    public void run() {
        try {
            process = Runtime.getRuntime().exec("python "+"C:\\programing\\java\\testAPI\\TestApplication\\MyData\\PyFiles\\testProcess.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while (true) {
                try {
                    if (!((line = reader.readLine()) != null)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
