// code by jph
// adapted from http://www.java2s.com/Code/Android/UI/BoundedLinkedList.htm
package ch.ethz.idsc.owl.data;

import java.util.Collection;
import java.util.LinkedList;

/** @param <E> the type of elements held in this collection */
public class BoundedLinkedList<E> extends LinkedList<E> {
  private final int maxSize;

  /** @param maxSize */
  public BoundedLinkedList(int maxSize) {
    this.maxSize = maxSize;
  }

  @Override // from LinkedList
  public boolean add(E object) {
    super.add(object);
    if (maxSize < size())
      removeFirst();
    return !isEmpty();
  }

  @Override // from LinkedList
  public void add(int location, E object) {
    super.add(location, object);
    if (maxSize < size())
      removeFirst();
  }

  @Override // from LinkedList
  public boolean addAll(Collection<? extends E> collection) {
    throw new UnsupportedOperationException();
  }

  @Override // from LinkedList
  public boolean addAll(int index, Collection<? extends E> collection) {
    throw new UnsupportedOperationException();
  }

  @Override // from LinkedList
  public void addFirst(E object) {
    throw new UnsupportedOperationException();
  }

  @Override // from LinkedList
  public void addLast(E object) {
    add(object);
  }
}
