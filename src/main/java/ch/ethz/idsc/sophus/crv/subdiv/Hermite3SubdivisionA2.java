// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensors;

public enum Hermite3SubdivisionA2 {
  ;
  public static HermiteSubdivision of(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    return new Hermite3Subdivision(lieGroup, lieExponential, biinvariantMean, //
        RationalScalar.of(-5, 56), RationalScalar.of(7, 12), RationalScalar.of(-1, 24), //
        Tensors.fromString("{7/96, 41/48, 7/96}"), //
        RationalScalar.of(-25, 1344), //
        RationalScalar.of(77, 384), //
        Tensors.fromString("{-19/384, 19/96, -19/384}"));
  }
}
