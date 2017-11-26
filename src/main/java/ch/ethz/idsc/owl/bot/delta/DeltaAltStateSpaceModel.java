// code by jl
package ch.ethz.idsc.owl.bot.delta;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public class DeltaAltStateSpaceModel implements StateSpaceModel {
  private final ImageGradient imageGradient;
  private final Scalar maxInput;

  /** @param imageGradient
   * @param maxInput positive */
  public DeltaAltStateSpaceModel(ImageGradient imageGradient, Scalar maxInput) {
    GlobalAssert.that(Sign.isPositive(maxInput));
    this.imageGradient = imageGradient;
    this.maxInput = maxInput;
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    return imageGradient.rotate(x).add(u);
  }

  @Override
  public Scalar getLipschitz() {
    // maxNorm is very big--> therefore eta with R^(1+LF) is huge? real lipschitz?
    Scalar n = RealScalar.of(4); // dimensions of StateSpace + Dimensions of InputSpace
    // lipschitz constant on vector-valued function from:
    // https://math.stackexchange.com/questions/1132078/proof-that-a-vector-valued-function-is-lipschitz-continuous-on-a-closed-rectangl
    return getMaxPossibleChange().multiply(n);
  }

  public Scalar getMaxPossibleChange() {
    return imageGradient.maxNormGradient().add(maxInput);
  }
}
