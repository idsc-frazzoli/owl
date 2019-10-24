// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensors;

public enum Hermite2Subdivisions {
  ;
  public static HermiteSubdivision a1(LieGroup lieGroup, LieExponential lieExponential) {
    return new Hermite2Subdivision(lieGroup, lieExponential, //
        RationalScalar.of(5, 32), //
        RationalScalar.of(+9, 64), //
        RationalScalar.of(-3, 64), //
        RationalScalar.of(9, 16), //
        Tensors.of(RationalScalar.of(3, 32), RationalScalar.of(-5, 32)));
  }

  /** Reference:
   * "Increasing the smoothness of vector and Hermite subdivision schemes"
   * by Moosmueller, Dyn, 2017
   * 
   * @param lieGroup
   * @param lieExponential
   * @return */
  public static HermiteSubdivision a2(LieGroup lieGroup, LieExponential lieExponential) {
    return new Hermite2Subdivision(lieGroup, lieExponential, //
        RationalScalar.of(6, 25), //
        RationalScalar.of(+31, 200), //
        RationalScalar.of(-29, 200), //
        RationalScalar.of(29, 400), //
        Tensors.of(RationalScalar.of(277, 800), RationalScalar.of(13, 160)));
  }
}
