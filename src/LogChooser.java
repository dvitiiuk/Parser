

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class LogChooser extends JPanel
                             implements ActionListener {
    static private final String newline = "\n";
    JTextField urlField, jobNumber, fileName;
    JButton openFileButton, parseChosenFile, byJobNumberButton, byUrlButton, copy;
    JTextArea log;
    JFileChooser fc;
    List<String> result;
    TitledBorder tb;
    MatteBorder mb;
    Parser parser = new Parser();
    JTable data;
    JScrollPane scrollData;

    JPanel byFile = new JPanel();
    JPanel byURL = new JPanel();
    JPanel byJobNumber = new JPanel();
    JPanel results = new JPanel();

    public LogChooser() {
        this.setLayout(new GridLayout(1, 2)); //4, 1
        JPanel options = new JPanel();
        options.setLayout(new GridLayout(3, 1));
        results.setLayout(new BorderLayout());
        add(options);
        add(results);

        mb = new MatteBorder(1, 1, 1, 1, Color.GRAY);

        options.add(byFile);
        options.add(byURL);
        options.add(byJobNumber);

        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(5, 20);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(false);
        jobNumber = new JTextField(20);
        urlField = new JTextField(20);
        fileName = new JTextField(20);

        //Create a file chooser
        fc = new JFileChooser();

        byJobNumberButton = new JButton("Parse");
        byJobNumberButton.addActionListener(this);
        byUrlButton = new JButton("Parse");
        byUrlButton.addActionListener(this);

        // byFile panel filling
        byFile.setLayout(new FlowLayout());
        tb = new TitledBorder(mb, " Choose file: " , TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        parseChosenFile = new JButton("Parse");
        parseChosenFile.setFocusPainted(false);
        parseChosenFile.addActionListener(this);
        parseChosenFile.setSize(50, 25);
        byFile.setBorder(tb);
        byFile.add(fileName);
        openFileButton = new JButton("...");
        openFileButton.addActionListener(this);

        byFile.add(openFileButton);
        byFile.add(parseChosenFile);

        // byJobNumber panel filling
        byJobNumber.setLayout(new FlowLayout());
        tb = new TitledBorder(mb, " Enter job number: " , TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        byJobNumber.setBorder(tb);
        byJobNumber.add(jobNumber);
        byJobNumber.add(byJobNumberButton);

        // byURL panel filling
        byURL.setLayout(new FlowLayout());
        tb = new TitledBorder(mb, " Enter URL: " , TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        byURL.setBorder(tb);
        byURL.add(urlField);
        byURL.add(byUrlButton);

        // results pane
        results.setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.

        if (e.getSource() == openFileButton) {
            int returnVal = fc.showOpenDialog(LogChooser.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                log.append("Opening: " + file.getName() + "..." + newline);
                log.setCaretPosition(log.getDocument().getLength());
                fileName.setText(file.getAbsolutePath());
            } else {
                log.append("Open command cancelled by user." + newline);
                log.setCaretPosition(log.getDocument().getLength());
            }



        } else if (e.getSource() == parseChosenFile) {

            try {
                result = parser.parseByFileName(fileName.getText());
                showResults(result);

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

        } else if (e.getSource() == byJobNumberButton) {

            int job = Integer.parseInt(jobNumber.getText());
            try {
                result = parser.parseByTaskNumber(job);
                showResults(result);

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == byUrlButton) {
            String url = urlField.getText();
            try {
                result = parser.parseByURL(url);
                showResults(result);

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == copy) {
            data.selectAll();
            Action copy = data.getActionMap().get("copy");
            ActionEvent ae = new ActionEvent(data, ActionEvent.ACTION_PERFORMED, "");
            copy.actionPerformed(ae);
        }
    }



    private void showResults(List<String> result) {
        //To change body of created methods use File | Settings | File Templates.
        //JFrame frame2 = new TableView("FrameDemo", parser.getCellsForTable(result));
        data = new JTable(parser.getCellsForTable(result),
                new String[]{"Class name", "Test failure details"});
        data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int firstColumnWidth = this.getWidth()/6;
        int secondColumnWidth = this.getWidth()/2 - firstColumnWidth - JScrollBar.WIDTH;
        data.getColumnModel().getColumn(0).setPreferredWidth(firstColumnWidth);
        data.getColumnModel().getColumn(1).setPreferredWidth(secondColumnWidth);
        scrollData = new JScrollPane(data);
        scrollData.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollData.setBounds(3, 3, firstColumnWidth + secondColumnWidth, this.getHeight() - 30);
        results.add(scrollData, BorderLayout.NORTH);
        copy = new JButton("Copy to clipboard");
        copy.addActionListener(this);
        //copy.setVisible(true);
        results.add(copy, BorderLayout.SOUTH);
        results.setVisible(true);
        this.repaint();
    }


    static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Log Chooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 490));

        //Add content to the window.
        frame.add(new LogChooser());

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

}