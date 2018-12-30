// code by jph
package ch.ethz.idsc.owl.math;

/** https://en.wikipedia.org/wiki/Fresnel_integral
 * 
 * Careful: not consistent with Mathematica::FresnelS
 * input off by a factor, output off by a factor */
public class FresnelS extends FresnelBase {
  public static final FresnelS FUNCTION = new FresnelS();

  private FresnelS() {
    // ---
  }

  @Override
  int first() {
    return 3;
  }

  @Override
  int second() {
    return 1;
  }
}
