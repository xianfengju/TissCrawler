
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alexander
 * Date: 23.09.13
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class TissCrawlerSoup {
    public static void main (String args[]) {
        try {
                     Response res =  Jsoup.connect("https://iu.zid.tuwien.ac.at/AuthServ.authenticate?app=76")
                     .data("name","yolo")
                     .data("pw", "yolo")
                     .data("totp", "")
                     .data("app","76")
                     .method(Connection.Method.POST)
                     .execute();
          Document doc = res.parse();
          Map<String,String> d = res.cookies();
            TissCrawlerSoup bl = new TissCrawlerSoup();
          Map<String,String> postparas = bl.getFormParamsCourseReg(Jsoup.connect("https://tiss.tuwien.ac.at/education/course/courseRegistration.xhtml?windowId=ba5&courseNr=107273&semester=2013S")
                  .cookies(d).execute().parse().html(),"registrationForm");
          Response res2 = Jsoup.connect("https://tiss.tuwien.ac.at/education/course/courseRegistration.xhtml?windowId=ba5&courseNr=107273&semester=2013S")
                            .cookies(d)
                            .followRedirects(true)
                            .method(Connection.Method.POST)
                            .data(postparas)
                            .execute();

          System.out.println(res2.parse().text());
            Map<String,String> postparas2 = bl.getFormParamsCourseReg(res2.parse().html().toString(),"regForm");
            Response res3 = Jsoup.connect("https://tiss.tuwien.ac.at/education/course/register.xhtml?windowId=ba5")
                    .cookies(d)
                    .method(Connection.Method.POST)
                    .data(postparas2)
                    .referrer("https://tiss.tuwien.ac.at/education/course/courseRegistration.xhtml?windowId=ba5")
                    .execute();
            System.out.println(res3.parse().text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Map<String,String> getFormParamsCourseReg(String html, String formId) throws UnsupportedEncodingException {
        // act like a browser
        Document doc = Jsoup.parse(html);

        Element loginform = doc.getElementById(formId);
        Map<String,String> returnmap = new HashMap<String, String>();
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if(!value.equals("Abbrechen")){
            System.out.println(value + " " + key);

            returnmap.put(key, URLEncoder.encode(value, "UTF-8"));
            }
        }

        return returnmap;
    }
}
