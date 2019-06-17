// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidCurve;
import ch.ethz.idsc.sophus.crv.clothoid.PseudoClothoidDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/* package */ class ClothoidTransition extends Se2Transition {
  public ClothoidTransition(Tensor start, Tensor end) {
    super(start, end);
  }

  @Override // from Transition
  public Scalar length() {
    return PseudoClothoidDistance.INSTANCE.distance(start(), end());
  }

  @Override // from Transition
  public Tensor sampled(Scalar ofs, Scalar dt) {
    if (Scalars.lessThan(dt, ofs))
      throw TensorRuntimeException.of(ofs, dt);
    Scalar length = length();
    ScalarTensorFunction scalarTensorFunction = scalar -> ClothoidCurve.INSTANCE.split(start(), end(), scalar);
    Tensor tensor = Tensors.empty();
    while (Scalars.lessThan(ofs, length)) {
      tensor.append(scalarTensorFunction.apply(ofs.divide(length)));
      ofs = ofs.add(dt);
    }
    return tensor;
  }
}
