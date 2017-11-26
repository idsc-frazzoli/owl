// code by jph
package ch.ethz.idsc.owl.data.tree;

import junit.framework.TestCase;

public class NodesTest extends TestCase {
  public void testFail() {
    try {
      Nodes.rootFrom(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      Nodes.listFromRoot(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      Nodes.listToRoot(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
