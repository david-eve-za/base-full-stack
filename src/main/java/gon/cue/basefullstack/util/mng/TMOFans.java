package gon.cue.basefullstack.util.mng;


import gon.cue.basefullstack.entities.mng.Book;
import lombok.extern.slf4j.Slf4j;
import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TMOFans {
    private WebClient webClient;

    public TMOFans() {
        this.webClient = new WebClient(BrowserVersion.FIREFOX);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setGeolocationEnabled(false);
        webClient.getOptions().setPopupBlockerEnabled(true);
        webClient.getOptions().setDoNotTrackEnabled(true);
        webClient.getOptions().setActiveXNative(false);
//        webClient.getOptions().setTimeout(8000);
//        webClient.getCookieManager().setCookiesEnabled(true); // disable cookies
    }

    public void login() {
        try {
            HtmlPage page = this.webClient.getPage("https://lectortmo.com/login");

            if (page.asXml().contains("Has sido baneado")) {  // check if the user is banned
                log.error("User is banned");
                this.close();
                return;
            }

            HtmlForm form = page.getForms().get(1);

            form.getInputByName("email").setValue("lmdjamel7867@artwerks.com");
            form.getInputByName("password").setValue("David.399385464");
            form.getInputByName("remember").setChecked(true);  // check the remember me checkbox

            DomNodeList<HtmlElement> button1 = form.getElementsByTagName("button");

            page = button1.get(0).click();  // click the button
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public void logout() {
        try {
            HtmlPage page = this.webClient.getPage("https://lectortmo.com/logout");
            this.close();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    private void close() {
        this.webClient.close();
    }

    public List<String> getProfileList() {
        List<String> hrefs = new ArrayList<>();
        try {
            HtmlPage page = this.webClient.getPage("https://lectortmo.com/profile/lists");
            if (page.asXml().contains("Has sido baneado")) {
                log.error("Has sido baneado");
                return hrefs;
            }
            List<HtmlDivision> byXPath = page.getByXPath("//*[@id=\"app\"]/section/main/div/div/div[1]/div[1]/div");
            byXPath.remove(0);

            //Retrieve every href from the list

            for (HtmlDivision div : byXPath) {
                hrefs.add(div.getElementsByTagName("a").get(0).getAttribute("href"));
            }

        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return hrefs;
    }

    public List<Book> getBooks(String uri) {
        List<Book> books = new ArrayList<>();
        try {
            HtmlPage page = this.webClient.getPage(uri);
            if (page.asXml().contains("Has sido baneado")) {
                log.error("Has sido baneado");
                return books;
            }
            List<HtmlDivision> byXPath = page.getByXPath("//*[@id=\"app\"]/section/main/div/div[2]/div[1]/div/div");
            byXPath.remove(0);

            for (HtmlDivision div : byXPath) {
                Book book = new Book();
                book.setUrl(div.getElementsByTagName("a").get(0).getAttribute("href"));
                book.setTitle(div.getElementsByTagName("h4").get(0).getTextContent());
                books.add(book);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return books;
    }

    public List<Map<String, String>> getBookInfo(String url) {
        List<Map<String, String>> chapters = new ArrayList<>();
        try {
            HtmlPage page = this.webClient.getPage(url);
            if (page.asXml().contains("Has sido baneado")) {
                log.error("Has sido baneado");
                return chapters;
            }
            List<HtmlListItem> byXPath = page.getByXPath("//*[@id='chapters']/ul/li");
            byXPath.addAll(page.getByXPath("//*[@id='chapters-collapsed']/li"));

            for (HtmlListItem li : byXPath) {
                Map<String, String> chapter = new HashMap<>();
                //*[@id="collapsible911618"]/div/ul/li[1]/div/div[6]/a
                chapter.put("url", ((HtmlElement) li.getByXPath("div/div/ul/li[1]/div/div[6]").get(0)).getElementsByTagName("a").get(0).getAttribute("href"));
                chapter.put("title", li.getElementsByTagName("h4").get(0).getTextContent().trim());
                chapters.add(chapter);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return chapters;
    }

    public List<String> getChapterInfo(String url) {
        List<String> hrefs = new ArrayList<>();
        try {
            HtmlPage page = this.webClient.getPage(url);
            if (page.asXml().contains("Has sido baneado")) {
                log.error("Has sido baneado");
                return hrefs;
            }
            if (!page.getUrl().toString().endsWith("/cascade"))
                if (page.getUrl().toString().endsWith("/paginated"))
                    page = this.webClient.getPage(page.getUrl().toString().replace("/paginated", "/cascade"));
                else if (page.getUrl().toString().endsWith("/"))
                    page = this.webClient.getPage(page.getUrl().toString() + "cascade");
                else
                    page = this.webClient.getPage(page.getUrl().toString() + "/cascade");
            page.getElementById("main-container").getElementsByTagName("img").forEach(img -> hrefs.add(img.getAttribute("data-src")));
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return hrefs;
    }
}
