// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.MatrixPower;
import ch.ethz.idsc.tensor.sca.Chop;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * reproduces polynomials of up to degree 3
 * 
 * implementation for R^n
 * 
 * References:
 * "A family of Hermite interpolants by bisection algorithms", 1992,
 * by Merrien
 * 
 * "de Rham Transform of a Hermite Subdivision Scheme", 2007
 * by Dubuc, Merrien, p.9, with lambda == 1/8, mu == 3/2
 * [in the paper the signs of the matrix entries seem to be incorrect]
 * 
 * @see BSpline1CurveSubdivision */
/* package */ class RnHermite1Subdivision implements HermiteSubdivision {
  private static final Tensor DIAG = DiagonalMatrix.of(RealScalar.ONE, RationalScalar.HALF);
  // ---
  final Tensor AMP;
  final Tensor AMQ;

  public RnHermite1Subdivision(Tensor AMP, Tensor AMQ) {
    this.AMP = AMP;
    this.AMQ = AMQ;
  }

  @Override // from HermiteSubdivision
  public TensorIteration string(Scalar delta, Tensor control) {
    Chop.NONE.requireClose(delta, RealScalar.ONE);
    return new Control(control).new StringIteration();
  }

  @Override // from HermiteSubdivision
  public TensorIteration cyclic(Scalar delta, Tensor control) {
    Chop.NONE.requireClose(delta, RealScalar.ONE);
    return new Control(control).new CyclicIteration();
  }

  private class Control {
    private Tensor control;
    private int k = 0;

    private Control(Tensor control) {
      this.control = control;
    }

    private class StringIteration implements TensorIteration {
      @Override // from TensorIteration
      public Tensor iterate() {
        int length = control.length();
        Tensor string = Tensors.reserve(2 * length - 1);
        Tensor Dk = MatrixPower.of(DIAG, k);
        Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
        Tensor amp = Dot.of(Dnk1, AMP, Dk);
        Tensor amq = Dot.of(Dnk1, AMQ, Dk);
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

    private class CyclicIteration implements TensorIteration {
      @Override // from TensorIteration
      public Tensor iterate() {
        int length = control.length();
        Tensor string = Tensors.reserve(2 * length);
        Tensor Dk = MatrixPower.of(DIAG, k);
        Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
        Tensor amp = Dot.of(Dnk1, AMP, Dk);
        Tensor amq = Dot.of(Dnk1, AMQ, Dk);
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
}
