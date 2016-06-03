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
package org.clueminer.clustering.benchmark.gen;

import com.beust.jcommander.Parameter;
import org.clueminer.clustering.benchmark.AbsParams;

/**
 *
 * @author deric
 */
public class NsgaGenParams extends AbsParams {

    @Parameter(names = "--population", description = "size of population in each generation")
    public int population = 20;

    @Parameter(names = "--solutions", description = "number of final solutions which will be returned as result")
    public int solutions = 10;

    @Parameter(names = "--mutation", description = "probability of mutation")
    public double mutation = 0.5;

    @Parameter(names = "--crossover", description = "probability of crossover")
    public double crossover = 0.5;

    @Parameter(names = "--dataset", description = "use specific dataset")
    public String dataset = null;

    @Parameter(names = "--c1", description = "criterion 1")
    public String c1 = "Davies-Bouldin";

    @Parameter(names = "--c2", description = "criterion 2")
    public String c2 = "AIC";

}
