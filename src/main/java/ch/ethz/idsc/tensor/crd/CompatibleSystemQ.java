// code by gjoel
package ch.ethz.idsc.tensor.crd;

import ch.ethz.idsc.tensor.Tensor;

public class CompatibleSystemQ {
  public static CompatibleSystemQ to(CoordinateSystem system) {
    return new CompatibleSystemQ(system);
  }

  public static CompatibleSystemQ to(Coordinates coords) {
    return new CompatibleSystemQ(coords.system());
  }

  // ---
  private final CoordinateSystem system;

  private CompatibleSystemQ(CoordinateSystem system) {
    this.system = system;
  }

  public boolean with(Tensor coords) {
    return coords instanceof Coordinates && with(((Coordinates) coords).system());
  }

  public boolean with(CoordinateSystem system) {
    return this.system.equals(system);
  }

  public CoordinateSystem require(CoordinateSystem system) {
    if (with(system))
      return system;
    throw new UnsupportedOperationException(String.format("incompatible: '%s' and '%s'", system.name(), system.name()));
  }

  public Coordinates require(Tensor coords) {
    if (with(coords))
      return (Coordinates) coords;
    throw new UnsupportedOperationException(String.format("incompatible: '%s' and '%s'", system.name(), ((Coordinates) coords).system().name()));
  }
}
