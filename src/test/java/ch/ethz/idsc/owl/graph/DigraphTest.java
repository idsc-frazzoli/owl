// code by astoll
package ch.ethz.idsc.owl.graph;

import junit.framework.TestCase;

public class DigraphTest extends TestCase {
  public void testSimple() {
    Digraph<Integer> digraph = new Digraph<>();
    digraph.addVertex("1");
    assertEquals(digraph.size(), 1);
  }
  
}
