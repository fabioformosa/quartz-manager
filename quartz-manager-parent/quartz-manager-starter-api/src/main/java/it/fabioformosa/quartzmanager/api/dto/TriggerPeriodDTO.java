package it.fabioformosa.quartzmanager.api.dto;

public interface TriggerPeriodDTO {
  java.util.Date getStartDate();

  java.util.Date getEndDate();

  void setStartDate(java.util.Date startDate);

  void setEndDate(java.util.Date endDate);
}
