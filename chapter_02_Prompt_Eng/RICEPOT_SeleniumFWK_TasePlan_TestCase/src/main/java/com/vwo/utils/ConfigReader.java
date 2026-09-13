package com.vwo.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static ConfigReader instance;
    private Properties props;

    private ConfigReader() {
        props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) props.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfigReader getInstance() {
        if (instance == null) instance = new ConfigReader();
        return instance;
    }

    public String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isEmpty()) return sys;
        return props.getProperty(key);
    }

    public String getBaseUrl() { return get("base.url"); }
    public String getUsername() { return get("username"); }
    public String getPassword() { return get("password"); }
    public String getBrowser() { return get("browser"); }
    public int getTimeout() { try { return Integer.parseInt(get("timeout")); } catch (Exception e) { return 15; } }
}
