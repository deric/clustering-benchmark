/*
 * Copyright (C) 2011-2015 clueminer.org
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

import com.beust.jcommander.JCommander;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.benchmark.Bench;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.printUsage;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 */
public class ConsensusExp extends Bench {

    public static final String name = "consensus";

    protected static ConsensusParams parseArguments(String[] args) {
        ConsensusParams params = new ConsensusParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd, params);
        return params;
    }

    @Override
    public void main(String[] args) {
        ConsensusParams params = parseArguments(args);
        if (params.dataset != null) {
            load(params.dataset);
        } else {
            loadDatasets();
        }
        setupLogging(params);

        int i = 0;
        for (Map.Entry<String, Map.Entry<Dataset<? extends Instance>, Integer>> e : availableDatasets.entrySet()) {
            System.out.println((i++) + ":" + e.getKey());
        }

        benchmarkFolder = params.home + '/' + "benchmark" + '/' + name;
        ensureFolder(benchmarkFolder);
        System.out.println("writing results to: " + benchmarkFolder);

        System.out.println("=== starting " + name);
        ConsensusRun exp = new ConsensusRun(params, benchmarkFolder, (Dataset<? extends Instance>) availableDatasets.get(params.dataset));
        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }
}
