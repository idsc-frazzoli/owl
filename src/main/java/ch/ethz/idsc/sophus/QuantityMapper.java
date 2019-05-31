// code by jph
package ch.ethz.idsc.sophus;

import java.util.function.UnaryOperator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/**  */
public class QuantityMapper implements ScalarUnaryOperator {
  private final ScalarUnaryOperator valueMapper;
  private final UnaryOperator<Unit> unitMapper;

  public QuantityMapper(ScalarUnaryOperator valueMapper, UnaryOperator<Unit> unitMapper) {
    this.valueMapper = valueMapper;
    this.unitMapper = unitMapper;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return Quantity.of( //
          valueMapper.apply(quantity.value()), //
          unitMapper.apply(quantity.unit()));
    }
    return Quantity.of( //
        valueMapper.apply(scalar), //
        unitMapper.apply(Unit.ONE));
  }
}
