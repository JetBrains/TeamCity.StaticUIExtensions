

package jetbrains.buildServer.staticUIExtensions.config;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:17
 */
public class ConfigurationWatcherRegistrar {
  public ConfigurationWatcherRegistrar(@NotNull EventDispatcher<BuildServerListener> event,
                                       @NotNull final ConfigurationWatcher watcher) {
    event.addListener(new BuildServerAdapter(){
      @Override
      public void serverStartup() {
        watcher.startWatching();
      }

      @Override
      public void serverShutdown() {
        watcher.stopWatching();
      }
    });
  }
}