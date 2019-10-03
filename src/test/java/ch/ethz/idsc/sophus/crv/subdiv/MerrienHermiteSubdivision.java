// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

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
 * "A family of Hermite interpolants by bisection algorithms", 1992,
 * by Merrien
 * 
 * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
 * by Byeongseon Jeong, Jungho Yoon */
/* package */ class MerrienHermiteSubdivision {
  private static final Tensor DIAG = Tensors.of(RealScalar.ONE, RationalScalar.HALF);
  private static final Tensor AM0 = Tensors.fromString("{{1/2, +1/8}, {-3/4, -1/8}}");
  private static final Tensor AM1 = Tensors.fromString("{{1/2, -1/8}, {+3/4, -1/8}}");
  private static final Tensor ARP = DiagonalMatrix.with(DIAG).unmodifiable();

  public static HermiteSubdivision string(Tensor control) {
    return new MerrienHermiteSubdivision(control).new StringIteration();
  }

  public static HermiteSubdivision cyclic(Tensor control) {
    return new MerrienHermiteSubdivision(control).new CyclicIteration();
  }

  // ---
  private Tensor control;
  private int k = 0;

  private MerrienHermiteSubdivision(Tensor control) {
    this.control = control;
  }

  private class StringIteration implements HermiteSubdivision {
    @Override // from HermiteSubdivision
    public Tensor iterate() {
      Tensor string = Tensors.reserve(2 * control.length() - 1);
      Tensor Dk = MatrixPower.of(ARP, k);
      Tensor Dnk1 = MatrixPower.of(ARP, -(k + 1));
      Tensor arp = Dot.of(Dnk1, ARP, Dk);
      Tensor am0 = Dot.of(Dnk1, AM0, Dk);
      Tensor am1 = Dot.of(Dnk1, AM1, Dk);
      for (int index = 0; index < control.length(); ++index) {
        Tensor q = control.get(index);
        if (0 < index) {
          Tensor p = control.get(index - 1);
          string.append(am1.dot(q).add(am0.dot(p)));
        }
        string.append(arp.dot(q));
      }
      ++k;
      return control = string;
    }
  }

  private class CyclicIteration implements HermiteSubdivision {
    @Override // from HermiteSubdivision
    public Tensor iterate() {
      Tensor string = Tensors.reserve(2 * control.length());
      Tensor Dk = MatrixPower.of(ARP, k);
      Tensor Dnk1 = MatrixPower.of(ARP, -(k + 1));
      Tensor arp = Dot.of(Dnk1, ARP, Dk);
      Tensor am0 = Dot.of(Dnk1, AM0, Dk);
      Tensor am1 = Dot.of(Dnk1, AM1, Dk);
      for (int index = 0; index < control.length(); ++index) {
        Tensor q = control.get(index);
        if (0 < index) {
          Tensor p = control.get(index - 1);
          string.append(am1.dot(q).add(am0.dot(p)));
        }
        string.append(arp.dot(q));
      }
      Tensor q = control.get(0);
      Tensor p = Last.of(control);
      string.append(am1.dot(q).add(am0.dot(p)));
      ++k;
      return control = string;
    }
  }
}
