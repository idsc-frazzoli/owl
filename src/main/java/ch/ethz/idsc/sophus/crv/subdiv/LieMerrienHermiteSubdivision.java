// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * References:
 * "A family of Hermite interpolants by bisection algorithms", 1992,
 * by Merrien
 * 
 * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
 * by Byeongseon Jeong, Jungho Yoon */
public class LieMerrienHermiteSubdivision {
  public static HermiteSubdivision string(LieGroup lieGroup, LieExponential lieExponential, Tensor control) {
    return new LieMerrienHermiteSubdivision(lieGroup, lieExponential, control).new StringIteration();
  }

  public static HermiteSubdivision cyclic(LieGroup lieGroup, LieExponential lieExponential, Tensor control) {
    return new LieMerrienHermiteSubdivision(lieGroup, lieExponential, control).new CyclicIteration();
  }

  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final LieGroupGeodesic lieGroupGeodesic;
  private Tensor control;

  private LieMerrienHermiteSubdivision(LieGroup lieGroup, LieExponential lieExponential, Tensor control) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
    lieGroupGeodesic = new LieGroupGeodesic(lieGroup, lieExponential);
    this.control = control;
  }

  private class StringIteration implements HermiteSubdivision {
    private Scalar rgk = RealScalar.of(8);
    private Scalar rvk = RationalScalar.of(3, 2);

    @Override // from HermiteSubdivision
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length - 1);
      for (int index = 0; index < length; ++index) {
        Tensor p = control.get(index);
        string.append(p);
        if (index < length - 1) {
          Tensor pg = p.get(0);
          Tensor pv = p.get(1);
          Tensor q = control.get(index + 1);
          Tensor qg = q.get(0);
          Tensor qv = q.get(1);
          Tensor rg1 = lieGroupGeodesic.midpoint(pg, qg);
          Tensor rg2 = lieExponential.exp(pv.subtract(qv).divide(rgk));
          Tensor rg = lieGroup.element(rg1).combine(rg2);
          Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg));
          Tensor rv1 = log.multiply(rvk);
          Tensor rv2 = qv.add(pv).multiply(RationalScalar.of(1, 4));
          Tensor rv = rv1.subtract(rv2);
          string.append(Tensors.of(rg, rv));
        }
      }
      rgk = rgk.add(rgk);
      rvk = rvk.add(rvk);
      return control = string;
    }
  }

  private class CyclicIteration implements HermiteSubdivision {
    private Scalar rgk = RealScalar.of(8);
    private Scalar rvk = RationalScalar.of(3, 2);

    @Override // from HermiteSubdivision
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length);
      for (int index = 0; index < length; ++index) {
        Tensor p = control.get(index);
        string.append(p);
        {
          Tensor pg = p.get(0);
          Tensor pv = p.get(1);
          Tensor q = control.get((index + 1) % length);
          Tensor qg = q.get(0);
          Tensor qv = q.get(1);
          Tensor rg1 = lieGroupGeodesic.midpoint(pg, qg);
          Tensor rg2 = lieExponential.exp(pv.subtract(qv).divide(rgk));
          Tensor rg = lieGroup.element(rg1).combine(rg2);
          Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg));
          Tensor rv1 = log.multiply(rvk);
          Tensor rv2 = qv.add(pv).multiply(RationalScalar.of(1, 4));
          Tensor rv = rv1.subtract(rv2);
          string.append(Tensors.of(rg, rv));
        }
      }
      rgk = rgk.add(rgk);
      rvk = rvk.add(rvk);
      return control = string;
    }
  }
}
