package es.deusto.ingenieria.ssdd.bitTorrent.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropertiesFileHandler {

    public static String getProperty(String property) {
    	Properties properties = new Properties();
 
    	try {
    		properties.load(new FileInputStream("conf/config.properties")); 
    		return properties.getProperty(property);
    	} catch (Exception ex) {
    		System.err.println("# Error getting a property: " + ex.getMessage());
        }
    	
    	return null;
    }
	
	
	public static void main(String[] args) {
		Properties prop = new Properties();

		try {
			// set the properties value
			prop.setProperty("ip", "127.0.0.1");
			prop.setProperty("port", "43611");
			// save properties to project root folder
			prop.store(new FileOutputStream("conf/config.properties"), null);

		} catch (Exception ex) {
			System.err.println("# Error creating properties file: " + ex.getMessage());
		}
	}
}