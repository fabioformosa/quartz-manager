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
  void givenAFunctionWhichRaisesAnException_whenSneakyThrowIsCalled_thenItReturnsNull(){
    String hello = Optional.of("hello").map(Try.sneakyThrow(this::raiseExceptionIfHello)).orElse(null);
    Assertions.assertThat(hello).isNull();
  }

  @Test
  void givenAFunctionWhichDoesntRaiseAnException_whenSneakyThrowIsCalled_thenItReturnsTheValue(){
    String hello = Optional.of("not hello").map(Try.sneakyThrow(this::raiseExceptionIfHello)).orElse(null);
    Assertions.assertThat(hello).isEqualTo("not hello");
  }

  @Test
  void givenAFunctionWhichRaisesAnException_whenTryWithIsCalled_thenItReturnsAFailureObj(){
    Try<String> aTry = Optional.of("hello").map(greet -> Try.with(this::raiseExceptionIfHello).apply(greet)).get();
    Assertions.assertThat(aTry.getFailure()).isNotNull();
    Assertions.assertThat(aTry.getFailure().getMessage()).isEqualTo("hello");
  }

  @Test
  void givenAFunctionWhichDoesntRaiseAnException_whenTryWithIsCalled_thenItReturnsTheValue(){
    Try<String> aTry = Optional.of("not hello").map(greet -> Try.with(this::raiseExceptionIfHello).apply(greet)).get();
    Assertions.assertThat(aTry.getFailure()).isNull();
    Assertions.assertThat(aTry.getSuccess()).isEqualTo("not hello");
  }

}
