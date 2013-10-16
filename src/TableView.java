import javax.swing.*;
import java.awt.*;

public class TableView extends JFrame {
    private JTable table;
    public TableView(String title, Object[][] cellValues) throws HeadlessException {
        super(title);
        table = new JTable(cellValues, new String[]{"Class name", "Test failure details"});
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }
}
