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
package org.clueminer.clustering.benchmark.nsga;

import com.beust.jcommander.Parameter;
import org.clueminer.clustering.benchmark.AbsParams;

/**
 *
 * @author Tomas Barton
 */
public class NsgaParams extends AbsParams {

    @Parameter(names = "--test", description = "test only on one dataset")
    public boolean test = false;

    @Parameter(names = "--generations", description = "number of generations in evolution")
    public int generations = 10;

    @Parameter(names = "--population", description = "size of population in each generation")
    public int population = 20;

    @Parameter(names = "--solutions", description = "number of final solutions which will be returned as result")
    public int solutions = 10;

    @Parameter(names = "--supervised", description = "supervised criterion for external evaluation")
    public String supervised = "Adjusted Rand";

    @Parameter(names = "--mutation", description = "probability of mutation")
    public double mutation = 0.5;

    @Parameter(names = "--crossover", description = "probability of crossover")
    public double crossover = 0.5;

    @Parameter(names = "--dataset", description = "use specific dataset")
    public String dataset = null;

    @Parameter(names = "--limit-k", description = "limit max. clusterings size to sqrt(n)")
    public boolean limitK = false;

}
