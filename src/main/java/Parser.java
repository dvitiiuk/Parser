import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private int taskNumber = 0;
    private String fileName = "";

    private List<String> header = new ArrayList<String>();

    public List<String> getHeader() {
        return header;
    }

    public List<String> parseByFileName(String fileName) throws FileNotFoundException {
        char slash = fileName.contains("/") ? '/' : '\\';
        String fileNameSplitted = fileName.substring(fileName.lastIndexOf(slash)+1, fileName.length());
        this.fileName = fileNameSplitted.replaceAll("-run.log","");
        return parse(FileWorker.read(fileName));
    }

    public List<String> parseByTaskNumber(int taskNumber) throws FileNotFoundException, MalformedURLException {
        this.taskNumber = taskNumber;
        return parse(HTMLContentGetter.getLogByUrl(String.format("http://10.10.11.125:8080/job/Run-HDP-Tests/%s/consoleText", taskNumber)));
    }

    public List<String> parseByURL(String url) throws FileNotFoundException, MalformedURLException {
        if (url.contains("Run-HDP-Tests")) {
            this.taskNumber = Integer.parseInt(url.split("Tests/")[1].split("/")[0]);
        } else {
            String[] urlSplitted = url.split("/");
            this.fileName = urlSplitted[urlSplitted.length-1].replaceAll("-run.log","");
        }
        return parse(HTMLContentGetter.getLogByUrl(url));
    }

    private List<String> parse(String content) throws FileNotFoundException {
        List<String> res = new ArrayList<String>();

        String header = "# Total test cases ran";
        if(content.contains(header)) {
            String summary = content.split(header)[1].split("# TestSuite Log")[0];
            String clusterName = content.split("CLUSTER -> ")[1].split("\n")[0];
            String forAdd = (header + summary).replaceAll(" ","_").replaceAll("\n"," ");
            res.add(clusterName+" "+forAdd);
        } else res.add(countAll(content));

        if (taskNumber > 0) {
            res.add(String.format("http://10.10.11.125:8080/job/Run-HDP-Tests/%d/",taskNumber));
        } else {
            res.add("");
        }
        taskNumber = 0;
        String[] raw = content.split("Results :");
        String[] tests = new String[raw.length -1];
        for (int i = 1; i < raw.length; i++) { tests[i-1] = raw[i].split("\nTests run:")[0]; }
        for (String test : tests) {
            if (test.contains("Tests in error:") || test.contains("Failed tests:")) {
                System.out.println(test);
                test = test.replace("Tests in error:","");
                test = test.replace("Failed tests:", "");
                String[] lines = test.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.length() > 0 && line.contains("com.")) {
                        res.add(line);
                    }
                }
            }
        }

        return res;
    }

    private String countAll(String log) {
        String[] testsRun = log.split("Tests run: ");
        String[] failed = log.split("Failures: ");
        String[] error = log.split("Errors: ");
        String[] skip = log.split("Skipped: ");
        int errors = 0;
        int testsSum = 0;
        int failures = 0;
        int skipped = 0;

        for(int i = 1; i < testsRun.length; i+=2) {
            errors += Integer.parseInt(error[i].split(",")[0]);
            failures += Integer.parseInt(failed[i].split(",")[0]);
            testsSum += Integer.parseInt(testsRun[i].split(",")[0]);
            skipped += Integer.parseInt(skip[i].split(",")[0].split("\n")[0]);
        }
        int passed = testsSum - errors - skipped - failures;
        return fileName + " #\u00A0Total\u00A0test\u00A0cases\u00A0ran:\u00A0" + testsSum +
               " #\u00A0Total\u00A0test\u00A0cases\u00A0passed:\u00A0" + passed +
               " #\u00A0Total\u00A0test\u00A0cases\u00A0failed:\u00A0" + failures +
               " #\u00A0Total\u00A0test\u00A0cases\u00A0skipped:\u00A0" + skipped +
               " #\u00A0Total\u00A0test\u00A0cases\u00A0failed\u00A0dependency:\u00A0" + errors;
    }

    public Object[][] getCellsForTable(List<String> failedTests) {
        Object[][] res = new Object[failedTests.size()-1][2];

        res[0][0] = failedTests.get(0);
        res[0][1] = failedTests.get(1);
        Pattern p = Pattern.compile("\\(([\\w\\d]+\\.)+([\\w\\d]+)\\)");
        String className = "";
        for (int i = 2; i < failedTests.size(); i++) {
            Matcher m = p.matcher(failedTests.get(i));
            if (!m.find() || className.equals(m.group(2))) {
                res[i-1][0] = "";
            } else {
                className = m.group(2);
                res[i-1][0] = className;
            }
            res[i-1][1] = failedTests.get(i);
        }
        return res;
    }
}
