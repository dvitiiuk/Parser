import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTMLContentGetter {
    /**
     * @author Ruslan Ostafiychuk
     */
    public static String getLogByUrl(String url) throws FileNotFoundException, MalformedURLException {
        StringBuffer log = new StringBuffer();
        try {
            URL logUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) logUrl.openConnection();
            connection.setRequestMethod("GET");
            InputStream content = connection.getInputStream();
            BufferedReader in = new BufferedReader (new InputStreamReader(content));
            int cp;
            while ((cp = in.read()) != -1) {
                log.append((char) cp);
            }
            content.close();
            in.close();
        } catch (FileNotFoundException fileE) {
            throw fileE;
        }  catch (MalformedURLException urlException) {
            throw urlException;
        }    catch (IOException e) {
            e.printStackTrace();

        }
        return log.toString();
    }
}
