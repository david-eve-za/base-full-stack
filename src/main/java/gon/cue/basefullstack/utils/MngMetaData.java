package gon.cue.basefullstack.utils;

import gon.cue.basefullstack.model.mng.Book;
import gon.cue.basefullstack.model.mng.Chapter;
import gon.cue.basefullstack.model.mng.Page;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MngMetaData {
    private static final int DOWNLOAD_BODY_SIZE = 80 * 1000000;
    private static final int DEFAULT_REQUEST_TIMEOUT = (60 * 7) * 1000;
    private static final int WEB_REQUEST_BODY_SIZE = 500000;
    private Book book;
    private static Logger log = LoggerFactory.getLogger(MngMetaData.class);
    private String bookPath;

    public MngMetaData(Book book) {
        this.book = book;
        this.bookPath = "/Volumes/Elements/Peliculas/.Hide/MNG/";
    }

    public void run() throws IOException {
        log.info("Fetching metadata for book: {}", book.getId());
        if (book.getUrl().contains("manganato")) {
            natoMetaDownload();
        }
        if (book.getUrl().contains("doujins")) {
            doujMetaDownload();
        }
        if (book.getUrl().contains("manhwas.net")) {
            mnhwMetaDownload();
        }
        createPDF();
        log.info("Metadata fetched for book: {}", book.getTitle());
    }

    private void mnhwMetaDownload() {
        String bookTitle = book.getUrl().substring(book.getUrl().lastIndexOf("/") + 1);
        book.setTitle(bookTitle);

        Document document = null;
        try {
            document = Jsoup.connect(book.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();

            List<Chapter> chapters = new ArrayList<>();

            document.select("ul.episodes-list").select("li").forEach(element -> {
                Chapter chapter = new Chapter();
                chapter.setUrl(element.select("a").attr("href"));
                chapter.setTitle(element.select("span").first().text().strip());
                chapter.setBook(book);
                chapter.setIsRead(false);
                chapters.add(0, chapter);
            });

            int bookSize = book.getChapters().size();
            log.info("Found {} chapters for book: {}", chapters.size(), book.getTitle());
            for (int i = bookSize; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);
                log.info("Adding chapter: {} on book: {}", chapter.getTitle(), book.getTitle());
                Document chapterDocument = Jsoup.connect(chapter.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();
                chapterDocument.select("div#chapter_imgs").select("img").forEach(element -> {
                    if (!element.attr("src").contains("discord.jpg")) {
                        Page page = new Page();
                        page.setUrl(element.attr("src"));
                        page.setChapter(chapter);
                        chapter.getPages().add(page);
                    }
                });
                book.getChapters().add(chapter);
                log.info("Added chapter: {} with: {} pages on book: {}", chapter.getTitle(), chapter.getPages().size(), book.getTitle());
            }
        } catch (IOException e) {
            log.error("Error fetching metadata for book: {}", book.getId(), e);
        }
    }

    private void doujMetaDownload() {
        String bookTitle = book.getUrl().substring(book.getUrl().lastIndexOf("/") + 1);
        book.setTitle(bookTitle);

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
                            chapter.setTitle(title);
                            chapter.setBook(book);
                            chapter.setIsRead(false);
                            chapters.add(0, chapter);
                        }
                    });

            log.info("Found {} chapters for book: {}", chapters.size(), book.getTitle());
            int bookSize = book.getChapters().size();
            for (int i = bookSize; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);
                log.info("Adding chapter: {} on book: {}", chapter.getTitle(), book.getTitle());
                Document document2 = Jsoup.connect(chapter.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();
                document2.select("img.wp-manga-chapter-img").forEach(element -> {
                    Page page = new Page();
                    page.setUrl(element.attr("src").strip());
                    page.setChapter(chapter);
                    chapter.getPages().add(page);
                });
                book.getChapters().add(chapter);
                log.info("Added chapter: {} with: {} pages on book: {}", chapter.getTitle(), chapter.getPages().size(), book.getTitle());
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

            document1.getElementsByClass("chapter-name text-nowrap")
                    .forEach(element -> {
                        String title = element.text().strip();
                        if (!title.isEmpty()) {
                            Chapter chapter = new Chapter();
                            chapter.setBook(book);
                            chapter.setIsRead(false);
                            chapter.setTitle(title);
                            chapter.setUrl(element.attr("href"));
                            chapter.setIsRead(false);

                            chapters.add(0, chapter);
                        }
                    });

            int bookSize = book.getChapters().size();
            for (int i = bookSize; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);
                log.info("Adding chapter: {} on book: {}", chapter.getTitle(), book.getTitle());
                Document document = Jsoup.connect(chapter.getUrl()).maxBodySize(WEB_REQUEST_BODY_SIZE).timeout(DEFAULT_REQUEST_TIMEOUT).get();
                document.select("div.container-chapter-reader").select("img").forEach(element -> {
                    Page page = new Page();
                    page.setChapter(chapter);
                    page.setUrl(element.attr("src").strip());
                    chapter.getPages().add(page);
                    log.info("Added page: {} to chapter: {} of book: {}", page.getUrl(), chapter.getTitle(), book.getTitle());
                });

                book.getChapters().add(chapter);
                log.info("Added chapter: {} with: {} pages on book: {}", chapter.getTitle(), chapter.getPages().size(), book.getTitle());
            }
        } catch (IOException e) {
            log.error("Error fetching book: " + book.getTitle(), e);
        }
    }

    private void createPDF() {
        bookPath += book.getTitle() + "/";
        File file = new File(bookPath);
        if (!file.exists())
            file.mkdirs();
        for (Chapter chapter : book.getChapters()) {
            File pdfFile = new File(bookPath + chapter.getTitle() + ".pdf");
            if (pdfFile.exists()) {
                log.info("PDF already exists for chapter: {} of book: {}", chapter.getTitle(), book.getTitle());
                continue;
            }

            List<File> images = downloadImages(chapter);
            if (images.isEmpty()) {
                log.error("No images found for chapter: {} of book: {}", chapter.getTitle(), book.getTitle());
                continue;
            }

            try {
                PDDocument document = new PDDocument();

                log.info("Creating PDF for chapter: {} of book: {}", chapter.getTitle(), book.getTitle());
                for (File image : images) {
                    PDImageXObject pdImage = PDImageXObject.createFromFile(image.getAbsolutePath(), document);
                    PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
                    document.addPage(page);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.drawImage(pdImage, 0, 0);
                    contentStream.close();
                }

                document.save(pdfFile.getAbsolutePath());
                document.close();

                for (File image : images) {
                    Files.delete(image.toPath());
                }
            } catch (Exception e) {
                log.error("Error creating PDF for chapter: {} of book: {}", chapter.getTitle(), book.getTitle(), e);
            }
        }
    }

    private List<File> downloadImages(Chapter chapter) {
        List<File> images = new ArrayList<>();
        for (Page page : chapter.getPages()) {
            Connection.Response response = null;
            try {
                response = Jsoup.connect(page.getUrl()).maxBodySize(DOWNLOAD_BODY_SIZE)
                        .referrer(chapter.getUrl()).timeout(DEFAULT_REQUEST_TIMEOUT)
                        .ignoreContentType(true).execute();
                File image = new File(bookPath + page.getUrl().substring(page.getUrl().lastIndexOf("/") + 1));
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
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, null);
            ImageIO.write(newBufferedImage, "jpg", image);
        } catch (Exception e) {
            log.error("Error preprocessing image: {}", image.getName(), e);
        }
    }
}
