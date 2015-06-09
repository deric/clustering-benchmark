package org.clueminer.clustering.benchmark;

import java.util.logging.Logger;
import org.clueminer.clustering.TreeDiff;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class Container implements Runnable {

    private HierarchicalResult result;
    private final AgglomerativeClustering algorithm;
    private final Dataset<? extends Instance> dataset;
    private static final Logger logger = Logger.getLogger(Container.class.getName());

    public Container(AgglomerativeClustering algorithm, Dataset<? extends Instance> dataset) {
        this.algorithm = algorithm;
        this.dataset = dataset;
    }

    public abstract HierarchicalResult hierarchical(AgglomerativeClustering algorithm, Dataset<? extends Instance> dataset, Props params);

    @Override
    public void run() {
        Props params = new Props();
        this.result = hierarchical(algorithm, dataset, params);
    }

    public boolean equals(Container other) {
        if (this.result == null || other.result == null) {
            throw new RuntimeException("got null result. this = " + result + " other = " + other);
        }
        return TreeDiff.compare(this.result, other.result);
    }

}
