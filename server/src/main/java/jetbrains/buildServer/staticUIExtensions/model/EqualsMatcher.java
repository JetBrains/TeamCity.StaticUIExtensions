

package jetbrains.buildServer.staticUIExtensions.model;

import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:29
 */
public class EqualsMatcher implements UrlMatcher {
  private final String myEq;

  public EqualsMatcher(@NotNull String eq) {
    myEq = eq;
  }

  public boolean matches(@NotNull String url) {
    return url.equals(myEq);
  }

  @Override
  public String toString() {
    return "EqualsMatcher{" +
            "myPrefix='" + myEq + '\'' +
            '}';
  }
}