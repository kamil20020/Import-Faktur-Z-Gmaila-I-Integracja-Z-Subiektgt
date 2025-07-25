package org.example.gui.doccument_listener;


import org.example.gui.doccument_listener.OnChangeDocumentListener;

import javax.swing.*;
import java.util.function.Consumer;

public class StringOnChangeDocumentListener extends OnChangeDocumentListener<String> {

    public StringOnChangeDocumentListener(JTextField input, Consumer<String> handleChangeValueExternal) {

        super(input, handleChangeValueExternal);
    }

    @Override
    protected String handleChangeValue(String newValue) {

        return newValue;
    }

}
