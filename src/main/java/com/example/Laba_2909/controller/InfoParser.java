package com.example.Laba_2909.controller;

import com.example.Laba_2909.model.Movie;
import com.example.Laba_2909.model.Rating;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InfoParser {

    Logger logger = LoggerFactory.getLogger(InfoParser.class);
    public static String[] fieldsNameArray = {"imdbID",
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


    public Movie parseJson(String jsonString) {
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


    public Movie parseXML(String xmlString) throws IOException, SAXException {
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



      /*  private Movie parseXML_new(String xmlString) throws JAXBException {
 JAXBContext jaxbContext = JAXBContext.newInstance(Movie.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        //Movie  film = (Movie) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
        Movie  film = (Movie) jaxbUnmarshaller.unmarshal("Node");
        return film;
    }


   private Movie parseGSON(String jsonString) {
        Gson g = new Gson();
        Movie film = g.fromJson(jsonString, Movie.class);
        return film;
    }



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

*/
}
