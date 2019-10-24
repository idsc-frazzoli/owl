// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.MatrixPower;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * reproduces polynomials up to degree 3
 * 
 * implementation for R^n
 * 
 * References:
 * "Dual Hermite subdivision schemes of de Rham-type", 2014
 * by Conti, Merrien, Romani
 * 
 * @see BSpline3CurveSubdivision */
/* package */ class RnHermite3Subdivision implements HermiteSubdivision {
  private static final Tensor DIAG = DiagonalMatrix.of(RealScalar.ONE, RationalScalar.HALF);
  // ---
  private static final Tensor AMP = Tensors.fromString("{{1/2, +1/8}, {-3/4, -1/8}}");
  private static final Tensor AMQ = Tensors.fromString("{{1/2, -1/8}, {+3/4, -1/8}}");

  /** default with theta == 1/128 and omega == -1/16 */
  public static HermiteSubdivision common() {
    return new RnHermite3Subdivision( //
        RationalScalar.of(1, 128), //
        RationalScalar.of(-1, 16));
  }

  // ---
  final Tensor ARP;
  final Tensor ARQ;
  final Tensor ARR;

  /** Example 3.7, eq. 28, p. 572
   * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
   * by Byeongseon Jeong, Jungho Yoon
   * 
   * @param theta
   * @param omega */
  public RnHermite3Subdivision(Scalar theta, Scalar omega) {
    ARP = Tensors.of( //
        Tensors.of(theta, theta.multiply(RationalScalar.HALF)), //
        Tensors.of(omega.multiply(RationalScalar.of(+3, 2)), omega.multiply(RationalScalar.HALF)));
    ARQ = DiagonalMatrix.of( //
        RealScalar.ONE.subtract(theta.add(theta)), //
        RationalScalar.HALF.add(omega.add(omega)));
    ARR = Tensors.of( //
        Tensors.of(theta, theta.multiply(RationalScalar.HALF).negate()), //
        Tensors.of(omega.multiply(RationalScalar.of(-3, 2)), omega.multiply(RationalScalar.HALF)));
  }

  @Override
  public TensorIteration string(Scalar delta, Tensor control) {
    return new Control(control).new StringIteration();
  }

  @Override
  public TensorIteration cyclic(Scalar delta, Tensor control) {
    return new Control(control).new CyclicIteration();
  }

  // ---
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
        Tensor arp = Dot.of(Dnk1, ARP, Dk);
        Tensor arq = Dot.of(Dnk1, ARQ, Dk);
        Tensor arr = Dot.of(Dnk1, ARR, Dk);
        Tensor p = control.get(0);
        Tensor q = control.get(1);
        string.append(p);
        string.append(amp.dot(p).add(amq.dot(q)));
        for (int index = 1; index < length - 1; ++index) {
          Tensor r = control.get(index + 1);
          string.append(arp.dot(p).add(arq.dot(q)).add(arr.dot(r)));
          p = q;
          q = r;
          string.append(amp.dot(p).add(amq.dot(q)));
        }
        string.append(Last.of(control));
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
        Tensor arp = Dot.of(Dnk1, ARP, Dk);
        Tensor arq = Dot.of(Dnk1, ARQ, Dk);
        Tensor arr = Dot.of(Dnk1, ARR, Dk);
        Tensor p = Last.of(control);
        Tensor q = control.get(0);
        for (int index = 0; index < length; ++index) {
          Tensor r = control.get((index + 1) % length);
          string.append(arp.dot(p).add(arq.dot(q)).add(arr.dot(r)));
          p = q;
          q = r;
          string.append(amp.dot(p).add(amq.dot(q)));
        }
        ++k;
        return control = string;
      }
    }
  }
}
