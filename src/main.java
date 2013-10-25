import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.List;

public class main {
    public static void main(String[] args) throws FileNotFoundException {


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LogChooser.createAndShowGUI();
            }
        });
    }
}
