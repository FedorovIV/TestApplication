package ru.FedorILyaCO.MLTests.application;

import javax.swing.*;
import java.awt.*;
import java.lang.module.Configuration;
import java.nio.file.Path;
import java.util.Collection;


public class App extends JFrame {
    private App getApp(){
        return this;
    }
    private Pages pages;
    public class Pages {
        private PageInterface mainPage = new MainPage(getApp());

        public PageInterface getMainPage(){
            return mainPage;
        }
        public PageInterface choosingPathsPage = new ChoosingPathsPage(getApp());

        public PageInterface getChoosingPathPage(){
            return choosingPathsPage;
        }

        public PageInterface showResultPage = new ShowResultPage(getApp());

        public PageInterface getShowResultPage(){
            return showResultPage;
        }
    }
    public Pages getPages(){
        return pages;
    }

    public void changePage(PageInterface page){
        getContentPane().setVisible(false);
        page.setComponents();
        getContentPane().setVisible(true);

    }

    private Configurations configurations;

    public class Configurations{
        private Path pathToPyFiles;

        public Path getPathToPyFiles(){
            return pathToPyFiles;
        }

        public void setPathToPyFiles(Path path){
            pathToPyFiles = path;
        }
        private Path pathToTempFiles;

        public Path getPathToTempFiles(){
            return pathToTempFiles;
        }

        public void setPathToTempFiles(Path path){
            pathToPyFiles = path;
        }

    }

    public Configurations getConfigurations(){
        return configurations;
    }

    public App() {

        super("MlTests");
        setStartAppConfig();
        pages = new Pages();
        changePage(pages.getMainPage());

        setVisible(true);
    }
    public static class AppSize {
        static int WIDTH = 800;
        static int HEIGHT = 600;
    }
    private void setStartAppConfig() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(AppSize.WIDTH, AppSize.HEIGHT);
    }

    public static void main(String[] Args){
        new App();
    }

}