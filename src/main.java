import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.List;

public class main {
    public static void main(String[] args) throws FileNotFoundException {

        //args = new String[]{"-f", "/home/dmitrii/exper"};

        Parser parser = new Parser();
        List<String> result = null;
        if (args.length == 1) {
            result =  parser.parseByTaskNumber(Integer.parseInt(args[0]));
        } else if (args.length >= 2 && args[0].equals("-u")) {
            result = parser.parseByURL(args[1]);
        } else if (args.length >= 2 && args[0].equals("-f")) {
            result = parser.parseByFileName(args[1]);
        } else if (args.length >= 2 && args[0].equals("-n")) {
            result =  parser.parseByTaskNumber(Integer.parseInt(args[1]));
        }

        for (int i = 0; i< parser.getHeader().size(); i++) {
            System.out.println(parser.getHeader().get(i));
        }

        System.out.println("-----------------------------------------------------------");

        for (int i = 0; i< result.size(); i++) {
            System.out.println(result.get(i));
        }

        //1. Create the frame.
        JFrame frame = new TableView("FrameDemo", parser.getCellsForTable(result));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();

        frame.setVisible(true);
    }
}
