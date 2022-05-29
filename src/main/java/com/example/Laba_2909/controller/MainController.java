package com.example.Laba_2909.controller;


import com.example.Laba_2909.model.Movie;
import com.example.Laba_2909.service.InfoLoader;
import com.example.Laba_2909.service.InfoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


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

    @Autowired
    private InfoLoader infoLoader;

    @Autowired
    private InfoParser infoParser;

    @Autowired
    ConversionService conversionService;

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
     *
     * @param model
     * @param t      - title of movie
     * @param imdbID
     * @param y      - year
     * @param p      - plot
     * @param r      - response format: json or xml
     * @return start page
     */
    @PostMapping("/home")
    public String searchFilm(ModelMap model,
                             @RequestParam String t,
                             @RequestParam String imdbID,
                             @RequestParam String y,
                             @RequestParam String p,
                             @RequestParam String r)  {
        String rez = null;
        logger.info("start to send request");
         try {

        if (!imdbID.isEmpty()) {
            CompletableFuture<String> result_imdbID = infoLoader.findMovieByImdbID(site_address, apikey_value, imdbID, r);
            result_imdbID.thenAcceptAsync(i -> {
                logger.warn("finished name result_imdbID=" + result_imdbID);
            });
            film_info.add(result_imdbID.get());
           // films.add(chooseParser(r, result_imdbID.get()));
            films.add(conversionService.convert(result_imdbID.get(), Movie.class));
        }

        if (!t.isEmpty()) {
            CompletableFuture<String> result_title = infoLoader.findMovieByTitle(site_address, apikey_value, t, y, p, r);
            result_title.thenAcceptAsync(ii -> {
                logger.warn("finished name result_title=" + result_title);
            });
            film_info.add(result_title.get());
            //films.add(chooseParser(r, result_title.get()));
            films.add(conversionService.convert(result_title.get(), Movie.class));
         }

        rez = String.join("\n\n", film_info);
        model.addAttribute("description", rez);
        logger.info("rez: " + rez);

        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
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
        infoLoader.saveMsWordFile(infoLoader.generateMsWordStructure(films), file_path);
        film_info.clear();
        return "index";
    }

    /**
     * Method used to choose a parsing method
     * @param format xml or json
     * @param film_info the film info line, need to parse
     * @return Movie object
     */
  /*  private Movie chooseParser (String format, String film_info) {
        Movie film = null;
        if (format.equals("xml")) {
            film = infoParser.parseXML(film_info);
        }
        if (format.equals("json")) {
            film = infoParser.parseJson(film_info);
        }
        return film;
    }
    */
}
