package it.fabioformosa.quartzmanager.jobs.myjobs;

import it.fabioformosa.quartzmanager.api.jobs.AbstractQuartzManagerJob;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import org.quartz.JobExecutionContext;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;

public class PythonJob extends AbstractQuartzManagerJob {
  @Override
  public LogRecord doIt(JobExecutionContext jobExecutionContext) {
    StringWriter writer = new StringWriter();
    ScriptContext context = new SimpleScriptContext();
    context.setWriter(writer);

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("python");
    // 1. Load as File
    File file = new File(jobExecutionContext.getTrigger().getJobDataMap().getString("file"));
    try {
      engine.eval(new FileReader(file), context);
      return new LogRecord(LogRecord.LogType.INFO, writer.toString().trim());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
