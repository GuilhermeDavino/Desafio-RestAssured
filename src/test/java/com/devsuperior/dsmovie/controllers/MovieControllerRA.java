package com.devsuperior.dsmovie.controllers;


import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private Long existId, noExistId;
	
	private String adminUsername, adminPassword, clientUsername, clientPassword;
	private String adminToken, clientToken, invalidToken;
	
	private Map<String, Object> postMovieInstance;
	
	
	@BeforeEach
	public void set() throws JSONException {
		baseURI="http://localhost:8080";
		existId=2L;
		noExistId=100L;
		
		adminUsername="maria@gmail.com";
		adminPassword="123456";
		clientUsername="alex@gmail.com";
		clientPassword="123456";
		
		adminToken=TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken=TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken=adminToken+"xpta"; //invalid password
		
		postMovieInstance=new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");

	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		
		given()
		.get("/movies")
		.then()
		.statusCode(200)
		.body("content[0].id", is(1))
		.body("content[0].title", equalTo("The Witcher"))
		.body("content[0].score", is(4.5F))
		.body("content[0].count", is(2))
		.body("content[0].image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
		
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {	
		String title="O Espetacular Homem-Aranha 2: A Ameaça de Electro";
		
		given()
		.get("/movies?title={title}", title )
		.then()
		.statusCode(200)
		.body("content[0].id", is(3))
		.body("content[0].title", equalTo(title))
		.body("content[0].score", is(0.0F))
		.body("content[0].count", is(0))
		.body("content[0].image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/u7SeO6Y42P7VCTWLhpnL96cyOqd.jpg"));
		
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		
		given()
		.get("movies/{id}", existId)
		.then()
		.statusCode(200)
		.body("id", is(2))
		.body("title", equalTo("Venom: Tempo de Carnificina"))
		.body("score", is(3.3F))
		.body("count", is(3))
		.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/vIgyYkXkg6NC2whRbYjBD7eb3Er.jpg"));

	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		
		given()
		.get("movies/{id}", noExistId)
		.then()
		.statusCode(404)
		.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		postMovieInstance.put("title", "");
		JSONObject newMovie=new JSONObject(postMovieInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer "+ adminToken)
		.body(newMovie)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post("movies")
		.then()
		.statusCode(422)
		.body("errors.message", hasItems("Tamanho deve ser entre 5 e 80 caracteres", "Campo requerido"));
			
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie=new JSONObject(postMovieInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer "+ clientToken)
		.body(newMovie)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post("movies")
		.then()
		.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie=new JSONObject(postMovieInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer "+ invalidToken)
		.body(newMovie)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post("movies")
		.then()
		.statusCode(401);
	}
}
