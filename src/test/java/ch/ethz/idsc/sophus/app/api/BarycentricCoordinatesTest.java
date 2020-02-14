// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class BarycentricCoordinatesTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (BarycentricCoordinates barycentricCoordinates : BarycentricCoordinates.values())
      Serialization.copy(barycentricCoordinates.barycentricCoordinate());
  }
}
