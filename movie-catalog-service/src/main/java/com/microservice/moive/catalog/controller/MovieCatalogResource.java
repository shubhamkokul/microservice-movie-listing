package com.microservice.moive.catalog.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.moive.catalog.model.CatalogItem;
import com.microservice.moive.catalog.model.Movie;
import com.microservice.moive.catalog.model.Rating;
import com.microservice.moive.catalog.model.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private WebClient.Builder webClientBuilder;

	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

		List<CatalogItem> result = new ArrayList<>();
		UserRating userRating = restTemplate.getForObject("http://movie-rating-service/ratingsdata/users/" + userId,
				UserRating.class);
		
		List<Rating> ratings = userRating.getRatings();
		
		for (int i = 0; i < ratings.size(); i++) {
			Rating rating = ratings.get(i);
			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
			/*
			 * Movie movie =
			 * webClientBuilder.build().get().uri("http://localhost:8082/movies/" +
			 * rating.getMovieId()).retrieve() .bodyToMono(Movie.class).block();
			 */
			CatalogItem catalogItem = new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
			result.add(catalogItem);
		}
		return result;

	}

}
