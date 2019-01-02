// code by jph
package ch.ethz.idsc.sophus.app.api;

public enum GeodesicDisplays {
  R2(R2GeodesicDisplay.INSTANCE), //
  SE2C(Se2CoveringGeodesicDisplay.INSTANCE), //
  // SE2(Se2GeodesicDisplay.INSTANCE), //
  ;
  private final GeodesicDisplay spaceDisplay;

  private GeodesicDisplays(GeodesicDisplay spaceDisplay) {
    this.spaceDisplay = spaceDisplay;
  }

  public GeodesicDisplay spaceDisplay() {
    return spaceDisplay;
  }
}
