package org.example.gui;

import javax.swing.*;

public abstract class ChangeableGui {

    private boolean isLoaded = false;

    protected void load(){

        isLoaded = true;
    }

    public boolean isLoaded(){

        return isLoaded;
    }

    public abstract JPanel getMainPanel();
}
