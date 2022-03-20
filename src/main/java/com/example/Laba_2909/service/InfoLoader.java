package com.example.Laba_2909.service;

import com.example.Laba_2909.model.Movie;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class InfoLoader {

    Logger logger = LoggerFactory.getLogger(InfoLoader.class);

    /**
     * Method to find movie with imdb ID
     * @param site_address
     * @param apikey_value
     * @param imdbID
     * @param format
     * @return
     */
    @Async("processExecutor")
    public CompletableFuture<String> findMovieByImdbID(String site_address,
                                                               String apikey_value,
                                                               String imdbID,
                                                               String format) {
        StringBuilder sb = new StringBuilder();
        try {
            UriComponentsBuilder builder1 = UriComponentsBuilder.fromUriString(site_address)
                    .queryParam("apikey", apikey_value);
            if (!imdbID.isEmpty()) {
                logger.info ("imdbID: "+imdbID);
                builder1
                        .queryParam("i", imdbID);
            }
            if (format.equals("xml")) {
                builder1.queryParam("r", format);
            }
            URI builder1URI = builder1.build().toUri();
            logger.info("URL is " + builder1URI);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(builder1URI.toURL().openStream()));

            if (in != null) {
                //read the result
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                logger.info(" findMovieByImdbID: "+inputLine);
                    sb.append(inputLine);
                }
                in.close();
            }
          Thread.sleep(1000L);
        }
        catch (  InterruptedIOException e) {
            logger.error("error");
        }
        catch (  IOException e) {
            logger.error("error");
        } catch (InterruptedException e) {
            logger.error("Error in InfoLoader.findMovieByImdbID(): {}", e.getMessage());
        }
        return CompletableFuture.completedFuture(sb.toString());
    }

    /**
     * Method to find movie with full or part of title and/or year and/or plot params
     * @param site_address
     * @param apikey_value
     * @param title
     * @param year
     * @param plot
     * @param format
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    @Async("processExecutor")
    public CompletableFuture<String> findMovieByTitle(String site_address,
                                                              String apikey_value,
                                                              String title,
                                                              String year,
                                                              String plot,
                                                              String format
    ) throws InterruptedException, IOException {
        StringBuilder sb = new StringBuilder();
         try {
            UriComponentsBuilder builder1 = UriComponentsBuilder.fromUriString(site_address)
                    .queryParam("apikey", apikey_value);
            if (!title.isEmpty()) {
                builder1
                        .queryParam("t", title)
                        .queryParam("y", year);
            }
            if (plot.equals("full")) {
                builder1.queryParam("plot", plot);
            }
            if (format.equals("xml")) {
                builder1.queryParam("r", format);
            }
            URI builder1URI = builder1.build().toUri();
            logger.info("URL is " + builder1URI);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(builder1URI.toURL().openStream()));
             if (in != null) {
                 //read the result
                 String inputLine;
                 while ((inputLine = in.readLine()) != null) {

                     sb.append(inputLine);
                 }
                 in.close();
             }
            // Artificial delay of 1s for demonstration purposes
            Thread.sleep(1000L);
        } catch (InterruptedException |
                 IOException e) {
            logger.error("error");
        }
        return CompletableFuture.completedFuture(sb.toString());
    }

    /**
     * Generates a structure of MS Word document
     * @param films - a list of Movie objects
     * @return a document structure
     */
    public XWPFDocument generateMsWordStructure(List<Movie> films) {
        XWPFDocument document = new XWPFDocument();
        try {
            if (films.size() == 0) throw new NullPointerException("collection of films is empty");
            logger.info("generateMsWordStructure film count: " + films.size());
            Movie film = null;
            Iterator<Movie> iter = films.iterator();
            while (iter.hasNext()) {
                film = iter.next();
                logger.info(" film: " + film.getTitle());
                //create title
                XWPFParagraph title = document.createParagraph();
                title.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun titleRun = title.createRun();
                titleRun.setText(film.getTitle());
                titleRun.setColor("009933");
                titleRun.setBold(true);
                titleRun.setFontFamily("Courier");
                titleRun.setFontSize(20);

                //write image
                XWPFParagraph image = document.createParagraph();
                image.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun imageRun = image.createRun();
                imageRun.setTextPosition(20);
                InputStream pic = new URL(film.getPoster()).openStream();
                imageRun.addPicture(pic, XWPFDocument.PICTURE_TYPE_JPEG, "image file", Units.toEMU(200), Units.toEMU(200));
                pic.close();

                //write all text
                XWPFParagraph para1 = document.createParagraph();
                para1.setAlignment(ParagraphAlignment.BOTH);
                XWPFRun para1Run = para1.createRun();
                para1Run.setText(film.toString());
            }
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        } catch (InvalidFormatException | IOException e) {
            logger.error("check URL of film poster");
            logger.error(e.getMessage());
        }
        return document;
    }

    /**
     * Creates file in file system with defined structure
     * @param document - a context necessary to write
     * @param file_path
     */
    public void saveMsWordFile(XWPFDocument document, String file_path) {
        File file = new File(file_path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            document.write(out);
            out.close();
            document.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
