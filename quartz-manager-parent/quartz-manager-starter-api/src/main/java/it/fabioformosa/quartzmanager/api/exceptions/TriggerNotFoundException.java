package it.fabioformosa.quartzmanager.api.exceptions;

public class TriggerNotFoundException extends Exception {
  public TriggerNotFoundException(String name) {
    super("Trigger with name " + name + " not found!");
  }
}
