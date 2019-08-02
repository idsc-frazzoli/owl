// code by jph
package ch.ethz.idsc.owl.bot.util;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import junit.framework.TestCase;

public class StreetScenarioDataTest extends TestCase {
  public void testLoad() {
    for (StreetScenario streetScenario : StreetScenario.values())
      try {
        StreetScenarioData streetScenarioData = streetScenario.load();
        assertTrue(0 < streetScenarioData.render.getWidth());
        MatrixQ.require(streetScenarioData.imagePedLegal);
        MatrixQ.require(streetScenarioData.imagePedIllegal);
        MatrixQ.require(streetScenarioData.imageCar_extrude(1));
        MatrixQ.require(streetScenarioData.imageLid);
        Tensor imageLanes = streetScenarioData.imageLanes();
        MatrixQ.require(imageLanes);
      } catch (Exception exception) {
        System.err.println(streetScenario.name());
        exception.printStackTrace();
        break;
      }
  }
}
