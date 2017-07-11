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

package jetbrains.buildServer.staticUIExtensions.web;

import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 19:58
 */
public class RulePageExtensionsFactory {
  private final PagePlaces myPlaces;
  private final PluginDescriptor myDescription;
  private final ControllerPaths myPaths;

  public RulePageExtensionsFactory(@NotNull final PagePlaces places,
                                   @NotNull final PluginDescriptor description,
                                   @NotNull final ControllerPaths paths) {
    myPlaces = places;
    myDescription = description;
    myPaths = paths;
  }

  @NotNull
  public RulePageExtension createExtension(@NotNull final Rule rule) {
    return new RulePageExtension(myPlaces, myDescription, myPaths, rule);
  }
}
