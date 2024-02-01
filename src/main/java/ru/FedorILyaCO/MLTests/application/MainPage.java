package ru.FedorILyaCO.MLTests.application;

import ru.FedorILyaCO.MLTests.application.logic.VerticalLayout;

import javax.swing.*;
import java.awt.*;

public class MainPage extends Page{

    JPanel layout = new JPanel(new VerticalLayout());
    JButton btnGoToChoosingPathsPage = new JButton("Расположение папок с файлами");
    JButton btnGoToTestSettingsPage = new JButton("Настройка тестов");

    JButton btnGoToShowResultPage = new JButton("Показать результаты");
    public MainPage(App app) {
        super(app);
        setLayoutStartConfig();
        addComponentsOnLayout();
        addEventListeners();
    }

    private void setLayoutStartConfig(){

    }
    private void addComponentsOnLayout(){
        layout.add(btnGoToChoosingPathsPage);
        layout.add(btnGoToTestSettingsPage);
        layout.add(btnGoToShowResultPage);
    }

    private void  addEventListeners(){
        btnGoToChoosingPathsPage.addActionListener(e -> {
            app.changePage(app.getPages().getChoosingPathPage());
        });
        btnGoToShowResultPage.addActionListener(e -> {
            app.changePage(app.getPages().getShowResultPage());
        });
    }


    @Override
    public void setComponents() {
        app.setContentPane(layout);
    }
}
