// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class BSpline5CurveSubdivisionTest extends TestCase {
  public void testCyclicMask() {
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.cyclic(Tensors.vector(1, 0, 0, 0, 0, 0, 0));
    assertEquals(tensor, Tensors.fromString( //
        "{5/8, 15/32, 3/16, 1/32, 0, 0, 0, 0, 0, 0, 0, 1/32, 3/16, 15/32}"));
  }

  public void testString() {
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    for (int length = 3; length < 7; ++length) {
      Tensor tensor = curveSubdivision.cyclic(UnitVector.of(length, 2));
      assertTrue(ExactScalarQ.all(tensor));
      assertEquals(Total.of(tensor), RealScalar.of(2));
    }
  }

  public void testTerminal() {
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    Clip clip = Clip.function(1, 2);
    for (int length = 2; length < 7; ++length) {
      Tensor tensor = curveSubdivision.string(UnitVector.of(length, 0));
      assertTrue(ExactScalarQ.all(tensor));
      clip.requireInside(Total.of(tensor).Get());
    }
  }
}
