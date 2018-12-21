// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public enum St1CoveringGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from TensorGeodesic
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    St1CoveringGroupElement p_act = new St1CoveringGroupElement(p);
    Tensor delta = p_act.inverse().combine(q);
    Tensor x = St1CoveringExponential.INSTANCE.log(delta);
    return scalar -> p_act.combine(St1CoveringExponential.INSTANCE.exp(x.multiply(scalar)));
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar);
  }
}
