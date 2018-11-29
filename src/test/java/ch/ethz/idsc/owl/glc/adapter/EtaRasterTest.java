// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EtaRasterTest extends TestCase {
  public void testState() {
    StateTimeRaster stateTimeRaster = EtaRaster.state(Tensors.vector(2., 3.));
    Tensor tensor = stateTimeRaster.convertToKey(new StateTime(Tensors.vector(100, 100), RealScalar.of(978667)));
    assertEquals(tensor, Tensors.vector(200, 300));
    assertTrue(ExactScalarQ.all(tensor));
  }

  public void testJoined() {
    StateTimeRaster stateTimeRaster = EtaRaster.joined(Tensors.vector(2., 3., 4.));
    Tensor tensor = stateTimeRaster.convertToKey(new StateTime(Tensors.vector(100, 100), RealScalar.of(97)));
    assertEquals(tensor, Tensors.vector(200, 300, 97 * 4));
    assertTrue(ExactScalarQ.all(tensor));
    EtaRaster etaRaster = (EtaRaster) stateTimeRaster;
    assertEquals(etaRaster.eta(), Tensors.vector(2, 3, 4));
  }

  public void testExtract() {
    StateTimeRaster stateTimeRaster = new EtaRaster(Tensors.vector(1.5), st -> st.state().extract(1, 2));
    Tensor tensor = stateTimeRaster.convertToKey(new StateTime(Tensors.vector(100, 100), RealScalar.of(97)));
    assertEquals(tensor, Tensors.vector(150));
    assertTrue(ExactScalarQ.all(tensor));
  }

  public void testFail() {
    EtaRaster.timeDependent(Tensors.vector(1, 2), RealScalar.of(1), StateTime::joined);
    try {
      EtaRaster.timeDependent(Tensors.vector(1, 2), RealScalar.of(1.), StateTime::joined);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
