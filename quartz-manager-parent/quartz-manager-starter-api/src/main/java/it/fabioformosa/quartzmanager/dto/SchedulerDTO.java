package it.fabioformosa.quartzmanager.dto;

import org.quartz.TriggerKey;

import java.util.Set;

public class SchedulerDTO {
  private String name;
  private String instanceId;
  private Set<TriggerKey> triggerKeys;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setTriggerKeys(Set<TriggerKey> triggerKeys) {
    this.triggerKeys = triggerKeys;
  }

  public Set<TriggerKey> getTriggerKeys() {
    return triggerKeys;
  }
}
