package com.example.Laba_2909.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Film {

    private Long imdbID;

    private int year;
    private String title,
                    rated,
                    runtime,
                    genre,
                    director,
                    writer,
                    actors,
                    plot,
                    language,
                    country,
                    awards,
                    poster,
                    metascore,
                    imdbRating,
                    imdbVotes,
                    type;


}
