package webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverFactory {

	private static WebDriver driver;

	public static WebDriver getDriver(DriverType driverType) {
		if (driver == null) {
			driver = driverType.getWebDriverObject(new DesiredCapabilities());
		}
		return driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public static void quitDriver() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

}
