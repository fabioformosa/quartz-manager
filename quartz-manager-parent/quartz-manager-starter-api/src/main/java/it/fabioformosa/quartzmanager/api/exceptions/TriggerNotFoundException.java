package it.fabioformosa.quartzmanager.api.exceptions;

import lombok.ToString;

@ToString
public class TriggerNotFoundException extends Exception {
  public TriggerNotFoundException(String name) {
    super("Trigger with name " + name + " not found!");
  }
}
