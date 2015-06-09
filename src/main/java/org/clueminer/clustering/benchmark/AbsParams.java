package org.clueminer.clustering.benchmark;

import com.beust.jcommander.Parameter;
import java.io.File;
import org.clueminer.utils.FileUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class AbsParams {

    @Parameter(names = "--dir", description = "directory for results", required = false)
    public String home = System.getProperty("user.home") + File.separatorChar
            + NbBundle.getMessage(FileUtils.class, "FOLDER_Home");

    @Parameter(names = "--repeat", description = "number of repetitions of each experiment")
    public int repeat = 5;

    @Parameter(names = "--log", description = "java log level")
    public String log = "INFO";

}
