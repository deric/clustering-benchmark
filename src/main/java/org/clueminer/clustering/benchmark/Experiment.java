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

import java.util.Random;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.report.NanoBench;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class Experiment<E extends Instance> implements Runnable {

    protected final Random rand;
    protected final BenchParams params;
    private ClusteringAlgorithm[] algorithms;
    protected final String results;
    private static final Logger LOG = LoggerFactory.getLogger(Experiment.class);

    public Experiment(BenchParams params, String results) {
        rand = new Random();
        this.params = params;
        this.results = results;
    }

    public Experiment(BenchParams params, String results, ClusteringAlgorithm[] algorithms) {
        rand = new Random();
        this.params = params;
        this.results = results;
        this.algorithms = algorithms;
    }

    @Override
    public void run() {
        int inc = (params.n - params.nSmall) / params.steps;

        String[] names = new String[algorithms.length];
        int j = 0;
        for (ClusteringAlgorithm alg : algorithms) {
            names[j++] = alg.getName();
        }

        GnuplotReporter reporter = new GnuplotReporter(results,
                new String[]{"algorithm", "linkage", "n"}, names, params.nSmall + "-" + params.n,
                10);
        LOG.info("increment = {}", inc);
        ClusteringBenchmark bench = new ClusteringBenchmark();
        Container container;
        AgglomerativeClustering aggl;
        for (int i = params.nSmall; i <= params.n; i += inc) {
            Dataset<E> dataset = generateData(i, params.dimension);
            for (ClusteringAlgorithm alg : algorithms) {
                String[] opts = new String[]{alg.getName(), params.linkage, String.valueOf(dataset.size())};

                if (alg instanceof AgglomerativeClustering) {
                    aggl = (AgglomerativeClustering) alg;
                    container = bench.hclust(aggl, dataset, params.linkage);
                } else {
                    container = bench.cluster(alg, dataset, new Props());
                }

                NanoBench.create().measurements(params.repeat).collect(reporter, opts).measure(
                        alg.getName() + " - " + dataset.size(),
                        container
                );
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

    /**
     * Generate random dataset of doubles with given dimensions
     *
     * @param size
     * @param dim
     * @return
     */
    public Dataset<E> generateData(int size, int dim) {
        LOG.info("generating data: {}x{}", size, dim);
        Dataset<E> dataset = new ArrayDataset<>(size, dim);
        for (int i = 0; i < dim; i++) {
            dataset.attributeBuilder().create("attr-" + i, "NUMERIC");
        }
        for (int i = 0; i < size; i++) {
            dataset.instance(i).setName(String.valueOf(i));
            for (int j = 0; j < dim; j++) {
                dataset.set(i, j, rand.nextDouble());
            }
        }

        return dataset;
    }

}
