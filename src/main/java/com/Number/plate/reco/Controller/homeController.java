package com.Number.plate.reco.Controller;

import java.io.File;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

@Controller
@RequestMapping("/api")
public class homeController {

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@PostMapping("/submit")
	public String sendNP(@RequestParam("img") String file, Model model) throws IOException {

		String res = setApi(file);
		String numberPLate=extractLicensePlate(res);
		System.out.println(numberPLate);
		model.addAttribute("result", numberPLate);
		return "index";
	}

	private static final String API_KEY = "ee35d474d2mshb09d834d201b03cp1d3f89jsn26775dd30a19";
	private static final String API_HOST = "zyanyatech1-license-plate-recognition-v1.p.rapidapi.com";

	public String setApi(String imageFile) {
		String API_URL = "https://zyanyatech1-license-plate-recognition-v1.p.rapidapi.com/recognize_url?image_url="
				+ imageFile;

		try {
			HttpResponse<JsonNode> response = Unirest.post(API_URL).header("Content-Type", "multipart/form-data")
					.header("X-RapidAPI-Key", API_KEY).header("X-RapidAPI-Host", API_HOST).asJson();

			int status = response.getStatus();
			JsonNode responseBody = response.getBody();
			// Handle the response based on the status and responseBody
			if (status == 200) {
				String result = responseBody.toString();
				// Process the result as needed
				return result;
			} else {
				// Handle error cases
				System.out.println("Error: " + status);
				System.out.println("Response: " + responseBody.toString());
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String extractLicensePlate(String jsonResponse) {
		@SuppressWarnings("deprecation")
		JsonParser jsonParser = new JsonParser();
		@SuppressWarnings("deprecation")
		JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

		JsonArray resultsArray = jsonObject.getAsJsonArray("results");
		if (resultsArray != null && resultsArray.size() > 0) {
			JsonObject firstResult = resultsArray.get(0).getAsJsonObject();

			JsonArray candidatesArray = firstResult.getAsJsonArray("candidates");
			if (candidatesArray != null && candidatesArray.size() > 0) {
				JsonObject firstCandidate = candidatesArray.get(0).getAsJsonObject();

				return firstCandidate.getAsJsonPrimitive("plate").getAsString();
			}
		}

		return null; // Return null if license plate not found in the response
	}

}
