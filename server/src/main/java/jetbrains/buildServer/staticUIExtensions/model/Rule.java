

package jetbrains.buildServer.staticUIExtensions.model;

import jetbrains.buildServer.web.openapi.PlaceId;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:51
 */
public class Rule {
  @NotNull private final String myRuleId;
  @NotNull private final UrlMatcher myUrlMatcher;
  @NotNull private final PlaceId myPlace;
  @NotNull private final StaticContent myContent;


  public Rule(@NotNull final String id,
              @NotNull final UrlMatcher urlMatcher,
              @NotNull final PlaceId place,
              @NotNull final StaticContent content) {
    myRuleId = id;
    myUrlMatcher = urlMatcher;
    myPlace = place;
    myContent = content;
  }

  @NotNull
  public String getRuleId() {
    return myRuleId;
  }

  @NotNull
  public UrlMatcher getUrlMatcher() {
    return myUrlMatcher;
  }

  @NotNull
  public PlaceId getPlace() {
    return myPlace;
  }

  @NotNull
  public StaticContent getContent() {
    return myContent;
  }

  @Override
  public String toString() {
    return "Rule{" +
            "myUrlMatcher=" + myUrlMatcher +
            ", myPlace=" + myPlace +
            ", myContent=" + myContent +
            '}';
  }
}