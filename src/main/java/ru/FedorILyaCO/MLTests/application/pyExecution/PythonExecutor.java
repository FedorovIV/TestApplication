package ru.FedorILyaCO.MLTests.application.pyExecution;

import ru.FedorILyaCO.MLTests.application.logic.DialogData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonExecutor {

    Process process;
    public PythonExecutor(Path path) throws Exception{
        if (!isPathExist(path)) throw new FileNotFoundException();
        Process process = Runtime.getRuntime().exec("python" + " " + path.toString() );
    }

    private static boolean isPathExist(Path path) {
        boolean result = true;
        try {
            boolean exists = Files.exists(path);
            if (!exists) {
                result = false;
            }
        } catch (Exception e) {

            result = false;
        }
        return result;
    }

    private static boolean isPathExist(String stringPath) {
        boolean result = true;
        try {
            Path path = Path.of(stringPath);
            boolean exists = Files.exists(path);
            if (!exists) {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
    public static void main(String[] args) throws IOException {
        try {
            new PythonExecutor((Path.of("C:\\programing\\java\\testAPI\\TestApplication\\MyData\\PyFiles\\HelloWorld.py")));
        } catch (FileNotFoundException e) {
            System.out.println("Путь не существует");
        } catch (Exception e) {
            System.out.println("SomeProblem");
        }
    }
}