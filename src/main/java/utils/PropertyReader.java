package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

	public static String get(String key) {
		File propertiesFile = new File("config.properties");
		Properties properties = new Properties();
		String property = null;
		try {
			properties.load(new FileInputStream(propertiesFile));
			property = properties.getProperty(key);
			if (property == null) {
				throw new IllegalArgumentException(
						"Property \"" + key + "\" not found in the file " + propertiesFile.getPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return property;
	}

}
