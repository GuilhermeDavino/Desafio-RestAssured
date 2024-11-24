package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private Long existId, noExistId;
	
	private String adminUsername, adminPassword;
	private String adminToken;
	private	Map<String, Object> putScoreInstance;

	
	@BeforeEach
	public void set() throws JSONException {
		baseURI="http://localhost:8080";
		
		existId=1L;
		noExistId=100L;
		
		adminUsername="maria@gmail.com";
		adminPassword="123456";
		
		adminToken=TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		
		putScoreInstance=new HashMap<>();
		putScoreInstance.put("movieId", existId);
		putScoreInstance.put("score", 5);
		
		
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		putScoreInstance.put("movieId", 100L);
		JSONObject newScore=new JSONObject(putScoreInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer "+ adminToken)
		.body(putScoreInstance)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put("/scores")
		.then()
		.statusCode(404)
		.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		putScoreInstance.put("movieId", null);
		JSONObject newScore=new JSONObject(putScoreInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer "+ adminToken)
		.body(putScoreInstance)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put("/scores")
		.then()
		.statusCode(422)
		.body("errors[0].message", equalTo("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		putScoreInstance.put("score", -1);
		JSONObject newScore=new JSONObject(putScoreInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer "+ adminToken)
		.body(putScoreInstance)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put("/scores")
		.then()
		.statusCode(422)
		.body("errors[0].message", equalTo("Valor mínimo 0"));
	}
}
