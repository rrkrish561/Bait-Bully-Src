package com.baitbully.middleware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
		
		String modelId = "TCN8043195838018617344";
		String projectId = "baitbully";
		String computeRegion = "us-central1";
		
		JSONParser parser = new JSONParser();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    JSONObject responseBody = new JSONObject();
	    //JSONArray titles = new JSONArray();
	    String url = null;
	    try {
			JSONObject event = (JSONObject) parser.parse(reader);
			url = (String) event.get("message");
		} catch (ParseException e) {
			responseBody.put("statusCode", 400);
	        responseBody.put("exception", e);
		}
	    ArrayList<String> titles = getTitles(url); 
	    
	    ArrayList<String> results = makePrediction("Moms are single and ready to mingle",projectId, computeRegion,modelId);
	    responseBody.put("results", results.get(0));

	    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
	    writer.write(responseBody.toString());
	    writer.close();
    }
	
	public ArrayList<String> makePrediction(String title, String projectId, String computeRegion, String modelId) throws IOException {
		ArrayList<String> results = null;
		try (PredictionServiceClient predictionClient = PredictionServiceClient.create()) {
			
		    // Get full path of model
			ModelName name = ModelName.of(projectId, computeRegion, modelId);

		    // Read the file content for prediction.
		    String content = title;

		    // Set the payload by giving the content and type of the file.
		    TextSnippet textSnippet =
		        TextSnippet.newBuilder().setContent(content).setMimeType("text/plain").build();
		    ExamplePayload payload = ExamplePayload.newBuilder().setTextSnippet(textSnippet).build();

		    // params is additional domain-specific parameters.
		    // currently there is no additional parameters supported.
		    Map<String, String> params = new HashMap<String, String>();
		    PredictResponse response = predictionClient.predict(name, payload, params);

		    //System.out.println("Prediction results:");
		      
		      for (AnnotationPayload annotationPayload : response.getPayloadList()) {
//		        System.out.println("Predicted Class name :" + annotationPayload.getDisplayName());
//		        System.out.println(
//		            "Predicted Class Score :" + annotationPayload.getClassification().getScore());
		    	results.add(annotationPayload.getDisplayName());
		      }
		    }
		
		return results;
	}
	
	public ArrayList<String> getTitles(String url) {
		
		
		return null;
	}
}
