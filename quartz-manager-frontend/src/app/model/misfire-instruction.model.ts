export enum MisfireInstruction {
  MISFIRE_INSTRUCTION_FIRE_NOW = 1,
  MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT = 2,
  MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT = 3 ,
  MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT = 4,
  MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT = 5
}

export function getMisfireInstructionByIndex(index: number) {
  return Object.keys(MisfireInstruction)[index];
}

// function enumFromStringValue<T> (enm: { [s: string]: T}, value: string): T | undefined {
//   return (Object.values(enm) as unknown as string[]).includes(value)
//     ? value as unknown as T
//     : undefined;
// }
//
// export function parseMisfireInstruction(str: string): MisfireInstruction {
//   return enumFromStringValue<MisfireInstruction>(MisfireInstruction, str);
//   // return  (<any>MisfireInstruction)[str]
//   // const indexOfStr = Object.values(MisfireInstruction).indexOf(str as unknown as MisfireInstruction);
//   // const key = Object.keys(Sizes)[indexOfStr];
//   // return MisfireInstruction[k]
//   // return Object.values(MisfireInstruction).find(val => val === str);
// }

export const MisfireInstructionCaption = new Map<number, string>([
  [MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW,
    `The job is executed immediately after the scheduler discovers misfire situation.<br/>
     In case of the trigger has been set with a repeat count, this policy is equals to RESCHEDULE NOW WITH REMAINING REPEAT COUNT`
  ],
  [MisfireInstruction.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT,
    `First misfired trigger is executed immediately. Then the scheduler waits desired interval and executes all remaining triggers.<br/>
     Effectively the first fire time of the misfired trigger is moved to current time with no other changes.`
  ],
  [MisfireInstruction.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT,
    `First misfired execution runs immediately. Remaining misfired executions are discarded. Remaining not-yet-fired triggers are executed
     with desired interval, starting from the recovered misfired execution.<br/>
     Use this policy if your constraint is to honor the end date time.<br/>
     <strong>Warning</strong> The actual number of job executions could be less than initially set,
     because some of the misfired triggers are ignored. The end date time you set is always `
  ],
  [MisfireInstruction.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT,
    `In case of misfire event, the scheduler won't do anything immediately. Instead it will wait for next scheduled time the trigger and
    run all triggers with scheduled interval. Misfired trigger are simply post-poned but not ignored.<br/>
    Use this policy if your constraint is to execute the job for the all times equals to the repeation counter.<br/>' +
    '<strong>Warning</strong> The scheduler can completed over the end date time you set `],
  [MisfireInstruction.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT,
    `In case of misfire event, the trigger is re-scheduled to the next scheduled time after 'now'
    with the repeat count set to what it would be if it had not missed any firings.<br/>
    Use this policy if no jobs must run after the end date time.<br/>
    <strong>Warning</strong> The actual number of job executions could be less than initially set, because the misfired trigger are ignored.<br/>
    This policy could cause the Trigger to go directly to the 'COMPLETE' state if all fire-times where missed.`
  ]
]);
