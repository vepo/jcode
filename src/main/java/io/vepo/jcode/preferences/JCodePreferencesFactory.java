package io.vepo.jcode.preferences;

import static java.util.Objects.isNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.PreferencesFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCodePreferencesFactory implements PreferencesFactory {
    private static final Logger logger = LoggerFactory.getLogger(JCodePreferencesFactory.class);
    private static File preferencesFile;

    public static File getPreferencesFile() {
        if (preferencesFile == null) {
            var prefsFile = Paths.get(System.getProperty("user.home"), ".jcode", "settings.json");
            if (!prefsFile.toFile().exists()) {
                prefsFile.toFile().getParentFile().mkdirs();
            }

            preferencesFile = prefsFile.toFile();
            logger.info("Preferences file is {}", preferencesFile);
        }
        return preferencesFile;
    }

    private JsonPreferences rootPreferences;

    public JsonPreferences systemRoot() {
        return userRoot();
    }

    public JsonPreferences userRoot() {
        if (rootPreferences == null) {
            logger.info("Instantiating root preferences");

            rootPreferences = new JsonPreferences();
        }
        return rootPreferences;
    }

    private static final AtomicReference<JCodePreferencesFactory> INSTANCE = new AtomicReference<>();

    public static JCodePreferencesFactory preferences() {
        return INSTANCE.updateAndGet(value -> {
            if (isNull(value)) {
                value = new JCodePreferencesFactory();
            }
            return value;
        });
    }
}