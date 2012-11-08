/**
 * 
 */
package net.jirasystems.cheeporm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * @author david
 * 
 */
public class DatabaseProperties {

	public static final String propertiesPath = "/database.properties";

	private static Properties properties;

	public static Properties getProperties() {

		if (properties == null) {

			InputStream stream = DatabaseProperties.class.getResourceAsStream(propertiesPath);

			try {
				Properties properties = new Properties();
				properties.load(stream);
				DatabaseProperties.properties = properties;
			} catch (IOException e) {
				throw new RuntimeException("Error loading properties file from " + propertiesPath, e);
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}
		return properties;
	}

	public static void main(String[] args) {
		DatabaseProperties databaseProperties = new DatabaseProperties();
		Properties properties = databaseProperties.getProperties();
		for (Object key : properties.keySet()) {
			System.out.println(key);
		}
	}
}
