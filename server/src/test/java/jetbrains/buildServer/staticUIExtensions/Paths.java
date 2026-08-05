

package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:16
 */
public class Paths {

  @NotNull
  public static File getConfigsFile(@NotNull final String name) {
    return FileUtil.getCanonicalFile(new File("./testData/", name));
  }

}