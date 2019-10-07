// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.MatrixPower;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * References:
 * "de Rham Transform of a Hermite Subdivision Scheme", 2007
 * by Dubuc, Merrien, p.9, with lambda == 1/8, mu == 3/2
 * 
 * "Hermite subdivision on manifolds via parallel transport", 2017
 * by Moosmueller
 * 
 * @see BSpline2CurveSubdivision */
/* package */ class DubucHermiteSubdivision {
  private static final Tensor DIAG = DiagonalMatrix.of(RealScalar.ONE, RationalScalar.HALF);
  private static final Tensor ALP = //
      Tensors.fromString("{{152/25, +31/25}, {-29/50, 277/100}}").divide(RealScalar.of(8));
  private static final Tensor ALQ = //
      Tensors.fromString("{{48/25, -29/25}, {+29/50, 13/20}}").divide(RealScalar.of(8));
  // ---
  private static final Tensor AHP = //
      Tensors.fromString("{{152/25, -31/25}, {+29/50, 277/100}}").divide(RealScalar.of(8));
  private static final Tensor AHQ = //
      Tensors.fromString("{{48/25, +29/25}, {-29/50, 13/20}}").divide(RealScalar.of(8));

  public static HermiteSubdivision string(Tensor control) {
    return new DubucHermiteSubdivision(control).new StringIteration();
  }

  public static HermiteSubdivision string(Tensor control, Tensor diag) {
    return new DubucHermiteSubdivision(control).new StringIteration();
  }

  public static HermiteSubdivision cyclic(Tensor control) {
    return new DubucHermiteSubdivision(control).new CyclicIteration();
  }

  // ---
  private Tensor control;
  private int k = 0;

  private DubucHermiteSubdivision(Tensor control) {
    this.control = control;
  }

  private class StringIteration implements HermiteSubdivision {
    @Override // from HermiteSubdivision
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length - 1);
      Tensor Dk = MatrixPower.of(DIAG, k);
      Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
      Tensor amp = Dot.of(Dnk1, ALP, Dk);
      Tensor amq = Dot.of(Dnk1, ALQ, Dk);
      for (int index = 0; index < length; ++index) {
        Tensor p = control.get(index);
        string.append(p);
        if (index < length - 1) {
          Tensor q = control.get(index + 1);
          string.append(amp.dot(p).add(amq.dot(q)));
        }
      }
      ++k;
      return control = string;
    }
  }

  private class CyclicIteration implements HermiteSubdivision {
    @Override // from HermiteSubdivision
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length);
      Tensor Dk = MatrixPower.of(DIAG, k);
      Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
      Tensor amp = Dot.of(Dnk1, ALP, Dk);
      Tensor amq = Dot.of(Dnk1, ALQ, Dk);
      for (int index = 0; index < length; ++index) {
        Tensor p = control.get(index);
        string.append(p);
        Tensor q = control.get((index + 1) % length);
        string.append(amp.dot(p).add(amq.dot(q)));
      }
      ++k;
      return control = string;
    }
  }
}
