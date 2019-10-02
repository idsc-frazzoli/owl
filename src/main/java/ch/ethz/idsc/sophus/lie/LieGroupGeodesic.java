// code by jph
package ch.ethz.idsc.sophus.lie;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class LieGroupGeodesic implements GeodesicInterface, Serializable {
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  public LieGroupGeodesic(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
  }

  @Override // from TensorGeodesic
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    LieGroupElement lieGroupElement = lieGroup.element(p);
    Tensor log = lieExponential.log(lieGroupElement.inverse().combine(q));
    return scalar -> lieGroupElement.combine(lieExponential.exp(log.multiply(scalar)));
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar);
  }
}
