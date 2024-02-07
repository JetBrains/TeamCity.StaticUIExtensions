

package jetbrains.buildServer.staticUIExtensions.model;

import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:53
 */
public class TrueMatcher implements UrlMatcher{
  public boolean matches(@NotNull String url) {
    return true;
  }

  public String toString() {
    return "TrueMatcher";
  }
}