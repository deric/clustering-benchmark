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

import com.google.common.collect.ObjectArrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import org.clueminer.gnuplot.GnuplotHelper;
import org.clueminer.gnuplot.PointTypeIterator;
import org.clueminer.report.BigORes;
import org.clueminer.report.Reporter;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotReporter extends GnuplotHelper implements Reporter {

    private final String dataDir;
    private final File dataFile;
    private final LinkedList<String> plots;

    public GnuplotReporter(String folder, String[] opts, String[] algorithms, String suffix, int xCol) {
        this.dataDir = folder + File.separatorChar + "data";
        mkdir(dataDir);
        this.dataFile = new File(dataDir + File.separatorChar + "results-" + suffix + ".csv");
        this.plots = new LinkedList<>();
        writeHeader(opts);

        String memPath = dataDir + File.separatorChar + "mem" + suffix + ".gpt";
        String cpuPath = dataDir + File.separatorChar + "cpu" + suffix + ".gpt";
        String cpu2Path = dataDir + File.separatorChar + "cpu2" + suffix + ".gpt";
        String tpsPath = dataDir + File.separatorChar + "tps" + suffix + ".gpt";
        String timePath = dataDir + File.separatorChar + "time" + suffix + ".gpt";

        writePlotScript(new File(memPath),
                plotComplexity(8, "memory (kB)", xCol, 7, dataFile.getName(), algorithms, "Memory usage of hierarchical clustering algorithms - " + opts[1], false));
        writePlotScript(new File(cpuPath),
                plotCpu(8, "CPU", xCol, 2, dataFile.getName(), algorithms, "CPU usage of hierarchical clustering algorithms - " + opts[1], false));
        writePlotScript(new File(cpu2Path),
                plotComplexity(8, "CPU time", xCol, 2, dataFile.getName(), algorithms, "CPU usage of hierarchical clustering algorithms - " + opts[1], false));
        writePlotScript(new File(tpsPath),
                plotComplexity(8, "tps", xCol, 5, dataFile.getName(), algorithms, "Transactuion per second - " + opts[1], true));

        writePlotScript(new File(timePath),
                plotComplexity(8, "time", xCol, 4, dataFile.getName(), algorithms, "Execution time - " + opts[1], true));

        writeBashScript(folder);
    }

    private void writeHeader(String[] opts) {
        String[] head = new String[]{"label", "avg time (ms)", "memory (MB)", "total time (s)", "tps", "repeats", "memory (kB)"};
        String[] line = ObjectArrays.concat(head, opts, String.class);
        writeCsvLine(dataFile, line, false);
    }

    /**
     *
     * @param result
     */
    @Override
    public void finalResult(BigORes result) {
        String[] res = new String[]{result.getLabel(), result.avgTimeMs(),
            result.totalMemoryInMb(), result.totalTimeInS(), result.tps(),
            result.measurements(), result.totalMemoryInKb()
        };
        String[] line = ObjectArrays.concat(res, result.getOpts(), String.class);
        writeCsvLine(dataFile, line, true);
    }

    /**
     *
     * @param file     to write Gnuplot script
     * @param dataFile
     * @param labelPos column of label which is used for data rows in chart
     * @param type
     * @param x
     * @param y
     */
    private void writePlotScript(File file, String script) {
        PrintWriter template;
        try {
            template = new PrintWriter(file, "UTF-8");
            template.write(script);
            template.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        plots.add(withoutExtension(file));
    }

    private String plotCpu(int labelPos, String yLabel, int x, int y, String dataFile, String[] algorithms, String title, boolean logscale) {
        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"" + title + "\"\n"
                + "set xlabel \"data size\" font \"Times,12\"\n"
                + "set ylabel \"" + yLabel + "\" font \"Times,12\"\n"
                //   + "set xtics 0,0.5 nomirror\n"
                //   + "set ytics 0,0.5 nomirror\n"
                + "set mytics 2\n"
                + "set mx2tics 2\n"
                + "set grid\n"
                + "set pointsize 0.5\n"
                + "f(x) = 0.5 * x**2\n";
        if (logscale) {
            res += "set logscale y 2\n";
        }
        int i = 0;
        PointTypeIterator pti = new PointTypeIterator();
        for (String alg : algorithms) {
            if (i == 0) {
                res += "plot ";
            }
            res += "\"< awk -F\\\",\\\" '{if($" + labelPos + " == \\\"" + alg
                    + "\\\") print}' " + dataFile + "\" u " + x + ":" + y
                    + " t \"" + alg + "\" w linespoints pt " + pti.next();
            res += ", \\\n";
            i++;
        }
        res += "f(x) title 'x^2' with lines linestyle 18\n";
        return res;
    }

    private String plotComplexity(int labelPos, String yLabel, int x, int y, String dataFile, String[] algorithms, String title, boolean logscale) {
        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"" + title + "\"\n"
                + "set xlabel \"data size\" font \"Times,12\"\n"
                + "set ylabel \"" + yLabel + "\" font \"Times,12\"\n"
                //   + "set xtics 0,0.5 nomirror\n"
                //   + "set ytics 0,0.5 nomirror\n"
                + "set mytics 2\n"
                + "set mx2tics 2\n"
                + "set grid\n"
                + "set pointsize 0.5\n";
        if (logscale) {
            res += "set logscale y 2\n";
        }
        int i = 0;
        int last = algorithms.length - 1;
        PointTypeIterator pti = new PointTypeIterator();
        for (String alg : algorithms) {
            if (i == 0) {
                res += "plot ";
            }
            res += "\"< awk -F\\\",\\\" '{if($" + labelPos + " == \\\"" + alg
                    + "\\\") print}' " + dataFile + "\" u " + x + ":" + y
                    + " t \"" + alg + "\" w linespoints pt " + pti.next();
            if (i != last) {
                res += ", \\\n";
            } else {
                res += "\n";
            }

            i++;
        }
        return res;
    }

    /**
     * Should be called when all plot files are written
     */
    public void finish() {
        //TODO maybe some cleanup?
    }

    private void writeBashScript(String dataDir) {
        try {
            bashPlotScript(plots.toArray(new String[plots.size()]), dataDir, "data", "set term pdf font 'Times-New-Roman,8'", "pdf");
            bashPlotScript(plots.toArray(new String[plots.size()]), dataDir, "data", "set terminal pngcairo size 1024,768 enhanced font 'Verdana,10'", "png");

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     *
     * @param plots      plot names without extension
     * @param dir        base dir
     * @param gnuplotDir directory with gnuplot file
     * @param term
     * @param ext        extentions of output format
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void bashPlotScript(String[] plots, String dir, String gnuplotDir, String term, String ext)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //bash script to generate results
        String shFile = dir + File.separatorChar + "_plot-" + ext;
        try (PrintWriter template = new PrintWriter(shFile, "UTF-8")) {
            template.write(bashTemplate(gnuplotDir));
            template.write("TERM=\"" + term + "\"\n");
            int pos;
            for (String plot : plots) {
                pos = plot.indexOf(".");
                if (pos > 0) {
                    //remove extension part
                    plot = plot.substring(0, pos);
                }
                template.write("gnuplot -e \"${TERM}\" " + plot + gnuplotExtension
                        + " > $PWD" + File.separatorChar + ".." + File.separatorChar + plot + "." + ext + "\n");
            }
        }
        Runtime.getRuntime().exec("chmod u+x " + shFile);
    }

}
