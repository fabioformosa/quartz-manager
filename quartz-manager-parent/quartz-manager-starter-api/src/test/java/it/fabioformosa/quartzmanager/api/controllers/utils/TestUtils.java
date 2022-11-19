package it.fabioformosa.quartzmanager.api.controllers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.SneakyThrows;

public class TestUtils {

  static public ObjectMapper objectMapper = new ObjectMapper();
  static{
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true)); // StdDateFormat is ISO8601 since jackson 2.9
  }

  @SneakyThrows
  static public String toJson(Object object){
    return objectMapper.writeValueAsString(object);
  };

}
