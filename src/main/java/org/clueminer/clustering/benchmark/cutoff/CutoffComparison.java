package org.clueminer.clustering.benchmark.cutoff;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.safeName;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Bruna
 */
public class CutoffComparison implements Runnable {

    private final CutoffParams params;
    private final String benchmarkFolder;
    private final ArrayList<Dataset<? extends Instance>> datasets;
    private Map<String, Average> averages;
    private LinkedList<ClusterEvaluation> externalEvals;
    CSVWriter csv;

    public CutoffComparison(CutoffParams params, String benchmarkFolder, ArrayList<Dataset<? extends Instance>> datasets) {
        this.params = params;
        this.benchmarkFolder = benchmarkFolder;
        this.datasets = datasets;
        loadExternalEvals();
    }

    @Override
    public void run() {
        try {
            String folder;
            AgglomerativeClustering alg = (AgglomerativeClustering) ClusteringFactory.getInstance().getProvider(params.algorithm);
            folder = benchmarkFolder + File.separatorChar + "Cutoff comparison";
            ensureFolder(folder);
            String csvRes = folder + File.separatorChar + "Cutoff comparison with " + safeName(alg.getName()) + ".csv";

            PrintWriter writer = new PrintWriter(csvRes, "UTF-8");
            csv = new CSVWriter(writer, ',');
            csv.writeLine(alg.getName());

            initAverages();
            for (Dataset<? extends Instance> dataset : datasets) {
                System.out.println("Running comparisons on " + dataset.getName());
                //create dendrogram
                HierarchicalResult rowsResult = alg.hierarchy(dataset, new Props());
                writeHeader(dataset);

                String strategies[] = params.strategies.split(",");
                String internalEvals[] = params.internalEvals.split(",");
                //try different cutoff methods
                for (String strategy : strategies) {
                    for (String internalEval : internalEvals) {
                        CutoffStrategy cutoff = getCutoffStrategy(strategy.trim(), internalEval.trim());
                        rowsResult.findCutoff(cutoff);
                        Clustering c = rowsResult.getClustering();

                        averages.get(strategy.trim() + internalEval.trim()).addValues(c);
                        writeValues(strategy.trim(), internalEval.trim(), c);

                        if (!"hill-climb cutoff".equals(strategy.trim()) && !"hill-climb inc".equals(strategy.trim())) {
                            break;
                        }
                    }
                }
                csv.writeLine("");
                csv.writeLine("");
            }
            writeAverages();
            csv.close();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    private void loadExternalEvals() {
        String evals[] = params.externalEvals.split(",");
        externalEvals = new LinkedList<>();
        EvaluationFactory ef = EvaluationFactory.getInstance();
        for (String eval : evals) {
            externalEvals.add(ef.getProvider(eval.trim()));
        }
    }

    private void writeHeader(Dataset<? extends Instance> dataset) {
        csv.writeLine("Dataset_" + safeName(dataset.getName()));
        String row[] = new String[externalEvals.size() + 2];
        row[0] = "Cutoff strategy";
        row[1] = "Internal eval";
        int i = 2;
        for (ClusterEvaluation eval : externalEvals) {
            row[i] = eval.getName();
            i++;
        }
        csv.writeNext(row);
    }

    private void writeValues(String strategy, String internaEval, Clustering c) {
        String row[] = new String[externalEvals.size() + 2];
        row[0] = strategy;
        if ("hill-climb cutoff".equals(strategy) || "hill-climb inc".equals(strategy)) {
            row[1] = internaEval;
        } else {
            row[1] = "";
        }
        int i = 2;
        for (ClusterEvaluation eval : externalEvals) {
            double score;
            if (c.getEvaluationTable() != null) {
                score = c.getEvaluationTable().getScore(eval);
            } else {
                score = eval.score(c);
            }
            row[i] = String.valueOf(score);
            i++;
        }
        csv.writeNext(row);
    }

    private void writeAverages() {
        csv.writeLine("AVERAGES");
        String row[] = new String[externalEvals.size() + 2];
        for (Average average : averages.values()) {
            row[0] = average.name;
            row[1] = "";
            int i = 2;
            for (ClusterEvaluation eval : externalEvals) {
                row[i] = String.valueOf(average.getAverage(eval.getName()));
                i++;
            }
            csv.writeNext(row);
        }
    }

    private CutoffStrategy getCutoffStrategy(String strategy, String eval) {
        CutoffStrategy cutoffStrategy = CutoffStrategyFactory.getInstance().getProvider(strategy);
        InternalEvaluator evaluator = InternalEvaluatorFactory.getInstance().getProvider(eval);
        cutoffStrategy.setEvaluator(evaluator);
        return cutoffStrategy;
    }

    private void initAverages() {
        averages = new HashMap<>();
        String strategies[] = params.strategies.split(",");
        String internalEvals[] = params.internalEvals.split(",");
        for (String strategy : strategies) {
            for (String internalEval : internalEvals) {
                if (!"hill-climb cutoff".equals(strategy.trim()) && !"hill-climb inc".equals(strategy.trim())) {
                    averages.put(strategy.trim() + internalEval.trim(), new Average(strategy.trim()));
                    break;
                } else {
                    averages.put(strategy.trim() + internalEval.trim(), new Average(strategy.trim() + " with " + internalEval.trim()));
                }
            }
        }

    }

    private class Average {

        String name;
        LinkedList<ClusterEvaluation> evals;
        Map<String, Double> sum;
        Map<String, Integer> cnt;

        Average(String name) {
            this.name = name;
            sum = new HashMap<>();
            cnt = new HashMap<>();
            for (ClusterEvaluation eval : externalEvals) {
                sum.put(eval.getName(), 0.0);
                cnt.put(eval.getName(), 0);
            }
        }

        public double getAverage(String eval) {
            return sum.get(eval) / cnt.get(eval);
        }

        public void addValues(Clustering c) {
            double score;
            for (ClusterEvaluation eval : externalEvals) {
                if (c.getEvaluationTable() != null) {
                    score = c.getEvaluationTable().getScore(eval);
                } else {
                    score = eval.score(c);
                }
                sum.put(eval.getName(), sum.get(eval.getName()) + score);
                cnt.put(eval.getName(), cnt.get(eval.getName()) + 1);
            }
        }

    }
}
