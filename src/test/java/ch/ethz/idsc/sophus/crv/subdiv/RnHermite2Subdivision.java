// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.MatrixPower;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * References:
 * "de Rham Transform of a Hermite Subdivision Scheme"
 * by Dubuc, Merrien, 2007, p.9, with lambda == 1/8, mu == 3/2
 * 
 * @see BSpline2CurveSubdivision */
/* package */ class RnHermite2Subdivision {
  private static final Tensor DIAG = DiagonalMatrix.of(RealScalar.ONE, RationalScalar.HALF);
  /** "Hermite subdivision on manifolds via parallel transport"
   * Example 1, p. 1063
   * by Moosmueller, 2017 */
  private static final Tensor ALP = //
      Tensors.fromString("{{152/25, +31/25}, {-29/50, 277/100}}").divide(RealScalar.of(8));
  private static final Tensor ALQ = //
      Tensors.fromString("{{48/25, -29/25}, {+29/50, 13/20}}").divide(RealScalar.of(8));
  // ---
  private static final Tensor AHP = //
      Tensors.fromString("{{48/25, +29/25}, {-29/50, 13/20}}").divide(RealScalar.of(8));
  private static final Tensor AHQ = //
      Tensors.fromString("{{152/25, -31/25}, {+29/50, 277/100}}").divide(RealScalar.of(8));

  public static TensorIteration string(Tensor control) {
    return new RnHermite2Subdivision(control).new StringIteration();
  }

  public static TensorIteration string(Tensor control, Tensor diag) {
    return new RnHermite2Subdivision(control).new StringIteration();
  }

  public static TensorIteration cyclic(Tensor control) {
    return new RnHermite2Subdivision(control).new CyclicIteration();
  }

  // ---
  private Tensor control;
  private int k = 0;

  private RnHermite2Subdivision(Tensor control) {
    this.control = control;
  }

  private class StringIteration implements TensorIteration {
    @Override // from TensorIteration
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length - 2);
      Tensor Dk = MatrixPower.of(DIAG, k);
      Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
      Tensor alp = Dot.of(Dnk1, ALP, Dk);
      Tensor alq = Dot.of(Dnk1, ALQ, Dk);
      Tensor ahp = Dot.of(Dnk1, AHP, Dk);
      Tensor ahq = Dot.of(Dnk1, AHQ, Dk);
      Tensor p = control.get(0);
      for (int index = 1; index < length; ++index) {
        Tensor q = control.get(index);
        string.append(alp.dot(p).add(alq.dot(q)));
        string.append(ahp.dot(p).add(ahq.dot(q)));
        p = q;
      }
      ++k;
      return control = string;
    }
  }

  private class CyclicIteration implements TensorIteration {
    @Override // from TensorIteration
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length - 2);
      Tensor Dk = MatrixPower.of(DIAG, k);
      Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
      Tensor alp = Dot.of(Dnk1, ALP, Dk);
      Tensor alq = Dot.of(Dnk1, ALQ, Dk);
      Tensor ahp = Dot.of(Dnk1, AHP, Dk);
      Tensor ahq = Dot.of(Dnk1, AHQ, Dk);
      Tensor p = control.get(0);
      for (int index = 1; index < length; ++index) {
        Tensor q = control.get(index);
        string.append(alp.dot(p).add(alq.dot(q)));
        string.append(ahp.dot(p).add(ahq.dot(q)));
        p = q;
      }
      {
        p = Last.of(control);
        Tensor q = control.get(0);
        string.append(alp.dot(p).add(alq.dot(q)));
        string.append(ahp.dot(p).add(ahq.dot(q)));
      }
      ++k;
      return control = string;
    }
  }
}
