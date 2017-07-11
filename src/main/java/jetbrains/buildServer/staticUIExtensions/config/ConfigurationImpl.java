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

import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.staticUIExtensions.Configuration;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:04
 */
public class ConfigurationImpl implements Configuration {
  private final String myToken;
  private final ServerPaths myPaths;

  public ConfigurationImpl(@NotNull final ServerPaths paths) {
    myToken = StringUtil.generateUniqueHash();
    myPaths = paths;
  }

  @NotNull
  public File getConfigurationXml() {
    return new File(getIncludeFilesBase(), "static-ui-extensions.xml");
  }

  public File mapIncludeFilePath(@NotNull String path) {
    if (path.contains("/") || path.contains("\\") || path.contains("..")) return null;

    return new File(getIncludeFilesBase(), path);
  }

  @NotNull
  public File getIncludeFilesBase() {
    return new File(myPaths.getConfigDir(), "_static_ui_extensions");
  }

  @NotNull
  public String getAccessToken() {
    return myToken;
  }
}
