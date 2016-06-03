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
package org.clueminer.clustering.benchmark.exp;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.benchmark.Bench;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.GnuplotWriter;
import org.clueminer.dataset.benchmark.ResultsCollector;
import org.clueminer.eval.external.JaccardIndex;
import org.clueminer.evolution.attr.AttrEvolution;
import org.clueminer.evolution.utils.ConsoleDump;
import org.clueminer.utils.FileUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author tombart
 */
public class Data extends Bench {

    private AttrEvolution test;
    //table for keeping results from experiments
    private Table<String, String, Double> table;
    private static ResultsCollector rc;
    private static String csvOutput;
    private static Data instance;

    /**
     * @param args the command line arguments
     */
    @Override
    public void main(String[] args) {
        int i = 0, j;
        String arg;
        char flag;
        boolean vflag = false;
        String datasetName = "";

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            // use this type of check for "wordy" arguments
            switch (arg) {
                // use this type of check for arguments that require arguments
                case "-verbose":
                    System.out.println("verbose mode on");
                    vflag = true;
                    break;
                // use this type of check for a series of flag arguments
                case "-dataset":
                    if (i < args.length) {
                        datasetName = args[i++];
                    } else {
                        System.err.println("-dataset requires a name");
                    }
                    if (vflag) {
                        System.out.println("dataset = " + datasetName);
                    }
                    break;
                default:
                    for (j = 1; j < arg.length(); j++) {
                        flag = arg.charAt(j);
                        switch (flag) {
                            case 'x':
                                if (vflag) {
                                    System.out.println("Option x");
                                }
                                break;
                            case 'n':
                                if (vflag) {
                                    System.out.println("Option n");
                                }
                                break;
                            default:
                                System.err.println("Run: illegal option " + flag);
                                break;
                        }
                    }
                    break;
            }
        }
        if (i == args.length) {
            System.err.println("Usage: Benchmark [-verbose] [-xn] [-dataset name]");
        }

        init();
        execute(datasetName);
    }

    private void init() {
        table = Tables.newCustomTable(
                Maps.<String, Map<String, Double>>newHashMap(),
                new Supplier<Map<String, Double>>() {
            @Override
            public Map<String, Double> get() {
                return Maps.newHashMap();
            }
        });

        String home = System.getProperty("user.home") + File.separatorChar
                + NbBundle.getMessage(
                        FileUtils.class,
                        "FOLDER_Home");
        ensureFolder(home);
        benchmarkFolder = home + File.separatorChar + "benchmark";
        ensureFolder(benchmarkFolder);
        rc = new ResultsCollector(table);
        csvOutput = benchmarkFolder + File.separatorChar + "results.csv";

        //preload dataset names
        loadDatasets();
    }

    public void execute(String datasetName) {
        Map<Dataset<? extends Instance>, Integer> datasets = new HashMap<>();
        if (availableDatasets.containsKey(datasetName)) {
            Map.Entry<Dataset<? extends Instance>, Integer> entry = availableDatasets.get(datasetName);
            datasets.put(entry.getKey(), entry.getValue());
        } else {
            System.out.println("dataset " + datasetName + " not found");
            System.out.println("known datasets: ");
            for (String d : availableDatasets.keySet()) {
                System.out.print(d + " ");
            }
            System.out.println("---");
        }
        // DatasetFixture.allDatasets();

        InternalEvaluatorFactory<Instance, Cluster<Instance>> factory = InternalEvaluatorFactory.getInstance();
        ExternalEvaluator ext = new JaccardIndex();

        String name;
        System.out.println("working folder: " + benchmarkFolder);
        for (Map.Entry<Dataset<? extends Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<? extends Instance> dataset = entry.getKey();
            name = dataset.getName();
            String csvRes = benchmarkFolder + File.separatorChar + name + File.separatorChar + name + ".csv";
            System.out.println("=== dataset " + name);
            System.out.println("size: " + dataset.size());
            System.out.println(dataset.toString());
            String dataDir = benchmarkFolder + File.separatorChar + name;
            (new File(dataDir)).mkdir();
            for (InternalEvaluator eval : factory.getAll()) {
                System.out.println("evaluator: " + eval.getName());
                test = new AttrEvolution(dataset, 20);
                test.setAlgorithm(new KMeans());
                test.setK(entry.getValue());
                test.setEvaluator(eval);
                test.setExternal(ext);
                GnuplotWriter gw = new GnuplotWriter(test, benchmarkFolder, name + "/" + name + "-" + safeName(eval.getName()));
                gw.setPlotDumpMod(50);
                //collect data from evolution
                test.addEvolutionListener(new ConsoleDump());
                test.addEvolutionListener(gw);
                test.addEvolutionListener(rc);
                test.run();
                rc.writeToCsv(csvRes);
            }
        }
    }
}
