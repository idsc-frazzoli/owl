// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Arrays;
import java.util.List;

public enum GeodesicDisplays {
  ;
  public static final List<ManifoldDisplay> ALL = Arrays.asList( //
      Se2ClothoidDisplay.ANALYTIC, //
      Se2ClothoidDisplay.LEGENDRE, //
      Se2CoveringClothoidDisplay.INSTANCE, //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      Spd2Display.INSTANCE, //
      R2Display.INSTANCE, //
      R3Display.INSTANCE, //
      S1Display.INSTANCE, //
      S2Display.INSTANCE, //
      H1Display.INSTANCE, //
      H2Display.INSTANCE, //
      So3Display.INSTANCE, //
      He1Display.INSTANCE, //
      Dt1Display.INSTANCE);
  // ---
  /** requires biinvariant mean */
  public static final List<ManifoldDisplay> METRIC = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      Spd2Display.INSTANCE, //
      R2Display.INSTANCE, //
      R3Display.INSTANCE, //
      S1Display.INSTANCE, //
      S2Display.INSTANCE, //
      H1Display.INSTANCE, //
      H2Display.INSTANCE, //
      So3Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> MANIFOLDS = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      Spd2Display.INSTANCE, //
      R2Display.INSTANCE, //
      R3Display.INSTANCE, //
      S1Display.INSTANCE, //
      S2Display.INSTANCE, //
      H1Display.INSTANCE, //
      H2Display.INSTANCE, //
      So3Display.INSTANCE, //
      He1Display.INSTANCE, //
      Dt1Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> WITHOUT_Sn_SO3 = Arrays.asList( //
      Se2ClothoidDisplay.ANALYTIC, //
      Se2ClothoidDisplay.LEGENDRE, //
      Se2CoveringClothoidDisplay.INSTANCE, //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      Spd2Display.INSTANCE, //
      R2Display.INSTANCE, //
      R3Display.INSTANCE, //
      He1Display.INSTANCE, //
      Dt1Display.INSTANCE);
  // ---
  /** lie groups */
  public static final List<ManifoldDisplay> LIE_GROUPS = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      R2Display.INSTANCE, //
      He1Display.INSTANCE, //
      Dt1Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> R2_ONLY = Arrays.asList( //
      R2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2C_ONLY = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2C_R2 = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      R2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2_ONLY = Arrays.asList( //
      Se2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2_R2 = Arrays.asList( //
      Se2Display.INSTANCE, //
      R2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2C_SE2_R2 = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      R2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2C_SE2_S2_H2_R2 = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      S2Display.INSTANCE, //
      H2Display.INSTANCE, //
      R2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2C_SE2_SPD2_S2_Rn = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE, //
      Spd2Display.INSTANCE, //
      S2Display.INSTANCE, //
      R2Display.INSTANCE, //
      R3Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SE2C_SE2 = Arrays.asList( //
      Se2CoveringDisplay.INSTANCE, //
      Se2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> CL_SE2_R2 = Arrays.asList( //
      Se2ClothoidDisplay.ANALYTIC, //
      Se2ClothoidDisplay.LEGENDRE, //
      Se2Display.INSTANCE, //
      R2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> SPD2_ONLY = Arrays.asList( //
      Spd2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> ARRAYS = Arrays.asList( //
      R2Display.INSTANCE, //
      H2Display.INSTANCE, //
      S2Display.INSTANCE, //
      Rp2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> R2_H2 = Arrays.asList( //
      R2Display.INSTANCE, //
      H2Display.INSTANCE);
  public static final List<ManifoldDisplay> R2_H2_S2 = Arrays.asList( //
      R2Display.INSTANCE, //
      H2Display.INSTANCE, //
      S2Display.INSTANCE);
  public static final List<ManifoldDisplay> R2_H2_S2_RP2 = Arrays.asList( //
      R2Display.INSTANCE, //
      H2Display.INSTANCE, //
      S2Display.INSTANCE, //
      Rp2Display.INSTANCE);
  public static final List<ManifoldDisplay> S2_RP2 = Arrays.asList( //
      S2Display.INSTANCE, //
      Rp2Display.INSTANCE);
  // ---
  public static final List<ManifoldDisplay> S2_ONLY = Arrays.asList( //
      S2Display.INSTANCE);
  public static final List<ManifoldDisplay> H2_ONLY = Arrays.asList( //
      H2Display.INSTANCE);
  public static final List<ManifoldDisplay> CL_ONLY = Arrays.asList( //
      Se2ClothoidDisplay.ANALYTIC, //
      Se2ClothoidDisplay.LEGENDRE //
  );
  public static final List<ManifoldDisplay> CLC_ONLY = Arrays.asList( //
      Se2CoveringClothoidDisplay.INSTANCE, //
      Se2ClothoidDisplay.ANALYTIC, //
      Se2ClothoidDisplay.LEGENDRE //
  );
}
