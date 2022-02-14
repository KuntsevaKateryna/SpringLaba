package com.example.Laba_2909.controller;

import com.example.Laba_2909.model.Movie;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class InfoLoader {
    Logger logger = LoggerFactory.getLogger(InfoLoader.class);

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

    //generating msWord
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
