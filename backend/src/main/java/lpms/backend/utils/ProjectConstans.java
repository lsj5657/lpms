package lpms.backend.utils;

import java.nio.file.Paths;

public class ProjectConstans {
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String STATIC_RESOURCE_PATH = Paths.get(USER_DIR,"backend","src","main","resources","static").toString();

    public static final String PYTHON_EXECUTABLE_PATH = Paths.get(USER_DIR,"pythonScript", "python.exe").toString();
    public static final String PYTHON_SCRIPT_PATH = Paths.get(USER_DIR,"pythonScript").toString();
}
