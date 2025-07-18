package org.example.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public abstract class OnChangeDocumentListener<T> implements DocumentListener {

    protected final JTextField input;
    private final Consumer<T> handleChangeValueExternal;

    public OnChangeDocumentListener(JTextField input, Consumer<T> handleChangeValueExternal){

        this.input = input;
        this.handleChangeValueExternal = handleChangeValueExternal;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {

        String newValue = input.getText();

        T actualValue = handleChangeValue(newValue);

        handleChangeValueExternal.accept(actualValue);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {

        String newValue = input.getText();

        T actualValue = handleChangeValue(newValue);

        handleChangeValueExternal.accept(actualValue);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

        String newValue = input.getText();

        T actualValue = handleChangeValue(newValue);

        handleChangeValueExternal.accept(actualValue);
    }

    protected abstract T handleChangeValue(String newValue);

}
