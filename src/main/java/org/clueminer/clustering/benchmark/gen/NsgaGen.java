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
package org.clueminer.clustering.benchmark.gen;

import com.beust.jcommander.JCommander;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.benchmark.Bench;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.printUsage;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.log.ClmLog;

/**
 *
 * @author deric
 */
public class NsgaGen extends Bench {

    public static final String name = "nsga-gen";

    protected static NsgaGenParams parseArguments(String[] args) {
        NsgaGenParams params = new NsgaGenParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd, params);
        return params;
    }

    @Override
    public void main(String[] args) {
        NsgaGenParams params = parseArguments(args);
        if (params.dataset != null) {
            load(params.dataset);
        } else {
            loadDatasets();
        }
        ClmLog.setup(params.log);

        int i = 0;
        for (Map.Entry<String, Map.Entry<Dataset<? extends Instance>, Integer>> e : availableDatasets.entrySet()) {
            System.out.println((i++) + ":" + e.getKey());
        }

        benchmarkFolder = params.home + '/' + "benchmark" + '/' + name;
        ensureFolder(benchmarkFolder);
        System.out.println("writing results to: " + benchmarkFolder);

        System.out.println("=== starting " + name);
        InternalEvaluatorFactory<Instance, Cluster<Instance>> factory = InternalEvaluatorFactory.getInstance();
        ClusterEvaluation c1 = factory.getProvider(params.c1);
        ClusterEvaluation c2 = factory.getProvider(params.c2);
        NsgaGenExp exp = new NsgaGenExp(params, benchmarkFolder, c1, c2, availableDatasets);
        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }
}
