package org.clueminer.clustering.benchmark;

import org.clueminer.clustering.aggl.linkage.CompleteLinkage;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class HclustBenchmark {

    public Container hclust(final AgglomerativeClustering algorithm, final Dataset<? extends Instance> dataset, final String linkage) {

        final Container runnable = new Container(algorithm, dataset) {

            @Override
            public HierarchicalResult hierarchical(AgglomerativeClustering algorithm, Dataset<? extends Instance> dataset, Props params) {
                params.putBoolean(AgglParams.CLUSTER_ROWS, true);
                params.put(AgglParams.LINKAGE, linkage);

                return algorithm.hierarchy(dataset, params);
            }
        };
        return runnable;
    }

    public Container singleLinkage(final AgglomerativeClustering algorithm, final Dataset<? extends Instance> dataset) {
        return hclust(algorithm, dataset, SingleLinkage.name);
    }

    public Container completeLinkage(final AgglomerativeClustering algorithm, final Dataset<? extends Instance> dataset) {
        return hclust(algorithm, dataset, CompleteLinkage.name);
    }

}
