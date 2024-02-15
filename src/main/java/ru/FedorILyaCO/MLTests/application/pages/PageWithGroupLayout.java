package ru.FedorILyaCO.MLTests.application.pages;

import ru.FedorILyaCO.MLTests.application.App;

import javax.swing.*;
import java.awt.*;

public abstract class PageWithGroupLayout extends Page{

    JPanel contents = new JPanel();
    JPanel group = new JPanel();
    GroupLayout layout = new GroupLayout(group);

    public PageWithGroupLayout(App app) {
        super(app);
        setLayoutStartConfig();
    }

    private void setLayoutStartConfig() {
        contents.setLayout(new FlowLayout(FlowLayout.LEFT));
        contents.add(group);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        group.setLayout(layout);
    }
    public abstract void addComponents();
    public abstract void addEventListeners();
    public abstract void setPrefs();

    @Override
    public void setComponents() {
        app.setContentPane(contents);
    }

}
