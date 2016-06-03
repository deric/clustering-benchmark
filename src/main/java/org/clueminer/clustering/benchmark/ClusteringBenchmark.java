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

import org.clueminer.clustering.aggl.linkage.CompleteLinkage;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Execute single clustering
 *
 * @author Tomas Barton
 * @param <E>
 */
public class ClusteringBenchmark<E extends Instance> {

    public Container<E> cluster(Dataset<E> dataset, Props props) {
        ClusteringFactory cf = ClusteringFactory.getInstance();
        ClusteringAlgorithm algorithm = cf.getProvider(props.get(AlgParams.ALG));

        return new Container(algorithm, dataset, props);
    }

    public Container<E> cluster(ClusteringAlgorithm algorithm, Dataset<E> dataset, Props props) {
        return new Container(algorithm, dataset, props);
    }

    public Container<E> hclust(final AgglomerativeClustering algorithm, final Dataset<E> dataset, final String linkage) {
        Props props = new Props();
        props.put(AlgParams.LINKAGE, linkage);
        final Container<E> runnable = new Container(algorithm, dataset, props);
        return runnable;
    }

    public Container<E> singleLinkage(final AgglomerativeClustering algorithm, final Dataset<E> dataset) {
        return hclust(algorithm, dataset, SingleLinkage.name);
    }

    public Container<E> completeLinkage(final AgglomerativeClustering algorithm, final Dataset<E> dataset) {
        return hclust(algorithm, dataset, CompleteLinkage.name);
    }

}
