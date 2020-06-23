package coviddata;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/*
 Endpoint : https://covid-19.dataflowkit.com/v1
 Details: The above end point return the COVID 19  world wide data
 */

public class Get_Covid_Data {
	@Test
	public void getCovid19Data() {
		RestAssured.baseURI = "https://covid-19.dataflowkit.com/v1";
		// Getting Covid 19
		Response response = RestAssured.given().accept(ContentType.JSON).get();
		JsonPath jsonResponse = response.jsonPath();
		
		// Get the whole response in the data list which a map of list datatype
		List<Map<Object, Object>> data = jsonResponse.getList("");
		
		// Getting the new cases in the reverse order as we required Top 5 new cases count
		TreeMap<Object, Object> newCases = new TreeMap<Object, Object>(Collections.reverseOrder());
		
		// Getting the death count as we required Top 5 new cases count
		TreeMap<Object, Object> deathCount = new TreeMap<Object, Object>();
		for (Map<Object, Object> eachResult : data) {
			if (eachResult.containsKey("New Cases_text")
					&& eachResult.get("New Cases_text").toString().replaceAll("\\D", "").length() > 1) {
				newCases.put(Integer.parseInt(eachResult.get("New Cases_text").toString().replaceAll("\\D", "")),
						eachResult.get("Country_text"));

			}
			if (eachResult.containsKey("New Deaths_text")
					&& eachResult.get("New Deaths_text").toString().replaceAll("\\D", "").length() > 1) {
				deathCount.put(Integer.parseInt(eachResult.get("New Deaths_text").toString().replaceAll("\\D", "")),
						eachResult.get("Country_text"));

			}
		}
		
		// Delete the world record
		newCases.pollFirstEntry();
		int resultCounter = 0;
		System.out.println(
				"--------------------- Covid Status of Top 5 Countries with Highest New Cases ------------------");
		for (Entry<Object, Object> entry : newCases.entrySet()) {
			if (resultCounter < 5) {
				resultCounter++;
				System.out.println("Number " + resultCounter + " : " + entry.getValue() + " With " + entry.getKey()
				+ " new cases.");
			} else {
				break;
			}
		}
		
		// Delete the world record
		deathCount.pollLastEntry();
		int deathCounter = 0;
		System.out.println(
				"--------------------- Covid Status of Top 5 Countries with Lowest New Death ------------------");
		for (Entry<Object, Object> entry : deathCount.entrySet()) {
			if (deathCounter < 5) {
				deathCounter++;
				System.out.println("Number " + deathCounter + " : " + entry.getValue() + " With " + entry.getKey()
				+ " new deaths.");
			} else {
				break;
			}
		}
		Response indiaStatus = RestAssured.given().get("india");
		// Getting Covid 19 for country india 
		System.out.println("--------------------- Covid Status of India ------------------");
		indiaStatus.prettyPrint();
		if (response.getStatusCode() == 200) {
			System.out.println("Expected and Actual status code are same");
		} else {
			System.out.println("Expected and Actual status code are not same");
		}
		if (response.getContentType().contentEquals("application/json")) {
			System.out.println("Expected and Actual content type are same");
		} else {
			System.out.println("Expected and Actual content type are not same");
		}

		if (response.getTime() < 600) {
			System.out.println("Expected and Actual response time matches the condition " + "Actual response time is "
					+ response.getTime());

		} else {
			System.out.println("Expected and Actual response time are not matching the condition "
					+ "Actual response time is " + response.getTime());
		}

	}

}
