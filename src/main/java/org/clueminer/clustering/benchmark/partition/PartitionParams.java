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

import com.beust.jcommander.Parameter;
import org.clueminer.clustering.benchmark.AbsParams;

/**
 *
 * @author deric
 */
public class PartitionParams extends AbsParams {

    @Parameter(names = "--n", description = "size of biggest dataset", required = false)
    public int n = 20;

    @Parameter(names = "--n-small", description = "size of smallest", required = false)
    public int nSmall = 5;

    @Parameter(names = "--steps", description = "number of datasets which will be generated")
    public int steps = 4;

    @Parameter(names = "--dimension", description = "number of attributes of each dataset")
    public int dimension = 5;

}
