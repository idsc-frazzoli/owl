// code by jph
package ch.ethz.idsc.sophus.app.util;

@FunctionalInterface
public interface SpinnerListener<Type> {
  void actionPerformed(Type type);
}
