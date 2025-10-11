package br.com.goibankline.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PropertiesLoader {

    private static final Logger LOG = Logger.getLogger(PropertiesLoader.class.getName());

    private PropertiesLoader() {}

    public static Properties load(String file) {
        Properties props = new Properties();
        try (InputStream is =
                     PropertiesLoader.class.getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                LOG.severe(file + " não encontrado na classpath");
            } else {
                props.load(is);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Falha ao carregar " + file, e);
        }
        return props;
    }
}
