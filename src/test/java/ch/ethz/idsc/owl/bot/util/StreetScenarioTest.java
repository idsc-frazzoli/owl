// code by jph
package ch.ethz.idsc.owl.bot.util;

import ch.ethz.idsc.tensor.alg.MatrixQ;
import junit.framework.TestCase;

public class StreetScenarioTest extends TestCase {
  public void testLoad() {
    for (String id : new String[] { "s1", "s3", "s4" }) {
      StreetScenarioData streetScenario = StreetScenarioData.load(id);
      MatrixQ.require(streetScenario.imagePedLegal);
      MatrixQ.require(streetScenario.imagePedIllegal);
      MatrixQ.require(streetScenario.imageCar_extrude(1));
      MatrixQ.require(streetScenario.imageLid);
      // MatrixQ.require(streetScenario.imageLanes);
    }
  }
}
