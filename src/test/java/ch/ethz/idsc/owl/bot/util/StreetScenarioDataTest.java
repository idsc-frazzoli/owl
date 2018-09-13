// code by jph
package ch.ethz.idsc.owl.bot.util;

import ch.ethz.idsc.tensor.alg.MatrixQ;
import junit.framework.TestCase;

public class StreetScenarioDataTest extends TestCase {
  public void testLoad() {
    for (String id : new String[] { "s1", "s3", "s4" }) {
      StreetScenarioData streetScenarioData = StreetScenarioData.load(id);
      assertTrue(0 < streetScenarioData.render.getWidth());
      MatrixQ.require(streetScenarioData.imagePedLegal);
      MatrixQ.require(streetScenarioData.imagePedIllegal);
      MatrixQ.require(streetScenarioData.imageCar_extrude(1));
      MatrixQ.require(streetScenarioData.imageLid);
      // MatrixQ.require(streetScenario.imageLanes);
    }
  }
}
