package constant;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tianxing
 */
public interface Constant {
    String USER_HOME = System.getProperty("user.home");
    Path MUSIC_PATH = Paths.get(USER_HOME, "Music");
}
