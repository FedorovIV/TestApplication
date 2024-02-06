package ru.FedorILyaCO.MLTests.application.pages;

import ru.FedorILyaCO.MLTests.application.App;
import ru.FedorILyaCO.MLTests.application.logic.DataHandler;
import ru.FedorILyaCO.MLTests.application.logic.DialogData;

import javax.swing.*;
import java.awt.*;

public class TestSettingsPage extends Page {
    JPanel contents = new JPanel();
    GroupLayout layout = new GroupLayout(contents);

    JButton btnBackToMainPage = new JButton("Вернуться на главную");
    JButton btnAddTestConf = new JButton("Добавить");
    JButton btnRemoveTestConf = new JButton("Удалить");
    JButton btnUpdate = new JButton("Обновить");

    JButton btnSave = new JButton("Сохранить");
    JList<String> listPyFiles = new JList<String>(new String[] {"Чай", "Кофе", "Минеральная", "Морс"});
    JList<String> listTestConf = new JList<String>(new String[] {"Hello.py", "World.py"});

    public JList<String> getListTestConf(){
        return listTestConf;
    }
    public TestSettingsPage(App app) {
        super(app);
        contents.setLayout(layout);
        setLayoutStartConfig();
        addComponentsOnLayout();
        addEventListeners();
        setPrefs();
    }

    private void setLayoutStartConfig() {
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
    }

    private void addComponentsOnLayout(){
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(btnBackToMainPage)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(listPyFiles)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(btnUpdate)
                                .addComponent(btnAddTestConf)
                        )
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(listTestConf)
                        .addComponent(btnRemoveTestConf)
                        .addComponent(btnSave)

                )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(btnBackToMainPage)
                .addGroup(layout.createParallelGroup()
                        .addComponent(listPyFiles)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnUpdate)
                                .addComponent(btnAddTestConf)
                        )
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(listTestConf)
                        .addComponent(btnRemoveTestConf)
                        .addComponent(btnSave)

                )
        );
    }
    private void addEventListeners(){
        btnBackToMainPage.addActionListener(e ->{
            app.changePage(app.getPages().getMainPage());
        });
        btnSave.addActionListener(e ->{
            app.getUP().setChosenPyFiles(DataHandler.getStringsFromJList(listTestConf));
            createDialogWithDialogData(new DialogData("Файлы выбраны", ""));
        });
    }
    private void setPrefs(){
        listTestConf.setFixedCellHeight(20);
    }
    @Override
    public void setComponents() {app.setContentPane(contents);}

}
