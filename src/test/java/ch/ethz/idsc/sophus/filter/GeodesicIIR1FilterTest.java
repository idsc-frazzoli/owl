// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicIIR1FilterTest extends TestCase {
  public void testSimple() {
    GeodesicIIR1Filter geodesicIIR1Filter = //
        new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(0.5));
    // irc=0.0[s^-2]
    // irc=1.9999999999999996[s^-2]
    // irc=1.0000000000000009[s^-2]
    // irc=1.9999999999999996[s^-2]
    // irc=-1.5000000000000002[s^-2]
    // irc=1.0000000000000009[s^-2]
    Scalar acc0 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(0, "s^-2"));
    Chop._13.requireClose(acc0, Quantity.of(0, "s^-2"));
    // System.out.println(acc0); // 0.0[s^-2]
    Scalar acc1 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(2, "s^-2"));
    Chop._13.requireClose(acc1, Quantity.of(1, "s^-2"));
    // System.out.println(acc1); // 1[s^-2]
    Scalar acc2 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(1, "s^-2"));
    Chop._13.requireClose(acc2, Quantity.of(1, "s^-2"));
    // System.out.println(acc2); // 1[s^-2]
    Scalar acc3 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(2, "s^-2"));
    Chop._13.requireClose(acc3, Quantity.of(1.5, "s^-2"));
    // System.out.println(acc3); // 1.5[s^-2]
    Scalar acc4 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(-1.5, "s^-2"));
    Chop._13.requireClose(acc4, Quantity.of(0, "s^-2"));
    // System.out.println(acc4); // 0[s^-2]
    Scalar acc5 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(1, "s^-2"));
    Chop._13.requireClose(acc5, Quantity.of(0.5, "s^-2"));
    // System.out.println(acc5); // 0.5[s^-2]
  }

  public void testInitialized() {
    GeodesicIIR1Filter geodesicIIR1Filter = //
        new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(0.5), Quantity.of(0, "s^-2"));
    // irc=0.0[s^-2]
    // irc=1.9999999999999996[s^-2]
    // irc=1.0000000000000009[s^-2]
    // irc=1.9999999999999996[s^-2]
    // irc=-1.5000000000000002[s^-2]
    // irc=1.0000000000000009[s^-2]
    Scalar acc0 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(0, "s^-2"));
    Chop._13.requireClose(acc0, Quantity.of(0, "s^-2"));
    // System.out.println(acc0); // 0.0[s^-2]
    Scalar acc1 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(2, "s^-2"));
    Chop._13.requireClose(acc1, Quantity.of(1, "s^-2"));
    // System.out.println(acc1); // 1[s^-2]
    Scalar acc2 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(1, "s^-2"));
    Chop._13.requireClose(acc2, Quantity.of(1, "s^-2"));
    // System.out.println(acc2); // 1[s^-2]
    Scalar acc3 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(2, "s^-2"));
    Chop._13.requireClose(acc3, Quantity.of(1.5, "s^-2"));
    // System.out.println(acc3); // 1.5[s^-2]
    Scalar acc4 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(-1.5, "s^-2"));
    Chop._13.requireClose(acc4, Quantity.of(0, "s^-2"));
    // System.out.println(acc4); // 0[s^-2]
    Scalar acc5 = (Scalar) geodesicIIR1Filter.apply(Quantity.of(1, "s^-2"));
    Chop._13.requireClose(acc5, Quantity.of(0.5, "s^-2"));
    // System.out.println(acc5); // 0.5[s^-2]
  }

  public void testNullFail() {
    try {
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(0.2), null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
