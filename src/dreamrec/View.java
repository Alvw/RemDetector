package dreamrec;

import javax.swing.*;

public abstract class View extends JFrame {
    public abstract void setDataStore(DataStore dataStore);
    public abstract void showMessage(String message);
}
