package ru.FedorILyaCO.MLTests.application.logic;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class DialogData {
    public String massage;
    public String title;

    public boolean modal = true;

    public DialogData(String massage,String title){
        this.massage = massage;
        this.title = title;
    }
    public DialogData(String massage,String title, boolean modal){
        this.massage = massage;
        this.title = title;
        this.modal = modal;
    }

    public void createDialogWithDialogData(JFrame frame) {

        JDialog dialog = new JDialog(frame, this.title,
                this.modal);
        dialog.add(new JLabel(this.massage));
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setSize(250, 120);
        dialog.setVisible(true);
    }

}