// code by jph
package ch.ethz.idsc.owl.bot.util;

import junit.framework.TestCase;

public class DemoLauncherTest extends TestCase {
  public void testSimple() {
    for (Class<?> cls : DemoLauncher.detect())
      try {
        DemoInterface demoInterface = (DemoInterface) cls.newInstance();
        demoInterface.start();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
