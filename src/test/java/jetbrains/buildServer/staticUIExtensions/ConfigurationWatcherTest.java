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

package jetbrains.buildServer.staticUIExtensions;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.staticUIExtensions.config.ConfigurationWatcher;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.WaitForAssert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 17.11.11 18:45
 */
public class ConfigurationWatcherTest extends BaseTestCase {
  private Mockery m;
  private Configuration myConfig;
  private ConfigurationChangeListener myListener;
  private ConfigurationWatcher myWatcher;
  private File myConfigXml;
  private AtomicInteger myChangeCount;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    m = new Mockery();
    myConfigXml = createTempFile();
    myConfig = m.mock(Configuration.class);
    myListener = m.mock(ConfigurationChangeListener.class);
    myChangeCount = new AtomicInteger();

    m.checking(new Expectations(){{
      allowing(myConfig).getConfigurationXml(); will(returnValue(myConfigXml));
      allowing(myListener).configurationChanged(); will(new CustomAction("update counter") {
        public Object invoke(Invocation invocation) throws Throwable {
          myChangeCount.incrementAndGet();
          return null;
        }
      });
    }});

    myWatcher = new ConfigurationWatcher(myConfig, Collections.singleton(myListener));
    myWatcher.setCheckInterval(10);
  }

  @AfterMethod
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    if (myWatcher != null) {
      myWatcher.stopWatching();
    }
  }

  private void waitForCounter(final int count) {
    new WaitForAssert(500) {
      @Override
      protected boolean condition() {
        return myChangeCount.get() >= count;
      }
    };
  }

  @Test
  public void testChangeCounterOnStart() {
    FileUtil.writeFile(myConfigXml, "this is config");
    startAndWaitForChange();
  }

  private int startAndWaitForChange() {
    final int count = myChangeCount.get();
    myWatcher.startWatching();

    waitForCounter(count + 1);
    return myChangeCount.get();
  }


  @Test
  public void testNotificationsOnFileDisappear() {
    FileUtil.writeFile(myConfigXml, "this is config");
    int count = startAndWaitForChange();
    FileUtil.delete(myConfigXml);

    waitForCounter(count  + 1);
  }

  @Test
  public void testNotificationsOnFileCreated() {
    FileUtil.delete(myConfigXml);
    startAndWaitForChange();

    int count = startAndWaitForChange();
    FileUtil.writeFile(myConfigXml, "this is config");
    waitForCounter(count  + 1);
  }
}
