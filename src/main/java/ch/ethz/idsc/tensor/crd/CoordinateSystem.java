// code by gjoel
package ch.ethz.idsc.tensor.crd;

import ch.ethz.idsc.tensor.alg.Array;

public class CoordinateSystem {
  static final char CS_OPENING_BRACKET = '(';
  static final char CS_CLOSING_BRACKET = ')';
  public static final CoordinateSystem DEFAULT = new CoordinateSystem("");

  public static CoordinateSystem of(String string) {
    return new CoordinateSystem(string);
  }

  // ---
  private final String name;

  private CoordinateSystem(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public Coordinates origin() {
    return origin(3);
  }

  public Coordinates origin(int dimensions) {
    return UnmodifiableCoordinates.of(Array.zeros(dimensions), this);
  }

  @Override // from Object
  public boolean equals(Object obj) {
    return obj instanceof CoordinateSystem && ((CoordinateSystem) obj).name().equals(name);
  }

  @Override // from Object
  public String toString() {
    return CS_OPENING_BRACKET + name + CS_CLOSING_BRACKET;
  }
}
