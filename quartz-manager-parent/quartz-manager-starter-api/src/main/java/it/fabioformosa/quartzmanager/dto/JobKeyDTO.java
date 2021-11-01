package it.fabioformosa.quartzmanager.dto;

import lombok.Builder;

@Builder
public class JobKeyDTO {
  private String name;
  private String group;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getGroup() {
    return group;
  }
}
