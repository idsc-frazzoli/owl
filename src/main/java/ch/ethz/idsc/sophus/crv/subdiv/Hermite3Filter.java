// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;

/**  */
public class Hermite3Filter implements HermiteFilter {
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final BiinvariantMean biinvariantMean;

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @throws Exception if either parameters is null */
  public Hermite3Filter(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.lieExponential = Objects.requireNonNull(lieExponential);
    this.biinvariantMean = Objects.requireNonNull(biinvariantMean);
  }

  @Override // from HermiteFilter
  public TensorIteration string(Scalar delta, Tensor control) {
    return new Control(delta, control).new StringIteration();
  }

  private static final Tensor CGW = Tensors.fromString("{1/128, 63/64, 1/128}");
  private static final Tensor CVW = Tensors.fromString("{-1/16, 3/4, -1/16}");

  private class Control {
    private Tensor control;
    private Scalar cgk;
    private Scalar cvk;

    private Control(Scalar delta, Tensor control) {
      this.control = control;
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

    private class StringIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Tensor string = Tensors.reserve(length);
        Tensor p = control.get(0);
        string.append(p); // interpolation
        Tensor q = control.get(1);
        for (int index = 2; index < length; ++index) {
          Tensor r = control.get(index);
          string.append(center(p, q, r));
          p = q;
          q = r;
        }
        string.append(q);
        return control = string;
      }
    }
  }
}
