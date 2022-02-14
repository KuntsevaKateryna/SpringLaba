package com.example.Laba_2909.controller;


import com.example.Laba_2909.model.Movie;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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
    List<Movie> films = new ArrayList<Movie>();

    /**
     * Show start page
     * @param model
     * @return start page
     */
    @GetMapping("/home")
    public String defaultPage(Model model) {
        return "index";
    }

    /**
     * Search film info and parse it in necessary format
     * @param model
     * @param t - title of movie
     * @param imdbID
     * @param y - year
     * @param p - plot
     * @param r - response format: json or xml
     * @return start page
     */
    @PostMapping("/home")
    public String searchFilm(ModelMap model,
                             @RequestParam String t,
                             @RequestParam String imdbID,
                             @RequestParam String y,
                             @RequestParam String p,
                             @RequestParam String r) {
        String rez = null;
        logger.info("start to send request");
        try {
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

            //read the result
            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                film_info.add(inputLine);
                InfoParser parser = new InfoParser();

                if (r.equals("xml")) {
                    films.add(parser.parseXML(inputLine));

                }
                if (r.equals("json")) {
                    films.add(parser.parseJson(inputLine));
                }
            }
            in.close();
            rez = String.join("\n\n", film_info);
            model.addAttribute("description", rez);
            logger.info("rez: " + rez);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
       return "index";
    }

    /**
     * Save movie into MS Word file
     * @return Start page
     */
    @PostMapping("/save")
    public String saveInfo() {
        InfoLoader loader = new InfoLoader();
        loader.saveMsWordFile(loader.generateMsWordStructure(films), file_path);
        film_info.clear();
        return "index";
    }
}
