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
package org.clueminer.clustering.benchmark.consensus;

import com.beust.jcommander.Parameter;
import org.clueminer.clustering.benchmark.AbsParams;

/**
 *
 * @author deric
 */
public class ConsensusParams extends AbsParams {

    @Parameter(names = "--dataset", description = "use specific dataset")
    public String dataset = null;

    @Parameter(names = "--algorithm", description = "clustering algorithm name")
    public String algorithm = "K-Means bagging";

    @Parameter(names = "--k", description = "expected number of clusters (some methods might not respect this)")
    public int k = -1;

    @Parameter(names = "--method", description = "Initialization and consensus approach")
    public String method = "";

    @Parameter(names = "--fixed", description = "whether to use 'correct' k as parameter")
    public boolean fixedK = false;

}
