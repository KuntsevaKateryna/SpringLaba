package com.example.Laba_2909.controller;

import com.sun.net.httpserver.HttpsParameters;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.TextUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/main")
public class MainController {

    Logger logger = LoggerFactory.getLogger(MainController.class);
    List<String> film_info = new ArrayList<String>();

    @GetMapping("/home")
    public String defaultPage(Model model) {
logger.info("ok");
        return "index";
    }

    @PostMapping("/home")
    public String searchFilm(ModelMap model,
                                   @RequestParam String t,
                                   @RequestParam String y,
                                   @RequestParam String p,
                                   @RequestParam String r) throws URISyntaxException, IOException {
        logger.info("start to send request");

 UriComponentsBuilder builder1 = UriComponentsBuilder.fromUriString("http://www.omdbapi.com")
                // Add query parameter
                .queryParam("i", "tt3896198")
                .queryParam("apikey", "30e949fb")
                .queryParam("t", t)
                .queryParam("y", y);
        if (p.equals("full")) {
            builder1.queryParam("p", p);
        }
        if (r.equals("xml")) {
            builder1.queryParam("r", r);
        }
        URI builder1URI = builder1.build().toUri();
        System.out.println("URL is " + builder1URI);
        logger.info("URL is " + builder1URI);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(builder1URI.toURL().openStream()));
/*
read the result
 */


        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            film_info.add(inputLine);

        }
        in.close();
        String rez = String.join("\n\n", film_info);
       /* String rez =  film_info.stream().map(Object::toString)
                .collect(Collectors.joining("// "));*/

 model.addAttribute("description", rez);
 System.out.println("rez: " + rez);
       return "index";
    }


    private void createFile (XWPFDocument doc) throws IOException {
        File file = new File("/film.docx");
        file.delete();
        file.createNewFile();
        doc.write(new FileOutputStream(file));
        doc.close();
    }

}
