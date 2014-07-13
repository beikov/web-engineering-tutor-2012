package achecker;

import com.mysql.management.MysqldResource;
import com.mysql.management.MysqldResourceI;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Christian
 */
public class DatabaseInitializer implements ServletContextListener {

    private static final int TEMP_DIR_ATTEMPTS = 10000;
    private MysqldResource mysqldResource;

    public DatabaseInitializer() {
        super();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        File databaseDir = createTempDir();
        int port = 3336;
        String user = "achecker";
        String password = "achecker";
        String url = "jdbc:mysql://localhost:3336/achecker?createDatabaseIfNotExist=true";

        mysqldResource = startDatabase(databaseDir, port, user, password);

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Could not find mysql jdbc driver", ex);
        }

        try (Connection connection = DriverManager.getConnection(url, user, password);
                Statement statement = connection.createStatement();
                Reader schemaReader = new InputStreamReader(sce.getServletContext().getResourceAsStream("/install/db/achecker_schema.sql"));
                Reader languageTextReader = new InputStreamReader(sce.getServletContext().getResourceAsStream("/install/db/language_text.sql"))) {
            statement.execute("DROP DATABASE IF EXISTS achecker");
            statement.execute("CREATE DATABASE IF NOT EXISTS achecker CHARACTER SET utf8 COLLATE utf8_general_ci");
            statement.execute("ALTER DATABASE achecker DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci");
            statement.execute("USE achecker");

            runScript(connection, schemaReader, ";");
            runScript(connection, languageTextReader, ";");

            statement.execute("INSERT INTO config(name, value) VALUES('site_name', 'Web Accessibility Checker')");
            statement.execute("INSERT INTO config(name, value) VALUES('contact_email', 'we@big.tuwien.ac.at')");

            // Default User: ATutor
            // Default Password: ATutor123
        } catch (Exception ex) {
            if (mysqldResource != null) {
                try {
                    mysqldResource.shutdown();
                } catch (Exception ex1) {
                } finally {
                    mysqldResource = null;
                }
            }

            throw new RuntimeException("Could not startup mysql db", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (mysqldResource != null) {
            try {
                mysqldResource.shutdown();
            } catch (Exception ex) {
                Logger.getLogger(DatabaseInitializer.class.getName()).log(Level.SEVERE, "Error when shutting down mysql db", ex);
            } finally {
                mysqldResource = null;
            }
        }
    }

    public static MysqldResource startDatabase(File databaseDir, int port, String userName, String password) {
        MysqldResource mysqldResource = new MysqldResource(databaseDir);
        Map<String, String> database_options = new HashMap<>();
        database_options.put(MysqldResourceI.PORT, Integer.toString(port));
        database_options.put(MysqldResourceI.INITIALIZE_USER, "true");
        database_options.put(MysqldResourceI.INITIALIZE_USER_NAME, userName);
        database_options.put(MysqldResourceI.INITIALIZE_PASSWORD, password);

        if (mysqldResource.isRunning()) {
            System.out.println("Mysql is already running!!!");
        }

        mysqldResource.setKillDelay(5000);
        mysqldResource.start("test-mysqld-thread", database_options);

        if (!mysqldResource.isRunning()) {
            throw new RuntimeException("MySQL did not start.");
        }

        System.out.println("MySQL is running.");
        return mysqldResource;
    }

    private static File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);

            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }

    private static void runScript(Connection conn, Reader reader, String delimiter) {
        StringBuilder command = new StringBuilder();
        String line;

        try (LineNumberReader lineReader = new LineNumberReader(reader)) {
            while ((line = lineReader.readLine()) != null) {
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()
                        || trimmedLine.startsWith("//")
                        || trimmedLine.startsWith("--")
                        || trimmedLine.startsWith("#")) {
                    // Do nothing
                } else if (trimmedLine.endsWith(delimiter)) {
                    command.append(line, 0, line.lastIndexOf(delimiter));
                    String cmd = command.toString();

                    try (Statement statement = conn.createStatement()) {
                        statement.execute(cmd);
                    } catch (Exception ex) {
                        if (!cmd.toLowerCase().startsWith("drop ")) {
                            Logger.getLogger(DatabaseInitializer.class.getName()).log(Level.SEVERE, "Could not execute statement", ex);
                        }
                    } finally {
                        command.setLength(0);
                    }
                } else {
                    command.append(line);
                    command.append(" ");
                }
            }
        } catch (Exception ex) {
        }
    }
}
