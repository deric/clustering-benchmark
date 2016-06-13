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

import java.util.Random;
import static org.clueminer.chameleon.Chameleon.K;
import static org.clueminer.chameleon.Chameleon.MAX_PARTITION;
import org.clueminer.clustering.benchmark.GnuplotReporter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.report.NanoBench;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class PartitionBench implements Runnable {

    protected final Random rand;
    protected final PartitionParams params;
    protected final Partitioning[] algorithms;
    protected final String results;

    public PartitionBench(PartitionParams params, String results, Partitioning[] algorithms) {
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
        for (Partitioning alg : algorithms) {
            names[j++] = alg.getName();
        }

        GnuplotReporter reporter = new GnuplotReporter(results,
                new String[]{"algorithm", "edges", "n"}, names, params.nSmall + "-" + params.n, 10);
        System.out.println("increment = " + inc);
        KNNGraphBuilder knn = new KNNGraphBuilder();
        Props pref = new Props();
        for (int i = params.nSmall; i <= params.n; i += inc) {
            Graph g = new AdjMatrixGraph(i);
            Dataset<? extends Instance> dataset = generateData(i, params.dimension);
            int datasetK = determineK(dataset);
            int maxPartitionSize = determineMaxPartitionSize(dataset);
            pref.putInt(MAX_PARTITION, maxPartitionSize);
            pref.putInt(K, datasetK);
            g = knn.getNeighborGraph(dataset, g, datasetK);

            for (Partitioning alg : algorithms) {
                String[] opts = new String[]{alg.getName(), String.valueOf(g.getEdgeCount()), String.valueOf(dataset.size())};
                NanoBench.create().measurements(params.repeat).collect(reporter, opts).measure(
                        alg.getName() + " - " + dataset.size(),
                        bench(alg, g, maxPartitionSize, pref)
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

    public Runnable bench(final Partitioning algorithm, final Graph g, final int maxPartitionSize, final Props props) {

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                algorithm.partition(maxPartitionSize, g, props);
            }

        };
        return runnable;
    }

    /**
     * Generate random dataset of doubles with given dimensions
     *
     * @param size
     * @param dim
     * @return
     */
    protected Dataset<? extends Instance> generateData(int size, int dim) {
        System.out.println("generating data: " + size + " x " + dim);
        Dataset<? extends Instance> dataset = new ArrayDataset<>(size, dim);
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

    private int determineK(Dataset<? extends Instance> dataset) {

        if (dataset.size() < 500) {
            return (int) (Math.log(dataset.size()) / Math.log(2));
        } else {
            return (int) (Math.log(dataset.size()) / Math.log(2)) * 2;
        }
    }

    private int determineMaxPartitionSize(Dataset<? extends Instance> dataset) {
        if (dataset.size() < 500) {
            return 5;
        } else if ((dataset.size() < 2000)) {
            return dataset.size() / 100;
        } else {
            return dataset.size() / 200;
        }
    }

}
