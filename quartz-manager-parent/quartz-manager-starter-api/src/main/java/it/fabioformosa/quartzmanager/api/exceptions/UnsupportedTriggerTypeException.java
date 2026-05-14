package it.fabioformosa.quartzmanager.api.exceptions;

public class UnsupportedTriggerTypeException extends RuntimeException {
  public UnsupportedTriggerTypeException(String group, String name) {
    super("Trigger " + group + "." + name + " is not a SimpleTrigger");
  }
}
