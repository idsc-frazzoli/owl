// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class BalloonStateSpaceModelTest extends TestCase {
  @SuppressWarnings("unused")
  public void testValidity() {
    Tensor xWithUnits = Tensors.fromString("{2[m], 2[m], 2[m*s^-1], 4[K]}");
    Tensor uWithUnits = Tensors.fromString("{3[K*s^-1]}");
    Tensor xWithoutUnits = Tensors.vector(1, 1, 2, 4);
    Tensor uWithoutUnits = Tensors.vector(2);
    StateSpaceModel stateSpaceModel = BalloonStateSpaceModels.defaultWithUnits();
    StateSpaceModel stateSpaceModelWithoutUnits = BalloonStateSpaceModels.defaultWithoutUnits();
    assertEquals(stateSpaceModelWithoutUnits.f(xWithoutUnits, uWithoutUnits).length(), 4);
    Tensor fWithUnits = stateSpaceModel.f(xWithUnits, uWithUnits);
    assertEquals(fWithUnits.length(), 4);
    System.out.println(((Quantity) fWithUnits.Get(0)).unit());
    assertEquals(((Quantity) fWithUnits.Get(0)).unit(), Unit.of("m*s^-1"));
    assertEquals(((Quantity) fWithUnits.Get(1)).unit(), Unit.of("m*s^-1"));
    assertEquals(((Quantity) fWithUnits.Get(2)).unit(), Unit.of("m*s^-2"));
    assertEquals(((Quantity) fWithUnits.Get(3)).unit(), Unit.of("K*s^-1"));
  }
}
