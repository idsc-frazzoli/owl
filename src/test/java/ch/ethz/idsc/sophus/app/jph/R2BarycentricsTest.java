// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class R2BarycentricsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (R2Barycentrics r2Barycentrics : R2Barycentrics.values())
      Serialization.copy(r2Barycentrics.span(HilbertMatrix.of(10, 2)));
  }
}
