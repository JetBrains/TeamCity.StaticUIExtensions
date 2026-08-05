

package jetbrains.buildServer.staticUIExtensions.web;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 12:29
 */
public class StaticContentCacheRegistrar {
  public StaticContentCacheRegistrar(@NotNull EventDispatcher<BuildServerListener> event,
                                       @NotNull final StaticContentCache watcher) {
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