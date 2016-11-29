/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.benchmark.cutoff;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.safeName;
import org.clueminer.io.csv.CSVWriter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.hclust.FirstJump;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Bruna
 */
public class FirstJumpOptimization implements Runnable {

    private final CutoffParams params;
    private final String benchmarkFolder;
    private final ArrayList<Dataset<? extends Instance>> datasets;
    private LinkedList<HierarchicalResult> dendrograms;
    FirstJump cutoff;
    ClusterEvaluation eval;

    public FirstJumpOptimization(CutoffParams params, String benchmarkFolder, ArrayList<Dataset<? extends Instance>> datasets) {
        this.params = params;
        this.benchmarkFolder = benchmarkFolder;
        this.datasets = datasets;
        EvaluationFactory ef = EvaluationFactory.getInstance();
        eval = ef.getProvider("NMI-sqrt");
        cutoff = (FirstJump) getCutoffStrategy("First jump cutoff");
    }

    @Override
    public void run() {
        try {
            String folder;
            AgglomerativeClustering alg = (AgglomerativeClustering) ClusteringFactory.getInstance().getProvider(params.algorithm);
            folder = benchmarkFolder + File.separatorChar + "FirstJumpParams";
            ensureFolder(folder);
            String csvRes = folder + File.separatorChar + safeName(alg.getName()) + "_" + "FirstJumpParams" + ".csv";

            PrintWriter writer = new PrintWriter(csvRes, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            csv.writeLine("Clustering_with_" + alg.getName());

            computeDendrograms(alg);

            String startRange[] = params.startRange.split("-");
            String factorRange[] = params.factorRange.split("-");

            for (int i = Integer.valueOf(startRange[0]); i <= Integer.valueOf(startRange[1]); i += 10) {
                double j = Double.valueOf(factorRange[0]);
                while (j <= Double.valueOf(factorRange[1])) {
                    csv.writeLine("AVERAGE_WITH_" + i + "_AND_" + j + ": " + testParameters(i, j));
                    j += 0.1;
                }
            }
            csv.close();
        } catch (NumberFormatException | IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    /**
     * Try cutoff with specified parameters on all results
     *
     * @param i
     * @param j
     * @return
     */
    private double testParameters(int i, double j) {
        System.out.println("Testing " + i + " and " + j);
        double score;
        Clustering c;
        cutoff.setStart(i);
        cutoff.setFactor(j);
        double sum = 0;
        int cnt = 0;
        //compute cutoff on all results
        for (HierarchicalResult rowsResult : dendrograms) {
            rowsResult.findCutoff(cutoff);
            c = rowsResult.getClustering();
            if (c.getEvaluationTable() != null) {
                score = c.getEvaluationTable().getScore(eval);
            } else {
                try {
                    score = eval.score(c);
                } catch (ScoreException ex) {
                    score = Double.NaN;
                    Exceptions.printStackTrace(ex);
                }
            }
            sum += score;
            cnt++;
        }
        return sum / cnt;
    }

    /**
     * Cluster all datasets and save results
     *
     * @param alg
     */
    private void computeDendrograms(AgglomerativeClustering alg) {
        dendrograms = new LinkedList<>();
        for (Dataset<? extends Instance> dataset : datasets) {
            HierarchicalResult rowsResult = alg.hierarchy(dataset, new Props());
            dendrograms.add(rowsResult);
        }
    }

    private CutoffStrategy getCutoffStrategy(String strategy) {
        CutoffStrategy cutoffStrategy = CutoffStrategyFactory.getInstance().getProvider(strategy);
        return cutoffStrategy;
    }

}
