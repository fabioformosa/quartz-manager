package it.fabioformosa.quartzmanager.controllers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class TestUtils {

   static public ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  static public String toJson(Object object){
    return objectMapper.writeValueAsString(object);
  };

}
