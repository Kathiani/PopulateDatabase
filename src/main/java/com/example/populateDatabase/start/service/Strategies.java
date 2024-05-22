package com.example.populateDatabase.start.service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@RestController
public class Strategies{  
    public static String createCapability(){
      String name = "environment_monitor"; 
      String jsonBody = "{\n" +
                  "  \"name\": \"" + name + "\",\n" +
                  "  \"description\": \"Measure conditions of the environment\",\n" +
                  "  \"capability_type\": \"sensor\"\n" +
                  "}";

      RestTemplate restTemplate = new RestTemplate();
      String endpointUrl = "http://10.10.10.104:8000/catalog/capabilities"; 
      HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
	   ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpointUrl, requestEntity, String.class);        
      if (responseEntity.getStatusCode().is2xxSuccessful()) {
	         System.out.println("Capability was created!");
	      } else {
	         System.out.println("Error to create capability: " + responseEntity.getStatusCode());
	   }
		;

      return responseEntity.getBody();
    }

    public static String createResource(){
      String jsonBody = "{\n" +
                  "  \"data\": {\n" +
                  "    \"description\": \"Sensores de monitoramento climático do parque ecológico de São Carlos-SP-BR\",\n" +
                  "    \"capabilities\": [\n" +
                  "      \"environment_monitor\"\n" + //versão anterior era environment-monitoring
                  "    ],\n" +
                  "    \"status\": \"active\",\n" +
                  "    \"lat\": -23.559616,\n" +
                  "    \"lon\": -46.731386\n" +
                  "  }\n" +
                  "}";
      
      RestTemplate restTemplate = new RestTemplate();
      String endpointUrl =  "http://10.10.10.104:8000/adaptor/resources";
      HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
	   ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpointUrl, requestEntity, String.class);
      if (responseEntity.getStatusCode().is2xxSuccessful()) {
	                System.out.println("Resource was created!");
	   } else {
	                System.out.println("Error to create resource:" + responseEntity.getStatusCode());
	      };
				
      return responseEntity.getBody();
    }

    public static String senddataResource(String uuid){  
      ResponseEntity<String> responseEntity = ResponseEntity.ok().body("");
      int batch = 2;
      int collectioninterval = 1000; //stopping for one second
      
      for (int i = 0; i < batch; i++){
         double temperature = generateTemperature();
         double humidity = generateHumidity();
         double pressure = generatePressure();

         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
         LocalDateTime timeReading = LocalDateTime.now();
         LocalDateTime timeStoring = LocalDateTime.now().plusSeconds(3);        
         String timeReadingFormat = timeReading.format(formatter);
         String timeStoringFormat = timeStoring.format(formatter);
           
         String jsonBody = String.format(Locale.US,
            "{" +
               "\"data\": {" +
                  "\"environment_monitor\": [" +
                        "{" +
                           "\"temperature\": \"%.3f\"," + 
                           "\"humidity\": \"%.3f\"," + 
                           "\"pressure\": \"%.3f\"," + 
                           //"\"read-date\": \"%s\"," + 
                           "\"timestamp\": \"%s\"" + 
                        "}" +
                  "]" +
               "}" +
            "}", temperature, humidity, pressure, timeReadingFormat);// timeStoringFormat);
            

         RestTemplate restTemplate = new RestTemplate();
         String endpointUrl =  String.format("http://10.10.10.104:8000/adaptor/resources/" + uuid + "/data");     
         HttpHeaders headers = new HttpHeaders();
	   	headers.setContentType(MediaType.APPLICATION_JSON);
         try {
              Thread.sleep(collectioninterval);
         } catch (InterruptedException e) {
              e.printStackTrace(); 
         }

         HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
	      responseEntity = restTemplate.postForEntity(endpointUrl, requestEntity, String.class);
         if (responseEntity.getStatusCode().is2xxSuccessful()) {
	                System.out.println("Data was added to resource!");
	      } else {
	                System.out.println("Error to add data: " + responseEntity.getStatusCode());
	      };
        
      }
         
      return responseEntity.getBody();
			    
   }

   public static double generateTemperature() {
      Random random = new Random();
      int randomDecimal = random.nextInt(1000);
      double randomValue = 30 + ((double) randomDecimal / 1000);
      return randomValue;
    }

   public static double generateHumidity() {
      Random random = new Random();
      int randomDecimal = random.nextInt(1000);
      double randomValue = 30 + ((double) randomDecimal / 1000);
      return randomValue;
    }
    
   public static double generatePressure() {
      double min = 900.0;
      double max = 1100.0;
      Random rand = new Random();
      double pressureValue = min + rand.nextDouble() * (max - min);
      return pressureValue;   
   }


}