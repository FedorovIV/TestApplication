package ru.FedorILyaCO.MLTests.application.pages;

import ru.FedorILyaCO.MLTests.application.logic.DialogData;
import ru.FedorILyaCO.MLTests.application.App;

import javax.swing.*;
import java.awt.*;

public abstract class Page implements PageInterface {
    public Container allComponents = new Container();
    App app;

    public Page(App app){
        this.app = app;

    }


    public void createDialogWithDialogData(DialogData dialogData){
        dialogData.createDialogWithDialogData(app);
    }
}
