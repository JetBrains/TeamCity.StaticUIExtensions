

package jetbrains.buildServer.staticUIExtensions.model;

import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:01
 */
public class StaticContent {
  @Nullable private final String myHTML;
  @Nullable private final String myJS;
  @Nullable private final String myCSS;

  public StaticContent(@Nullable final String HTML,
                       @Nullable final String JS,
                       @Nullable final String CSS) {
    myHTML = nullIfEmpty(HTML);
    myJS = nullIfEmpty(JS);
    myCSS = nullIfEmpty(CSS);
  }

  @Nullable
  private static String nullIfEmpty(@Nullable String text) {
    if (text == null) return null;
    text = text.trim();
    if (text.length() == 0) return null;
    return text;
  }

  @Nullable
  public String getHTML() {
    return myHTML;
  }

  @Nullable
  public String getJS() {
    return myJS;
  }

  @Nullable
  public String getCSS() {
    return myCSS;
  }

  public boolean isValid() {
    return getCSS() != null || getJS() != null || getHTML() != null;
  }

  @Override
  public String toString() {
    return "StaticContent{" +
            "myHTML='" + myHTML + '\'' +
            ", myJS='" + myJS + '\'' +
            ", myCSS='" + myCSS + '\'' +
            '}';
  }
}