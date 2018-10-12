// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.Optional;

import ch.ethz.idsc.owl.math.group.Se2CoveringIntegrator;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CarLieIntegratorTest extends TestCase {
  public void testCombine() {
    for (int index = 0; index < 20; ++index) {
      Tensor g = RandomVariate.of(NormalDistribution.standard(), 3);
      Tensor x = RandomVariate.of(NormalDistribution.standard(), 3);
      x.set(RealScalar.ZERO, 1);
      assertTrue(Chop._10.close( //
          Se2CoveringIntegrator.INSTANCE.spin(g, x), //
          Se2CarLieIntegrator.INSTANCE.spin(g, x)));
    }
  }

  public void testRatioPositiveX() {
    for (Tensor lookAhead : Tensors.of(Tensors.vector(3, 1), Tensors.vector(3, -1))) {
      Optional<Scalar> optional = PurePursuit.ratioPositiveX(lookAhead);
      Scalar ratio = optional.get();
      Scalar speed = RealScalar.of(3.217506); // through experimentation
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor tensor = Se2CarLieIntegrator.INSTANCE.spin(Array.zeros(3), u);
      assertTrue(Chop._06.close(tensor.extract(0, 2), lookAhead));
    }
  }

  public void testCreateLookAhead() {
    Distribution distribution = UniformDistribution.of(-0.3, +0.3);
    Distribution speeds = UniformDistribution.of(0, 3);
    for (Tensor _ratio : RandomVariate.of(distribution, 100)) {
      Scalar ratio = _ratio.Get();
      Scalar speed = RandomVariate.of(speeds);
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor lookAhead = Se2CarLieIntegrator.INSTANCE.spin(Array.zeros(3), u);
      Optional<Scalar> optional = PurePursuit.ratioPositiveX(lookAhead);
      Scalar scalar = optional.get();
      assertTrue(Chop._07.close(ratio, scalar));
    }
  }

  public void testRatioNegativeX() {
    for (Tensor lookAhead : Tensors.of(Tensors.vector(-3, 1), Tensors.vector(-3, -1))) {
      Optional<Scalar> optional = PurePursuit.ratioNegativeX(lookAhead);
      Scalar ratio = optional.get();
      Scalar speed = RealScalar.of(-3.217506); // through experimentation
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor tensor = Se2CarLieIntegrator.INSTANCE.spin(Array.zeros(3), u);
      assertTrue(Chop._06.close(tensor.extract(0, 2), lookAhead));
    }
  }
}
