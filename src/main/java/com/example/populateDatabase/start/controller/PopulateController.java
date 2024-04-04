package com.example.populateDatabase.start.controller;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.populateDatabase.start.service.*;

public class PopulateController {

      @GetMapping("/populate_database")
      public static String populateDatabase(){
      
      //String responseEntityCapability = Strategies.createCapability();  //*create capability*
      String responseEntityResource = Strategies.createResource(); 
      JSONObject jsonObject = new JSONObject(responseEntityResource);
      JSONObject dataObject = jsonObject.getJSONObject("data");
      String uuid = dataObject.getString("uuid");
      String responseSendData = Strategies.senddataResource(uuid);  //*adicionar recurso com um uuid específico
      return "Dados permeados na base";         
    }
    
}
