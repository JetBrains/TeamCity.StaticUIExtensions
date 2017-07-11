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

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.configuration.ChangeListener;
import jetbrains.buildServer.configuration.FilesWatcher;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 12:29
 */
public class StaticContentCache {
  private static final Logger LOG = Logger.getInstance(StaticContentCache.class.getName());

  private final FilesWatcher myWatcher;
  private final Map<File, Entry> myCache = new ConcurrentHashMap<File, Entry>();

  public StaticContentCache() {
    myWatcher = new FilesWatcher(new FilesWatcher.WatchedFilesProvider() {
      public File[] getWatchedFiles() {
        final Collection<File> values = new ArrayList<File>();
        values.addAll(myCache.keySet());
        return values.toArray(new File[values.size()]);
      }
    });

    myWatcher.registerListener(new ChangeListener() {
      public void changeOccured(String requestor) {
        for (File file : CollectionsUtil.join(myWatcher.getModifiedFiles(), myWatcher.getRemovedFiles())) {
          LOG.info("Detected change in " + file + ".");
          removeEntry(file);
        }
      }
    });

    myWatcher.setSleepingPeriod(5000);
  }

  public char[] getContent(@NotNull final File file) throws IOException {
    Entry entry = myCache.get(file);
    if (entry != null) return entry.getChars();

    try {
      char[] chars = FileUtil.loadFileText(file, "utf-8");
      myCache.put(file, new DataEntry(chars));
      return chars;
    } catch (IOException e) {
      myCache.put(file, new ErrorEntry(e));
      throw e;
    }
  }

  private void removeEntry(@NotNull final File file) {
    myCache.remove(file);
  }

  public void startWatching() {
    myWatcher.start();
  }

  public void stopWatching() {
    myWatcher.stop();
  }

  private static interface Entry {
    @NotNull
    char[] getChars() throws IOException;
  }

  private static class DataEntry implements Entry {
    private final char[] myData;

    private DataEntry(@NotNull final char[] data) {
      myData = data;
    }

    @NotNull
    public char[] getChars() throws IOException {
      return myData;
    }
  }

  private static class ErrorEntry implements Entry {
    private final IOException myException;

    private ErrorEntry(@NotNull final IOException exception) {
      myException = exception;
    }

    @NotNull
    public char[] getChars() throws IOException {
      throw new IOException(myException.getMessage()) {{initCause(myException);}};
    }
  }
}
