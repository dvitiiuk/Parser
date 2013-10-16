import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private List<String> header = new ArrayList<String>();

    public List<String> getHeader() {
        return header;
    }

    public List<String> parseByFileName(String fileName) throws FileNotFoundException {
        return parse(FileWorker.read(fileName));
    }

    public List<String> parseByTaskNumber(int taskNumber) throws FileNotFoundException {
        return parse(HTMLContentGetter.getLogByUrl(String.format("http://10.10.11.125:8080/job/Run-HDP-Tests/%s/consoleFull", taskNumber)));
    }

    public List<String> parseByURL(String url) throws FileNotFoundException {
        return parse(HTMLContentGetter.getLogByUrl(url));
    }

    private List<String> parse(String content) throws FileNotFoundException {
        List<String> res = new ArrayList<String>();

        List<String> raw = new ArrayList<String>(Arrays.asList(content.split("\n")));

        int length = raw.size();
        for (int k = 0; k < length; k++) {
            int i = 0;
            if (raw.get(i).startsWith("Results :")) {
                int last = -1;
                for (int j = i + 1; j < raw.size(); j++) {
                    if (raw.get(j).startsWith("Tests run:")) {
                        last = j;
                        break;
                    }
                }
                for (int aim = i + 1; aim < last; aim++) {
                    String row = raw.get(aim).replace("Failed tests:  ", "").replace("Tests in error: ", "").trim();
                    if (!(row.equals("\n") || row.equals(""))) {
                        res.add(row);
                    }
                }
            } else if (raw.get(i).startsWith("# TestSuite")) {
                int j = i + 1;
                while (raw.get(j).startsWith("# Total")) {
                    header.add(raw.get(j));
                    j++;
                }
            }
            raw.remove(0);
        }

        return res;
    }

    public Object[][] getCellsForTable(List<String> failedTests) {
        Object[][] res = new Object[failedTests.size()][2];
        Pattern p = Pattern.compile("\\(([\\w\\d]+\\.)+([\\w\\d]+)\\)");
        String className = "";
        for (int i = 0; i < failedTests.size(); i++) {
            Matcher m = p.matcher(failedTests.get(i));
            m.find();
            if (m.group(2).equals(className)) {
                res[i][0] = "";
            } else {
                className = m.group(2);
                res[i][0] = className;
            }
            res[i][1] = failedTests.get(i);
        }
        return res;
    }
}
