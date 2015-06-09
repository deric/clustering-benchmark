package org.clueminer.clustering.benchmark.evolve;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.factory.ExternalEvaluatorFactory;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.safeName;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.ConsoleDump;
import org.clueminer.dataset.benchmark.GnuplotWriter;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Exceptions;

/**
 * Evolution of hierarchical clusterings with different (unsupervised)
 * optimization criterion (single criterion)
 *
 * @author Tomas Barton
 */
public class EvolveExp implements Runnable {

    private static ResultsCollector rc;
    private EvolveParams params;
    private String benchmarkFolder;
    private ClusterEvaluation[] scores;
    private HashMap<String, Entry<Dataset<? extends Instance>, Integer>> datasets;
    //table for keeping results from experiments
    private final Table<String, String, Double> table;

    public EvolveExp(EvolveParams params, String benchmarkFolder, ClusterEvaluation[] scores, HashMap<String, Entry<Dataset<? extends Instance>, Integer>> availableDatasets) {
        this.params = params;
        this.benchmarkFolder = benchmarkFolder;
        this.scores = scores;
        this.datasets = availableDatasets;

        table = Tables.newCustomTable(
                Maps.<String, Map<String, Double>>newHashMap(),
                new Supplier<Map<String, Double>>() {
                    @Override
                    public Map<String, Double> get() {
                        return Maps.newHashMap();
                    }
                });
        rc = new ResultsCollector(table);
    }

    @Override
    public void run() {
        try {
            MultiMuteEvolution evolution;
            String name;

            ClusterEvaluation ext = fetchExternal(params.external);
            //evolution.setAlgorithm(new HACLW());
            System.out.println("datasets size: " + datasets.size());
            for (Map.Entry<String, Map.Entry<Dataset<? extends Instance>, Integer>> e : datasets.entrySet()) {
                Dataset<? extends Instance> d = e.getValue().getKey();
                name = safeName(d.getName());
                String csvRes = benchmarkFolder + File.separatorChar + name + File.separatorChar + name + ".csv";
                System.out.println("=== dataset " + name);
                System.out.println("size: " + d.size());
                ensureFolder(benchmarkFolder + File.separatorChar + name);
                for (ClusterEvaluation eval : scores) {
                    evolution = new MultiMuteEvolution();
                    evolution.setDataset(d);
                    evolution.setEvaluator(eval);
                    evolution.setExternal(ext);
                    evolution.setGenerations(params.generations);
                    evolution.setPopulationSize(params.population);
                    GnuplotWriter gw = new GnuplotWriter(evolution, benchmarkFolder, name + File.separatorChar + safeName(eval.getName()));
                    gw.setPlotDumpMod(50);
                    gw.setCustomTitle("cutoff=" + evolution.getDefaultParam(AgglParams.CUTOFF_STRATEGY) + "(" + evolution.getDefaultParam(AgglParams.CUTOFF_SCORE) + ")");
                    //collect data from evolution
                    evolution.addEvolutionListener(new ConsoleDump());
                    evolution.addEvolutionListener(gw);
                    evolution.addEvolutionListener(rc);
                    evolution.run();
                    System.out.println("## updating results in: " + csvRes);
                    rc.writeToCsv(csvRes);
                }
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    private ClusterEvaluation fetchExternal(String external) {
        return ExternalEvaluatorFactory.getInstance().getProvider(external);
    }

}
