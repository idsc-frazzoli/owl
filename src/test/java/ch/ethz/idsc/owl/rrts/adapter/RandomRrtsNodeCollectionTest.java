// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Collection;

import ch.ethz.idsc.owl.rrts.RandomRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RandomRrtsNodeCollectionTest extends TestCase {
  public void testSimple() {
    RandomRrtsNodeCollection randomRrtsNodeCollection = new RandomRrtsNodeCollection();
    Collection<RrtsNode> collection = randomRrtsNodeCollection.nearFrom(Tensors.empty(), 10);
    assertTrue(collection.isEmpty());
  }
}
