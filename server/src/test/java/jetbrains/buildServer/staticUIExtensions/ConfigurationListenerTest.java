

package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationReader;
import jetbrains.buildServer.staticUIExtensions.model.Rule;
import jetbrains.buildServer.util.FileUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 18:56
 */
public class ConfigurationListenerTest extends BaseTestCase {
  private Mockery m;
  private PagePlacesInitializer myInitializer;
  private ConfigurationReader myReader;
  private ConfigurationListener myListener;
  private Configuration myConfig;
  private File myConfigXml;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m = new Mockery();
    myConfigXml = createTempFile();
    myInitializer = m.mock(PagePlacesInitializer.class);
    myReader = m.mock(ConfigurationReader.class);
    myConfig = m.mock(Configuration.class);

    m.checking(new Expectations(){{
      allowing(myConfig).getConfigurationXml(); will(returnValue(myConfigXml));
    }});

    myListener = new ConfigurationListener(
            myReader,
            myInitializer,
            myConfig
            );
  }


  @Test
  public void testLoadIfNoConfig() {
    FileUtil.delete(myConfigXml);
    m.checking(new Expectations(){{
      oneOf(myInitializer).registerPagePlaces(with(equal(Collections.<Rule>emptyList())));
    }});

    myListener.configurationChanged();
    m.assertIsSatisfied();
  }

  @Test
  public void testLoadIfConfig() throws ConfigurationException {
    FileUtil.writeFile(myConfigXml, "complicated config");
    final Collection<Rule> rulez = new ArrayList<Rule>();
    m.checking(new Expectations(){{
      oneOf(myReader).parseConfiguration(myConfigXml); will(returnValue(rulez));
      oneOf(myInitializer).registerPagePlaces(with(equal(rulez)));
    }});

    myListener.configurationChanged();
    m.assertIsSatisfied();
  }

  @Test
  public void testLoadIfConfigError() throws ConfigurationException {
    FileUtil.writeFile(myConfigXml, "complicated config");
    m.checking(new Expectations(){{
      oneOf(myReader).parseConfiguration(myConfigXml); will(throwException(new ConfigurationException("oopsy")));
      oneOf(myInitializer).registerPagePlaces(with(equal(Collections.<Rule>emptyList())));
    }});

    myListener.configurationChanged();
    m.assertIsSatisfied();
  }
}