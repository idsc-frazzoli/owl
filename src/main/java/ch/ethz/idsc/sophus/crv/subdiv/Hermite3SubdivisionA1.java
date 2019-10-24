// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public enum Hermite3SubdivisionA1 {
  ;
  public static HermiteSubdivision of(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    return new Hermite3Subdivision(lieGroup, lieExponential, biinvariantMean, //
        RationalScalar.of(-1, 16), RationalScalar.of(15, 16), RationalScalar.of(-7, 32), //
        Tensors.fromString("{1/128, 63/64, 1/128}"), //
        RationalScalar.of(+7, 256), //
        RealScalar.ZERO, //
        Tensors.fromString("{1/16, 3/8, 1/16}"));
  }
}
