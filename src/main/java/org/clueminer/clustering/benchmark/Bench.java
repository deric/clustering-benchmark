package org.clueminer.clustering.benchmark;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.clueminer.data.DataLoader;
import org.clueminer.dataset.api.DataProvider;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.DatasetFixture;
import org.clueminer.log.ClmFormatter;

/**
 *
 * @author Tomas Barton
 */
public abstract class Bench {

    protected static String benchmarkFolder;
    protected HashMap<String, Map.Entry<Dataset<? extends Instance>, Integer>> availableDatasets = new HashMap<>();
    protected DataProvider provider;

    public Bench() {
        //constructor without arguments
    }

    public static void ensureFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("Directory " + folder + " created!");
            } else {
                System.out.println("Failed to create " + folder + "directory!");
            }
        }
    }

    public abstract void main(String[] args);

    public static void printUsage(String[] args, JCommander cmd, AbsParams params) {

        try {
            cmd.parse(args);

        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            cmd.usage();
            System.exit(0);
        }
    }

    protected void loadDatasets() {
        Map<Dataset<? extends Instance>, Integer> datasets = DatasetFixture.allDatasets();
        for (Map.Entry<Dataset<? extends Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<? extends Instance> d = entry.getKey();
            availableDatasets.put(d.getName(), entry);
        }
    }

    protected void loadBenchArtificial() {
        provider = DataLoader.createLoader("datasets", "artificial");
    }

    protected void loadBenchRealWorld() {
        provider = DataLoader.createLoader("datasets", "real-world");
    }

    /**
     * Load specific dataset by name
     *
     * @param name
     */
    protected void load(String name) {
        Map<Dataset<? extends Instance>, Integer> datasets = DatasetFixture.allDatasets();
        for (Map.Entry<Dataset<? extends Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<? extends Instance> d = entry.getKey();
            if (d.getName().equalsIgnoreCase(name)) {
                availableDatasets.put(d.getName(), entry);
            }
        }
    }

    public static String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

    public void setupLogging(AbsParams params) {
        Logger log = LogManager.getLogManager().getLogger("");
        Formatter formater = new ClmFormatter();
        Level level;

        switch (params.log.toUpperCase()) {
            case "INFO":
                level = Level.INFO;
                break;
            case "SEVERE":
                level = Level.SEVERE;
                break;
            case "WARNING":
                level = Level.WARNING;
                break;
            case "ALL":
                level = Level.ALL;
                break;
            case "FINE":
                level = Level.FINE;
                break;
            case "FINER":
                level = Level.FINER;
                break;
            case "FINEST":
                level = Level.FINEST;
                break;
            default:
                throw new RuntimeException("log level " + log + " is not supported");
        }
        setupHandlers(log, level, formater);

        //remove date line from logger
        log.setUseParentHandlers(false);
    }

    private void setupHandlers(Logger logger, Level level, Formatter formater) {
        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(level);
            handler.setFormatter(formater);
        }
        Logger parentLogger = logger.getParent();
        if (null != parentLogger) {
            for (Handler handler : parentLogger.getHandlers()) {
                handler.setLevel(level);
                handler.setFormatter(formater);
            }
        }
    }

}
