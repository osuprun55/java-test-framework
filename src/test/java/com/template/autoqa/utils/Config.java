package com.velti.template.utils;

import com.velti.template.core.exceptions.TestInterruptException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;



public class Config {
    private final String CONFIG_FILE_PATH = "/web.properties";

    private final Properties properties = new Properties();
    private static final Config instance = new Config();

    private Config() {
        loadConfig();
    }

    /**
     * Returns an instance of Config class
     */
    public static Config getInstance() {
        return instance;
    }

    private void loadConfig() {
        String resourcePath = GeneralUtils.getRootFolder() + CONFIG_FILE_PATH;
        FileReader propertiesFileReader = null;
        try {
            propertiesFileReader = new FileReader(resourcePath);
            properties.load(propertiesFileReader);
        } catch (IOException e) {
            throw new TestInterruptException("An exception occurred during the config loading", e);
        } finally {
            if (propertiesFileReader != null) {
                try {
                    propertiesFileReader.close();
                } catch (IOException ignoredException) {
                    // do nothing
                }
            }
        }
    }


    /**
     * Returns a value from properties file by a key. If there is no the key, en empty value is returned
     *
     * @return a value from properties file by a key
     */
    public String get(String key) {
        return properties.getProperty(key, "");
    }

}
