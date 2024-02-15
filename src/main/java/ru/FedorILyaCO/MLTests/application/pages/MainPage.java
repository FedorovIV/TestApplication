package ru.FedorILyaCO.MLTests.application.pages;

import ru.FedorILyaCO.MLTests.application.App;
import ru.FedorILyaCO.MLTests.application.logic.VerticalLayout;

import javax.swing.*;
import java.awt.*;

public class MainPage extends Page {

    JPanel contents = new JPanel();
    JPanel grid = new JPanel(new GridLayout(0, 1, 5, 10));
    JButton btnGoToChoosingPathsPage = new JButton("Расположение папок с файлами");
    JButton btnGoToTestSettingsPage = new JButton("Настройка тестов");
    JButton btnGoToByBitAPIPage = new JButton("ByBitAPI");
    JButton btnGoToCreateDataSet = new JButton("Создать DataSet");
    JButton btnGoToShowResultPage = new JButton("Показать результаты");

    public MainPage(App app) {
        super(app);
        setContentsStartConfig();
        addContents();
        addEventListeners();
    }

    private void setContentsStartConfig(){

        contents.setLayout(new FlowLayout(FlowLayout.LEFT));
        contents.add(grid);
    }
    private void addContents(){
        grid.add(btnGoToChoosingPathsPage);
        grid.add(btnGoToTestSettingsPage);
        grid.add(btnGoToByBitAPIPage);
        grid.add(btnGoToCreateDataSet);
        grid.add(btnGoToShowResultPage);
    }

    private void  addEventListeners(){
        btnGoToChoosingPathsPage.addActionListener(e -> {
            app.changePage(app.getPages().getChoosingPathPage());
        });
        btnGoToTestSettingsPage.addActionListener(e ->{
            app.changePage(app.getPages().getTestSettingsPage());
        });
        btnGoToByBitAPIPage.addActionListener(e -> {
            app.changePage(app.getPages().getByBitAPIPage());
        });
        btnGoToCreateDataSet.addActionListener(e -> {
            app.changePage((app.getPages().getCreateDataSetPage()));
        });
        btnGoToShowResultPage.addActionListener(e -> {
            app.changePage(app.getPages().getShowResultPage());
        });
    }


    @Override
    public void setComponents() {
        app.setContentPane(contents);
    }
}
