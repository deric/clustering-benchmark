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
package org.clueminer.clustering.benchmark;

import org.clueminer.clustering.api.AlgParams;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.report.NanoBench;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 * @param <E>
 */
public class ParamExperiment<E extends Instance> extends Experiment<E> {

    private Props[] configs;

    public ParamExperiment(BenchParams params, String results) {
        super(params, results);
    }

    public ParamExperiment(BenchParams params, String results, Props[] configs) {
        super(params, results);

        this.configs = configs;
    }

    @Override
    public void run() {
        int inc = (params.n - params.nSmall) / params.steps;

        String[] names = new String[configs.length];
        int j = 0;
        for (Props alg : configs) {
            names[j++] = alg.get(AlgParams.ALG);
        }

        //json props must be last column (in order to avoid issues with gnuplot parsing commas in json)
        GnuplotReporter reporter = new GnuplotReporter(results,
                new String[]{"algorithm", "n", "config"},
                names, params.nSmall + "-" + params.n, 9);
        System.out.println("increment = " + inc);
        ClusteringBenchmark bench = new ClusteringBenchmark();
        Container container;
        for (int i = params.nSmall; i <= params.n; i += inc) {
            Dataset<E> dataset = generateData(i, params.dimension);
            for (Props props : configs) {
                String[] opts = new String[]{props.get(AlgParams.ALG), String.valueOf(dataset.size()), props.toJson()};
                try {
                    container = bench.cluster(dataset, props);
                    NanoBench.create().measurements(params.repeat).collect(reporter, opts).measure(
                            props.get(AlgParams.ALG) + " - " + dataset.size(),
                            container
                    );
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                // Get the Java runtime
                Runtime runtime = Runtime.getRuntime();
                // Run the garbage collector
                runtime.gc();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        reporter.finish();
    }

}
