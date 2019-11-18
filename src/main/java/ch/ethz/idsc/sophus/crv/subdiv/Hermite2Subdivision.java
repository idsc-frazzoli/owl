// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupGeodesic;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n */
public class Hermite2Subdivision implements HermiteSubdivision, Serializable {
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final LieGroupGeodesic lieGroupGeodesic;
  private final Scalar lgg;
  private final Scalar lgv;
  private final Scalar hgv;
  private final Scalar hgg;
  private final Scalar hvg;
  private final Tensor vpq;

  /** @param lieGroup
   * @param lieExponential
   * @param lgg
   * @param lgv
   * @param hgv
   * @param hvg
   * @param vpq
   * @throws Exception if either parameters is null */
  public Hermite2Subdivision( //
      LieGroup lieGroup, LieExponential lieExponential, //
      Scalar lgg, Scalar lgv, Scalar hgv, Scalar hvg, Tensor vpq) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
    lieGroupGeodesic = new LieGroupGeodesic(lieGroup, lieExponential);
    this.lgg = lgg;
    hgg = RealScalar.ONE.subtract(this.lgg);
    this.lgv = Objects.requireNonNull(lgv);
    this.hgv = Objects.requireNonNull(hgv);
    this.hvg = hvg.add(hvg);
    this.vpq = VectorQ.requireLength(vpq.add(vpq), 2);
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
    private Scalar rgp;
    private Scalar rgq;
    private Scalar rvk;

    private Control(Scalar delta, Tensor control) {
      this.control = control;
      rgp = delta.multiply(lgv);
      rgq = delta.multiply(hgv).negate();
      rvk = hvg.divide(delta);
    }

    private void refine(Tensor curve, Tensor p, Tensor q) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      ScalarTensorFunction scalarTensorFunction = lieGroupGeodesic.curve(pg, qg);
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg)); // q - p
      Tensor rv1 = log.multiply(rvk);
      {
        Tensor rg1 = scalarTensorFunction.apply(lgg);
        Tensor rg2 = lieExponential.exp(pv.multiply(rgp).subtract(qv.multiply(rgq)));
        Tensor rg = lieGroup.element(rg1).combine(rg2);
        // ---
        Tensor rv2 = vpq.dot(Tensors.of(pv, qv));
        Tensor rv = rv1.add(rv2);
        curve.append(Tensors.of(rg, rv));
      }
      {
        Tensor rg1 = scalarTensorFunction.apply(hgg);
        Tensor rg2 = lieExponential.exp(pv.multiply(rgq).subtract(qv.multiply(rgp)));
        Tensor rg = lieGroup.element(rg1).combine(rg2);
        // ---
        Tensor rv2 = vpq.dot(Tensors.of(qv, pv));
        Tensor rv = rv1.add(rv2);
        curve.append(Tensors.of(rg, rv));
      }
    }

    private Tensor protected_string(Tensor tensor) {
      int length = tensor.length();
      Tensor curve = Tensors.reserve(2 * length); // allocation for cyclic case
      Iterator<Tensor> iterator = tensor.iterator();
      Tensor p = iterator.next();
      while (iterator.hasNext()) {
        Tensor q = iterator.next();
        refine(curve, p, q);
        p = q;
      }
      return curve;
    }

    private class StringIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        Tensor curve = protected_string(control);
        rgp = rgp.multiply(RationalScalar.HALF);
        rgq = rgq.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        return control = curve;
      }
    }

    private class CyclicIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        Tensor curve = protected_string(control);
        refine(curve, Last.of(control), control.get(0));
        rgp = rgp.multiply(RationalScalar.HALF);
        rgq = rgq.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        return control = curve;
      }
    }
  }
}
