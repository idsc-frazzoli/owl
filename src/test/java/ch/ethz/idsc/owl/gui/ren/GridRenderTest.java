// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class GridRenderTest extends TestCase {
  public void testSimple() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    GridRender.INSTANCE.render( //
        new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3)), bi.createGraphics());
  }
}
