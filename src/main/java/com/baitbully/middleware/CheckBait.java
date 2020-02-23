package com.baitbully.middleware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import com.google.cloud.automl.v1beta1.AnnotationPayload;
import com.google.cloud.automl.v1beta1.ExamplePayload;
import com.google.cloud.automl.v1beta1.ModelName;
import com.google.cloud.automl.v1beta1.PredictResponse;
import com.google.cloud.automl.v1beta1.PredictionServiceClient;
import com.google.cloud.automl.v1beta1.TextSnippet;

public class CheckBait implements RequestStreamHandler {
	
	private class CoupledData {
		ArrayList<String> results;
		ArrayList<Double> scores;
	}
	
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
		
		String modelId = "TCN8043195838018617344";
		String projectId = "baitbully";
		String computeRegion = "us-central1";
		
		JSONParser parser = new JSONParser();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    JSONObject responseBody = new JSONObject();

	    String url = null;
	    try {
			JSONObject event = (JSONObject) parser.parse(reader);
			url = (String) event.get("message");
		} catch (ParseException e) {
			responseBody.put("statusCode", 400);
	        responseBody.put("exception", e);
		}
	    
	    ArrayList<String> titles = new ArrayList<String>();
		try {
			titles = getTitles(url);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	    
	    CoupledData results = makePrediction(titles,projectId, computeRegion,modelId);
	    JSONArray jsonArrayResults = new JSONArray();
	    JSONArray jsonArrayScores = new JSONArray();
	    
	    for(int i = 0; i < results.results.size(); i++) {
	    	jsonArrayResults.add(results.results.get(i));
	    	jsonArrayScores.add(results.scores.get(i));
	    }
	    
	    responseBody.put("results", jsonArrayResults);
	    responseBody.put("scores", jsonArrayScores);

	    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
	    writer.write(responseBody.toString());
	    writer.close();
    }
	
	public CoupledData makePrediction(ArrayList<String> titles, String projectId, String computeRegion, String modelId) throws IOException {
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<Double> scores = new ArrayList<Double>();
		try (PredictionServiceClient predictionClient = PredictionServiceClient.create()) {
		    // Get full path of model
			ModelName name = ModelName.of(projectId, computeRegion, modelId);
			
			for(int i = 0; i < titles.size();i++) {
				String content = titles.get(i);
				
				TextSnippet textSnippet =
				        TextSnippet.newBuilder().setContent(content).setMimeType("text/plain").build();
			    ExamplePayload payload = ExamplePayload.newBuilder().setTextSnippet(textSnippet).build();

			    // params is additional domain-specific parameters.
			    // currently there is no additional parameters supported.
			    Map<String, String> params = new HashMap<String, String>();
			    PredictResponse response = predictionClient.predict(name, payload, params);

			    //System.out.println("Prediction results:");
			    AnnotationPayload annotationPayload = response.getPayload(0);
				      
				System.out.println("Title:" + content);
		        System.out.println("Predicted Class name :" + annotationPayload.getDisplayName());
		        System.out.println(
		            "Predicted Class Score :" + annotationPayload.getClassification().getScore());
		        results.add(annotationPayload.getDisplayName());
		        scores.add((double) annotationPayload.getClassification().getScore());
			}
		}
		CoupledData data = new CoupledData();
		data.results = results;
		data.scores = scores;
		return data;
	}
	
	/*public static ArrayList<String> getTitles(String url) throws Exception {
      String html = getHTML(url);
      ArrayList<String> bruh = new ArrayList<>();
      int begin = 0;
      int stringStart;
      String key = "BNeawe vvjwJb AP7Wnd";
      String title = "";
      for (int i = 0; i < 10; i++)   {
         stringStart = html.indexOf(key, begin) + 2 + key.length();
         title = html.substring(stringStart, html.indexOf("<", stringStart));
         while(title.contains("&#")) {
            title = title.replaceAll(title.substring(title.indexOf("&#"), title.indexOf("&#") + 7 ), "'");
         }
         bruh.add(title);
         begin = stringStart;
      }
      return bruh;
   }*/
	
	
   public static ArrayList<String> getTitles(String url) throws Exception	{
	   String html = getHTML(url);
	   ArrayList<String> titles = new ArrayList<>();
	   String key = "BNeawe vvjwJb AP7Wnd";
	   String title = "";
	   int stringStart;
	   while (html.contains(key))	{
		   stringStart = html.indexOf(key) + 2 + key.length();
		   title = html.substring(stringStart, html.indexOf("<", stringStart));
		   while(title.contains("&#")) {
	            title = title.replaceAll(title.substring(title.indexOf("&#"), title.indexOf("&#") + 7 ), "'");
	       }
		   html = html.substring(stringStart + title.length());
		   titles.add(title);
	   }
	   return titles;
   }
   
   public static String getHTML(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.setReadTimeout(500000);
      conn.setConnectTimeout(500000);

      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
         result.append(line);
      }
      rd.close();
      return result.toString();
   }
   
   
}
