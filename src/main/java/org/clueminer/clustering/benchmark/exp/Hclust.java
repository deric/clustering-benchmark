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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.aggl.HCLWMS;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.benchmark.Bench;
import org.clueminer.clustering.benchmark.BenchParams;
import org.clueminer.clustering.benchmark.Experiment;
import org.clueminer.log.ClmLog;

/**
 *
 * @author deric
 */
public class Hclust extends Bench {

    protected static Hclust instance;

    /**
     * @param args the command line arguments
     */
    @Override
    public void main(String[] args) {
        BenchParams params = parseArguments(args);
        ClmLog.setup(params.log);

        benchmarkFolder = params.home + File.separatorChar + "benchmark" + File.separatorChar + "hclust";
        ensureFolder(benchmarkFolder);

        System.out.println("# n = " + params.n);
        System.out.println("=== starting experiment:");
        AgglomerativeClustering[] algorithms = new AgglomerativeClustering[]{new HC(), new HCLW(), new HCLWMS()};
        Experiment exp = new Experiment(params, benchmarkFolder, algorithms);
        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }

    protected static BenchParams parseArguments(String[] args) {
        BenchParams params = new BenchParams();
        JCommander cmd = new JCommander(params);
        printUsage(args, cmd, params);
        return params;
    }

    public static void printUsage(String[] args, JCommander cmd, BenchParams params) {
        /* if (args.length == 0) { StringBuilder sb = new StringBuilder();
         * cmd.usage(sb);
         * sb.append("\n").append("attributes marked with * are mandatory");
         * System.out.println(sb);
         * System.err.println("missing mandatory arguments");
         * System.exit(0);
         * } */
        try {
            cmd.parse(args);
            /**
             * TODO validate values of parameters
             */
            if (params.n <= 0 || params.dimension <= 0) {
                throw new ParameterException("invalid data dimensions " + params.n + " x " + params.dimension);
            }

            if (params.steps <= 0) {
                throw new ParameterException("invalid steps size " + params.steps);
            }

            if (params.nSmall == params.n) {
                throw new ParameterException("n can't be same as n-small! " + params.nSmall);
            }

        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            cmd.usage();
            System.exit(0);
        }
    }

}
