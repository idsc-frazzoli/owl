// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SurvivorSetTest extends TestCase {
  public void testSimple() {
    Tensor x = Tensors.fromString("{1,2,2}");
    Tensor y = Tensors.fromString("{2,2,2}");
    Tensor z = Tensors.fromString("{3,2,2}");
    Tensor w = Tensors.fromString("{1,2,2}");
    Collection<Tensor> feasibleInputs = new LinkedList<>();
    feasibleInputs.add(x);
    feasibleInputs.add(y);
    feasibleInputs.add(z);
    feasibleInputs.add(w);
    Tensor slack = Tensors.fromString("{1,1,1}");
    Collection<UtilityFunction<Scalar, Scalar>> utFV = new LinkedList<>();
    utFV.add(IdentityUtilityFunction.identity());
    utFV.add(IdentityUtilityFunction.identity());
    utFV.add(IdentityUtilityFunction.identity());
    SurvivorSet survivorSet = new SurvivorSet(feasibleInputs, utFV, slack);
    survivorSet.getSurvivorSetStream(feasibleInputs, 0);
    assertTrue(survivorSet.getSurvivorSetStream(feasibleInputs, 0).contains(x));
  }

  public void testFailNull() {
    Tensor slack = Tensors.fromString("{1,1,1}");
    Collection<UtilityFunction<Scalar, Scalar>> utFV = new LinkedList<>();
    utFV.add(IdentityUtilityFunction.identity());
    utFV.add(IdentityUtilityFunction.identity());
    utFV.add(IdentityUtilityFunction.identity());
    try {
      new SurvivorSet(null, utFV, slack);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}