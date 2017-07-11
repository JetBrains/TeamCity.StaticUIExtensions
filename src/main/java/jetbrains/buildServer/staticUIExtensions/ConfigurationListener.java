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

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationReader;
import jetbrains.buildServer.staticUIExtensions.model.Rule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:19
 */
public class ConfigurationListener implements ConfigurationChangeListener {
  private static final Logger LOG = Logger.getInstance(ConfigurationListener.class.getName());

  private final ConfigurationReader myReader;
  private final PagePlacesInitializer myInitializer;
  private final Configuration myConfiguration;

  public ConfigurationListener(@NotNull final ConfigurationReader reader,
                               @NotNull final PagePlacesInitializer initializer,
                               @NotNull final Configuration configuration) {
    myReader = reader;
    myInitializer = initializer;
    myConfiguration = configuration;
  }

  public void configurationChanged() {
    final Collection<Rule> rules = parseConfigs();

    myInitializer.registerPagePlaces(rules);
  }

  @NotNull
  private Collection<Rule> parseConfigs() {
    final File xml = myConfiguration.getConfigurationXml();
    if (xml.isFile()) {
      try {
        return myReader.parseConfiguration(xml);
      } catch (ConfigurationException e) {
        LOG.warn("Failed to parse configuration file: " + e.getMessage(), e);
      }
    }
    return Collections.emptyList();
  }
}
