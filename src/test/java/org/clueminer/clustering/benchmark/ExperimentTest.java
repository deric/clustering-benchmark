package org.clueminer.clustering.benchmark;

import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ExperimentTest {

    private Experiment subject;

    @Test
    public void testGenerateData() {
        BenchParams params = new BenchParams();
        params.n = 15;
        subject = new Experiment(params, null, new AgglomerativeClustering[]{new HC()});
        Dataset<? extends Instance> data = subject.generateData(params.n, params.dimension);
        assertEquals(params.n, data.size());
        assertEquals(params.dimension, data.attributeCount());
        Matrix m = data.asMatrix();
        assertEquals(params.n, m.rowsCount());
        assertEquals(params.dimension, m.columnsCount());
        m.print(5, 2);
    }

}
