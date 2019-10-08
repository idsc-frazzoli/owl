// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;

/**  */
public class Hermite3Subdivision {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final BiinvariantMean biinvariantMean;

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @throws Exception if either parameters is null */
  public Hermite3Subdivision(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.lieExponential = Objects.requireNonNull(lieExponential);
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
  }

  /** @param control
   * @return */
  public HermiteSubdivision string(Tensor control) {
    return new Control(RealScalar.ONE, control).new StringIteration();
  }

  /** @param delta between two samples in control
   * @param control
   * @return */
  public HermiteSubdivision string(Scalar delta, Tensor control) {
    return new Control(delta, control).new StringIteration();
  }

  /** @param control
   * @return */
  public HermiteSubdivision cyclic(Tensor control) {
    return new Control(RealScalar.ONE, control).new CyclicIteration();
  }

  /** @param delta between two samples in control
   * @param control
   * @return */
  public HermiteSubdivision cyclic(Scalar delta, Tensor control) {
    return new Control(delta, control).new CyclicIteration();
  }

  private static final Tensor MGW = Tensors.of(RationalScalar.HALF, RationalScalar.HALF);
  private static final Tensor CGW = Tensors.fromString("{1/128, 63/64, 1/128}");
  private static final Tensor CVW = Tensors.fromString("{-1/16, 3/4, -1/16}");

  private class Control {
    private Tensor control;
    private Scalar rgk;
    private Scalar rvk;
    // ---
    private Scalar cgk;
    private Scalar cvk;

    private Control(Scalar delta, Tensor control) {
      this.control = control;
      rgk = RealScalar.of(8).divide(delta);
      rvk = RationalScalar.of(3, 2).divide(delta);
      // ---
      cgk = RealScalar.of(256).divide(delta);
      cvk = RationalScalar.of(3, 16).divide(delta);
    }

    private Tensor center(Tensor p, Tensor q, Tensor r) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      Tensor rg = r.get(0);
      Tensor rv = r.get(1);
      Tensor cg1 = biinvariantMean.mean(Unprotect.byRef(pg, qg, rg), CGW);
      Tensor cg2 = pv.subtract(rv).divide(cgk);
      Tensor cg = lieGroup.element(cg1).combine(cg2);
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(rg)); // r - p
      Tensor cv1 = log.multiply(cvk);
      Tensor cv2 = CVW.dot(Unprotect.byRef(pv, qv, rv));
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
      // TODO interpret tangent vectors at element in group -> use adjoint map
      Tensor rg1 = biinvariantMean.mean(Unprotect.byRef(pg, qg), MGW);
      Tensor rg2 = lieExponential.exp(pv.subtract(qv).divide(rgk));
      Tensor rg = lieGroup.element(rg1).combine(rg2);
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg)); // q - p
      Tensor rv1 = log.multiply(rvk);
      Tensor rv2 = qv.add(pv).multiply(_1_4);
      Tensor rv = rv1.subtract(rv2);
      return Tensors.of(rg, rv);
    }

    private class StringIteration implements HermiteSubdivision {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Tensor string = Tensors.reserve(2 * length - 1);
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
        rgk = rgk.add(rgk);
        rvk = rvk.add(rvk);
        cgk = cgk.add(cgk);
        cvk = cvk.add(cvk);
        return control = string;
      }
    }

    private class CyclicIteration implements HermiteSubdivision {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Tensor string = Tensors.reserve(2 * length);
        Tensor p = Last.of(control);
        Tensor q = control.get(0);
        for (int index = 1; index <= length; ++index) {
          Tensor r = control.get(index % length);
          string.append(center(p, q, r));
          p = q;
          q = r;
          string.append(midpoint(p, q));
        }
        rgk = rgk.add(rgk);
        rvk = rvk.add(rvk);
        cgk = cgk.add(cgk);
        cvk = cvk.add(cvk);
        return control = string;
      }
    }
  }
}
