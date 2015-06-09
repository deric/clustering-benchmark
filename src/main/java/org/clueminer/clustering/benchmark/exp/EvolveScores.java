package org.clueminer.clustering.benchmark.exp;

import com.beust.jcommander.JCommander;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.benchmark.Bench;
import org.clueminer.clustering.benchmark.evolve.EvolveExp;
import org.clueminer.clustering.benchmark.evolve.EvolveParams;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class EvolveScores extends Bench {

    public static final String name = "evolve-sc";

    protected static EvolveParams parseArguments(String[] args) {
        EvolveParams params = new EvolveParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd, params);
        return params;
    }

    @Override
    public void main(String[] args) {
        EvolveParams params = parseArguments(args);
        if (params.test) {
            load("iris");
        } else {
            loadDatasets();
        }
        setupLogging(params);
        System.out.println("loaded dataset");
        int i = 0;
        for (Map.Entry<String, Map.Entry<Dataset<? extends Instance>, Integer>> e : availableDatasets.entrySet()) {
            System.out.println((i++) + ":" + e.getKey());
        }

        benchmarkFolder = params.home + '/' + "benchmark" + '/' + name;
        ensureFolder(benchmarkFolder);
        System.out.println("writing results to: " + benchmarkFolder);

        System.out.println("=== starting " + name);
        List<InternalEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        ClusterEvaluation[] scores = eval.toArray(new ClusterEvaluation[eval.size()]);
        System.out.println("scores size: " + scores.length);
        EvolveExp exp = new EvolveExp(params, benchmarkFolder, scores, availableDatasets);
        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }

}
