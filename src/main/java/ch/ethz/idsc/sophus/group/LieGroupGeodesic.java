// code by jph
package ch.ethz.idsc.sophus.group;

import java.util.function.Function;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class LieGroupGeodesic implements GeodesicInterface {
  private final Function<Tensor, LieGroupElement> function;
  private final LieExponential lieExponential;

  public LieGroupGeodesic(Function<Tensor, LieGroupElement> function, LieExponential lieExponential) {
    this.function = function;
    this.lieExponential = lieExponential;
  }

  @Override // from TensorGeodesic
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    LieGroupElement lieGroupAction = function.apply(p);
    Tensor delta = lieGroupAction.inverse().combine(q);
    Tensor x = lieExponential.log(delta);
    return scalar -> lieGroupAction.combine(lieExponential.exp(x.multiply(scalar)));
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar);
  }
}
