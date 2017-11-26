// code by jph
package ch.ethz.idsc.owl.data.tree;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class SetNodeTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    Node node = new SetNode<>();
    Serialization.copy(node);
  }
}
