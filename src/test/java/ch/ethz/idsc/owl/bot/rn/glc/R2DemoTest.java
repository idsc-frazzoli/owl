// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import junit.framework.TestCase;

public class R2DemoTest extends TestCase {
  public void testSimpleEmpty() {
    R2Demo.simpleEmpty().getBest().get();
  }

  public void testSimpleR2Bubbles() {
    R2Demo.simpleR2Bubbles().getBest().get();
  }
}
