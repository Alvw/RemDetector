package gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

class NumberDocument extends PlainDocument
{
    private int numberSize;

    public NumberDocument(int numberSize)
    {
        this.numberSize = numberSize;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException
    {
        if (getLength() + str.length() > numberSize) {
            str = str.substring(0, numberSize - getLength());
            Toolkit.getDefaultToolkit().beep();
        }

        try {
            int number = Integer.parseInt(str);
            if(number == 0) {
                // inserted number is 0
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        } catch (NumberFormatException e) {
            // inserted text is not a number
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        super.insertString(offs, str, a);
    }
}
