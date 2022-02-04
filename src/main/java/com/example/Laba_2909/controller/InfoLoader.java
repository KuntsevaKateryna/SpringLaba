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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class InfoLoader {
    Logger logger = LoggerFactory.getLogger(InfoLoader.class);

    public XWPFDocument generateMsWordStructure(Movie film) throws URISyntaxException, IOException, InvalidFormatException {
        XWPFDocument document = new XWPFDocument();
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

        return document;
    }

    //generating msWord
    public void saveMsWordFile(XWPFDocument document, String file_path) throws IOException {
        File file = new File(file_path);
        //  System.out.println(file.getAbsolutePath().toString());
        FileOutputStream out = new FileOutputStream(file);
        document.write(out);
        out.close();
        document.close();
    }
}
