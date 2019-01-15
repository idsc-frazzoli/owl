// code by jph
package ch.ethz.idsc.sophus.sym;

import junit.framework.TestCase;

public class SymLinkTest extends TestCase {
  public void testNodeNull() {
    new SymLink(null, null, null);
  }
}
