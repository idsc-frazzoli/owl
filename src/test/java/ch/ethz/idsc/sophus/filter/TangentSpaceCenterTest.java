// code by jph
package ch.ethz.idsc.sophus.filter;

import java.io.IOException;

import ch.ethz.idsc.sophus.group.RnExponential;
import ch.ethz.idsc.sophus.group.RnGroup;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2CoveringGroup;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TangentSpaceCenterTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator tensorUnaryOperator = TangentSpaceCenter.of( //
        Se2CoveringGroup.INSTANCE, Se2CoveringExponential.INSTANCE, SmoothingKernel.GAUSSIAN);
    Tensor p0 = Tensors.vector(1, 2, 0);
    Tensor p1 = Se2CoveringIntegrator.INSTANCE.spin(p0, Tensors.vector(1, .2, .3));
    Tensor p2 = Se2CoveringIntegrator.INSTANCE.spin(p1, Tensors.vector(.9, .1, .2));
    Tensor seq = Tensors.of(p0, p1, p2);
    Tensor expect = Tensors.vector(1.9441411371777133, 2.3251725558812866, 0.28336214894300776);
    Chop._10.requireClose(expect, tensorUnaryOperator.apply(seq));
  }

  public void testRnMean() throws ClassNotFoundException, IOException {
    Tensor tensor = Range.of(4, 11).unmodifiable();
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          Serialization.copy(TangentSpaceCenter.of(RnGroup.INSTANCE, RnExponential.INSTANCE, smoothingKernel));
      Chop._12.requireClose(tensorUnaryOperator.apply(tensor), Mean.of(tensor));
    }
  }
}
