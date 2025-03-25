package org.team3534.env;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

public enum EnvironmentConfig {
    TBA_API_KEY;

    private static Dotenv dotenv;

    @Getter private String value = getEnv(name());

    private static void loadDotEnv() {
        if (dotenv == null)
            dotenv =
                    Dotenv.configure()
                            .directory("../../../resources/main")
                            .ignoreIfMissing()
                            .load();
    }

    /**
     * Helper method to fetch an environment variable.
     *
     * @param key the environment variable name.
     * @return the value from System.getenv() or from the .env file if not present.
     */
    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (value == null) {
            loadDotEnv();
            value = dotenv.get(key);
        }
        if (value == null)
            throw new RuntimeException(
                    "Environement Variable Not Found During Loading: "
                            + key
                            + "\nCurrent Directoy: "
                            + System.getProperty("user.dir"));
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
