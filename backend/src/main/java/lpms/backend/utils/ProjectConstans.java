package lpms.backend.utils;

import java.nio.file.Paths;

public class ProjectConstans {
    public static final String ROOT_DIR = System.getProperty("user.dir");

    public static final String PYTHON_EXECUTABLE_PATH = Paths.get(ROOT_DIR,"pythonScript", "python.exe").toString();
    public static final String PYTHON_SCRIPT_PATH = Paths.get(ROOT_DIR,"pythonScript").toString();
}
