// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Iterator;

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
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * References:
 * "A family of Hermite interpolants by bisection algorithms", 1992,
 * by Merrien
 * 
 * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
 * by Byeongseon Jeong, Jungho Yoon */
public class Hermite2Subdivision implements HermiteSubdivision {
  private static final Scalar _29 = RealScalar.of(29);
  private static final Scalar _31 = RealScalar.of(31);
  private static final Scalar _06_25 = RationalScalar.of(6, 25);
  private static final Scalar _19_25 = RationalScalar.of(19, 25);
  private static final Scalar _13_80 = RationalScalar.of(13, 80);
  private static final Scalar _277_400 = RationalScalar.of(277, 400);
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final LieGroupGeodesic lieGroupGeodesic;

  /** @param lieGroup
   * @param lieExponential
   * @throws Exception if either parameters is null */
  public Hermite2Subdivision(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
    lieGroupGeodesic = new LieGroupGeodesic(lieGroup, lieExponential);
  }

  /** @param control
   * @return */
  public TensorIteration string(Tensor control) {
    return new Control(RealScalar.ONE, control).new StringIteration();
  }

  @Override
  public TensorIteration string(Scalar delta, Tensor control) {
    return new Control(delta, control).new StringIteration();
  }

  /** @param control
   * @return */
  public TensorIteration cyclic(Tensor control) {
    return new Control(RealScalar.ONE, control).new CyclicIteration();
  }

  @Override
  public TensorIteration cyclic(Scalar delta, Tensor control) {
    return new Control(delta, control).new CyclicIteration();
  }

  private class Control {
    private Tensor control;
    private Scalar rgk;
    private Scalar rvk;

    private Control(Scalar delta, Tensor control) {
      this.control = control;
      rgk = RealScalar.of(200).divide(delta);
      rvk = RationalScalar.of(29, 200).divide(delta);
    }

    private void refine(Tensor curve, Tensor p, Tensor q) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      ScalarTensorFunction scalarTensorFunction = lieGroupGeodesic.curve(pg, qg);
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg)); // q - p
      Tensor rv1 = log.multiply(rvk);
      // TODO interpret tangent vectors at element in group -> use adjoint map
      {
        Tensor rg1 = scalarTensorFunction.apply(_06_25);
        Tensor rg2 = lieExponential.exp(pv.multiply(_31).subtract(qv.multiply(_29)).divide(rgk));
        Tensor rg = lieGroup.element(rg1).combine(rg2);
        // ---
        Tensor rv2 = qv.multiply(_13_80).add(pv.multiply(_277_400));
        Tensor rv = rv1.add(rv2);
        curve.append(Tensors.of(rg, rv));
      }
      {
        Tensor rg1 = scalarTensorFunction.apply(_19_25);
        Tensor rg2 = lieExponential.exp(pv.multiply(_29).subtract(qv.multiply(_31)).divide(rgk));
        Tensor rg = lieGroup.element(rg1).combine(rg2);
        // ---
        Tensor rv2 = qv.multiply(_277_400).add(pv.multiply(_13_80));
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
        rgk = rgk.add(rgk);
        rvk = rvk.add(rvk);
        return control = curve;
      }
    }

    private class CyclicIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        Tensor curve = protected_string(control);
        refine(curve, Last.of(control), control.get(0));
        rgk = rgk.add(rgk);
        rvk = rvk.add(rvk);
        return control = curve;
      }
    }
  }
}
