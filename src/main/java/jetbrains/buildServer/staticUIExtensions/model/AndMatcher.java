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
