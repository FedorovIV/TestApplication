package ru.FedorILyaCO.MLTests.application.pages;

import org.apache.log4j.Level;
import ru.FedorILyaCO.MLTests.application.App;
import ru.FedorILyaCO.MLTests.application.logic.DataHandler;
import ru.FedorILyaCO.MLTests.application.logic.DialogData;
import ru.FedorILyaCO.MLTests.application.logic.VerticalLayout;
import ru.FedorILyaCO.MLTests.application.preferences.UserPreferences;
import ru.FedorILyaCO.MLTests.application.pyExecution.ExecuteInNewThread;
import ru.FedorILyaCO.MLTests.application.pyExecution.PythonExecutor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ByBitAPIPage extends Page {

    JPanel contents = new JPanel();
    JPanel group = new JPanel();
    GroupLayout layout = new GroupLayout(group);
    JLabel labelPageName = new JLabel("API BYBIT. Загрузить DataFrame");
    JButton btnBackToMainPage = new JButton("Вернуться на главную");
    JLabel labelChoseTicker = new JLabel("Выберите Ticker");
    JTextField textFieldFirstTicker = new JTextField(10);
    JTextField textFieldSecondTicker = new JTextField(10);
    JLabel labelBaseInterval = new JLabel("Базовый интервал");
    JTextField textFieldBaseInterval = new JTextField(15);
    JLabel labelBeginData = new JLabel("Дата начала");
    JLabel labelEndData = new JLabel("Дата конца");
    JTextField textFieldBeginData = new JTextField(10);
    JTextField textFieldEndData = new JTextField(10);
    JButton btnSaveTemplate = new JButton("Сохранить");
    JButton btnRemoveTemplate = new JButton("Удалить");

    JButton btnExecute = new JButton("Выполнить");
    DefaultListModel<String> dataListOfTemplate = new DefaultListModel<>();
    JList<String> listOfTemplate = new JList<String>(dataListOfTemplate);
    List<DataHandler.ByBitAPITemplate> byBitAPITemplateList =
            new ArrayList<>();


    public ByBitAPIPage(App app) {
        super(app);
        contents.setLayout(new FlowLayout(FlowLayout.LEFT));
        contents.add(group);
        setLayoutStartConfig();
        addComponents();
        addEventListeners();
        setPrefs();
    }

    private void addComponents() {

        group.setLayout(layout);
        JPanel containerTextFieldTicker = new JPanel(new FlowLayout(FlowLayout.LEFT));
        containerTextFieldTicker.add(textFieldFirstTicker);
        containerTextFieldTicker.add(textFieldSecondTicker);

        JPanel containerTextFieldBaseInterval = new JPanel(new FlowLayout(FlowLayout.LEFT));
        containerTextFieldBaseInterval.add(textFieldBaseInterval);


        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(labelPageName)
                .addComponent(btnBackToMainPage)
                .addComponent(labelChoseTicker)
                .addComponent(containerTextFieldTicker)
                .addComponent(labelBaseInterval)
                .addComponent(containerTextFieldBaseInterval)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(labelBeginData)
                                .addComponent(textFieldBeginData)
                        )
                        .addGroup(layout.createParallelGroup()
                                .addComponent(labelEndData)
                                .addComponent(textFieldEndData)
                        )
                )
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(btnSaveTemplate)
                                .addComponent(btnRemoveTemplate)
                        )
                        .addComponent(listOfTemplate)
                )
                .addComponent(btnExecute)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(labelPageName)
                .addComponent(btnBackToMainPage)
                .addComponent(labelChoseTicker)
                .addComponent(containerTextFieldTicker)
                .addComponent(labelBaseInterval)
                .addComponent(containerTextFieldBaseInterval)
                .addGroup(layout.createParallelGroup()
                        .addComponent(labelBeginData)
                        .addComponent(labelEndData)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(textFieldBeginData)
                        .addComponent(textFieldEndData)
                )
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSaveTemplate)
                                .addComponent(btnRemoveTemplate)
                        )
                        .addComponent(listOfTemplate)
                )
                .addComponent(btnExecute)
        );
    }

    private void setLayoutStartConfig() {
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
    }

    private void addEventListeners() {
        btnBackToMainPage.addActionListener(e -> {
            app.changePage(app.getPages().getMainPage());
        });
        btnSaveTemplate.addActionListener(e -> {
            savePreference();
            DataHandler.ByBitAPITemplate byBitAPITemplate = createTemplate();
            byBitAPITemplateList.add(byBitAPITemplate);
            dataListOfTemplate.addElement(DataHandler.getNameOfTemplate(byBitAPITemplate));

        });
        btnRemoveTemplate.addActionListener(e -> {
            if (!listOfTemplate.isSelectionEmpty()) {
                int selectedIndex = listOfTemplate.getSelectedIndex();
                byBitAPITemplateList.remove(selectedIndex);
                dataListOfTemplate.remove(selectedIndex);
            }
        });
        btnExecute.addActionListener(e -> {

            JPanel panelExecuteInfo = new JPanel(new VerticalLayout());
            JDialog executeStatusDialog = new JDialog(app, "Выполнение", true);
            executeStatusDialog.setSize(new Dimension(600, 400));

            executeStatusDialog.setContentPane(panelExecuteInfo);

            Thread executor = new Thread(() -> {
                Path pathToFolderWithDataFrame = Path.of(app.getUP().getPathToTempData() + "\\DataFrames\\");
                if (Files.notExists(pathToFolderWithDataFrame)){
                    try {
                        Files.createDirectory(pathToFolderWithDataFrame);
                    } catch (IOException ex) {
                        panelExecuteInfo.add(new JLabel("Fatal Error: Не удалось создать папку DataFrames"));
                        return;
                    }
                }
                for (DataHandler.ByBitAPITemplate byBitAPITemplate : byBitAPITemplateList) {
                        if (Thread.currentThread().isInterrupted())
                        {
                            break;
                        }
                        try {
                            String result = new PythonExecutor().executeByBitAPIScript(byBitAPITemplate,
                                    Path.of(app.getUP().getPathToPyFiles()),
                                    pathToFolderWithDataFrame);
                            app.getLog().info(result);
                            panelExecuteInfo.add(new JLabel("Data Frame " + DataHandler.getNameOfTemplate(byBitAPITemplate) + " успешно загружен"));

                        } catch (FileNotFoundException fileNotFoundException) {
                            panelExecuteInfo.add(new JLabel("Не найден файл BybitAPI.py"));
                        } catch (Exception exception) {
                            panelExecuteInfo.add(new JLabel("Проблема с исполнением шаблона " + DataHandler.getNameOfTemplate(byBitAPITemplate)));
                        } finally {
                            executeStatusDialog.setContentPane(panelExecuteInfo);
                        }
                }
            });
            executeStatusDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    executor.interrupt();
                    super.windowClosed(e);
                }
            });

            executor.start();
            executeStatusDialog.setVisible(true);
        });
    }

    private void setPrefs() {
        labelPageName.setFont(new Font("Aria", Font.BOLD, 20));

        UserPreferences.ByBitAPIPageData byBitAPIPageData = app.getUP().getByBitAPIPageData();
        textFieldFirstTicker.setText(byBitAPIPageData.getFirstTicker());
        textFieldSecondTicker.setText(byBitAPIPageData.getSecondTicker());
        textFieldBaseInterval.setText(byBitAPIPageData.getBaseInterval());
        textFieldBeginData.setText(byBitAPIPageData.getDataBegin());
        textFieldEndData.setText(byBitAPIPageData.getDataEnd());

        listOfTemplate.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);


    }

    @Override
    public void setComponents() {
        app.setContentPane(contents);
    }

    public void savePreference() {
        app.getUP().setByBitAPIPageData(new UserPreferences.ByBitAPIPageData(
                textFieldFirstTicker.getText(),
                textFieldSecondTicker.getText(),
                textFieldBaseInterval.getText(),
                textFieldBeginData.getText(),
                textFieldEndData.getText()
        ));
    }

    public DataHandler.ByBitAPITemplate createTemplate() {
        return new DataHandler.ByBitAPITemplate(
                textFieldFirstTicker.getText(),
                textFieldSecondTicker.getText(),
                textFieldBaseInterval.getText(),
                textFieldBeginData.getText(),
                textFieldEndData.getText()
        );
    }
}
