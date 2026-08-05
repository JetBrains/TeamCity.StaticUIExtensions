

package jetbrains.buildServer.staticUIExtensions.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:56
 */
public class OrMatcher implements UrlMatcher {
  private final Collection<UrlMatcher> myMatchers;

  public OrMatcher(@NotNull final Collection<UrlMatcher> matchers) {
    myMatchers = matchers;
  }

  public boolean matches(@NotNull String url) {
    for (UrlMatcher matcher : myMatchers) {
      if (matcher.matches(url)) return true;
    }
    return myMatchers.isEmpty();
  }

  @Override
  public String toString() {
    return "OrMatcher{" +
            "myMatchers=" + myMatchers +
            '}';
  }
}