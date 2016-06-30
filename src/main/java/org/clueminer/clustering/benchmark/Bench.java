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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.data.DataLoader;
import org.clueminer.dataset.api.DataProvider;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.benchmark.DatasetFixture;

/**
 *
 * @author Tomas Barton
 */
public abstract class Bench {

    protected static String benchmarkFolder;
    protected HashMap<String, Map.Entry<Dataset<? extends Instance>, Integer>> availableDatasets = new HashMap<>();
    protected DataProvider provider;

    public Bench() {
        //constructor without arguments
    }

    public static void ensureFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("Directory " + folder + " created!");
            } else {
                System.out.println("Failed to create " + folder + "directory!");
            }
        }
    }

    public abstract void main(String[] args);

    public static void printUsage(String[] args, JCommander cmd, AbsParams params) {

        try {
            cmd.parse(args);

        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            cmd.usage();
            System.exit(0);
        }
    }

    protected void loadDatasets() {
        Map<Dataset<? extends Instance>, Integer> datasets = DatasetFixture.allDatasets();
        for (Map.Entry<Dataset<? extends Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<? extends Instance> d = entry.getKey();
            availableDatasets.put(d.getName(), entry);
        }
    }

    protected void loadBenchArtificial() {
        provider = DataLoader.createLoader("datasets", "artificial");
    }

    protected void loadBenchRealWorld() {
        provider = DataLoader.createLoader("datasets", "real-world");
    }

    /**
     * Load specific dataset by name
     *
     * @param name
     */
    protected void load(String name) {
        Map<Dataset<? extends Instance>, Integer> datasets = DatasetFixture.allDatasets();
        for (Map.Entry<Dataset<? extends Instance>, Integer> entry : datasets.entrySet()) {
            Dataset<? extends Instance> d = entry.getKey();
            if (d.getName().equalsIgnoreCase(name)) {
                availableDatasets.put(d.getName(), entry);
            }
        }
    }

    public static String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

}
