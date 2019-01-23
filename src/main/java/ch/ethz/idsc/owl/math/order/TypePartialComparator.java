// code by jph
package ch.ethz.idsc.owl.math.order;

/** in the Java language the type hierarchy may not contain cycles
 * 
 * https://en.wikipedia.org/wiki/Subtyping */
public enum TypePartialComparator {
  ;
  public static final PartialComparator<Class<?>> INSTANCE = PartialOrder.comparator(Class::isAssignableFrom);
}
