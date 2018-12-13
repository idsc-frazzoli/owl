// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BalloonStateSpaceModelTest extends TestCase {
  public void testValidity() {
    // TODO write new test since introduction of state x and usage of SimplexNoise changed the test outcome
    Tensor xWithUnits = Tensors.fromString("{2[m],2[m],2[m*s^-1],4[K]}");
    Tensor uWithUnits = Tensors.fromString("{3[K*s^-1]}");
    Tensor xWithoutUnits = Tensors.vector(1, 1, 2, 4);
    Tensor uWithoutUnits = Tensors.vector(2);
    StateSpaceModel stateSpaceModel = BalloonStateSpaceModels.defaultWithUnits();
    StateSpaceModel stateSpaceModelWithoutUnits = BalloonStateSpaceModels.defaultWithoutUnits();
    assertEquals(stateSpaceModel.f(xWithUnits, uWithUnits).length(), 4);
    assertEquals(stateSpaceModelWithoutUnits.f(xWithoutUnits, uWithoutUnits).length(), 4);
  }
}
