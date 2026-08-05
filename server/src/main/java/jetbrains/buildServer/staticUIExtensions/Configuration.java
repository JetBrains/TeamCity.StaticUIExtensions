

package jetbrains.buildServer.staticUIExtensions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:47
 */
public interface Configuration {
  /**
   * @return path to plugin configuration file
   */
  @NotNull
  File getConfigurationXml();

  /**
   * Maps relative file path from configuration to FileSystem path
   *
   * @param path path to map
   * @return mapped path of null
   */
  @Nullable
  File mapIncludeFilePath(@NotNull final String path);

  /**
   * @return access token for resource controller
   */
  @NotNull
  String getAccessToken();

  @NotNull
  File getIncludeFilesBase();
}