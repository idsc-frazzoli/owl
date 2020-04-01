// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

public enum GeodesicDisplays {
  ;
  public static final List<GeodesicDisplay> ALL = Arrays.asList( //
      Se2ClothoidDisplay.INSTANCE, //
      Se2CoveringClothoidDisplay.INSTANCE, //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      Spd2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE, //
      R3GeodesicDisplay.INSTANCE, //
      S1GeodesicDisplay.INSTANCE, //
      S2GeodesicDisplay.INSTANCE, //
      HP2GeodesicDisplay.INSTANCE, //
      H1GeodesicDisplay.INSTANCE, //
      H2GeodesicDisplay.INSTANCE, //
      So3GeodesicDisplay.INSTANCE, //
      He1GeodesicDisplay.INSTANCE, //
      St1GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> WITHOUT_Sn_SO3 = Arrays.asList( //
      Se2ClothoidDisplay.INSTANCE, //
      Se2CoveringClothoidDisplay.INSTANCE, //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      Spd2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE, //
      R3GeodesicDisplay.INSTANCE, //
      HP2GeodesicDisplay.INSTANCE, //
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
  public static final List<GeodesicDisplay> SE2C_SE2_S2_H2_R2 = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      S2GeodesicDisplay.INSTANCE, //
      H2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_SPD2_S2_Rn = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Spd2GeodesicDisplay.INSTANCE, //
      S2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE, //
      R3GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_SE2 = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> CLOTH_SE2_R2 = Arrays.asList( //
      Se2ClothoidDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SPD2_ONLY = Arrays.asList( //
      Spd2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> S2_ONLY = Arrays.asList( //
      S2GeodesicDisplay.INSTANCE);
  public static final List<GeodesicDisplay> H2_ONLY = Arrays.asList( //
      H2GeodesicDisplay.INSTANCE);
}
