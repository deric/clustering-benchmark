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
package org.clueminer.clustering.benchmark.partition;

import com.beust.jcommander.JCommander;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.benchmark.Bench;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import static org.clueminer.clustering.benchmark.Bench.printUsage;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.partitioning.impl.RecursiveBisection;

/**
 * An experiment to test time complexity of graph partitioning methods
 *
 * @author deric
 */
public class PartitionExp extends Bench {

    public static final String name = "partition";

    protected static PartitionParams parseArguments(String[] args) {
        PartitionParams params = new PartitionParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd, params);
        return params;
    }

    @Override
    public void main(String[] args) {
        PartitionParams params = parseArguments(args);

        benchmarkFolder = params.home + '/' + "benchmark" + '/' + name;
        ensureFolder(benchmarkFolder);
        System.out.println("writing results to: " + benchmarkFolder);

        System.out.println("=== starting " + name);

        Partitioning[] algorithms = new Partitioning[]{
            new RecursiveBisection()
        };

        Runnable exp = new PartitionBench(params, name, algorithms);

        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }

}
