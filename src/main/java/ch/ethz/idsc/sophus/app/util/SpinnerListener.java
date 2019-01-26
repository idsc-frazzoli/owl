// code by jph
package ch.ethz.idsc.sophus.app.util;

@FunctionalInterface
public interface SpinnerListener<Type> {
  /** @param type */
  void actionPerformed(Type type);
}
