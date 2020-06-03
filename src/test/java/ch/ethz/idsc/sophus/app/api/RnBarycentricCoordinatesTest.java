// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.IOException;

import ch.ethz.idsc.sophus.krg.InversePowerVariogram;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class RnBarycentricCoordinatesTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (RnBarycentricCoordinates barycentricCoordinates : RnBarycentricCoordinates.values())
      Serialization.copy(barycentricCoordinates.from(RnManifold.INSTANCE, InversePowerVariogram.of(2), null));
  }
}
