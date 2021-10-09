package com.example.Laba_2909.controller;


import com.example.Laba_2909.model.Movie;
import com.example.Laba_2909.model.Rating;
import com.google.gson.Gson;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


@Controller
@RequestMapping("/main")
public class MainController {

    Logger logger = LoggerFactory.getLogger(MainController.class);


   @Value("${apikey_value}")
    private String apikey_value;

    @Value("${file_path}")
    private String file_path;

    @Value("${site_address}")
    private String site_address;



    List<String> film_info = new ArrayList<String>();
    Movie film = null;

    static String[] fieldsNameArray = {"imdbID",
            "Title",
            "Year",
            "Rated",
            "Released",
            "Runtime",
            "Genre",
            "Director",
            "Writer",
            "Actors",
            "Plot",
            "Language",
            "Country",
            "Awards",
            "Poster",
            "Metascore",
            "imdbRating",
            "imdbVotes",
            "type",
            "DVD",
            "BoxOffice",
            "Production",
            "Website",
            "Response"};


    @GetMapping("/home")
    public String defaultPage(Model model) {
        logger.info("ok");
        System.out.println("film_info.size() " + film_info.size());
        return "index";
    }

    @PostMapping("/home")
    public String searchFilm(ModelMap model,
                             @RequestParam String t,
                             @RequestParam String imdbID,
                             @RequestParam String y,
                             @RequestParam String p,
                             @RequestParam String r) throws URISyntaxException, IOException {
        String rez = null;
        logger.info("start to send request");

        UriComponentsBuilder builder1 = UriComponentsBuilder.fromUriString(site_address)
                .queryParam("apikey", apikey_value);


        if (imdbID != null) {

             builder1
                   .queryParam("i", imdbID);
        }
        if (t != null) {
            builder1
                    .queryParam("t", t)
                    .queryParam("y", y);
        }

            // Add query parameter

            if (p.equals("full")) {
                builder1.queryParam("plot", p);
            }
            if (r.equals("xml")) {
                builder1.queryParam("r", r);
            }
            URI builder1URI = builder1.build().toUri();
            logger.info("URL is " + builder1URI);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(builder1URI.toURL().openStream()));

/*
read the result
 */
        String inputLine;
        while ((inputLine = in.readLine()) != null) {

            film_info.add(inputLine);

            if (r.equals("xml")) {
                System.out.println("before parseXML filmInfo: " + inputLine);
                try {

                    //  parseXML(builder1.toUriString());
                    film = parseXML(inputLine);

                } catch (IOException | SAXException e) {
                    e.printStackTrace();
                }
            }
            if (r.equals("json")) {
                // parseJSON(inputLine);
                film = parseJson_toMovie(inputLine);
                System.out.println("parseJson_toMovie filmInfo: " + film.toString());
            }
        }
        in.close();
        rez = String.join("\n\n", film_info);
        model.addAttribute("description", rez);

