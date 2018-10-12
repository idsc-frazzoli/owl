// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.RnExponential;
import ch.ethz.idsc.owl.math.group.RnGroup;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class GeodesicDifferencesTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.unit();
    Tensor tensor = RandomVariate.of(distribution, 10, 4);
    GeodesicDifferences geodesicDifferences = //
        new GeodesicDifferences(RnGroup.INSTANCE, RnExponential.INSTANCE);
    assertEquals(geodesicDifferences.apply(tensor), Differences.of(tensor));
  }
}
