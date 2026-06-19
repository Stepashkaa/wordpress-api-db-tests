package ru.simbirsoft.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    public TestConfig() {
    }

    public static String wordpressBaseUrl(){
        return getRequiredProperty("wordpress.base.url");
    }

    public static String wordpressUsername(){
        return getRequiredProperty("wordpress.username");
    }

    public static String wordpressPassword(){
        return getRequiredProperty("wordpress.password");
    }

    public static String dbUrl(){
        return getRequiredProperty("db.url");
    }

    public static String dbUsername(){
        return getRequiredProperty("db.username");
    }

    public static String dbPassword(){
        return getRequiredProperty("db.password");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = TestConfig.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)){

            if(inputStream == null) {
                throw new IllegalStateException("Файл config.properties не найден");
            }

            properties.load(inputStream);
            return properties;
        } catch (IOException ioException){
            throw new IllegalStateException("Не удалось прочитать config.properties", ioException);
        }
    }

    private static String getRequiredProperty(String propertyName){
        String propertyValue = System.getProperty(propertyName, PROPERTIES.getProperty(propertyName));

        if(propertyValue == null || propertyValue.isBlank()){
            throw new IllegalStateException("Не заполнено поле: "+ propertyName);
        }

        return propertyValue;
    }
}
