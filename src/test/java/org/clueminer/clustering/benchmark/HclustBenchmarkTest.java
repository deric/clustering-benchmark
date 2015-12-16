package org.clueminer.clustering.benchmark;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.aggl.linkage.AverageLinkage;
import org.clueminer.clustering.aggl.linkage.MedianLinkage;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.NanoBench;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HclustBenchmarkTest {

    private final AgglomerativeClustering[] algorithms;

    public HclustBenchmarkTest() {
        //algorithms = new AgglomerativeClustering[]{new HAC(), new HACLW(), new HCL(), new HACLWMS()};
        algorithms = new AgglomerativeClustering[]{new HC(), new HCLW()};
    }

    @BeforeClass
    public static void setUp() {
        Logger logger = NanoBench.getLogger();
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        logger.addHandler(new ConsoleHandler());
    }

    @Test
    public void testSingleLinkage() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        for (AgglomerativeClustering alg : algorithms) {
            NanoBench.create().measurements(2).cpuAndMemory().measure(
                    alg.getName() + " single link - " + dataset.getName(),
                    new HclustBenchmark().singleLinkage(alg, dataset)
            );
        }
    }

    @Test
    public void testCompleteLinkage() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        for (AgglomerativeClustering alg : algorithms) {
            NanoBench.create().cpuAndMemory().measurements(2).measure(
                    alg.getName() + " complete link - " + dataset.getName(),
                    new HclustBenchmark().completeLinkage(alg, dataset)
            );
        }
    }

    @Test
    public void testSingleLinkageSameResultTwoAlg() {
        //Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        Dataset<? extends Instance> dataset = FakeDatasets.kumarData();
        //use one algorithm as reference one
        AgglomerativeClustering alg1 = new HC();
        Container ref = new HclustBenchmark().completeLinkage(alg1, dataset);
        ref.run();
        Container other;

        AgglomerativeClustering alg2 = new HCLW();
        other = new HclustBenchmark().completeLinkage(alg2, dataset);
        other.run();
        System.out.println("comparing " + algorithms[0].getName() + " vs " + alg2.getName());
        assertEquals(true, ref.equals(other));

    }

    /**
     * TODO: single linkage gives different results
     */
    //@Test
    public void testSingleLinkageSameResult() {
        //Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        Dataset<? extends Instance> dataset = FakeDatasets.kumarData();
        String linkage = SingleLinkage.name;
        compareTreeResults(dataset, linkage, algorithms);
    }

    @Test
    public void testAverageLinkageResult() {
        String linkage = AverageLinkage.name;
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();

        compareTreeResults(dataset, linkage, algorithms);
    }

    /**
     * TODO: median (centroid) linkage is broken
     */
    //@Test
    public void testMedianLinkageResult() {
        String linkage = MedianLinkage.name;
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();

        compareTreeResults(dataset, linkage, new AgglomerativeClustering[]{new HC(), new HCLW()});
    }

    private void compareTreeResults(Dataset<? extends Instance> dataset, String linkage, AgglomerativeClustering[] algs) {
        //use one algorithm as reference one
        Container ref = new HclustBenchmark().hclust(algs[0], dataset, linkage);
        ref.run();
        Container other;

        //compare result to others
        for (int i = 1; i < algs.length; i++) {
            AgglomerativeClustering algorithm = algs[i];
            other = new HclustBenchmark().hclust(algorithm, dataset, linkage);
            other.run();
            System.out.println("comparing " + algs[0].getName() + " vs " + algorithm.getName() + " linkage: " + linkage);
            assertEquals(true, ref.equals(other));
        }
    }

}
