package ru.FedorILyaCO.MLTests.application.logic;

import java.nio.file.Path;

public class PathMaker {

    public static Path getPath(Path pathDir, Path pathFile){
        return Path.of(pathDir.toString() + "\\" +  pathFile.toString());
    }
    public static Path getPath(String pathDir, String pathFile){
        return Path.of(pathDir + "\\" + pathFile);
    }
}
