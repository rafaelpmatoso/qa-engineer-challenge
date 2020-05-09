package runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, features = { "src/test/resources/features" }, glue = { "steps" }, tags = {
		"@BuscaCursos and @OrdenacaoPorValor" })
public class Runner {

}
