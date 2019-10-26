// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Nocopy;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class Hermite3Subdivision implements HermiteSubdivision, Serializable {
  /** midpoint group element contribution from group elements
   * factor in position (1, 1) of matrices A(-1) A(1)
   * with same sign and equal to 1/2 */
  private static final Tensor MGW = Tensors.of(RationalScalar.HALF, RationalScalar.HALF);
  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final BiinvariantMean biinvariantMean;
  private final Scalar mgv;
  private final Scalar mvv;
  private final Scalar mvg;
  /** for instance {1/128, 63/64, 1/128} */
  private final Tensor cgw;
  private final Scalar cgv;
  private final Scalar cvg;
  /** for instance {-1/16, 3/4, -1/16} */
  private final Tensor cvw;

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @param mgv
   * @param mvg
   * @param mvv
   * @param cgw vector of length 3
   * @param cgv
   * @param vpr
   * @param vpqr */
  public Hermite3Subdivision( //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
      Scalar mgv, Scalar mvg, Scalar mvv, //
      Tensor cgw, Scalar cgv, Scalar vpr, Tensor vpqr) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.lieExponential = Objects.requireNonNull(lieExponential);
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
    this.mgv = Objects.requireNonNull(mgv);
    this.mvg = mvg.add(mvg);
    this.mvv = mvv.add(mvv);
    // ---
    this.cgw = VectorQ.requireLength(cgw, 3);
    this.cgv = cgv;
    cvg = vpr.add(vpr);
    cvw = VectorQ.requireLength(vpqr.add(vpqr), 3);
  }

  @Override // from HermiteSubdivision
  public TensorIteration string(Scalar delta, Tensor control) {
    return new Control(delta, control).new StringIteration();
  }

  @Override // from HermiteSubdivision
  public TensorIteration cyclic(Scalar delta, Tensor control) {
    return new Control(delta, control).new CyclicIteration();
  }

  private class Control {
    private Tensor control;
    private Scalar rgk;
    private Scalar rvk;
    // ---
    private Scalar cgk;
    private Scalar cvk;

    private Control(Scalar delta, Tensor control) {
      this.control = control;
      rgk = delta.multiply(mgv);
      rvk = mvg.divide(delta);
      // ---
      cgk = delta.multiply(cgv);
      cvk = cvg.divide(delta);
    }

    private Tensor center(Tensor p, Tensor q, Tensor r) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      Tensor rg = r.get(0);
      Tensor rv = r.get(1);
      Tensor cg1 = biinvariantMean.mean(Unprotect.byRef(pg, qg, rg), cgw);
      Tensor cg2 = rv.subtract(pv).multiply(cgk);
      Tensor cg = lieGroup.element(cg1).combine(cg2);
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(rg)); // r - p
      Tensor cv1 = log.multiply(cvk);
      Tensor cv2 = cvw.dot(Unprotect.byRef(pv, qv, rv));
      Tensor cv = cv1.add(cv2);
      return Tensors.of(cg, cv);
    }

    /** @param p == {pg, pv}
     * @param q == {qg, qv}
     * @return r == {rg, rv} */
    private Tensor midpoint(Tensor p, Tensor q) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      Tensor rg1 = biinvariantMean.mean(Unprotect.byRef(pg, qg), MGW);
      Tensor rg2 = lieExponential.exp(qv.subtract(pv).multiply(rgk));
      Tensor rg = lieGroup.element(rg1).combine(rg2);
      // ---
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg)); // q - p
      Tensor rv1 = log.multiply(rvk);
      Tensor rv2 = qv.add(pv).multiply(mvv);
      Tensor rv = rv1.add(rv2);
      return Tensors.of(rg, rv);
    }

    private class StringIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Nocopy string = new Nocopy(2 * length - 1);
        Tensor p = control.get(0);
        string.append(p); // interpolation
        Tensor q = control.get(1);
        string.append(midpoint(p, q));
        for (int index = 2; index < length; ++index) {
          Tensor r = control.get(index);
          string.append(center(p, q, r));
          p = q;
          q = r;
          string.append(midpoint(p, q));
        }
        string.append(q);
        rgk = rgk.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        cgk = cgk.multiply(RationalScalar.HALF);
        cvk = cvk.add(cvk);
        return control = string.tensor();
      }
    }

    private class CyclicIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Nocopy string = new Nocopy(2 * length);
        Tensor p = Last.of(control);
        Tensor q = control.get(0);
        for (int index = 1; index <= length; ++index) {
          Tensor r = control.get(index % length);
          string.append(center(p, q, r));
          p = q;
          q = r;
          string.append(midpoint(p, q));
        }
        rgk = rgk.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        cgk = cgk.multiply(RationalScalar.HALF);
        cvk = cvk.add(cvk);
        return control = string.tensor();
      }
    }
  }
}
