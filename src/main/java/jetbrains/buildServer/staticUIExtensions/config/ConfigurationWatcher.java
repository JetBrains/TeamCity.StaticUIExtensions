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

package jetbrains.buildServer.staticUIExtensions.config;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.configuration.ChangeListener;
import jetbrains.buildServer.configuration.FileWatcher;
import jetbrains.buildServer.staticUIExtensions.Configuration;
import jetbrains.buildServer.staticUIExtensions.ConfigurationChangeListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:09
 */
public class ConfigurationWatcher {
  private static final Logger LOG = Logger.getInstance(ConfigurationWatcher.class.getName());

  private final FileWatcher myConfigWatcher;
  @NotNull
  private final Configuration myConfig;
  @NotNull
  private final Collection<ConfigurationChangeListener> myListeners;

  public ConfigurationWatcher(@NotNull final Configuration config,
                              @NotNull final Collection<ConfigurationChangeListener> listeners) {
    myConfig = config;
    myListeners = listeners;
    myConfigWatcher = new FileWatcher(config.getConfigurationXml());
    myConfigWatcher.setSleepingPeriod(5000);
    myConfigWatcher.registerListener(new ChangeListener() {
      public void changeOccured(String requestor) {
        ConfigurationWatcher.this.configurationFileChanged();
      }
    });
  }

  public void setCheckInterval(int interval) {
    myConfigWatcher.setSleepingPeriod(interval);
  }

  public void startWatching() {
    configurationFileChanged();
    myConfigWatcher.start();
  }

  public void stopWatching() {
    myConfigWatcher.stop();
  }

  private void configurationFileChanged() {
    LOG.info("Detected change in " + myConfig.getConfigurationXml() + ". StaticUIExtensions config will be reloaded. ");
    for (ConfigurationChangeListener listener : myListeners) {
      listener.configurationChanged();
    }
  }
}