        System.out.println("rez: " + rez);
        return "index";
    }


    @PostMapping("/save")
    public String saveInfo() {
        try {
            if (film != null)
                createFile(loadIntoMsWordFormat(film));
            else logger.info("Search film before");
        } catch (IOException | URISyntaxException | InvalidFormatException e) {
            logger.error(e.toString());
        }
        return "index";
    }


 /*   private Movie parseGSON(String jsonString) {
        Gson g = new Gson();
        Movie film = g.fromJson(jsonString, Movie.class);
        return film;
    }
    */

    //deprecated
    private void parseJson(String jsonString) {
        try {
            Object obj = new JSONParser().parse(jsonString);
            JSONObject jo = (JSONObject) obj;
            String _Title = (String) jo.get("Title");
            String _Year = (String) jo.get("Year");
            String _Released = (String) jo.get("Released");
            String _Genre = (String) jo.get("Genre");
            String _Runtime = (String) jo.get("Runtime");
            String _Director = (String) jo.get("Director");
            String _Writer = (String) jo.get("Writer");
            String _Actors = (String) jo.get("Actors");
            String _Plot = (String) jo.get("Plot");
            String _Language = (String) jo.get("Language");
            String _Country = (String) jo.get("Country");
            String _Awards = (String) jo.get("Awards");
            String _Poster = (String) jo.get("Poster");

// to get array Ratings []
            JSONArray getRatings = (JSONArray) jo.get("Ratings");
            Iterator iter = getRatings.iterator();

// to output array items
            while (iter.hasNext()) {
                JSONObject raitings = (JSONObject) iter.next();

                String _Source = raitings.get("Source").toString();
                String _Value = raitings.get("Value").toString();
            }
            String _Metascore = (String) jo.get("Metascore");
            String _imdbRating = (String) jo.get("imdbRating");
            String _imdbVotes = (String) jo.get("imdbVotes");
            String _imdbID = (String) jo.get("imdbID");
            String _Type = (String) jo.get("Type");
            String _DVD = (String) jo.get("DVD");
            String _BoxOffice = (String) jo.get("BoxOffice");
            String _Production = (String) jo.get("Production");
            String _Website = (String) jo.get("Website");

        } catch (ClassCastException | ParseException e) {
            logger.info("impossiable to parse: " + jsonString);
        }

    }

    private Movie parseJson_toMovie(String jsonString) {
        Movie film = new Movie();
        List<Rating> rating = new ArrayList<Rating>();
        film.setRatings(rating);
        try {
            Object obj = new JSONParser().parse(jsonString);
            JSONObject jo = (JSONObject) obj;

            Class cls = film.getClass();
            String currentFieldName = null;
            for (int i = 0; i < fieldsNameArray.length; i++) {
                currentFieldName = fieldsNameArray[i];
                Method[] methods = cls.getDeclaredMethods();
                for (Method m : methods) {
                    if (m.getName().equalsIgnoreCase("set" + currentFieldName)) {
                        m.invoke(film, (String) jo.get(currentFieldName));
                    }
                }
            }
            // to get array Ratings []
            JSONArray getRatings = (JSONArray) jo.get("Ratings");
            Iterator iter = getRatings.iterator();
// to output array items
            while (iter.hasNext()) {
                JSONObject raitings = (JSONObject) iter.next();
                rating.add(new Rating(

                                raitings.get("Source").toString(),
                                raitings.get("Value").toString()
                        )
                );
                film.setRatings(rating);
            }

        } catch (ClassCastException | ParseException | IllegalAccessException | /*NoSuchMethodException | */ InvocationTargetException e) {
            e.printStackTrace();
            //  logger.info("impossiable to parse: " + jsonString);
        }
        return film;
    }


      /*  private Movie parseXML_new(String xmlString) throws JAXBException {
 JAXBContext jaxbContext = JAXBContext.newInstance(Movie.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        //Movie  film = (Movie) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
        Movie  film = (Movie) jaxbUnmarshaller.unmarshal("Node");
        return film;
    }*/


    private Movie parseXML(String xmlString) throws IOException, SAXException {
        // створення DOM-аналізатора
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Movie film = new Movie();
        List<Rating> rating = new ArrayList<Rating>();
        film.setRatings(rating);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            Element root = doc.getDocumentElement(); // звернення до кореневого вузла
            XPathFactory xpFactory = XPathFactory.newInstance();
            XPath path = xpFactory.newXPath();

            Class cls = film.getClass();
            String currentFieldName = null;
            NodeList nodes1 = null;

            for (int i = 0; i < fieldsNameArray.length; i++) {
                currentFieldName = fieldsNameArray[i];
                Method[] methods = cls.getDeclaredMethods();
                nodes1 = (NodeList) path.evaluate("/root/movie/@" + fieldsNameArray[i].toLowerCase(), doc, XPathConstants.NODESET);

                for (int ii = 0; ii < nodes1.getLength(); ii++) {
                    System.out.println(nodes1.item(ii).getNodeName() + ": " + nodes1.item(ii).getTextContent());
                    for (Method m : methods) {
                        if (m.getName().equalsIgnoreCase("set" + currentFieldName)) {
                            m.invoke(film, (String) nodes1.item(ii).getTextContent());
                        }
                    }
                }
                // to get array Ratings []
                NodeList raitingNodeList = (NodeList) path.evaluate("/root/movie/@ratings", doc, XPathConstants.NODESET);
                Node someth = null;
                if (raitingNodeList != null) {
                    for (int z = 0; z < raitingNodeList.getLength(); z++) {
                        someth = raitingNodeList.item(z);
                        rating.add(
                                new Rating(
                                        someth.getNodeName().equals("source") ? someth.getTextContent() : null,
                                        someth.getNodeName().equals("value") ? someth.getTextContent() : null
                                )
                        );
                        film.setRatings(rating);
                    }
                }
            }
        } catch (IllegalAccessException | XPathExpressionException | ParserConfigurationException | InvocationTargetException e) {
            logger.error(e.getMessage());
        }
        System.out.println("film :" + film.toString());
        return film;
    }


    private XWPFDocument loadIntoMsWordFormat(Movie film) throws URISyntaxException, IOException, InvalidFormatException {
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
    private void createFile(XWPFDocument document) throws IOException {
        File file = new File(file_path);
        //  System.out.println(file.getAbsolutePath().toString());
        FileOutputStream out = new FileOutputStream(file);
        document.write(out);
        out.close();
        document.close();
    }
}
