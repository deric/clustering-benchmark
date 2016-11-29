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
package org.clueminer.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.clueminer.dataset.api.DataProvider;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.exception.ParserError;
import org.clueminer.io.arff.ARFFHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class DataLoader implements DataProvider {

    private final Map<String, String> datasets;
    private final Map<String, String> fullPaths;
    private final Map<String, Dataset<? extends Instance>> cache;
    private String prefix = "datasets" + File.separatorChar + "artificial";

    public DataLoader(Map<String, String> datasets, String prefix, Map<String, String> fullPaths) {
        this.datasets = datasets;
        this.cache = new HashMap<>(datasets.size());
        this.prefix = prefix;
        this.fullPaths = fullPaths;
    }

    @Override
    public String[] getDatasetNames() {
        return datasets.keySet().toArray(new String[0]);
    }

    @Override
    public Dataset<? extends Instance> getDataset(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        if (!datasets.containsKey(name)) {
            throw new RuntimeException("unknown dataset " + name);
        }

        Dataset<? extends Instance> dataset = loadDataset(name, datasets.get(name), fullPaths.get(name));
        cache.put(name, dataset);
        return dataset;
    }

    @Override
    public Dataset<? extends Instance> first() {
        Iterator<String> it = datasets.keySet().iterator();
        if (!it.hasNext()) {
            throw new RuntimeException("no datasets were loaded");
        }
        return getDataset(it.next());
    }

    @Override
    public int count() {
        return datasets.size();
    }

    /**
     * Tries to load dataset by its name. There must be a method for loading the
     * dataset in this class.
     *
     * @param name
     * @return
     */
    private Dataset<? extends Instance> loadDataset(String name, String type, String fullPath) {
        Dataset<? extends Instance> dataset = null;
        switch (type) {
            case "arff":
                //TODO: multi dimensions support
                dataset = new ArrayDataset(10, 2);
                dataset.setName(name);
                ARFFHandler arff = new ARFFHandler();
                try {
                    arff.load(resource(name + "." + type, fullPath), dataset);
                } catch (FileNotFoundException | ParserError ex) {
                    Exceptions.printStackTrace(ex);
                }
                break;
            default:
                throw new RuntimeException("unsupported format " + type);
        }

        return dataset;
    }

    /**
     * Resource packed in jar is not possible to open directly, this method uses
     * a .tmp file which should be on exit deleted
     *
     * @param path
     * @param fullPath
     * @return
     */
    public File resource(String path, String fullPath) {
        String resource = prefix + File.separatorChar + path;
        File file;
        URL url = DataLoader.class.getResource(resource);
        if (url == null) {
            //probably on Windows
            file = new File(fullPath);
            if (file.exists()) {
                return file;
            }
            //non existing URL
            //no classpath, compiled as JAR
            //if path is in form: "jar:path.jar!resource/data.arff"
            int pos = fullPath.lastIndexOf("!");
            if (pos > 0) {
                resource = fullPath.substring(pos + 1);
                if (!resource.startsWith("/")) {
                    //necessary for loading as a stream
                    resource = "/" + resource;
                }
            }
            return loadResource(resource);
        }

        if (url.toString().startsWith("jar:")) {
            return loadResource(resource);
        } else {
            file = new File(url.getFile());
        }
        return file;
    }

    private File loadResource(String resource) {
        File file = null;
        try {
            InputStream input = getClass().getResourceAsStream(resource);
            file = File.createTempFile("nodesfile", ".tmp");
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            file.deleteOnExit();
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
        return file;
    }

    public static DataProvider createLoader(String p1, String p2) {
        String path = p1 + File.pathSeparatorChar + p2;
        Map<String, String> datasets = new TreeMap<>();
        Map<String, String> paths = new HashMap<>();

        final Collection<String> list = ResourceList.getResources(p1, p2);
        int idx, dot;
        String dataset;
        String ext;
        for (final String name : list) {
            idx = name.lastIndexOf(File.separatorChar);
            dot = name.lastIndexOf(".");
            if (dot > 0) {
                dataset = name.substring(idx + 1, dot);
            } else {
                dataset = name;
            }
            ext = name.substring(dot + 1);
            datasets.put(dataset, ext);
            paths.put(dataset, name);
        }

        return new DataLoader(datasets, path, paths);
    }

    @Override
    public Iterator<Dataset<? extends Instance>> iterator() {
        return new DataLoaderIterator();
    }

    @Override
    public boolean hasDataset(String name) {
        for (String dataset : datasets.keySet()) {
            if (name.equals(dataset)) {
                return true;
            }
        }
        return false;
    }

    private class DataLoaderIterator implements Iterator<Dataset<? extends Instance>> {

        private final Iterator<String> it;

        public DataLoaderIterator() {
            it = datasets.keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Dataset<? extends Instance> next() {
            return getDataset(it.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("not supported yet.");
        }

    }

}
