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

    @Test
    public void testSingleLinkage() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        for (AgglomerativeClustering alg : algorithms) {
            NanoBench.create().measurements(2).cpuAndMemory().measure(
                    alg.getName() + " single link - " + dataset.getName(),
                    new ClusteringBenchmark().singleLinkage(alg, dataset)
            );
        }
    }

    @Test
    public void testCompleteLinkage() {
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        for (AgglomerativeClustering alg : algorithms) {
            NanoBench.create().cpuAndMemory().measurements(2).measure(
                    alg.getName() + " complete link - " + dataset.getName(),
                    new ClusteringBenchmark().completeLinkage(alg, dataset)
            );
        }
    }

    @Test
    public void testSingleLinkageSameResultTwoAlg() {
        //Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        Dataset<? extends Instance> dataset = FakeDatasets.kumarData();
        //use one algorithm as reference one
        AgglomerativeClustering alg1 = new HC();
        Container ref = new ClusteringBenchmark().completeLinkage(alg1, dataset);
        ref.run();
        Container other;

        AgglomerativeClustering alg2 = new HCLW();
        other = new ClusteringBenchmark().completeLinkage(alg2, dataset);
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
        Container ref = new ClusteringBenchmark().hclust(algs[0], dataset, linkage);
        ref.run();
        Container other;

        //compare result to others
        for (int i = 1; i < algs.length; i++) {
            AgglomerativeClustering algorithm = algs[i];
            other = new ClusteringBenchmark().hclust(algorithm, dataset, linkage);
            other.run();
            System.out.println("comparing " + algs[0].getName() + " vs " + algorithm.getName() + " linkage: " + linkage);
            assertEquals(true, ref.equals(other));
        }
    }

}
