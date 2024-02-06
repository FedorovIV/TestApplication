package ru.FedorILyaCO.MLTests.application.logic;

import javax.swing.*;
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
}
