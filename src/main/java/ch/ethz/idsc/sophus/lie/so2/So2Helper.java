// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;

/* package */ enum So2Helper {
  ;
  static Tensor rangeQ(Tensor sequence) {
    ScalarSummaryStatistics scalarSummaryStatistics = sequence.stream() //
        .map(Scalar.class::cast) //
        .collect(ScalarSummaryStatistics.collector());
    Scalar width = scalarSummaryStatistics.getMax().subtract(scalarSummaryStatistics.getMin());
    if (Scalars.lessEquals(Pi.VALUE, width))
      throw TensorRuntimeException.of(sequence);
    return sequence;
  }
}
