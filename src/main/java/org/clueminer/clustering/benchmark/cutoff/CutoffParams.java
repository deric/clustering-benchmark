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
package org.clueminer.clustering.benchmark.cutoff;

import com.beust.jcommander.Parameter;
import org.clueminer.clustering.benchmark.AbsParams;

/**
 *
 * @author Tomas Bruna
 */
public class CutoffParams extends AbsParams {

    @Parameter(names = "--datasets", description = "Datasets to test separated by ,")
    public String datasets = "triangle1, triangle2, flame, jain, long1, long2, long3, sizes1, sizes2, sizes3,"
            + " sizes4, sizes5, compound, atom, aggregation, lsun, pathbased, smile1, smile2, smile3, twodiamonds, "
            + "wingnut, target, st900, square1, square2, square3, square4, square5, spiral, spiral2, spherical_6_2, "
            + "spherical_5_2, longsquare, engytime, donutcurves, diamond9, complex8, complex9, chainlink, R15, D31, "
            + "2d-4c, 2d-20c-no0, 2d-10c";

    @Parameter(names = "--algorithm", description = "Clustering algorithm name")
    public String algorithm = "Chameleon";

    @Parameter(names = "--strategy", description = "Cutoff strategies to compare separated by ,")
    public String strategies = "hill-climb cutoff, hill-climb inc, First jump cutoff";

    @Parameter(names = "--internalEvals", description = "Iternal evaluations to compare separated by ,")
    public String internalEvals = "Silhouette, SD index";

    @Parameter(names = "--externalEvals", description = "External evaluations to determine quality of results"
               + " separated by ,")
    public String externalEvals = "NMI-sqrt, NMI-sum, Deviation, Adjusted Rand";

    @Parameter(names = "--startRange", description = "Range of the start parameter the in the First jump cutoff")
    public String startRange = "30-400";

    @Parameter(names = "--factorRange", description = "Range of the factor parameter in the First jump cutoff")
    public String factorRange = "1.01-6";

    @Parameter(names = "--mode", description = "Whether to compare different cutoff methods (option comparison)"
               + " or to try different parameters for the First jump cutoff (option firstJump)")
    public String mode = "comparison";

}
