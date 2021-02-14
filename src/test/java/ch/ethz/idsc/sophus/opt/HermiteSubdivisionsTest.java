// code by jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.lie.LieTransport;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2.Se2Manifold;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class HermiteSubdivisionsTest extends TestCase {
  public void testStringReverseRn() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          RnManifold.INSTANCE, //
          LieTransport.INSTANCE, //
          RnBiinvariantMean.INSTANCE);
      TensorIteration ti1 = hermiteSubdivision.string(RealScalar.ONE, cp1);
      TensorIteration ti2 = hermiteSubdivision.string(RealScalar.ONE, Reverse.of(cp2));
      for (int count = 0; count < 3; ++count) {
        Tensor result1 = ti1.iterate();
        Tensor result2 = Reverse.of(ti2.iterate());
        result2.set(Tensor::negate, Tensor.ALL, 1);
        Chop._12.requireClose(result1, result2);
      }
    }
  }

  public void testStringReverseSe2() {
    Tensor cp1 = RandomVariate.of(UniformDistribution.unit(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          Se2Manifold.INSTANCE, //
          LieTransport.INSTANCE, //
          Se2BiinvariantMeans.LINEAR);
      TensorIteration ti1 = hermiteSubdivision.string(RealScalar.ONE, cp1);
      TensorIteration ti2 = hermiteSubdivision.string(RealScalar.ONE, Reverse.of(cp2));
      for (int count = 0; count < 3; ++count) {
        Tensor result1 = ti1.iterate();
        Tensor result2 = Reverse.of(ti2.iterate());
        result2.set(Tensor::negate, Tensor.ALL, 1);
        Chop._08.requireClose(result1, result2);
      }
    }
  }

  public void testSe2ConstantReproduction() {
    Tensor control = ConstantArray.of(Tensors.fromString("{{2, 3, 1}, {0, 0, 0}}"), 10);
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          Se2Manifold.INSTANCE, //
          LieTransport.INSTANCE, //
          Se2BiinvariantMeans.LINEAR);
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      Tensor iterate = Do.of(tensorIteration::iterate, 2);
      Chop._13.requireAllZero(iterate.get(Tensor.ALL, 1));
    }
  }

  public void testSe2LinearReproduction() {
    Tensor pg = Tensors.vector(1, 2, 3);
    Tensor pv = Tensors.vector(0.3, -0.2, -0.1);
    Tensor control = Tensors.empty();
    for (int count = 0; count < 10; ++count) {
      control.append(Tensors.of(pg, pv));
      pg = Se2Group.INSTANCE.element(pg).combine(Se2CoveringExponential.INSTANCE.exp(pv));
    }
    control = control.unmodifiable();
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      // System.out.println(hermiteSubdivisions);
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          Se2Manifold.INSTANCE, //
          LieTransport.INSTANCE, //
          Se2BiinvariantMeans.LINEAR);
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      Tensor iterate = Do.of(tensorIteration::iterate, 2);
      for (Tensor rv : iterate.get(Tensor.ALL, 1))
        Chop._13.requireClose(pv, rv);
    }
  }
}
