// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import junit.framework.TestCase;

public class LruCacheTest extends TestCase {
  public void testLru1() {
    Map<Integer, String> map = LruCache.create(2);
    map.put(3, "1");
    map.put(4, "2");
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 3);
    }
    map.get(3);
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 4);
    }
    String m1 = map.toString();
    assertTrue(map.containsKey(4)); // does not change order
    assertEquals(m1, map.toString());
    assertTrue(map.containsKey(3)); // does not change order
    assertEquals(m1, map.toString());
    map.get(3);
    assertEquals(m1, map.toString());
    map.put(4, "0");
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 3);
    }
  }

  public void testMax() {
    Map<Tensor, Tensor> map = LruCache.create(3);
    for (Tensor tensor : Range.of(0, 100))
      map.put(tensor, tensor);
    assertEquals(map.size(), 3);
  }
}
