// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

/** determines membership for elements of type T */
public interface Region<T> extends Serializable {
  /** @param element
   * @return membership status of given element */
  boolean isMember(T element);
}
