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
