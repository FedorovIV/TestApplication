package ru.FedorILyaCO.MLTests.application;

import org.jetbrains.annotations.NotNull;
import ru.FedorILyaCO.MLTests.application.logic.DialogData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ChoosingPathsPage extends Page {

    JPanel contents = new JPanel();
    GroupLayout layout = new GroupLayout(contents);
    public JTextField textFieldPathPyFiles = new JTextField();
    public JTextField textFieldPathTempData = new JTextField();
    public JLabel labelPathPyFiles = new JLabel("Путь к файлам Python:");
    public JLabel labelPathTempData = new JLabel("Путь к папке с временными файлами:");

    public JButton btnPathPyFiles = new JButton("Выбрать");

    public JButton btnPathTempData = new JButton("Выбрать");
    public JButton btnSave = new JButton("Сохранить");

    public JButton btnBackToMainPage = new JButton("Вернуться на главную");
    private void localizeComponentsOfWindow() {
        // Локализация компонентов окна JFileChooser
        UIManager.put(
                "FileChooser.saveButtonText", "Сохранить");
        UIManager.put(
                "FileChooser.cancelButtonText", "Отмена");
        UIManager.put(
                "FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put(
                "FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put(
                "FileChooser.lookInLabelText", "Директория");
        UIManager.put(
                "FileChooser.saveInLabelText", "Сохранить в директории");
        UIManager.put(
                "FileChooser.folderNameLabelText", "Путь директории");
    }

    public ChoosingPathsPage(App app) {
        super(app);
        contents.setLayout(layout);
        localizeComponentsOfWindow();
        setLayoutStartConfig();
        addComponentsOnLayout();
        addEventListeners();

    }

    private void addComponentsOnLayout() {
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(labelPathPyFiles)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldPathPyFiles)
                                .addComponent(btnPathPyFiles)
                        )
                        .addComponent(labelPathTempData)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldPathTempData)
                                .addComponent(btnPathTempData)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBackToMainPage)
                                .addComponent(btnSave)
                        )
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(labelPathPyFiles)
                .addGroup(layout.createParallelGroup()
                        .addComponent(textFieldPathPyFiles)
                        .addComponent(btnPathPyFiles)
                )
                .addComponent(labelPathTempData)
                .addGroup(layout.createParallelGroup()
                        .addComponent(textFieldPathTempData)
                        .addComponent(btnPathTempData)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(btnBackToMainPage)
                        .addComponent(btnSave)
                )
        );
    }

    private void setLayoutStartConfig() {
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
    }

    private void addEventListeners() {
        JFileChooser fileChooser = new JFileChooser();

        btnPathPyFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор директории");
                // Определение режима - только каталог
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(app);
                // Если директория выбрана, покажем ее в сообщении
                if (result == JFileChooser.APPROVE_OPTION) {
                    textFieldPathPyFiles.setText(fileChooser.getSelectedFile().getPath());
                }

            }
        });
        btnPathTempData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор директории");
                // Определение режима - только каталог
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(app);
                // Если директория выбрана, покажем ее в сообщении
                if (result == JFileChooser.APPROVE_OPTION) {
                    textFieldPathTempData.setText(fileChooser.getSelectedFile().getPath());
                }

            }
        });
        btnSave.addActionListener(e -> {
            //TODO
            if (checkPaths()) {

            }
        });
        btnBackToMainPage.addActionListener(e -> {
            app.changePage(app.getPages().getMainPage());
        });
    }

    private boolean checkPaths() {
        boolean result = true;

        if (Objects.equals(this.textFieldPathPyFiles.getText(), "")) {
            createDialogWithDialogData(new DialogData("Путь к Python файлам пустой",
                    "Неверный путь"));
            result = false;
        } else if (Objects.equals(this.textFieldPathTempData.getText(), "")) {
            createDialogWithDialogData(new DialogData("Путь к временным файлам пустой",
                    "Неверный путь"));
            result = false;
        } else {
            try {
                Path path = Path.of(this.textFieldPathPyFiles.getText());
                boolean exists = Files.exists(path);
                if (!exists) {
                    createDialogWithDialogData(new DialogData("Неверный путь к Python файлам",
                            "Неверный путь"));
                    result = false;
                }
            } catch (Exception e) {
                createDialogWithDialogData(new DialogData("Неверный путь к Python файлам",
                        "Неверный путь"));
                result = false;
            }
            if (result) {
                try {
                    Path path = Path.of(this.textFieldPathTempData.getText());
                    boolean exists = Files.exists(path);
                    if (!exists) {
                        createDialogWithDialogData(new DialogData("Неверный путь к временным файлам",
                                "Неверный путь"));
                        result = false;
                    }
                } catch (Exception e) {
                    createDialogWithDialogData(new DialogData("Неверный путь к временным файлам",
                            "Неверный путь"));
                    result = false;
                }
            }
        }
        return result;
    }



    @Override
    public void setComponents() {
        app.setContentPane(contents);
    }
}


