// code by jph
package ch.ethz.idsc.sophus.app;

import java.awt.Color;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PathRenderTest extends TestCase {
  public void testFail() {
    PathRender pathRender = new PathRender(Color.BLACK);
    pathRender.setCurve(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}"), false);
    try {
      pathRender.render(null, null);
    } catch (Exception exception) {
      // ---
    }
  }
}
