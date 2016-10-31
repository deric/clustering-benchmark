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
package org.clueminer.clustering.benchmark.nsga;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import static org.clueminer.clustering.benchmark.Bench.safeName;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.GnuplotMO;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.evolution.mo.MoEvolution;
import org.clueminer.evolution.utils.ConsoleDump;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class NsgaExp implements Runnable {

    private static ResultsCollector rc;
    private NsgaParams params;
    private String benchmarkFolder;
    private ClusterEvaluation[] scores;
    private HashMap<String, Map.Entry<Dataset<? extends Instance>, Integer>> datasets;
    //table for keeping results from experiments
    private Table<String, String, Double> table;
    private static final Logger LOG = LoggerFactory.getLogger(NsgaExp.class);

    public NsgaExp(NsgaParams params, String benchmarkFolder, ClusterEvaluation[] scores, HashMap<String, Map.Entry<Dataset<? extends Instance>, Integer>> availableDatasets) {
        this.params = params;
        this.benchmarkFolder = benchmarkFolder;
        this.scores = scores;
        this.datasets = availableDatasets;

        createTable();
        rc = new ResultsCollector(table);
    }

    @Override
    public void run() {
        try {
            MoEvolution evolution = new MoEvolution();
            evolution.setGenerations(params.generations);
            evolution.setPopulationSize(params.population);
            evolution.setNumSolutions(params.solutions);
            evolution.setExternal(EvaluationFactory.getInstance().getProvider(params.supervised));
            evolution.setMutationProbability(params.mutation);
            evolution.setCrossoverProbability(params.crossover);
            evolution.setkLimit(params.limitK);
            ClusterEvaluation c1, c2;

            GnuplotMO gw = new GnuplotMO();
            //gw.setCustomTitle("cutoff=" + evolution.getDefaultParam(AgglParams.CUTOFF_STRATEGY) + "(" + evolution.getDefaultParam(AgglParams.CUTOFF_SCORE) + ")");
            //collect data from evolution
            evolution.addEvolutionListener(new ConsoleDump());
            evolution.addMOEvolutionListener(gw);
            evolution.addMOEvolutionListener(rc);

            String name;
            LOG.info("datasets size: {}", datasets.size());
            for (Map.Entry<String, Map.Entry<Dataset<? extends Instance>, Integer>> e : datasets.entrySet()) {
                Dataset<? extends Instance> d = e.getValue().getKey();
                name = safeName(d.getName());
                String csvRes = benchmarkFolder + File.separatorChar + name + File.separatorChar + "_" + name + ".csv";
                LOG.info("dataset: {} size: {} num attr: {}", name, d.size(), d.attributeCount());
                //ensureFolder(benchmarkFolder + File.separatorChar + name);

                gw.setCurrentDir(benchmarkFolder, name);

                evolution.setDataset(d);

                for (int i = 0; i < scores.length; i++) {
                    c1 = scores[i];
                    //lower triangular matrix without diagonal
                    //(doesn't matter which criterion is first, we want to try
                    //all combinations)
                    for (int j = 0; j < i; j++) {
                        c2 = scores[j];
                        evolution.clearObjectives();
                        evolution.addObjective(c1);
                        evolution.addObjective(c2);
                        //run!
                        for (int k = 0; k < params.repeat; k++) {
                            LOG.info("run {}: {} & {}", k, c1.getName(), c2.getName());
                            evolution.run();
                            rc.writeToCsv(csvRes);
                        }
                        evolution.fireFinishedBatch();
                        LOG.info("finished {} & {}", c1.getName(), c2.getName());
                    }
                }
                createTable();
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
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
}
