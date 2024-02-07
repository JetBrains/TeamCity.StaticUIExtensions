

package jetbrains.buildServer.staticUIExtensions.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:55
 */
public class AndMatcher implements UrlMatcher {
  private final Collection<UrlMatcher> myMatchers;

  public AndMatcher(@NotNull final Collection<UrlMatcher> matchers) {
    myMatchers = matchers;
  }

  public boolean matches(@NotNull String url) {
    for (UrlMatcher matcher : myMatchers) {
      if (!matcher.matches(url)) return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "AndMatcher{" +
            "myMatchers=" + myMatchers +
            '}';
  }
}