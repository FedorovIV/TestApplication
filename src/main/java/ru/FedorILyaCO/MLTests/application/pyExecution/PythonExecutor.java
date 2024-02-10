package ru.FedorILyaCO.MLTests.application.pyExecution;

import ru.FedorILyaCO.MLTests.application.logic.DataHandler;
import ru.FedorILyaCO.MLTests.application.logic.DialogData;
import ru.FedorILyaCO.MLTests.application.logic.PathMaker;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonExecutor {

    private Process process;
    public PythonExecutor() throws Exception{

    }

    public String executeByBitAPIScript(DataHandler.ByBitAPITemplate byBitAPITemplate,
                                      Path pathToPyFiles) throws Exception{

        Path pathToByBitAPIScript = PathMaker.getPath(pathToPyFiles.toString(), "BybitAPI.py");

        if (!isPathExist(pathToByBitAPIScript))
            throw new FileNotFoundException();

        process = Runtime.getRuntime().exec("python" + " " + pathToByBitAPIScript.toString());
        System.out.println("python" + " " + pathToByBitAPIScript.toString());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        System.out.println(makeCommandForByBitAPIScript(byBitAPITemplate));
        writer.write(makeCommandForByBitAPIScript(byBitAPITemplate));
        writer.close();
        return getResults(process);
    }

    private String makeCommandForByBitAPIScript(DataHandler.ByBitAPITemplate byBitAPITemplate){

        return "1\n" +
                byBitAPITemplate.getFirstTicker() + byBitAPITemplate.getSecondTicker() + "\n" +
                byBitAPITemplate.getBaseInterval() + "\n" +
                byBitAPITemplate.getDataBegin() + "\n" +
                byBitAPITemplate.getDataEnd() + "\n";
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

    public static String getResults(Process process) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
    public static void main(String[] args) throws Exception {
        new PythonExecutor().executeByBitAPIScript
                (null, Path.of("C:\\programing\\java\\testAPI\\TestApplication\\MyData\\PyFiles"));

    }
}