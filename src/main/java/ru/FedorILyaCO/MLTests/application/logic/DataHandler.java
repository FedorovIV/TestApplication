package ru.FedorILyaCO.MLTests.application.logic;

import org.jfree.data.time.Second;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataHandler {
    public static List<String> getStringsFromJList(JList<String> list){

        List<String> data = new ArrayList<String>();

        for(int i = 0; i< list.getModel().getSize();i++){
            data.add(list.getModel().getElementAt(i));
        }

        return data;
    }

    public static List<Path> getPathList (List<String> listFileNames, String pathDir){
        List<Path> pathList = new ArrayList<Path>();

        for (String listFileName : listFileNames) {
            pathList.add(PathMaker.getPath(pathDir, listFileName));
        }
        return pathList;
    }

    public static List<String> getPyFilesName(Path path){

        List<String> pyFilesNameList = new ArrayList<>();

        File folder = new File(path.toString());
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
               pyFilesNameList.add(listOfFile.getName());
            }
        }

        return pyFilesNameList;
    }

    public static class ByBitAPITemplate{
        String firstTicker;
        String secondTicker;
        String baseInterval;
        String dataBegin;
        String dataEnd;

        public ByBitAPITemplate(String firstTicker, String secondTicker,
        String baseInterval, String dataBegin, String dataEnd){
            this.firstTicker = firstTicker;
            this.secondTicker = secondTicker;
            this.baseInterval = baseInterval;
            this.dataBegin = dataBegin;
            this.dataEnd = dataEnd;
        }

        public String getFirstTicker(){
            return firstTicker;
        }
        public String getSecondTicker(){
            return secondTicker;
        }
        public String getBaseInterval(){
            return baseInterval;
        }
        public String getDataBegin(){
            return dataBegin;
        }
        public String getDataEnd(){
            return dataEnd;
        }
    }
}
