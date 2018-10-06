// code by jph
package ch.ethz.idsc.owl.bot.util;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import junit.framework.TestCase;

public class StreetScenarioDataTest extends TestCase {
  public void testLoad() {
    for (StreetScenario streetScenario : StreetScenario.values()) {
      System.out.println(streetScenario.name());
      StreetScenarioData streetScenarioData = streetScenario.load();
      assertTrue(0 < streetScenarioData.render.getWidth());
      // System.out.println("imagePedLegal");
      MatrixQ.require(streetScenarioData.imagePedLegal);
      // System.out.println("imagePedIllegal");
      MatrixQ.require(streetScenarioData.imagePedIllegal);
      // System.out.println("imageCar");
      MatrixQ.require(streetScenarioData.imageCar_extrude(1));
      // System.out.println("imageLid");
      MatrixQ.require(streetScenarioData.imageLid);
      Tensor imageLanes = streetScenarioData.imageLanes();
      MatrixQ.require(imageLanes);
    }
  }
}
