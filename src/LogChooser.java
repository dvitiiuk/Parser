

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class LogChooser extends JPanel
                             implements ActionListener {
    static private final String newline = "\n";
    JButton byUrlButton;
    JTextField urlField;
    JButton openButton, byJobNumberButton;
    JTextArea log;
    JFileChooser fc;
    JTextField jobNumber;
    List<String> result;

    Parser parser = new Parser();

    public LogChooser() {
        super(new BorderLayout());

        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        jobNumber = new JTextField(15);
        urlField = new JTextField();

        //Create a file chooser
        fc = new JFileChooser();

        openButton = new JButton("Open a log file...");
        openButton.addActionListener(this);

        byJobNumberButton = new JButton("Parse job");
        byJobNumberButton.addActionListener(this);


        byUrlButton = new JButton("Parse job");
        byUrlButton.addActionListener(this);

        //For layout purposes, put the buttons in a separate panel
        JPanel logFileChooser = new JPanel(new BorderLayout());
        JPanel jobParser = new JPanel(new BorderLayout());
        JPanel urlParser = new JPanel(new BorderLayout());

        logFileChooser.add(openButton, BorderLayout.PAGE_END);
        logFileChooser.add(new JLabel("Parse log file:"), BorderLayout.PAGE_START);

        jobParser.add(byJobNumberButton, BorderLayout.EAST);
        jobParser.add(jobNumber, BorderLayout.CENTER);
        jobParser.add(new JLabel("Parse by job number:"), BorderLayout.PAGE_START);

        urlParser.add(urlField, BorderLayout.CENTER);
        urlParser.add(byUrlButton, BorderLayout.PAGE_END);
        urlParser.add(new JLabel("Parse by log url:"), BorderLayout.PAGE_START);

        add(logFileChooser, BorderLayout.PAGE_START);
        add(jobParser, BorderLayout.CENTER);
        add(urlParser, BorderLayout.PAGE_END);
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(LogChooser.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                log.append("Opening: " + file.getName() + "." + newline);
                log.setCaretPosition(log.getDocument().getLength());
                try {
                    result = parser.parseByFileName(file.getAbsolutePath());
                    showResults(result);

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            } else {
                log.append("Open command cancelled by user." + newline);
                log.setCaretPosition(log.getDocument().getLength());
            }


        //Handle save button action.
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
        }
    }

    private void showResults(List<String> result) {
        //To change body of created methods use File | Settings | File Templates.
        JFrame frame2 = new TableView("FrameDemo", parser.getCellsForTable(result));

        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame2.pack();

        frame2.setVisible(true);
    }

    static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("LogChooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new LogChooser());

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

}