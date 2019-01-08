// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Color;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CurveRenderTest extends TestCase {
  public void testFail() {
    CurveRender curveRender = new CurveRender(Tensors.fromString("{{1,2,3},{4,5,6},{7,8,9}}"), false, Color.BLACK);
    try {
      curveRender.render(null, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
