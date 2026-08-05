

package jetbrains.buildServer.staticUIExtensions.web;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.configuration.ChangeListener;
import jetbrains.buildServer.configuration.FileWatcherListener;
import jetbrains.buildServer.configuration.FilesWatcher;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.serverSide.impl.FileWatcherFactory;
import jetbrains.buildServer.staticUIExtensions.ConfigurationChangeListener;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 12:29
 */
public class StaticContentCache implements ConfigurationChangeListener {
  private static final Logger LOG = Logger.getInstance(StaticContentCache.class.getName());

  private final FilesWatcher myWatcher;
  private final Map<File, Entry> myCache = new ConcurrentHashMap<>();

  public StaticContentCache(@NotNull FileWatcherFactory fileWatcherFactory) {
    myWatcher = fileWatcherFactory.createManyFilesWatcher(() -> {
      final List<File> values = new ArrayList<>();
      values.addAll(myCache.keySet());
      return values.toArray(new File[0]);
    }, TeamCityProperties.getInteger("teamcity.staticUIExtensions.filesCache.updatePeriod", 5000));

    myWatcher.registerListener((newFiles, modifiedFiles, removedFiles) -> {
      for (File file : CollectionsUtil.join(newFiles, modifiedFiles, removedFiles)) {
        LOG.info("Detected change in " + file + ".");
        removeEntry(file);
      }
    });

    myWatcher.start();
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

  @Override
  public void configurationChanged() {
    // clear cache to re-read files content once a change is done to the plugin configuration
    myCache.clear();
  }

  private interface Entry {
    @NotNull
    char[] getChars() throws IOException;
  }

  private static class DataEntry implements Entry {
    private final char[] myData;

    private DataEntry(@NotNull final char[] data) {
      myData = data;
    }

    @NotNull
    public char[] getChars() {
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