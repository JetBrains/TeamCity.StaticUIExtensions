/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
