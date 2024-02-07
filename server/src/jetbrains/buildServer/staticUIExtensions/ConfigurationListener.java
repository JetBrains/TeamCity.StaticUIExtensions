

package jetbrains.buildServer.staticUIExtensions;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationReader;
import jetbrains.buildServer.staticUIExtensions.model.Rule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 20:19
 */
public class ConfigurationListener implements ConfigurationChangeListener {
  private static final Logger LOG = Logger.getInstance(ConfigurationListener.class.getName());

  private final ConfigurationReader myReader;
  private final PagePlacesInitializer myInitializer;
  private final Configuration myConfiguration;

  public ConfigurationListener(@NotNull final ConfigurationReader reader,
                               @NotNull final PagePlacesInitializer initializer,
                               @NotNull final Configuration configuration) {
    myReader = reader;
    myInitializer = initializer;
    myConfiguration = configuration;
  }

  public void configurationChanged() {
    final Collection<Rule> rules = parseConfigs();

    myInitializer.registerPagePlaces(rules);
  }

  @NotNull
  private Collection<Rule> parseConfigs() {
    final File xml = myConfiguration.getConfigurationXml();
    if (xml.isFile()) {
      try {
        return myReader.parseConfiguration(xml);
      } catch (ConfigurationException e) {
        LOG.warn("Failed to parse configuration file: " + e.getMessage(), e);
      }
    }
    return Collections.emptyList();
  }
}