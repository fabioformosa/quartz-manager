package it.fabioformosa.quartzmanager.api.common.utils;

import lombok.Getter;

import java.util.function.Function;

/**
 *
 * @param <R> success type
 */
@Getter
public class Try<R> {

  private final Throwable failure;
  private final R success;

  private Try(Throwable failure, R success) {
    this.failure = failure;
    this.success = success;
  }

  public R getSuccess() {
    return success;
  }

  private static <R> Try<R> success(R r){
    return new Try<>(null, r);
  }

  private static <R> Try<R> failure(Throwable e){
    return new Try<>(e, null);
  }

  public static <T, R> Function<T, Try<R>> with(CheckedFunction<T, R> checkedFunction){
    return t -> {
      try {
        return Try.success(checkedFunction.apply(t));
      } catch (java.lang.Exception e) {
        return Try.failure(e);
      }
    };
  }

  public static <T, R> Function<T, R> sneakyThrow(CheckedFunction<T, R> checkedFunction){
   return t -> Try.with(checkedFunction).apply(t).getSuccess();
  }

  @FunctionalInterface
  public static interface CheckedFunction<T, R> {
    R apply(T t) throws java.lang.Exception;
  }
}

