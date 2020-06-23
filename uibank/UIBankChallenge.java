package uibank;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/*
 Application Link : https://uibank.uipath.com/
 Manual Process: Create an account in the above link and verify the email address
 
   Test -1
   1. Login to the application
   2. Get and print the user details
   
   Test -2 
   1. Apply for the loan from the user registered.
   2. Get and print the loan status and details
 */

public class UIBankChallenge {
	
	// The following variables are used across methods in this file, so assigned as class variables
	public String accessToken;
	public File loginData, loanData;

	@BeforeTest
	public void setData() {
		
		// Initializing base URI and fetching the files for passing in body from project location
		RestAssured.baseURI = "https://uibank-api.azurewebsites.net/api";
		loginData = new File("./login.json");
		loanData = new File("./applyLoan.json");
	}

	@Test(priority = 0)
	public void loginToUIBankAndGetAccountDetails() {
		
		// Logging in to the application
		Response response = RestAssured.given().contentType(ContentType.JSON).body(loginData).post("users/login");
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath jsonRes = response.jsonPath();
		
		// Saving the access token for further tests
		accessToken = jsonRes.getString("id");
		
		// Fetching the user id for getting user details
		String userId = jsonRes.getString("userId");
		Response userDetails = RestAssured.given().header("authorization", "Bearer " + accessToken)
				.contentType(ContentType.JSON).get("users/" + userId);
		System.out.println("User details");
		userDetails.prettyPrint();
	}

	@Test(priority = 1)
	public void applyLoanAndGetLoanStatus() {
		
		// Applying for loan
		Response response = RestAssured.given().contentType(ContentType.JSON).body(loanData).post("quotes/newquote");
		response.prettyPrint();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath jsonRes = response.jsonPath();
		
		// Fetching and saving the quote id from the applied loan to get the loan details
		String quoteID = jsonRes.getString("quoteid");
		Response loanDetails = RestAssured.given().contentType(ContentType.JSON).get("quotes/" + quoteID);
		System.out.println("Loan details");
		loanDetails.prettyPrint();

	}
}
