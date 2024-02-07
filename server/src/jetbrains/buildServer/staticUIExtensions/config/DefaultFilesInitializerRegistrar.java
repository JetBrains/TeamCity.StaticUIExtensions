

package jetbrains.buildServer.staticUIExtensions.config;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 14:49
 */
public class DefaultFilesInitializerRegistrar {
  public DefaultFilesInitializerRegistrar(@NotNull final EventDispatcher<BuildServerListener> events,
                                          @NotNull final DefaultFilesInitializer init) {
    events.addListener(new BuildServerAdapter(){
      @Override
      public void pluginsLoaded() {
        super.pluginsLoaded();
        init.writeInitialFiles();
      }
    });
  }
}