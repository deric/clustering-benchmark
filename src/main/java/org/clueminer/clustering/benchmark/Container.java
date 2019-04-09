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

import org.clueminer.clustering.TreeDiff;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exec.ClusteringExecutorCached;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class Container<E extends Instance> implements Runnable {

    private HierarchicalResult result;
    private Clustering clustering;
    private final Dataset<E> dataset;
    private Props params;
    private static final Logger LOG = LoggerFactory.getLogger(Container.class);
    private ClusteringExecutorCached executor;

    public Container(ClusteringAlgorithm algorithm, Dataset<E> dataset) {
        this.executor = new ClusteringExecutorCached();
        executor.setAlgorithm(algorithm);
        this.dataset = dataset;
        this.params = new Props();
    }

    public Container(ClusteringAlgorithm algorithm, Dataset<E> dataset, Props params) {
        this.executor = new ClusteringExecutorCached();
        executor.setAlgorithm(algorithm);
        this.dataset = dataset;
        this.params = params;
    }

    public HierarchicalResult hierarchical(AgglomerativeClustering algorithm, Dataset<E> dataset, Props params) {
        params.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        return algorithm.hierarchy(dataset, params);
    }

    @Override
    public void run() {
        if (executor.getAlgorithm() instanceof AgglomerativeClustering) {
            this.result = executor.hclustRows(dataset, params);
        } else {
            this.clustering = executor.clusterRows(dataset, params);
        }
    }

    public Clustering cluster(ClusteringAlgorithm algorithm, Dataset<E> dataset, Props params) {
        return executor.clusterRows(dataset, params);
    }

    public boolean equals(Container other) {
        if (this.result == null || other.result == null) {
            throw new RuntimeException("got null result. this = " + result + " other = " + other);
        }
        return TreeDiff.compare(this.result, other.result);
    }

}
