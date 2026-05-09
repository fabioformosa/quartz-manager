package it.fabioformosa.quartzmanager.api.controllers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;

public class TestUtils {

  static public ObjectMapper objectMapper = new ObjectMapper();
  static{
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @SneakyThrows
  static public String toJson(Object object){
    return objectMapper.writeValueAsString(object).replace("+00:00", "Z");
  };

}
