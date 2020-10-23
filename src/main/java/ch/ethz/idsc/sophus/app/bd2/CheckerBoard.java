// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.util.Objects;

import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Floor;

/* package */ class CheckerBoard implements TensorScalarFunction {
  private final TensorUnaryOperator tensorUnaryOperator;

  public CheckerBoard(TensorUnaryOperator tensorUnaryOperator) {
    this.tensorUnaryOperator = Objects.requireNonNull(tensorUnaryOperator);
  }

  @Override
  public Scalar apply(Tensor point) {
    try {
      Scalar scalar = Total.ofVector(tensorUnaryOperator.apply(point).map(Floor.FUNCTION));
      if (DeterminateScalarQ.of(scalar))
        return RealScalar.of(Math.floorMod(scalar.number().intValue(), 2));
    } catch (Exception exception) {
      System.err.println("---");
    }
    return DoubleScalar.INDETERMINATE;
  }
}