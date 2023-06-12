package gon.cue.basefullstack.util.mng;


import gon.cue.basefullstack.entities.mng.Book;
import gon.cue.basefullstack.entities.mng.Chapter;
import gon.cue.basefullstack.entities.mng.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MngMetaData {
    private static final int DOWNLOAD_BODY_SIZE = 80 * 1000000;
    private static final int DEFAULT_REQUEST_TIMEOUT = (60 * 7) * 1000;
    private static final int WEB_REQUEST_BODY_SIZE = 500000;
    private Book book;
    private static Logger log = LoggerFactory.getLogger(MngMetaData.class);
    private String bookPath;

    public MngMetaData(Book book) {
        this.book = book;
        this.setBookPath();
    }

    private void setBookPath() {
        this.bookPath = "/Volumes/Elements/Peliculas/.Hide/MNG/" + book.getTitle() + "/";
    }

    public void run() throws IOException, InterruptedException {
        log.info("Fetching metadata for book: {}", book.getId());
        if (book.getUrl().contains("manganato.com")) {
            natoMetaDownload();
        }
        if (book.getUrl().contains("doujins.me")) {
            doujMetaDownload();
        }
        if (book.getUrl().contains("manhwas.net")) {
            mnhwMetaDownload();
        }
        if (book.getUrl().contains("lectortmo.com")) {
            tmoMetaDownload();
        }
        log.info("Metadata fetched for book: {}", book.getTitle());
        LocalDateTime now = LocalDateTime.now();
        book.setLastUpdated(now.toEpochSecond(ZoneOffset.UTC));
    }

    private void tmoMetaDownload() throws InterruptedException {
        TMOFans tmoFans = new TMOFans();
//        tmoFans.login();

        List<Map<String, String>> bookInfo = tmoFans.getBookInfo(book.getUrl());
        List<Chapter> chapters = new ArrayList<>();

        bookInfo.forEach(chapterInfo -> {
            Chapter chapter = new Chapter();
            chapter.setUrl(chapterInfo.get("url"));
            chapter.setTitle(correct(chapterInfo.get("title")));
            chapter.setIsRead(false);
            chapters.add(0, chapter);
        });

        log.info("Found {} chapters for book: {}", chapters.size(), book.getTitle());
        for (Chapter chapter : chapters) {
            if (book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).count() == 0) {
                book.getChapters().add(chapter);
            }
            if (!pdfName(chapter.getTitle()).exists()) {
                log.info("Adding chapter: {} on book: {}", chapter.getTitle(), book.getTitle());
                Thread.sleep(5000);
                List<String> chapterInfo = tmoFans.getChapterInfo(chapter.getUrl());
                chapterInfo.forEach(pageInfo -> {
                    Page page = new Page();
                    page.setUrl(pageInfo);
                    chapter.getPages().add(page);
                });
                Chapter ch = book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).findFirst().get();
                ch.setPages(chapter.getPages());
                log.info("Added chapter: {} with: {} pages on book: {}", chapter.getTitle(), chapter.getPages().size(), book.getTitle());
            }
        }
        tmoFans.logout();
        Thread.sleep(5000);
    }

    private void mnhwMetaDownload() {
        Document document = null;
        try {
            document = Jsoup.connect(book.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();

            List<Chapter> chapters = new ArrayList<>();

            document.select("ul.episodes-list").select("li").forEach(element -> {
                Chapter chapter = new Chapter();
                chapter.setUrl(element.select("a").attr("href"));
                chapter.setTitle(correct(element.select("span").first().text().strip()));
                chapter.setIsRead(false);
                chapters.add(0, chapter);
            });

            log.info("Found {} chapters for book: {}", chapters.size(), book.getTitle());
            for (Chapter chapter : chapters) {
                if (book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).count() == 0) {
                    book.getChapters().add(chapter);
                }
                if (!pdfName(chapter.getTitle()).exists()) {
                    log.info("Adding chapter: {} on book: {}", chapter.getTitle(), book.getTitle());
                    Document chapterDocument = Jsoup.connect(chapter.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();
                    chapterDocument.select("div#chapter_imgs").select("img").forEach(element -> {
                        if (!element.attr("src").contains("discord.jpg")) {
                            Page page = new Page();
                            page.setUrl(element.attr("src"));
                            chapter.getPages().add(page);
                        }
                    });
                    Chapter ch = book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).findFirst().get();
                    ch.setPages(chapter.getPages());
                    log.info("Added chapter: {} with: {} pages on book: {}", chapter.getTitle(), chapter.getPages().size(), book.getTitle());
                }
            }
        } catch (IOException e) {
            log.error("Error fetching metadata for book: {}", book.getId(), e);
        }
    }

    private void doujMetaDownload() {
        Document document = null;
        try {
            document = Jsoup.connect(book.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();

            Elements select = document.getElementsByTag("link").select("[rel=shortlink]");
            String index = select.attr("href").split("=")[1];

            Document document1 = null;
            document1 = Jsoup.connect("https://doujins.me/wp-admin/admin-ajax.php")
                    .data("action", "manga_get_chapters", "manga", index)
                    .ignoreContentType(true).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT)
                    .post();

            List<Chapter> chapters = new ArrayList<>();
            document1.select("li.wp-manga-chapter").select("a")
                    .forEach(element -> {
                        String title = element.text().strip();
                        if (!title.isEmpty()) {
                            Chapter chapter = new Chapter();
                            chapter.setUrl(element.attr("href"));
                            chapter.setTitle(correct(title));
                            chapter.setIsRead(false);
                            chapters.add(0, chapter);
                        }
                    });

            log.info("Found {} chapters for book: {}", chapters.size(), book.getTitle());
            for (Chapter chapter : chapters) {
                if (book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).count() == 0) {
                    book.getChapters().add(chapter);
                }
                if (!pdfName(chapter.getTitle()).exists()) {
                    log.info("Adding chapter: {} on book: {}", chapter.getTitle(), book.getTitle());
                    Document document2 = Jsoup.connect(chapter.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();
                    document2.select("img.wp-manga-chapter-img").forEach(element -> {
                        Page page = new Page();
                        page.setUrl(element.attr("src").strip());
                        chapter.getPages().add(page);
                    });
                    Chapter ch = book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).findFirst().get();
                    ch.setPages(chapter.getPages());
                    log.info("Added chapter: {} with: {} pages on book: {}", chapter.getTitle(), chapter.getPages().size(), book.getTitle());
                }
            }
        } catch (IOException e) {
            log.error("Error fetching book: " + book.getTitle(), e);
        }
    }

    private void natoMetaDownload() {
        List<Chapter> chapters = new ArrayList<>();

        Document document1 = null;
        try {
            document1 = Jsoup.connect(book.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();

            String h1 = document1.select("div.story-info-right").select("h1").text().strip();
            book.setTitle(h1);
            this.setBookPath();

            document1.getElementsByClass("chapter-name text-nowrap")
                    .forEach(element -> {
                        String title = element.text().strip();
                        if (!title.isEmpty()) {
                            Chapter chapter = new Chapter();
                            chapter.setIsRead(false);
                            chapter.setTitle(correct(title));
                            chapter.setUrl(element.attr("href"));
                            chapter.setIsRead(false);

                            chapters.add(0, chapter);
                        }
                    });

            log.info("Found {} chapters for book: {}", chapters.size(), book.getTitle());
            for (Chapter chapter : chapters) {
                if (book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).count() == 0) {
                    book.getChapters().add(chapter);
                }
                if (!pdfName(chapter.getTitle()).exists()) {
                    log.info("Adding chapter: {} on book: {}", chapter.getTitle(), book.getTitle());
                    Document document = Jsoup.connect(chapter.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();
                    document.select("div.container-chapter-reader").select("img").forEach(element -> {
                        Page page = new Page();
                        page.setUrl(element.attr("src").strip());
                        chapter.getPages().add(page);
                        log.info("Added page: {} to chapter: {} of book: {}", page.getUrl(), chapter.getTitle(), book.getTitle());
                    });
                    Chapter ch = book.getChapters().stream().filter(c -> c.getTitle().equals(chapter.getTitle())).findFirst().get();
                    ch.setPages(chapter.getPages());
                    log.info("Added chapter: {} with: {} pages on book: {}", chapter.getTitle(), chapter.getPages().size(), book.getTitle());
                }
            }
        } catch (IOException e) {
            log.error("Error fetching book: " + book.getTitle(), e);
        }
    }

    private String correct(String title) {
        char[] chars = {'/', ':', '?', '*', '"', '<', '>', '|'};
        Matcher matcher = Pattern.compile("[" + new String(chars) + "]").matcher(title);
        return matcher.replaceAll("-");
//        return title.replace("/", "-")
//                .replace(":", "-")
//                .replace("?", "-")
//                .replace("*", "-")
//                .replace("\"", "-")
//                .replace("<", "-")
//                .replace(">", "-")
//                .replace("|", "-");
    }

    public void createPDF() throws IOException {
        for (Chapter chapter : book.getChapters()) {
            File pdfFile = pdfName(chapter.getTitle());
            if (pdfFile.exists()) {
                log.info("PDF already exists for chapter: {} of book: {}", chapter.getTitle(), book.getTitle());
                continue;
            }

            List<File> images = downloadImages(chapter);
            if (images.isEmpty()) {
                log.error("No images found for chapter: {} of book: {}", chapter.getTitle(), book.getTitle());
                continue;
            }
            PDDocument document = new PDDocument();
            try {
                log.info("Creating PDF for chapter: {} of book: {}", chapter.getTitle(), book.getTitle());
                for (File image : images) {
                    if (image.getAbsolutePath().toLowerCase().equals("022.jpg"))
                        log.info("Processing WebP image");
                    PDImageXObject pdImage = PDImageXObject.createFromFile(image.getAbsolutePath(), document);
                    PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
                    document.addPage(page);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.drawImage(pdImage, 0, 0);
                    contentStream.close();
                }
                document.save(pdfFile.getAbsolutePath());
            } catch (Exception e) {
                log.error("Error creating PDF for chapter: {} of book: {}", chapter.getTitle(), book.getTitle(), e);
            } finally {
                for (File image : images) {
                    Files.delete(image.toPath());
                }
                document.close();
            }
        }
        log.info("Finished creating PDF for book: {}", book.getTitle());
    }

    private File pdfName(String title) {
        File file = new File(this.bookPath);
        if (!file.exists())
            file.mkdirs();
        return new File(this.bookPath + title + ".pdf");
    }

    private List<File> downloadImages(Chapter chapter) {
        List<File> images = new ArrayList<>();
        for (Page page : chapter.getPages()) {
            Connection.Response response = null;
            try {
                response = Jsoup.connect(page.getUrl()).maxBodySize(DOWNLOAD_BODY_SIZE)
                        .referrer(chapter.getUrl()).timeout(DEFAULT_REQUEST_TIMEOUT)
                        .ignoreContentType(true).execute();
                String imgName = page.getUrl().substring(page.getUrl().lastIndexOf("/") + 1);
                imgName = imgName.endsWith(".webp") ? imgName.replace(".webp", ".jpg") : imgName;
                File image = new File(this.bookPath + imgName);
                log.info("Downloading image: {} for chapter: {} of book: {}", image.getName(), chapter.getTitle(), book.getTitle());
                FileOutputStream out = new FileOutputStream(image);
                out.write(response.bodyAsBytes());
                out.close();
                images.add(image);
                preprocessImage(image);

            } catch (Exception e) {
                log.error("Error downloading image: {} for chapter: {} of book: {}", page.getUrl(), chapter.getTitle(), book.getTitle(), e);
            }

        }
        return images;
    }

    private void preprocessImage(File image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            BufferedImage newBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            ImageIO.write(newBufferedImage, "jpg", image);
        } catch (Exception e) {
            log.error("Error preprocessing image: {}", image.getName(), e);
        }
    }

    public byte[] getPDFByteArray(Long chapter) {
        byte[] pdfBytes = new byte[0];
        try {
            File pdfFile = pdfName(book.getChapters().stream().filter(chapter1 ->
                    chapter1.getId().equals(chapter)).findFirst().get().getTitle());
            if (!pdfFile.exists()) {
                log.error("PDF does not exist for chapter: {} of book: {}", book.getChapters().stream().filter(chapter1 ->chapter1.getId().equals(chapter)).findFirst().get().getTitle(), book.getTitle());
                return pdfBytes;
            }
            log.info("Reading PDF for chapter: {} of book: {}",book.getChapters().stream().filter(chapter1 ->
                    chapter1.getId().equals(chapter)).findFirst().get().getTitle(), book.getTitle());
            pdfBytes = Files.readAllBytes(pdfFile.toPath());
        } catch (Exception e) {
            log.error("Error reading PDF for chapter: {} of book: {}", book.getChapters().stream().filter(chapter1 ->chapter1.getId().equals(chapter)).findFirst().get().getTitle(), book.getTitle(), e);
        }
        return pdfBytes;
    }
}
