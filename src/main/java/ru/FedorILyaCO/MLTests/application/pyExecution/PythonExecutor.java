package ru.FedorILyaCO.MLTests.application.pyExecution;

import ru.FedorILyaCO.MLTests.application.logic.DataHandler;
import ru.FedorILyaCO.MLTests.application.logic.DialogData;
import ru.FedorILyaCO.MLTests.application.logic.PathMaker;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;
public class PythonExecutor {

    private Process process;

    public PythonExecutor() throws Exception {

    }

    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }


    public static String getResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        while ((line = errReader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    public String executeByBitAPIScript(DataHandler.ByBitAPITemplate byBitAPITemplate,
                                        Path pathToPyFiles, Path pathToTempFilesDataFrame) throws Exception {

        Path pathToByBitAPIScript = PathMaker.getPath(pathToPyFiles.toString(), "BybitAPI.py");

        if (!isPathExist(pathToByBitAPIScript))
            throw new FileNotFoundException();

        process = Runtime.getRuntime().exec("python" + " " + pathToByBitAPIScript.toString());
        //System.out.println("python" + " " + pathToByBitAPIScript.toString());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        //System.out.println(makeCommandForByBitAPIScript(byBitAPITemplate));
        writer.write(makeCommandForByBitAPIScript(byBitAPITemplate, pathToTempFilesDataFrame));
        writer.close();
        return getResults(process);
    }


    private String makeCommandForByBitAPIScript(DataHandler.ByBitAPITemplate byBitAPITemplate, Path pathToTempFilesDataFrame) {

        return "1\n" +
                byBitAPITemplate.getFirstTicker() + byBitAPITemplate.getSecondTicker() + "\n" +
                byBitAPITemplate.getBaseInterval() + "\n" +
                byBitAPITemplate.getDataBegin() + "\n" +
                byBitAPITemplate.getDataEnd() + "\n" +
                pathToTempFilesDataFrame.toString() + "\n";

    }

    public String executeCreateDataSet(DataHandler.CreateDataSetTemplate createDataSetTemplate,
                                       Path pathToPyFiles, Path pathToTempFilesDataSets) throws Exception {

        Path pathToCreateDataSetScript = PathMaker.getPath(pathToPyFiles.toString(), "CreateDataSets.py");

        if (!isPathExist(pathToCreateDataSetScript)) {
            System.out.println("lol");
            throw new FileNotFoundException();
        }


        process = Runtime.getRuntime().exec("python" + " " + pathToCreateDataSetScript.toString());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        System.out.println(makeCommandForCreateDataSet(createDataSetTemplate, pathToTempFilesDataSets));

        writer.write(makeCommandForCreateDataSet(createDataSetTemplate, pathToTempFilesDataSets));
        writer.close();
        return getResults(process);
    }

    private String makeCommandForCreateDataSet(DataHandler.CreateDataSetTemplate createDataSetTemplate,
                                               Path pathToTempFilesDataSets) {

//        return """
//                C:\\programing\\java\\testAPI\\TestApplication\\MyData\\TempData\\DataFrames\\
//                C:\\programing\\java\\testAPI\\TestApplication\\MyData\\TempData\\DataSets\\
//                5_BTCUSDT_2022_12_31_2023_1_3
//                0.35
//                0.5
//                15x4
//                0
//                60
//
//                """;

        return  createDataSetTemplate.getPathToParentDirectory() + "\\" + "\n" +
                pathToTempFilesDataSets.toString() + "\\" + "\n" +
                FilenameUtils.removeExtension(createDataSetTemplate.getNameOfDataSetFile()) + "\n" +
                createDataSetTemplate.getDtp() + "\n" +
                createDataSetTemplate.getDls() + "\n" +
                createDataSetTemplate.getRule() + "\n" +
                createDataSetTemplate.getOneOrZeroInDependForTypeConsOrPar() + "\n" +
                createDataSetTemplate.getY_Time();
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


}

