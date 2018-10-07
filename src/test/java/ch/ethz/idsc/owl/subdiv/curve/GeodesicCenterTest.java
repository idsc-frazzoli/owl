// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class GeodesicCenterTest extends TestCase {
  public void testSimple() {
    // function generates window to compute mean: all points in window have same weight
    Function<Integer, Tensor> function = i -> Array.of(k -> RationalScalar.of(1, 2 * i + 1), 2 * i + 1);
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, function);
    for (int index = 0; index < 9; ++index) {
      Tensor apply = geodesicCenter.apply(UnitVector.of(9, index));
      assertEquals(apply, RationalScalar.of(1, 9));
    }
  }

  public void testSplitsMean() {
    {
      Tensor tensor = GeodesicCenter.splits(FilterMask.DIRICHLET.apply(1));
      assertEquals(tensor, Tensors.fromString("{1/3}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(FilterMask.DIRICHLET.apply(2));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/5}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(FilterMask.DIRICHLET.apply(3));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/3, 1/7}"));
    }
  }

  public void testSplitsBinomial() {
    {
      Tensor tensor = GeodesicCenter.splits(FilterMask.BINOMIAL.apply(1));
      assertEquals(tensor, Tensors.fromString("{1/2}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(FilterMask.BINOMIAL.apply(2));
      assertEquals(tensor, Tensors.fromString("{4/5, 3/8}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(FilterMask.BINOMIAL.apply(3));
      assertEquals(tensor, Tensors.fromString("{6/7, 15/22, 5/16}"));
    }
  }
}
