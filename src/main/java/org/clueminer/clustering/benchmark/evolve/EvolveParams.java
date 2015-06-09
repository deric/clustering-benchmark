package org.clueminer.clustering.benchmark.evolve;

import com.beust.jcommander.Parameter;
import org.clueminer.clustering.benchmark.AbsParams;

/**
 *
 * @author Tomas Barton
 */
public class EvolveParams extends AbsParams {

    @Parameter(names = "--external", description = "reference criterion for comparing with internal criterion (Precision, Accuracy, NMI)")
    public String external = "AUC";

    @Parameter(names = "--test", description = "test only on one dataset")
    public boolean test = false;

    @Parameter(names = "--generations", description = "number of generations in evolution")
    public int generations = 10;

    @Parameter(names = "--population", description = "size of population in each generation")
    public int population = 10;

}
