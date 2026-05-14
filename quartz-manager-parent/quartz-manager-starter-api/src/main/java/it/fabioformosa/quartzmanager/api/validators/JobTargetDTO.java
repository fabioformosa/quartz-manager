package it.fabioformosa.quartzmanager.api.validators;

import it.fabioformosa.quartzmanager.api.dto.JobKeyDTO;

public interface JobTargetDTO {
  String getJobClass();
  JobKeyDTO getJobKey();
}
