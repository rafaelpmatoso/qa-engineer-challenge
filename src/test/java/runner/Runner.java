package runner;

import org.junit.AfterClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import webdriver.DriverFactory;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, features = { "src/test/resources/features" }, glue = { "steps" }, tags = {
		"@BuscaCursos" })
public class Runner {

	@AfterClass
	public void closeDriver() {
		DriverFactory.quitDriver();
	}

}
