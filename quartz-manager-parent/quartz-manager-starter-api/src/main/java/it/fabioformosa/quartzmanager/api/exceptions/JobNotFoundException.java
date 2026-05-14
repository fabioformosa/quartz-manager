package it.fabioformosa.quartzmanager.api.exceptions;

public class JobNotFoundException extends Exception {
  public JobNotFoundException(String group, String name) {
    super("Job " + group + "." + name + " not found!");
  }
}
