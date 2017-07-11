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

package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.staticUIExtensions.web.RulePageExtension;
import jetbrains.buildServer.staticUIExtensions.web.RulePageExtensionsFactory;
import jetbrains.buildServer.staticUIExtensions.web.StaticContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:11
 */
public class PagePlacesInitializerImpl implements PagePlacesInitializer {
  private final RulePageExtensionsFactory myExtensionsFactory;
  private final StaticContentManager myRegistry;

  public PagePlacesInitializerImpl(@NotNull final RulePageExtensionsFactory extensionsFactory,
                                   @NotNull final StaticContentManager registry) {
    myExtensionsFactory = extensionsFactory;
    myRegistry = registry;
  }

  public void registerPagePlaces(@NotNull final Collection<Rule> rules) {
    List<RulePageExtension> exts = new ArrayList<RulePageExtension>();
    for (Rule rule : rules) {
      exts.add(myExtensionsFactory.createExtension(rule));
    }
    myRegistry.updateExtensions(exts);
  }
}
