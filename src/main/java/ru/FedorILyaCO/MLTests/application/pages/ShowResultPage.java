package ru.FedorILyaCO.MLTests.application.pages;

import ru.FedorILyaCO.MLTests.application.logic.*;
import ru.FedorILyaCO.MLTests.application.App;
import ru.FedorILyaCO.MLTests.application.pyExecution.PythonExecutor;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

public class ShowResultPage extends Page{

    JPanel contents = new JPanel(new VerticalLayout());
    JButton btnMakeGraphs = new JButton("Построить графики");

    JButton btnBackToMainPage = new JButton("Вернуться на главную");

    JButton btnExecutePyFiles = new JButton("Выполнить");

    public ShowResultPage(App app) {
        super(app);
        addContents();
        addEventListeners();
    }

    private void addContents(){
        contents.add(btnBackToMainPage);
        contents.add(btnMakeGraphs);
        contents.add(btnExecutePyFiles);
    }
    private void addEventListeners(){
        btnMakeGraphs.addActionListener(e ->{

            MyGraphs GraphMaker = new MyGraphs();
            GraphMaker.makeLineGraph();
        });

        btnBackToMainPage.addActionListener(e -> {
            app.changePage(app.getPages().getMainPage());
        });

        btnExecutePyFiles.addActionListener(e ->{

            try {
                List<Path> pathList = DataHandler.getPathList(app.getUP().getChosenPyFiles(),
                        app.getUP().getPathToPyFiles());
                try {
                    for (Path path : pathList){
                        new PythonExecutor();
                    }
                } catch (Exception exception){
                    createDialogWithDialogData(new DialogData("Проблема с выполнением файла"
                            , "Error"));
                }
            } catch (Exception exception){
                createDialogWithDialogData(new DialogData("Проблема с созданием путей"
                        , "Error"));
            }

        });
    }
    @Override
    public void setComponents() {
        app.setContentPane(contents);
    }
}
