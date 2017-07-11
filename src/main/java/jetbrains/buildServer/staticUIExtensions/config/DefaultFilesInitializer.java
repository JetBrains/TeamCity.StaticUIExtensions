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
import jetbrains.buildServer.staticUIExtensions.PagePlacesCollector;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 14:11
 */
public class DefaultFilesInitializer {
  private static final Logger LOG = Logger.getInstance(DefaultFilesInitializer.class.getName());

  private final PagePlacesCollector myCollector;
  private final ConfigurationImpl myConfig;

  public DefaultFilesInitializer(@NotNull final ConfigurationImpl config,
                                 @NotNull final PagePlacesCollector collector) {
    myConfig = config;
    myCollector = collector;
  }

  public void writeInitialFiles() {
    createPluginDirectory();
    writeDistConfig();
  }

  private void createPluginDirectory() {
    final File includeFilesBase = myConfig.getIncludeFilesBase();
    if (!includeFilesBase.isDirectory()) {
      LOG.info("Creating folder for StaticUIExtensions files at: " + includeFilesBase);
      //noinspection ResultOfMethodCallIgnored
      includeFilesBase.mkdirs();
    }
  }

  private void writeDistConfig() {
    final File config = myConfig.getConfigurationXml();
    final File distConfig = new File(myConfig.getConfigurationXml() + ".dist");

    final boolean useDefaultConfigs = usesDefaultConfig(config);

    updateDistConfig(distConfig);

    if (useDefaultConfigs) {
      updateConfig(config, distConfig);

      for (String name : Arrays.asList("beforeContent.html")) {
        final File file = new File(myConfig.getIncludeFilesBase(), name);
        if (!file.isFile()) {
          FileUtil.copyResource(getClass(), "/" + name, file);
        }
      }
    }
  }

  private void updateConfig(File config, File distConfig) {
    try {
      FileUtil.copy(distConfig, config);
    } catch (IOException e) {
      LOG.warn("Failed to copy ");
    }
  }

  private void updateDistConfig(File distConfig) {
    try {
      FileUtil.copyResource(getClass(), "/static-ui-extensions.xml", distConfig);
      String configText = new String(FileUtil.loadFileText(distConfig, "utf-8"));
      configText = configText.replaceAll("@@@PAGE_PLACES_LIST@@@", generatePagePlaces());
      FileUtil.writeFile(distConfig, configText);
    } catch (IOException e) {
      LOG.warn("Failed to update " + distConfig + ". " + e.getMessage(), e);
    }
  }

  private boolean usesDefaultConfig(File config) {
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
