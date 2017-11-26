// code by jph & jl
package ch.ethz.idsc.owl.bot.delta.glc;

import ch.ethz.idsc.owl.bot.delta.ImageGradient;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// TODO JONAS should be obsolete
//@Deprecated
class DeltaxtStateSpaceModel implements StateSpaceModel {
  private final ImageGradient imageGradient;
  private final Scalar maxInput;

  public DeltaxtStateSpaceModel(ImageGradient imageGradient, Scalar maxInput) {
    this.imageGradient = imageGradient;
    this.maxInput = maxInput;
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    GlobalAssert.that(x.length() == 3);
    return imageGradient.rotate(x.extract(0, 2)).add(u).append(RealScalar.ONE);
  }

  @Override
  public Scalar getLipschitz() {
    // maxNorm is very big--> therefore eta with R^(1+LF) is huge? real lipschitz?
    Scalar n = RealScalar.of(4); // dimensions of StateSpace + Dimensions of InputSpace
    // lipschitz constant on vector-valued function from:
    // https://math.stackexchange.com/questions/1132078/proof-that-a-vector-valued-function-is-lipschitz-continuous-on-a-closed-rectangl
    return imageGradient.maxNormGradient().add(maxInput).multiply(n);
  }

  public Scalar getMaxInput() {
    return maxInput;
  }

  public Scalar getMaxPossibleChange() {
    return maxInput.add(imageGradient.maxNormGradient());
  }
}
