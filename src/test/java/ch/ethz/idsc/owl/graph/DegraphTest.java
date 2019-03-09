// code by jph
package ch.ethz.idsc.owl.graph;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class DegraphTest extends TestCase {
  public void testSimple() {
    Degraph<Integer> degraph = new Degraph<>();
    Vert<Integer> v1 = degraph.createSingletonVert(1);
    Vert<Integer> v2 = degraph.createSingletonVert(2);
    Vert<Integer> v3 = degraph.createSingletonVert(3);
    v1.insertLinkTo(v2, RealScalar.of(12));
    v3.insertLinkTo(v2, RealScalar.of(32));
    v3.insertLinkTo(v1, RealScalar.of(31));
    assertEquals(v1.children().size(), 1);
    assertEquals(v2.children().size(), 0);
    assertEquals(v3.children().size(), 2);
    assertEquals(v1.parents().size(), 1);
    assertEquals(v2.parents().size(), 2);
    assertEquals(v3.parents().size(), 0);
  }
}
