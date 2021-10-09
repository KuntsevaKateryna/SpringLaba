package com.example.Laba_2909.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Movie {

    private String imdbID,
                    Title,
                    Year,
                    Rated,
                    Released,
                    Runtime,
                    Genre,
                    Director,
                    Writer,
                    Actors,
                    Plot,
                    Language,
                    Country,
                    Awards,
                    Poster;
    private List<Rating> Ratings;
    private String   Metascore,
                    imdbRating,
                    imdbVotes,
                    type,
                    DVD,
                    BoxOffice,
                    Production,
                    Website,
                    Response;
}

