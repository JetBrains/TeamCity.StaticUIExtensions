

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