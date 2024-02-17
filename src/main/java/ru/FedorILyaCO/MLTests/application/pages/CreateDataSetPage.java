package ru.FedorILyaCO.MLTests.application.pages;

import ru.FedorILyaCO.MLTests.application.App;
import ru.FedorILyaCO.MLTests.application.logic.DataHandler;
import ru.FedorILyaCO.MLTests.application.logic.Localizator;
import ru.FedorILyaCO.MLTests.application.logic.VerticalLayout;
import ru.FedorILyaCO.MLTests.application.preferences.UserPreferences;
import ru.FedorILyaCO.MLTests.application.pyExecution.PythonExecutor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

public class CreateDataSetPage extends PageWithGroupLayout{
    JLabel labelPageName = new JLabel("Создать DataSet");
    JButton btnBackToMainPage = new JButton("Вернуться на главную");
    JLabel labelDataFrame = new JLabel("DataFrame");
    JButton btnChooseDataFrame = new JButton("Выбрать");
    JTextField textFieldChooseDataFrame = new JTextField(20);
    JLabel labelDTP = new JLabel("Dtp");
    JTextField textFieldDTP = new JTextField(5);
    JLabel labelDLS = new JLabel("Dls");
    JTextField textFieldDLS= new JTextField(5);
    JLabel labelRule = new JLabel("Rule");
    JTextArea textAreaRule = new JTextArea();
    JRadioButton radioButtonConsistent = new JRadioButton("Consistent");
    JRadioButton radioButtonParallel = new JRadioButton("Parallel");
    JLabel labelYTime = new JLabel("Y_Time");
    JTextField textFieldYTime = new JTextField();
    JButton btnSaveTemplate = new JButton("Сохранить");
    JButton btnRemoveTemplate = new JButton("Удалить");
    DefaultListModel<String> dataListOfTemplate = new DefaultListModel<>();
    JList<String> listOfTemplate = new JList<String>(dataListOfTemplate);
    JButton btnExecute = new JButton("Выполнить");
    JFileChooser fileChooser = new JFileChooser();
    ButtonGroup groupRadioButton = new ButtonGroup();

    List<DataHandler.CreateDataSetTemplate> createDataSetTemplateList =
            new ArrayList<>();

    public CreateDataSetPage(App app) {
        super(app);

        addComponents();
        addEventListeners();
        setPrefs();
    }

