// code by jl
package ch.ethz.idsc.owl.math.region;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RegionUnionTest extends TestCase {
  public void testSimple() {
    List<StateTime> stateList = new ArrayList<>();
    List<Region<Tensor>> regionList = new ArrayList<>();
    Tensor radius = Tensors.vector(0.1, 0.1);
    // Goalstates: {0, 0}, {1, 1},{2, 2},{3, 3},{4, 4},{5, 5},{6, 6},{7, 7}
    for (int i = 0; i < 8; ++i) {
      Tensor goal = Tensors.of(RealScalar.of(1 * i), RealScalar.of(1 * i));
      stateList.add(new StateTime(goal, RealScalar.ZERO));
      regionList.add(new EllipsoidRegion(goal, radius));
    }
    Region<Tensor> region = RegionUnion.wrap(regionList);
    for (int i = 0; i < 8; ++i) {
      Tensor testState = Tensors.of(RealScalar.of(1 * i), RealScalar.of(1 * i));
      assertTrue(region.isMember(testState));
      assertTrue(region.isMember(testState.add(Tensors.vector(0.05, 0.05))));
      assertTrue(region.isMember(testState.add(Tensors.vector(-0.05, -0.05))));
      assertFalse(region.isMember(testState.add(Tensors.vector(0.15, 0.05))));
      assertFalse(region.isMember(testState.add(Tensors.vector(0.05, 0.15))));
      assertFalse(region.isMember(testState.add(Tensors.vector(-0.15, -0.15))));
    }
    assertFalse(region.isMember(Tensors.vector(4, 7)));
    assertFalse(region.isMember(Tensors.vector(-9, 1)));
    assertFalse(region.isMember(Tensors.vector(3, 0)));
  }

  public void testSimple2() {
    List<Region<Tensor>> regionList = new ArrayList<>();
    final Region<Tensor> region1 = new HyperplaneRegion(Tensors.vector(-1, 0), RealScalar.ZERO); // right halfplane going through {0,0}: x>0
    {
      assertTrue(region1.isMember(Tensors.vector(1, 1)));
      assertFalse(region1.isMember(Tensors.vector(-1, 1)));
      assertFalse(region1.isMember(Tensors.vector(-1, -1)));
      assertTrue(region1.isMember(Tensors.vector(1, -1)));
      regionList.add(region1);
    }
    final Region<Tensor> region2 = new HyperplaneRegion(Tensors.vector(0, -1), RealScalar.ZERO); // upper halfplane going through {0,0} y>0
    {
      assertTrue(region2.isMember(Tensors.vector(1, 1)));
      assertTrue(region2.isMember(Tensors.vector(-1, 1)));
      assertFalse(region2.isMember(Tensors.vector(-1, -1)));
      assertFalse(region2.isMember(Tensors.vector(1, -1)));
      regionList.add(region2);
    }
    {
      Region<Tensor> regionUnion = RegionUnion.wrap(regionList);
      assertTrue(regionUnion.isMember(Tensors.vector(1, 1)));
      assertTrue(regionUnion.isMember(Tensors.vector(-1, 1)));
      assertFalse(regionUnion.isMember(Tensors.vector(-1, -1)));
      assertTrue(regionUnion.isMember(Tensors.vector(1, -1)));
      regionList.remove(region1);
      assertTrue(regionUnion.isMember(Tensors.vector(1, 1)));
      assertTrue(regionUnion.isMember(Tensors.vector(-1, 1)));
      assertFalse(regionUnion.isMember(Tensors.vector(-1, -1)));
      assertFalse(regionUnion.isMember(Tensors.vector(1, -1)));
    }
  }
}
