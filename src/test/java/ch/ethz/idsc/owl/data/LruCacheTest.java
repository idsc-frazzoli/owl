// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Map;

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
    // System.out.println(map);
    map.get(3);
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 4);
    }
    String m1 = map.toString();
    // System.out.println(map);
    assertTrue(map.containsKey(4)); // does not change order
    // System.out.println("containsKey 4");
    // System.out.println(map);
    assertEquals(m1, map.toString());
    assertTrue(map.containsKey(3)); // does not change order
    assertEquals(m1, map.toString());
    map.get(3);
    assertEquals(m1, map.toString());
    // System.out.println(map);
    map.put(4, "0");
    // System.out.println(map);
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 3);
    }
  }
}
