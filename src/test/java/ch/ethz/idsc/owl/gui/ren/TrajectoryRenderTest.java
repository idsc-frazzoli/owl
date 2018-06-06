// code by jph
package ch.ethz.idsc.owl.gui.ren;

import junit.framework.TestCase;

public class TrajectoryRenderTest extends TestCase {
  public void testNull() {
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(null);
    trajectoryRender.render(null, null);
  }
}
