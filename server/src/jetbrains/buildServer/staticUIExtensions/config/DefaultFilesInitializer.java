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
import jetbrains.buildServer.serverSide.impl.persisting.SettingsPersister;
import jetbrains.buildServer.staticUIExtensions.PagePlacesCollector;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 14:11
 */
public class DefaultFilesInitializer {
  private static final Logger LOG = Logger.getInstance(DefaultFilesInitializer.class.getName());

  private final PagePlacesCollector myCollector;
  private final ConfigurationImpl myConfig;
  private final SettingsPersister mySettingsPersister;

  public DefaultFilesInitializer(@NotNull final ConfigurationImpl config,
                                 @NotNull final PagePlacesCollector collector,
                                 @NotNull final SettingsPersister settingsPersister) {
    myConfig = config;
    myCollector = collector;
    mySettingsPersister = settingsPersister;
  }

  public void writeInitialFiles() {
    writeDistConfig();
  }

  private void writeDistConfig() {
    final File config = myConfig.getConfigurationXml();
    final File distConfig = new File(myConfig.getConfigurationXml() + ".dist");

    final boolean defaultConfig = isDefaultConfig(config);

    writeDistConfig(distConfig);

    if (defaultConfig) {
      // if main config is the same as default, then it was not changed and we can update
      // all the configs from the resources
      writeDistConfig(config);

      for (String name : Collections.singletonList("beforeContent.html")) {
        final File file = new File(myConfig.getIncludeFilesBase(), name);
        if (!file.isFile()) {
          saveResourceToFile("/" + name, file);
        }
      }
    }
  }

  private void writeDistConfig(@NotNull File targetFile) {
    try {
      String configText = FileUtil.readResourceAsString(getClass(), "/static-ui-extensions.xml", StandardCharsets.UTF_8);
      if (configText == null) return;

      configText = configText.replaceAll("@@@PAGE_PLACES_LIST@@@", generatePagePlaces());
      mySettingsPersister.scheduleSaveFile("Updating static-ui-extensions.xml.dist", targetFile, configText.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      LOG.warnAndDebugDetails("Failed to write " + targetFile, e);
    }
  }

  private void saveResourceToFile(@NotNull String resourcePath, @NotNull File targetFile) {
    try {
      String configText = FileUtil.readResourceAsString(getClass(), resourcePath, StandardCharsets.UTF_8);
      if (configText == null) return;

      mySettingsPersister.scheduleSaveFile("Writing the " + targetFile.getName(), targetFile, configText.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      LOG.warnAndDebugDetails("Failed to write " + targetFile, e);
    }
  }

  private boolean isDefaultConfig(@NotNull File config) {
    if (!config.exists()) return true;
    try {
      return FileUtil.readText(config).equals(FileUtil.readText(new File(config.getPath() + ".dist")));
    } catch(IOException e) {
      return false;
    }
  }

  @NotNull
  private String generatePagePlaces() {
    StringBuilder sb = new StringBuilder();
    final String newLine = System.getProperty("line.separator", "\r\n");
    final String gap = "      - ";

    sb.append(newLine);
    sb.append("    Supported values for place-id are: ").append(newLine);

    for (String placeId : myCollector.getPlaceIds().keySet()) {
      sb.append(gap);
      sb.append(placeId);
      sb.append(newLine);
    }

    return sb.toString();
  }
}
