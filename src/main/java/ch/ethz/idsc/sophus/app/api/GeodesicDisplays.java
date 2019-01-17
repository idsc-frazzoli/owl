// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

public enum GeodesicDisplays {
  ;
  public static final List<GeodesicDisplay> ALL = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE, //
      H2GeodesicDisplay.INSTANCE, //
      He1GeodesicDisplay.INSTANCE, //
      St1GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> R2_ONLY = Arrays.asList( //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_ONLY = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2_ONLY = Arrays.asList( //
      Se2GeodesicDisplay.INSTANCE);
}
