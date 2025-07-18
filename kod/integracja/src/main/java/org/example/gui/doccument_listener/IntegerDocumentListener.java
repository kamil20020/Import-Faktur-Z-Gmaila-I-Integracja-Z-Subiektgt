package org.example.gui.doccument_listener;

import javax.swing.*;
import java.util.function.Consumer;

public class IntegerDocumentListener extends OnChangeDocumentListener<Integer> {

    private Integer actualValue = null;

    public IntegerDocumentListener(JTextField input, Consumer<Integer> handleChangeValueExternal){

        super(input, handleChangeValueExternal);
    }

    @Override
    public Integer handleChangeValue(String newValueStr){

        if (newValueStr == null || newValueStr.isBlank()) {

            actualValue = null;

            return actualValue;
        }

        Integer newValue = null;

        try {

            newValue = Integer.valueOf(newValueStr);

            if (newValue < 0) {

                newValue = 0;
            }

            actualValue = newValue;
        }
        catch (NumberFormatException e) {

            String actualValueStr = null;

            if (actualValue != null) {

                actualValueStr = actualValue.toString();
            }

            String finalActualValueStr = actualValueStr;

            SwingUtilities.invokeLater(() -> {

                input.setText(finalActualValueStr);
            });
        }

        return actualValue;
    }

}
