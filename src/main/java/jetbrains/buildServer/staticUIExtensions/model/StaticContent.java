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
