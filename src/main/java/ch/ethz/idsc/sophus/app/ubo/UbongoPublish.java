// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.Arrays;
import java.util.List;

/* package */ enum UbongoPublish {
  /** 2020 October */
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
  /** 2020 November */
  RAYRAC_1(UbongoBoards.RAYRAC_1, Arrays.asList(1, 10, 13, 22, 23, 27)), //
  CRANED_1(UbongoBoards.CRANED_1, Arrays.asList(4, 10, 25, 31, 32, 35)), //
  BARCODE1(UbongoBoards.BARCODE1, Arrays.asList(3, 6, 12, 17, 22, 27)), //
  BARCODE2(UbongoBoards.BARCODE1, Arrays.asList(5, 9, 16, 20, 26, 28)), //
  BOTTLE_1(UbongoBoards.BOTTLE_1, Arrays.asList(0, 5, 9, 13, 18, 24)), //
  BOTTLE_2(UbongoBoards.BOTTLE_1, Arrays.asList(1, 7, 12, 16, 21, 25)), //
  SHOTGUN1(UbongoBoards.SHOTGUN1, Arrays.asList(0, 2, 3, 4, 5, 7)), //
  CHEESE_1(UbongoBoards.CHEESE_1, Arrays.asList(0, 3, 7, 13, 16, 23)), //
  CHEESE_2(UbongoBoards.CHEESE_1, Arrays.asList(1, 4, 11, 14, 17, 24)), //
  CHEESE_3(UbongoBoards.CHEESE_1, Arrays.asList(2, 5, 12, 15, 22, 27)), //
  ;

  public final UbongoBoards ubongoBoards;
  public final List<Integer> list;

  private UbongoPublish(UbongoBoards ubongoBoards, List<Integer> list) {
    this.ubongoBoards = ubongoBoards;
    this.list = list;
  }
}
