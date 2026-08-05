

package jetbrains.buildServer.staticUIExtensions.model;

import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:54
 */
public class StartsWithMatcher implements UrlMatcher {
  private final String myPrefix;

  public StartsWithMatcher(@NotNull String prefix) {
    myPrefix = prefix;
  }

  public boolean matches(@NotNull String url) {
    return url.startsWith(myPrefix);
  }

  @Override
  public String toString() {
    return "StartsWithMatcher{" +
            "myPrefix='" + myPrefix + '\'' +
            '}';
  }
}