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

import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.benchmark.BenchParams;
import org.clueminer.clustering.benchmark.ParamExperiment;
import org.clueminer.utils.Props;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class Cham2BenchTest {

    @Test
    public void testMain() {
        Props ch2 = new Props();
        ch2.put(AlgParams.ALG, "Chameleon");

        Props km = new Props();
        km.put(AlgParams.ALG, "k-means");
        km.putInt("k", 5);

        Props[] algorithms = new Props[]{
            ch2,
            km
        };

        BenchParams bp = new BenchParams();
        bp.n = 10;
        ParamExperiment exp = new ParamExperiment(bp, "ch2-bench", algorithms);
        exp.run();
    }

}
