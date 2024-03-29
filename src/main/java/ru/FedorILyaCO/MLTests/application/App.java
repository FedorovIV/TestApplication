package ru.FedorILyaCO.MLTests.application;


import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.FedorILyaCO.MLTests.application.pages.*;
import ru.FedorILyaCO.MLTests.application.preferences.UserPreferences;
import javax.swing.*;



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
        private PageInterface choosingPathsPage = new ChoosingPathsPage(getApp());

        public PageInterface getChoosingPathPage(){
            return choosingPathsPage;
        }

        private PageInterface showResultPage = new ShowResultPage(getApp());

        public PageInterface getShowResultPage(){
            return showResultPage;
        }

        private PageInterface TestSettingsPage = new TestSettingsPage(getApp());

        public PageInterface getTestSettingsPage(){
            return TestSettingsPage;
        }

        private PageInterface byBitAPIPage = new ByBitAPIPage(getApp());

        public PageInterface getByBitAPIPage() {return byBitAPIPage;}

        private PageInterface createDataSetPage = new CreateDataSetPage(getApp());

        public PageInterface getCreateDataSetPage(){return createDataSetPage;}


    }
    public Pages getPages(){
        return pages;
    }

    public void changePage(PageInterface page){
        getContentPane().setVisible(false);
        page.setComponents();
        getContentPane().setVisible(true);

    }

    private UserPreferences up = new UserPreferences();

    public UserPreferences getUP(){
        return up;
    }

    private Logger log = LogManager.getLogger("log");
    public Logger getLog(){
        return log;
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