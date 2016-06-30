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
package org.clueminer.clustering.benchmark.chameleon2;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.clueminer.clustering.api.AlgParams;
import static org.clueminer.clustering.benchmark.Bench.ensureFolder;
import org.clueminer.clustering.benchmark.BenchParams;
import org.clueminer.clustering.benchmark.ParamExperiment;
import org.clueminer.clustering.benchmark.exp.Hclust;
import org.clueminer.log.ClmLog;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class Cham2Bench extends Hclust {

    /**
     * @param args the command line arguments
     */
    @Override
    public void main(String[] args) {
        BenchParams params = parseArguments(args);
        ClmLog.setup(params.log);

        benchmarkFolder = params.home + File.separatorChar + "chameleon2";
        ensureFolder(benchmarkFolder);

        System.out.println("# n = " + params.n);
        System.out.println("=== starting experiment:");

        Props ch2 = new Props();
        ch2.put(AlgParams.ALG, "Chameleon");

        Props hc = new Props();
        hc.put(AlgParams.ALG, "HC-LW(ms)");
        hc.put(AlgParams.LINKAGE, "Single");

        Props dbscan = new Props();
        dbscan.put(AlgParams.ALG, "DBSCAN");

        Props km = new Props();
        km.put(AlgParams.ALG, "k-means");

        Props[] algorithms = new Props[]{
            ch2,
            hc,
            dbscan, km
        };
        ParamExperiment exp = new ParamExperiment(params, benchmarkFolder, algorithms);
        ExecutorService execService = Executors.newFixedThreadPool(1);
        execService.submit(exp);
        execService.shutdown();
    }

}
