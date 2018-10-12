// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2IntegratorTest extends TestCase {
  private static Tensor exp_of(Scalar x, Scalar y, Scalar theta) {
    Tensor matrix = Array.zeros(3, 3);
    matrix.set(theta, 1, 0);
    matrix.set(theta.negate(), 0, 1);
    matrix.set(x, 0, 2);
    matrix.set(y, 1, 2);
    return MatrixExp.of(matrix);
  }

  private static Tensor exp_of(Number x, Number y, Number theta) {
    return exp_of(RealScalar.of(x), RealScalar.of(y), RealScalar.of(theta));
  }

  public void testExpSubstitute() {
    Tensor mat = exp_of(1, 2, .3);
    Tensor vec = Se2CoveringIntegrator.INSTANCE.spin(Array.zeros(3), Tensors.vector(1, 2, .3));
    Tensor v0 = Se2CoveringExponential.INSTANCE.exp(Tensors.vector(1, 2, .3));
    assertEquals(vec, v0);
    Tensor alt = Se2Utils.toSE2Matrix(vec);
    assertTrue(Chop._13.close(mat, alt));
  }

  public void testExpSubstitute2() {
    for (int index = 0; index < 20; ++index) {
      Tensor rnd = RandomVariate.of(NormalDistribution.standard(), 3);
      Tensor mat = exp_of(rnd.Get(0), rnd.Get(1), rnd.Get(2));
      Tensor vec = Se2CoveringIntegrator.INSTANCE.spin(Array.zeros(3), rnd);
      Tensor v0 = Se2CoveringExponential.INSTANCE.exp(rnd);
      assertEquals(vec, v0);
      Tensor alt = Se2Utils.toSE2Matrix(vec);
      boolean close = Chop._11.close(mat, alt);
      if (!close) {
        System.err.println(alt);
        System.err.println(mat);
      }
      assertTrue(close);
    }
  }

  public void testUnits() {
    Tensor spin = Se2CoveringIntegrator.INSTANCE.spin( //
        Tensors.fromString("{1[m],2[m],3}"), //
        Tensors.fromString("{.4[m],-.3[m],.7}"));
    // System.out.println(spin);
    Tensor expected = Tensors.fromString("{0.5557854299223493[m], 2.2064712267635618[m], 3.7}");
    assertTrue(Chop._13.close(spin, expected));
  }
}
