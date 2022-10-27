package it.fabioformosa.quartzmanager.api.dto;

public interface RepetitionDTO {
  Integer getRepeatCount();

  Long getRepeatInterval();

  void setRepeatCount(Integer repeatCount);

  void setRepeatInterval(Long repeatInterval);
}
