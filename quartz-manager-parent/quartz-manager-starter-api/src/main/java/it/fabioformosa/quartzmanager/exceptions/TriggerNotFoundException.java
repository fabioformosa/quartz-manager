package it.fabioformosa.quartzmanager.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class TriggerNotFoundException extends Exception {

  private String name;

  public TriggerNotFoundException(String name) {
    super("Trigger with name " + name + " not found!");
    this.name = name;
  }
}
