// code by jph
package ch.ethz.idsc.sophus.app.filter;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class Regularization2StepDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Regularization2StepDemo());
  }
}
