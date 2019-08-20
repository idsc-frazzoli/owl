// code by jph
package ch.ethz.idsc.sophus.flt.bm;

import java.util.function.Function;

import ch.ethz.idsc.sophus.flt.ga.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum BiinvariantMeanFIRnFilter {
  ;
  public static TensorUnaryOperator of( //
      BiinvariantMean biinvariantMean, Function<Integer, Tensor> function, //
      BinaryAverage binaryAverage, int radius, Scalar alpha) {
    return GeodesicFIRnFilter.of( //
        BiinvariantMeanExtrapolation.of(biinvariantMean, function), //
        binaryAverage, radius, alpha);
  }
}