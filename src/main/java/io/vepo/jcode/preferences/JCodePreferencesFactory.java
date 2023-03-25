package io.vepo.jcode.preferences;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.prefs.PreferencesFactory;

public class JCodePreferencesFactory implements PreferencesFactory {
    private static final Logger logger = Logger.getLogger(JCodePreferencesFactory.class.getName());
    private static File preferencesFile;

    public static File getPreferencesFile() {
        if (preferencesFile == null) {
            Path prefsFile = Paths.get(System.getProperty("user.home"), ".jcode", "settings.json");
            if (!prefsFile.toFile().exists()) {
                prefsFile.toFile().getParentFile().mkdirs();
            }

            preferencesFile = prefsFile.toFile();
            logger.finer("Preferences file is " + preferencesFile);
        }
        return preferencesFile;
    }

    private JsonPreferences rootPreferences;

    public JsonPreferences systemRoot() {
        return userRoot();
    }

    public JsonPreferences userRoot() {
        if (rootPreferences == null) {
            logger.finer("Instantiating root preferences");

            rootPreferences = new JsonPreferences();
        }
        return rootPreferences;
    }

    private static final AtomicReference<JCodePreferencesFactory> INSTANCE = new AtomicReference<>();

    public static JCodePreferencesFactory preferences() {
        return INSTANCE.updateAndGet(value -> {
            if (Objects.isNull(value)) {
                value = new JCodePreferencesFactory();
            }
            return value;
        });
    }
}