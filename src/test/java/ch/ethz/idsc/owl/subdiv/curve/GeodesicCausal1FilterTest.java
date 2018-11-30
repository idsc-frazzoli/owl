// code by ob and jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.math.group.LieGroupGeodesic;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicCausal1FilterTest extends TestCase {
  public void testSimple() {
    GeodesicInterface geodesicInterface = //
        new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
    Scalar alpha = RationalScalar.HALF;
    GeodesicCausal1Filter geodesicCausal1Filter = new GeodesicCausal1Filter(geodesicInterface, alpha, Tensors.vector(1, 2, 0.25), Tensors.vector(4, 5, 0.5));
    Tensor extrapolate = geodesicCausal1Filter.extrapolate();
    Tensor expected = Tensors.vector(6.164525387368366, 8.648949142895502, 0.75);
    assertTrue(Chop._10.close(extrapolate, expected));
    Tensor filtered = geodesicCausal1Filter.apply(expected);
    assertTrue(Chop._10.close(filtered, expected));
  }
}
