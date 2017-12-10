// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class GoalRenderTest extends TestCase {
  public void testSimple() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    GoalRender goalRender = new GoalRender(Arrays.asList(new StateTime(Array.zeros(2), RealScalar.ONE)));
    goalRender.render(new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3)), bi.createGraphics());
  }

  public void testNull() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    GoalRender goalRender = new GoalRender(null);
    goalRender.render(new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3)), bi.createGraphics());
  }
}
