// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Arrays;
import java.util.stream.Collectors;

import junit.framework.TestCase;

public class BoundedLinkedListTest extends TestCase {
  public void testSimple() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(3);
    assertTrue(boundedLinkedList.add(0));
    assertTrue(boundedLinkedList.add(1));
    assertTrue(boundedLinkedList.add(2));
    assertTrue(boundedLinkedList.add(3));
    assertTrue(boundedLinkedList.add(4));
    assertEquals(boundedLinkedList.size(), 3);
    assertEquals(boundedLinkedList.get(0), Integer.valueOf(2));
    assertEquals(boundedLinkedList.get(1), Integer.valueOf(3));
    assertEquals(boundedLinkedList.get(2), Integer.valueOf(4));
    assertEquals(boundedLinkedList.stream().collect(Collectors.toList()), Arrays.asList(2, 3, 4));
    assertEquals(boundedLinkedList.peek(), Integer.valueOf(2));
    assertEquals(boundedLinkedList.poll(), Integer.valueOf(2));
    assertEquals(boundedLinkedList.size(), 2);
    assertEquals(boundedLinkedList.stream().collect(Collectors.toList()), Arrays.asList(3, 4));
    boundedLinkedList.clear();
    assertEquals(boundedLinkedList.size(), 0);
  }

  public void testAddAll() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(2);
    assertTrue(boundedLinkedList.add(0));
    assertTrue(boundedLinkedList.add(1));
    try {
      boundedLinkedList.addAll(Arrays.asList(6, 7));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testEmpty() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(0);
    assertFalse(boundedLinkedList.add(0));
    assertFalse(boundedLinkedList.add(1));
  }
}
