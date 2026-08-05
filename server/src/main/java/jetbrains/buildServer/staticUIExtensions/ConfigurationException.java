

package jetbrains.buildServer.staticUIExtensions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:58
 */
public class ConfigurationException extends Exception {
  public ConfigurationException(@NotNull final String message) {
    super(message);
  }

  public ConfigurationException(@NotNull final String message, @NotNull final Throwable cause) {
    super(message, cause);
  }
}