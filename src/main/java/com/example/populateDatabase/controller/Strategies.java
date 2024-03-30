package com.example.populateDatabase.controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@RestController
public class Strategies{
   @GetMapping("/populate_database")
      public static String populateDatabase(){
      
      //createCapability();  //*create capability*
      String responseEntityResource = createResource(); 
      JSONObject jsonObject = new JSONObject(responseEntityResource);
      JSONObject dataObject = jsonObject.getJSONObject("data");
      String uuid = dataObject.getString("uuid");
      String responseSendData = senddataResource(uuid);  //*adicionar um uuid específico
      return responseSendData.toString();         
    }

    public static String createCapability(){
      String jsonBody = "{\n" +
                  "  \"name\": \"temperature_and_humidity_monitoring\",\n" +
                  "  \"description\": \"Measure the temperature and humidity of the environment\",\n" +
                  "  \"capability_type\": \"sensor\"\n" +
                  "}";

      RestTemplate restTemplate = new RestTemplate();
      String endpointUrl = "http://10.10.10.104:8000/catalog/capabilities"; 
      HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
	   ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpointUrl, requestEntity, String.class);

        System.out.println("Olá, mundo!");
        
         if (responseEntity.getStatusCode().is2xxSuccessful()) {
	                System.out.println("Capacidade criada com sucesso!");
	            } else {
	                System.out.println("Erro ao criar a capacidade: " + responseEntity.getStatusCode());
	            }
				;

        return responseEntity.getBody();
    }

    public static String createResource(){
      String jsonBody = "{\n" +
                  "  \"data\": {\n" +
                  "    \"description\": \"Um sensor de temperatura e umidade no campus da UFSCar\",\n" +
                  "    \"capabilities\": [\n" +
                  "      \"temperature_and_humidity_monitoring\"\n" + //versão anterior era environment-monitoring
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
	                System.out.println("Recurso criado com sucesso!");
	   } else {
	                System.out.println("Erro ao criar recurso: " + responseEntity.getStatusCode());
	      };
				
      return responseEntity.getBody();
    }

    public static String senddataResource(String uuid){  
      ResponseEntity<String> responseEntity = ResponseEntity.ok().body("");
      int batch = 1;
      int collectioninterval = 10000; //em segundos feitos teste com 1000 e 5000
      

      for (int i = 0; i < batch; i++) {
         double temperature = generateTemperature();
         double humidity = generateHumidity();
         
         LocalDateTime now = LocalDateTime.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
         String horaAtual = now.format(formatter);

         String jsonBody = String.format(
            "{" +
               "\"data\": {" +
                  "\"temperature_and_humidity_monitoring\": [" +
                        "{" +
                           "\"temperature\": \"%f\"," + 
                           "\"humidity\": \"%f\"," + 
                           "\"timestamp\": \"%s\"" + 
                        "}" +
                  "]" +
               "}" +
            "}", temperature, humidity, horaAtual);
            

         RestTemplate restTemplate = new RestTemplate();
         String endpointUrl =  String.format("http://10.10.10.104:8000/adaptor/resources/" + uuid + "/data");
         System.err.println(endpointUrl);
         System.err.println(jsonBody);
         HttpHeaders headers = new HttpHeaders();
	   	headers.setContentType(MediaType.APPLICATION_JSON);
         try {
               Thread.sleep(collectioninterval);
         } catch (InterruptedException e) {
        // Tratamento da exceção aqui, se necessário
          e.printStackTrace(); // ou qualquer outra ação desejada
         }

         HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
	      responseEntity = restTemplate.postForEntity(endpointUrl, requestEntity, String.class);
         if (responseEntity.getStatusCode().is2xxSuccessful()) {
	                System.out.println("Dado adicionado com sucesso!");
	      } else {
	                System.out.println("Erro ao adicionar dado: " + responseEntity.getStatusCode());
	      };
        
      }
         
      return responseEntity.getBody();
			    
   }

      public static double generateTemperature() {
        // Simplesmente gera um valor aleatório entre 0 e 5 para a temperatura
        int valueRandom = (int) (Math.random() * 10) + 1;
        return valueRandom * 5;
    }

    public static double generateHumidity() {
        // Simplesmente gera um valor aleatório entre 0 e 5 para a temperatura
        int valueRandom = (int) (Math.random() * 10) + 1;
        return valueRandom * 5;
    }
    


}