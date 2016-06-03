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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.benchmark.chameleon2.Cham2Bench;
import org.clueminer.clustering.benchmark.consensus.ConsensusExp;
import org.clueminer.clustering.benchmark.cutoff.CutoffExp;
import org.clueminer.clustering.benchmark.exp.Data;
import org.clueminer.clustering.benchmark.exp.EvolveScores;
import org.clueminer.clustering.benchmark.exp.HclusPar;
import org.clueminer.clustering.benchmark.exp.HclusPar2;
import org.clueminer.clustering.benchmark.exp.Hclust;
import org.clueminer.clustering.benchmark.gen.NsgaGen;
import org.clueminer.clustering.benchmark.nsga.NsgaScore;
import org.clueminer.clustering.benchmark.partition.PartitionExp;

/**
 *
 * @author deric
 */
public class Main {

    private static final Map<String, Bench> map = new HashMap<>();
    private static Main instance;

    public Main() {
        map.put("hclust", new Hclust());
        map.put("data", new Data());
        map.put("hclust-par", new HclusPar());
        map.put("hclust-par2", new HclusPar2());
        map.put("evolve-sc", new EvolveScores());
        map.put("nsga", new NsgaScore());
        map.put("nsga-gen", new NsgaGen());
        map.put("consensus", new ConsensusExp());
        map.put("cutoff", new CutoffExp());
        map.put("partition", new PartitionExp());
        map.put("chameleon2", new Cham2Bench());
    }

    /**
     * Entrypoint to all experiments
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (instance == null) {
            instance = new Main();
        }
        if (args.length == 0) {
            usage();
        }
        String exp = args[0];
        if (!Main.map.containsKey(exp)) {
            usage();
        }

        String[] other = Arrays.copyOfRange(args, 1, args.length);
        Bench bench = Main.map.get(exp);
        //run it
        bench.main(other);
    }

    private static void usage() {
        System.out.println("Usage: java -jar {jar name} [experiment name] [[optional arguments]]");
        System.out.println("Valid experriments values are:");
        for (String key : map.keySet()) {
            for (int i = 0; i < 5; i++) {
                System.out.print(" ");
            }
            System.out.print("- " + key + "\n");
        }
        System.out.println("use '[experiment] --help' to find out more about optional arguments");
        System.out.println("--------------------------");
        System.exit(1);
    }

}
