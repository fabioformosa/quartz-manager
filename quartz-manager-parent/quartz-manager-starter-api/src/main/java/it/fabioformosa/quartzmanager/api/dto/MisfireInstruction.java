package it.fabioformosa.quartzmanager.api.dto;

import lombok.Getter;

import java.util.Arrays;

public enum MisfireInstruction {
  MISFIRE_INSTRUCTION_FIRE_NOW(1),
  MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT(2),
  MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT(3),
  MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT(4),
  MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT(5);

  @Getter
  private int num;

  MisfireInstruction(int num) {
    this.num = num;
  }

  public static MisfireInstruction parseInt(int num) {
    return Arrays.stream(MisfireInstruction.values())
      .filter(misfireInstruction -> misfireInstruction.getNum() == num)
      .findFirst().orElseThrow(() -> new IllegalArgumentException(num + " is not a valid misfire instruction code!"));
  }

}
