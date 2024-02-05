package it.fabioformosa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.FileReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Paths;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = QuartzManagerDemoApplication.class)
@WebAppConfiguration
class QuartManagerApplicationTests {

    @Test
    void contextLoads() {
    }

  @Test
  public void givenPythonScriptEngineIsAvailable_whenScriptInvoked_thenOutputDisplayed() throws Exception {
    StringWriter writer = new StringWriter();
    ScriptContext context = new SimpleScriptContext();
    context.setWriter(writer);

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("python");
    URL resource = getClass().getClassLoader().getResource("hello.py");
    engine.eval(new FileReader(Paths.get(resource.toURI()).toFile()), context);
    assertEquals("Should contain script output: ", "Hello Baeldung Readers!!", writer.toString().trim());
  }

}
