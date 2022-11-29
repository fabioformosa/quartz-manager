package it.fabioformosa.quartzmanager.api.common.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class TryTest {

  String raiseExceptionIfHello(String greetings) throws Exception {
    if("hello".equals(greetings))
      throw new Exception("hello");
    return greetings;
  }

  @Test
  void givenAFunction_whenItRaisesAnException_thenItReturnsNull(){
    String hello = Optional.of("hello").map(Try.sneakyThrow(this::raiseExceptionIfHello)).orElse(null);
    Assertions.assertThat(hello).isNull();
  }

  @Test
  void givenAFunction_whenItDoesntRaisesAnException_thenItReturnsTheValue(){
    String hello = Optional.of("not hello").map(Try.sneakyThrow(this::raiseExceptionIfHello)).orElse(null);
    Assertions.assertThat(hello).isEqualTo("not hello");
  }

}
