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
package org.clueminer.clustering.benchmark.consensus;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import org.clueminer.bagging.COMUSA;
import org.clueminer.bagging.CoAssociationReduce;
import org.clueminer.bagging.KMeansBagging;
import org.clueminer.clustering.aggl.linkage.AverageLinkage;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.safeName;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.exec.ClusteringExecutorCached;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class ConsensusRun implements Runnable {

    private static ResultsCollector rc;
    private ConsensusParams params;
    private String benchmarkFolder;
    //table for keeping results from experiments
    private Table<String, String, Double> table;
    private static final Logger LOG = LoggerFactory.getLogger(ConsensusRun.class);
    private Dataset<? extends Instance> dataset;

    public ConsensusRun(ConsensusParams params, String benchmarkFolder, Dataset<? extends Instance> dataset) {
        this.params = params;
        this.benchmarkFolder = benchmarkFolder;
        this.dataset = dataset;

        createTable();
        rc = new ResultsCollector(table);
    }

    private void createTable() {
        table = Tables.newCustomTable(
                Maps.<String, Map<String, Double>>newHashMap(),
                new Supplier<Map<String, Double>>() {
            @Override
            public Map<String, Double> get() {
                return Maps.newHashMap();
            }
        });
    }

    @Override
    public void run() {
        try {
            String name;
            String algorithm;
            String folder;
            EvaluationFactory ef = EvaluationFactory.getInstance();
            LinkedList<ClusterEvaluation> evals = new LinkedList<>();
            evals.add(ef.getProvider("NMI-sqrt"));
            evals.add(ef.getProvider("NMI-sum"));
            evals.add(ef.getProvider("Adjusted Rand"));
            evals.add(ef.getProvider("Deviation"));

            ClusteringAlgorithm alg = ClusteringFactory.getInstance().getProvider(params.algorithm);
            algorithm = safeName(alg.getName());
            Executor exec = new ClusteringExecutorCached(alg);

            createTable();
            name = safeName(dataset.getName());
            folder = benchmarkFolder + File.separatorChar + name;
            ensureFolder(folder);

            String csvRes = folder + File.separatorChar + algorithm + "_" + params.method + "_" + name + ".csv";
            LOG.info("dataset: {} size: {} num attr: {}", name, dataset.size(), dataset.attributeCount());
            //ensureFolder(benchmarkFolder + File.separatorChar + name);
            Clustering c;
            Props props = algorithmSetup(params.method);
            if (params.fixedK) {
                props.putBoolean(KMeansBagging.FIXED_K, true);
            }
            if (params.k > 0) {
                props.putInt("k", params.k);
            } else if (!props.containsKey("k") && props.getBoolean(KMeansBagging.FIXED_K, false)) {
                //use "correct" number of clusters if k not specified
                props.putInt("k", dataset.getClasses().size());
            }
            double score;
            System.out.println(props.toString());
            for (int i = 0; i < params.repeat; i++) {
                c = exec.clusterRows(dataset, props);
                for (ClusterEvaluation eval : evals) {
                    if (c.getEvaluationTable() != null) {
                        score = c.getEvaluationTable().getScore(eval);
                    } else {
                        score = eval.score(c);
                    }
                    System.out.println(eval.getName() + ": " + score + ", clusters: " + c.size());
                    table.put("run " + i, eval.getName(), score);
                }
            }
            rc.writeAvgColsCsv(table, csvRes);

        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    private Props algorithmSetup(String alg) {
        Props p = new Props();
        p.putInt(KMeansBagging.BAGGING, 10);
        switch (alg) {
            case "KmB-COMUSA-RAND":
                p.put(KMeansBagging.CONSENSUS, COMUSA.name);
                p.put(KMeansBagging.INIT_METHOD, "RANDOM");
                p.putDouble(COMUSA.RELAX, 1.0);
                p.putInt(KMeansBagging.MAX_K, 25);
                break;
            case "KmB-COMUSA-MO":
                p.put(KMeansBagging.CONSENSUS, COMUSA.name);
                p.put(KMeansBagging.INIT_METHOD, "MO");
                p.putDouble(COMUSA.RELAX, 1.0);
                p.put("mo_1", "AIC");
                p.put("mo_2", "SD index");
                p.putInt(KMeansBagging.MAX_K, 25);
                break;
            case "KmB-COMUSA-RAND-fixed":
                p.put(KMeansBagging.CONSENSUS, COMUSA.name);
                p.put(KMeansBagging.INIT_METHOD, "RANDOM");
                p.putDouble(COMUSA.RELAX, 1.0);
                p.putBoolean(KMeansBagging.FIXED_K, true);
                break;
            case "KmB-CoAssocHAC-MO-avg":
                p.put(KMeansBagging.CONSENSUS, CoAssociationReduce.name);
                p.put(KMeansBagging.INIT_METHOD, "MO");
                p.put("mo_1", "AIC");
                p.put("mo_2", "SD index");
                p.put(AlgParams.LINKAGE, AverageLinkage.name);
                break;
            case "KmB-CoAssocHAC-MO-AIC_SD":
                p.put(KMeansBagging.CONSENSUS, CoAssociationReduce.name);
                p.put(KMeansBagging.INIT_METHOD, "MO");
                p.put("mo_1", "AIC");
                p.put("mo_2", "SD index");
                break;
            default:
                break;

        }
        return p;
    }

}
