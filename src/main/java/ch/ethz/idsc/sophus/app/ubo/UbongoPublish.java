// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.Arrays;
import java.util.List;

/* package */ enum UbongoPublish {
  /** BATCH 1 2020 October */
  STANDARD(UbongoBoards.STANDARD, Arrays.asList(0, 1, 2, 3, 10, 12)), //
  CARPET_1(UbongoBoards.CARPET_1, Arrays.asList(2, 6, 9, 12, 16, 19)), //
  CARPET_A(UbongoBoards.CARPET_1, Arrays.asList(5, 7, 10, 15, 17, 20)), //
  CARPET_2(UbongoBoards.CARPET_2, Arrays.asList(0, 3, 5, 9, 12, 14)), //
  CARPET_3(UbongoBoards.CARPET_3, Arrays.asList(0, 1, 4, 5, 6, 8)), //
  DOTTED_1(UbongoBoards.DOTTED_1, Arrays.asList(0, 8, 10, 15, 21, 25)), //
  DOTTED_A(UbongoBoards.DOTTED_1, Arrays.asList(5, 9, 14, 19, 23, 27)), //
  BULLET_1(UbongoBoards.BULLET_1, Arrays.asList(2, 4, 7, 14, 16, 21)), //
  BULLET_A(UbongoBoards.BULLET_1, Arrays.asList(3, 5, 13, 15, 20, 23)), //
  HORSES_1(UbongoBoards.HORSES_1, Arrays.asList(0, 1, 3, 5, 10, 11)), //
  PYRAMID3(UbongoBoards.PYRAMID3, Arrays.asList(0, 2, 8, 9, 14, 16)), //
  PYRAMID4(UbongoBoards.PYRAMID4, Arrays.asList(0, 3, 6, 12, 16, 19)), //
  PYRAMIDA(UbongoBoards.PYRAMID4, Arrays.asList(2, 5, 7, 15, 18, 20)), //
  PIGGIES1(UbongoBoards.PIGGIES1, Arrays.asList(2, 4, 5, 6, 9, 10)), //
  PIGGIES2(UbongoBoards.PIGGIES2, Arrays.asList(0, 5, 14, 18, 24, 28)), //
  PIGGIESA(UbongoBoards.PIGGIES2, Arrays.asList(3, 12, 17, 20, 26, 30)), //
  MICKEY_1(UbongoBoards.MICKEY_1, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  MICKEY_2(UbongoBoards.MICKEY_2, Arrays.asList(0, 1, 3, 4, 5, 6)), //
  /** BATCH 2 2020 November 1 */
  RAYRAC_1(UbongoBoards.RAYRAC_1, Arrays.asList(1, 10, 13, 22, 23, 27)), //
  CRANED_1(UbongoBoards.CRANED_1, Arrays.asList(4, 10, 25, 31, 32, 35)), //
  BARCODE1(UbongoBoards.BARCODE1, Arrays.asList(3, 6, 12, 17, 22, 27)), //
  BARCODE2(UbongoBoards.BARCODE1, Arrays.asList(5, 9, 16, 20, 26, 28)), //
  BOTTLE_1(UbongoBoards.BOTTLE_1, Arrays.asList(0, 5, 9, 13, 18, 24)), //
  BOTTLE_2(UbongoBoards.BOTTLE_1, Arrays.asList(1, 7, 12, 16, 21, 25)), //
  PLANET_1(UbongoBoards.PLANET_1, Arrays.asList(0, 2, 3, 4, 5, 7)), //
  CHEESE_1(UbongoBoards.CHEESE_1, Arrays.asList(0, 3, 7, 13, 16, 23)), //
  CHEESE_2(UbongoBoards.CHEESE_1, Arrays.asList(1, 4, 11, 14, 17, 24)), //
  CHEESE_3(UbongoBoards.CHEESE_1, Arrays.asList(2, 5, 12, 15, 22, 27)), //
  /** BATCH 3 2020 November 7 */
  SPIRAL_1(UbongoBoards.SPIRAL_1, Arrays.asList(0, 10, 16, 19, 24, 30)), //
  SPIRAL_A(UbongoBoards.SPIRAL_1, Arrays.asList(4, 12, 17, 21, 25, 31)), //
  SPIRAL_B(UbongoBoards.SPIRAL_1, Arrays.asList(9, 14, 18, 22, 27, 33)), //
  SPIRAL_3(UbongoBoards.SPIRAL_3, Arrays.asList(1, 5, 9, 13, 16, 19)), //
  SPIRAL_C(UbongoBoards.SPIRAL_3, Arrays.asList(2, 6, 11, 14, 17, 20)), //
  SPIRAL_D(UbongoBoards.SPIRAL_3, Arrays.asList(4, 8, 12, 15, 18, 21)), //
  SPIRAL_4(UbongoBoards.SPIRAL_4, Arrays.asList(0, 3, 5, 12, 14, 16)), //
  SPIRAL_E(UbongoBoards.SPIRAL_4, Arrays.asList(2, 4, 10, 13, 15, 17)), //
  SHOTGUN1(UbongoBoards.SHOTGUN1, Arrays.asList(0, 2, 3, 4, 5, 6)), //
  SHOTGUN2(UbongoBoards.SHOTGUN2, Arrays.asList(1, 3, 5, 7, 11, 13)), //
  SHOTGUNA(UbongoBoards.SHOTGUN2, Arrays.asList(2, 4, 6, 8, 12, 14)), //
  SPIRAL_2(UbongoBoards.SPIRAL_2, Arrays.asList(0, 2, 3, 7, 11, 14)), //
  SHOTGUN3(UbongoBoards.SHOTGUN3, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  SHOTGUN4(UbongoBoards.SHOTGUN4, Arrays.asList(0)), //
  CORNER_2(UbongoBoards.CORNER_2, Arrays.asList(0, 1, 2, 3, 5, 6)), //
  CORNER_3(UbongoBoards.CORNER_3, Arrays.asList(0, 2, 6, 8, 10, 12)), //
  CORNER_A(UbongoBoards.CORNER_3, Arrays.asList(1, 3, 7, 9, 11, 16)), //
  CORNER_4(UbongoBoards.CORNER_4, Arrays.asList(0, 5, 9, 12, 16, 21)), //
  CORNER_B(UbongoBoards.CORNER_4, Arrays.asList(3, 6, 10, 14, 17, 24)), //
  CORNER_C(UbongoBoards.CORNER_4, Arrays.asList(4, 7, 11, 15, 20, 26)), //
  CORNER_5(UbongoBoards.CORNER_5, Arrays.asList(1, 2, 3, 4, 6, 7)), //
  CORNER_6(UbongoBoards.CORNER_6, Arrays.asList(0, 3, 5, 7, 9, 11)), //
  CORNER_D(UbongoBoards.CORNER_6, Arrays.asList(1, 4, 6, 8, 10, 12)), //
  /** BATCH 4 2020 Dezember 6 */
  MODERN_1(UbongoBoards.MODERN_1, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  AUTOMOB1(UbongoBoards.AUTOMOB1, Arrays.asList(0, 1, 3, 4, 8, 9)), //
  AUTOMOB2(UbongoBoards.AUTOMOB2, Arrays.asList(0, 3, 4, 5, 7, 8)), //
  AUTOMOB3(UbongoBoards.AUTOMOB3, Arrays.asList(0, 1, 4, 6, 7, 14)), //
  FACTORY1(UbongoBoards.FACTORY1, Arrays.asList(4, 5, 6, 8, 9, 11)), //
  FACTORY2(UbongoBoards.FACTORY2, Arrays.asList(2, 3, 7, 8, 9, 10)), //
  NKAPPE_1(UbongoBoards.NKAPPE_1, Arrays.asList(6, 7, 10, 11, 12, 14)), //
  TERRIER1(UbongoBoards.TERRIER1, Arrays.asList(6, 7, 11, 14, 15, 19)), //
  MASKEYES(UbongoBoards.MASKEYES, Arrays.asList(2, 5, 9, 14, 15, 17)), //
  MODERN_2(UbongoBoards.MODERN_2, Arrays.asList(0, 1, 2, 3, 6, 7)), //
  HOLGRAIL(UbongoBoards.HOLGRAIL, Arrays.asList(7, 8, 9, 12, 14, 19)), //
  SICHEL_1(UbongoBoards.SICHEL_1, Arrays.asList(1, 2, 6, 7, 8, 10)), //
  SICHAL_2(UbongoBoards.SICHAL_2, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  GIRLHAT1(UbongoBoards.GIRLHAT1, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  GIRLHAT2(UbongoBoards.GIRLHAT2, Arrays.asList(0, 2, 3, 4, 6, 7)), //
  MICKEY_3(UbongoBoards.MICKEY_3, Arrays.asList(0, 1, 4, 6, 8, 10)), //
  FREIGHT1(UbongoBoards.FREIGHT1, Arrays.asList(1, 4, 5, 6, 10, 12)), //
  CHRISTMT(UbongoBoards.CHRISTMT, Arrays.asList(0, 1, 2, 3, 5, 7)), //
  KERZENH1(UbongoBoards.KERZENH1, Arrays.asList(0, 1, 2, 4, 7, 9)), //
  /** BATCH 5 2020 Dezember 9 */
  KEYLOCK1(UbongoBoards.KEYLOCK1, Arrays.asList(0, 2, 3, 5, 6, 8)), //
  SHOEBLK1(UbongoBoards.SHOEBLK1, Arrays.asList(1, 2, 5, 7, 10, 11)), //
  PRINTED1(UbongoBoards.PRINTED1, Arrays.asList(1, 2, 4, 5, 6, 8)), //
  LETTERA1(UbongoBoards.PRINTED1, Arrays.asList(1, 2, 3, 4, 5, 6)), //
  LETTER_C(UbongoBoards.LETTER_C, Arrays.asList(0, 1, 2, 3, 4, 6)), //
  LETTER_G(UbongoBoards.LETTER_G, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  LETTER_I(UbongoBoards.LETTER_I, Arrays.asList(1, 2, 3, 5, 7, 9)), //
  LETTERJ0(UbongoBoards.LETTER_J, Arrays.asList(1, 2, 4, 11, 12, 14)), //
  LETTERJ1(UbongoBoards.LETTER_J, Arrays.asList(6, 7, 9, 16, 17, 18)), //
  LETTER_M(UbongoBoards.LETTER_J, Arrays.asList(1, 2, 3, 4, 5, 6)), //
  LETTER_P(UbongoBoards.LETTER_P, Arrays.asList(8, 10, 15, 16, 18, 20)), //
  RINGRI_1(UbongoBoards.RINGRI_1, Arrays.asList(0, 1, 2, 7, 8, 9)), //
  RINGRI3A(UbongoBoards.RINGRI_3, Arrays.asList(0, 2, 3, 8, 9, 11)), //
  RINGRI3B(UbongoBoards.RINGRI_3, Arrays.asList(4, 5, 6, 12, 14, 15)), //
  RINGRI_4(UbongoBoards.RINGRI_4, Arrays.asList(1, 2, 3, 5, 7, 13)), //
  RINGRI_5(UbongoBoards.RINGRI_5, Arrays.asList(1, 2, 3, 4, 7, 8)), //
  CHAPEL_2(UbongoBoards.CHAPEL_2, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  ARROWHD1(UbongoBoards.ARROWHD1, Arrays.asList(0, 1, 2, 3, 4, 5)), //
  FACTORY3(UbongoBoards.FACTORY3, Arrays.asList(4, 8, 10, 12, 15, 31)), //
  FACTORY4(UbongoBoards.FACTORY4, Arrays.asList(5, 6, 8, 12, 16, 20)), //
  AIRPLAN1(UbongoBoards.AIRPLAN1, Arrays.asList(1, 3, 6, 7, 14, 16)), //
  CACTUS_1(UbongoBoards.CACTUS_1, Arrays.asList(0, 6, 7, 9, 14, 19)), //
  CHAPEL_3(UbongoBoards.CHAPEL_3, Arrays.asList(0, 2, 4, 5, 6, 7)), //
  BATMAN_1(UbongoBoards.BATMAN_1, Arrays.asList(4, 9, 11, 12, 14, 16)), //
  LETTER_S(UbongoBoards.LETTER_S, Arrays.asList(1, 2, 3, 4, 6, 7)), //
  ;

  public final UbongoBoards ubongoBoards;
  public final List<Integer> list;

  private UbongoPublish(UbongoBoards ubongoBoards, List<Integer> list) {
    this.ubongoBoards = ubongoBoards;
    this.list = list;
  }
}
