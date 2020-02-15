// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

public enum GeodesicDisplays {
  ;
  public static final List<GeodesicDisplay> ALL = Arrays.asList( //
      CommonClothoidDisplay.INSTANCE, //
      ClothoidDisplay.INSTANCE, //
      PolarClothoidDisplay.INSTANCE, //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      Spd2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE, //
      S1GeodesicDisplay.INSTANCE, //
      S2GeodesicDisplay.INSTANCE, //
      H2GeodesicDisplay.INSTANCE, //
      So3GeodesicDisplay.INSTANCE, //
      He1GeodesicDisplay.INSTANCE, //
      St1GeodesicDisplay.INSTANCE);
  // ---
  /** lie groups */
  public static final List<GeodesicDisplay> LIE_GROUPS = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE, //
      He1GeodesicDisplay.INSTANCE, //
      St1GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> R2_ONLY = Arrays.asList( //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_ONLY = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_R2 = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2_ONLY = Arrays.asList( //
      Se2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2_R2 = Arrays.asList( //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_SE2_R2 = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_SPD2_S2_R2 = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Spd2GeodesicDisplay.INSTANCE, //
      S2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_SE2 = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> CLOTH_SE2_R2 = Arrays.asList( //
      ClothoidDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SPD2_ONLY = Arrays.asList( //
      Spd2GeodesicDisplay.INSTANCE);
}
