package org.clueminer.clustering.benchmark.cuotoff;

import com.beust.jcommander.JCommander;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.benchmark.Bench;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.printUsage;
import org.clueminer.clustering.benchmark.cutoff.CutoffParams;
import org.clueminer.clustering.benchmark.cutoff.CutoffComparison;
import org.clueminer.clustering.benchmark.cutoff.FirstJumpOptimization;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Bruna
 */
public class CutoffExp extends Bench {

    public static final String name = "cutoff";

    protected static CutoffParams parseArguments(String[] args) {
        CutoffParams params = new CutoffParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd, params);
        return params;
    }

    @Override
    public void main(String[] args) {
        CutoffParams params = parseArguments(args);

        loadBenchArtificial();
        System.out.println("datasets: " + params.datasets);

        benchmarkFolder = params.home + '/' + "benchmark" + '/' + name;
        ensureFolder(benchmarkFolder);
        System.out.println("writing results to: " + benchmarkFolder);

        System.out.println("=== starting " + name);
        Runnable exp = null;
        switch (params.mode) {
            case "comparison": {
                exp = new CutoffComparison(params, benchmarkFolder, createDatasetsArray(params.datasets));
                break;
            }
            case "firstJump": {
                exp = new FirstJumpOptimization(params, benchmarkFolder, createDatasetsArray(params.datasets));
                break;
            }
            default: {
                throw new IllegalArgumentException("Mode " + params.mode + " is not supported");
            }
        }

        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }

    private ArrayList<Dataset<? extends Instance>> createDatasetsArray(String datasets) {
        String stringSets[] = datasets.split(",");
        ArrayList<Dataset<? extends Instance>> sets = new ArrayList<>(stringSets.length);
        for (String dataset : stringSets) {
            sets.add(provider.getDataset(dataset.trim()));
        }
        return sets;
    }

}