    @Override
    public void addComponents() {

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(labelPageName)
                .addComponent(btnBackToMainPage)
                .addComponent(labelDataFrame)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnChooseDataFrame)
                        .addComponent(textFieldChooseDataFrame)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(labelDTP)
                        .addComponent(textFieldDTP)
                        .addComponent(labelDLS)
                        .addComponent(textFieldDLS)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(labelRule)
                        .addComponent(textAreaRule)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(radioButtonConsistent)
                        .addComponent(radioButtonParallel)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(labelYTime)
                        .addComponent(textFieldYTime)
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
                .addComponent(labelDataFrame)
                .addGroup(layout.createParallelGroup()
                        .addComponent(btnChooseDataFrame)
                        .addComponent(textFieldChooseDataFrame)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(labelDTP)
                        .addComponent(textFieldDTP)
                        .addComponent(labelDLS)
                        .addComponent(textFieldDLS)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(labelRule)
                        .addComponent(textAreaRule)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(radioButtonConsistent)
                        .addComponent(radioButtonParallel)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(labelYTime)
                        .addComponent(textFieldYTime)
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

    @Override
    public void addEventListeners() {
        btnBackToMainPage.addActionListener(e -> {
            app.changePage(app.getPages().getMainPage());
        });
        btnChooseDataFrame.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!Objects.equals(app.getUP().getPathToPyFiles(), "")){
                    fileChooser.setCurrentDirectory(new File(app.getUP().getPathToTempData()));
                }
                fileChooser.setDialogTitle("Выбор CSV файла");
                int result = fileChooser.showOpenDialog(app);
                if (result == JFileChooser.APPROVE_OPTION) {
                    textFieldChooseDataFrame.setText(fileChooser.getSelectedFile().getPath());
                }

            }
        });

        btnSaveTemplate.addActionListener(e -> {
            savePreference();
            DataHandler.CreateDataSetTemplate createDataSetTemplate = createTemplate();
            createDataSetTemplateList.add(createDataSetTemplate);
            dataListOfTemplate.addElement(DataHandler.getNameOfTemplate(createDataSetTemplate));

        });
        btnRemoveTemplate.addActionListener(e -> {
            if (!listOfTemplate.isSelectionEmpty()) {
                int selectedIndex = listOfTemplate.getSelectedIndex();
                createDataSetTemplateList.remove(selectedIndex);
                dataListOfTemplate.remove(selectedIndex);
            }
        });

        btnExecute.addActionListener(e -> {

            JPanel panelExecuteInfo = new JPanel(new VerticalLayout());
            JDialog executeStatusDialog = new JDialog(app, "Выполнение", true);
            executeStatusDialog.setSize(new Dimension(600, 400));

            executeStatusDialog.setContentPane(panelExecuteInfo);

            Thread executor = new Thread(() -> {
                Path pathToFolderWithDataSets = Path.of(app.getUP().getPathToTempData() + "\\DataSets\\");
                if (Files.notExists(pathToFolderWithDataSets)){
                    try {
                        Files.createDirectory(pathToFolderWithDataSets);
                    } catch (IOException ex) {
                        panelExecuteInfo.add(new JLabel("Fatal Error: Не удалось создать папку DataSets"));
                        return;
                    }
                }
                for (DataHandler.CreateDataSetTemplate createDataSetTemplate : createDataSetTemplateList) {
                    if (Thread.currentThread().isInterrupted())
                    {
                        break;
                    }
                    try {
                        String result = new PythonExecutor().executeCreateDataSet(createDataSetTemplate,
                                Path.of(app.getUP().getPathToPyFiles()),
                                pathToFolderWithDataSets);
                        app.getLog().info(result);
                        panelExecuteInfo.add(new JLabel("DataSet " + DataHandler.getNameOfTemplate(createDataSetTemplate) + " успешно создан"));

                    } catch (FileNotFoundException fileNotFoundException) {
                        panelExecuteInfo.add(new JLabel("Не найден файл CreateDataSets.py"));
                    } catch (Exception exception) {
                        panelExecuteInfo.add(new JLabel("Проблема с исполнением шаблона " + DataHandler.getNameOfTemplate(createDataSetTemplate)));
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

    @Override
    public void setPrefs() {
        labelPageName.setFont(new Font("Aria", Font.BOLD, 20));

        groupRadioButton.add(radioButtonConsistent);
        groupRadioButton.add(radioButtonParallel);

        Localizator.localizeComponentsOfWindow();

        FileNameExtensionFilter filter = new FileNameExtensionFilter( "CSV","csv");
        fileChooser.setFileFilter(filter);

        UserPreferences.CreateDataSetData createDataSetData = app.getUP().getCreateDataSetData();
        textFieldChooseDataFrame.setText(createDataSetData.getPathToDataFrame());
        textFieldDLS.setText(createDataSetData.getDls());
        textFieldDTP.setText(createDataSetData.getDtp());
        textAreaRule.setText(createDataSetData.getRule());
        textFieldYTime.setText(createDataSetData.getY_Time());


        if (Objects.equals(createDataSetData.getTypeConsOrPar(), "Consistent")){
            radioButtonConsistent.setSelected(true);
        } else {
            radioButtonParallel.setSelected(true);
        }


    }

    public void savePreference(){
        app.getUP().setCreateDataSetData(new UserPreferences.CreateDataSetData(
               textFieldChooseDataFrame.getText(),
               textFieldDTP.getText(),
               textFieldDLS.getText(),
               textAreaRule.getText(),
               getSelectedButtonText(groupRadioButton),
               textFieldYTime.getText()
        ));
    }

    public DataHandler.CreateDataSetTemplate createTemplate(){
        return new DataHandler.CreateDataSetTemplate(
                textFieldChooseDataFrame.getText(),
                textFieldDTP.getText(),
                textFieldDLS.getText(),
                textAreaRule.getText(),
                getSelectedButtonText(groupRadioButton),
                textFieldYTime.getText()
        );
    }

    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return "";
    }
}
