// code by gjoel
package ch.ethz.idsc.sophus.math.sample;

public interface BiasedSample {
  void encourage();

  void discourage();

  void resetCurrent();

  void resetAll();
}
