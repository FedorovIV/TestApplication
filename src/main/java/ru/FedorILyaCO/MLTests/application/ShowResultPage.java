package ru.FedorILyaCO.MLTests.application;

import ru.FedorILyaCO.MLTests.application.logic.MyGraphs;

import javax.swing.*;
import java.awt.*;

public class ShowResultPage extends Page{

    JPanel contents = new JPanel();
    JButton btnMakeGraphs = new JButton("Построить графики");

    public JButton btnBackToMainPage = new JButton("Вернуться на главную");

    public ShowResultPage(App app) {
        super(app);
        contents.add(btnMakeGraphs);
        contents.add(btnBackToMainPage);
        addEventListeners();
    }

    private void addEventListeners(){
        btnMakeGraphs.addActionListener(e ->{

            MyGraphs GraphMaker = new MyGraphs();
            GraphMaker.makeLineGraph();
        });

        btnBackToMainPage.addActionListener(e -> {
            app.changePage(app.getPages().getMainPage());
        });
    }
    @Override
    public void setComponents() {
        app.setContentPane(contents);
    }
}
