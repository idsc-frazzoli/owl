// code by jph
package ch.ethz.idsc.owl.math;

/** https://en.wikipedia.org/wiki/Fresnel_integral
 * 
 * Careful: not consistent with Mathematica::FresnelC
 * input off by a factor, output off by a factor */
public class FresnelC extends FresnelBase {
  public static final FresnelC FUNCTION = new FresnelC();

  private FresnelC() {
    // ---
  }

  @Override
  int first() {
    return 1;
  }

  @Override
  int second() {
    return 0;
  }
}
