

package jetbrains.buildServer.staticUIExtensions.model;

import org.jetbrains.annotations.NotNull;

public class ContainsMatcher implements UrlMatcher {
  private final String mySubstring;

  public ContainsMatcher(@NotNull String substring) {
    mySubstring = substring;
  }

  public boolean matches(@NotNull String url) {
    return url.contains(mySubstring);
  }

  @Override
  public String toString() {
    return "ContainsMatcher{" +
            "mySubstring='" + mySubstring + '\'' +
            '}';
  }
}