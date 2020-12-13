// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.List;

/* package */ enum UbongoBoards {
  /***************************************************/
  /************ BATCH 1 ******************************/
  STANDARD(3, "oooo", "oooo", "ooo", " o"), //
  /** 2,5,6,7,9,10,12,15,16,17,19,20 */
  CARPET_1(5, "o oo ", "ooooo", " oooo", " oooo", "ooooo"), //
  /** 0,1,3,5,9,12,14 */
  CARPET_2(5, "oooo ", "ooooo", " oooo", " oooo", "ooooo"), //
  /** 0,1,4,5,6,8 */
  CARPET_3(5, "oooo  ", "oooooo", " oooo", " oooo", "ooooo"), //
  /** only 1 unique solution */
  CARPET_4(5, "oooo  ", "ooooo ", " ooooo", " ooooo", "ooooo"), //
  // CARPET5(6, "oooo ", "oooooo", " ooooo", " ooooo", "ooooo"), //
  DOTTED_1(5, "oooo ", "ooooo", " oooo", " oo o", "ooooo"), //
  /** 2,3,4,5,7,13,14,15,16,20,21,23 */
  BULLET_1(5, "oooo ", "ooooo", "oo  o", "oo  o", "ooooo"), //
  /** 0,1,3,5,10,11 */
  HORSES_1(5, "oooooo", "oo  oo", "o   ", "oo  oo", "ooooo"), //
  /** 0,2,8,9,14,16 */
  PYRAMID3(5, "   o   ", "  oooo ", " oooo", "ooooo", " ooo", " oo"), //
  /** 0,2,3,5,6,7, 12,15,16,18,19,20 */
  PYRAMID4(5, "   o   ", "  oooo ", " oooo", "ooooo", " oooo", " oo"), //
  /**  */
  PIGGIES1(5, " oooo ", "oooooo", "oooo", " o o", " o o"), //
  /**  */
  PIGGIES2(5, " oooo ", "oooooo", "ooooo", " ooo", " o o"), //
  MICKEY_1(5, "oo  oo", "oo  oo", " oooo", " oooo", " o oo", "  oo  "), //
  MICKEY_2(6, "oo  oo", "oo  oo", " oooo", " oooo", " o oo", "  oo  "), //
  /***************************************************/
  /************ BATCH 2 ******************************/
  RAYRAC_1(5, "  oooo ", " oo oo", "ooo ", " oooo", " ooo"), //
  /**  */
  CRANED_1(5, "  oooo ", " oo oo", "ooo ", " oooo", " oooo"), //
  /**  */
  BARCODE1(5, "  oooo ", " oo o ", "ooo o", " oo oo", " ooooo"), //
  // interesting
  BOTTLE_1(5, "  oooo ", " oo o ", "ooooo", " oo oo", " ooooo"), //
  // interesting
  PLANET_1(5, "  oooo ", " oo o ", "o ooo", "ooo oo", " ooooo"), //
  // very nice with 6
  CHEESE_1(6, "o oooo ", "ooo oo", "ooooo", "ooo oo", " oooo "), //
  /***************************************************/
  /************ BATCH 3 ******************************/
  // interesting
  SPIRAL_1(5, "oooooo ", "  oooo", "  ooo ", "  oooo", "   oooo"),
  // interesting
  SPIRAL_2(6, "oooooo  ", " ooooo", "  ooo ", "  oooo", "   ooooo"),
  // interesting
  SPIRAL_3(5, "ooooo  ", "  oooo", "  ooo ", "  oooo", "   oooo"),
  // interesting
  SPIRAL_4(5, "ooooo  ", "oooooo", "  ooo ", "  oooo", "   oooo"),
  // 7 solutions
  SHOTGUN1(4, "oooooo", "ooooo", "ooooo", "oo"),
  // interesting
  SHOTGUN2(5, "ooooooo", "oooooo", "oooooo", "oo"),
  // 6 solutions
  SHOTGUN3(6, "oooooooo", " ooooooo", " oooooo", " oo"),
  // one solution
  SHOTGUN4(6, "ooooooo", "ooooooo", "oooooo", "oo"),
  // 7 sol
  CORNER_2(5, "oooooo", "ooooo", "oooo", "oo", "o"),
  // many solutions
  CORNER_3(5, "oooooo", "ooooo", "oooo", "ooo", "o"),
  // many solutions
  CORNER_4(5, "oooooo", "ooooo", "oooo", "ooo", "oo"),
  // sufficient solutions
  CORNER_5(6, "ooooooo", "oooooo", "oooo", "ooo", "oo", "o"),
  // many solutions
  CORNER_6(6, "ooooooo", "ooooo", "oooo", "ooooo", "oo"),
  /***************************************************/
  /************ UNPUBLISHED **************************/
  // ---
  // many solutions
  CORNER_1(4, "oooooo", "ooooo", "ooo", "o"),
  // 12 solutions
  SPIRAL_6(6, " o     ", "ooooo  ", "oooooo", "  ooo ", "  oooo", "   oooo"), //
  // only 3 solutions
  SPIRAL_5(6, "ooooo  ", "oooooo", "  ooo ", "  oooo", "   oooo"), //
  // maybe not very exciting
  HOOK_A3(5, "  oooo ", " oo o ", "ooo ", " oo oo", " ooooo"), //
  // 6 solutions
  GRID_A6(5, "  oooo ", " oo oo", "o ooo", "ooo oo", " oooo "), //
  PYRAMID5(6, "   o   ", "  oooo ", " oooo", "ooooo", " oooo", " ooo"), //
  HOLE2(5, "oooo ", "ooooo", " oooo", " o  o", "ooooo"), //
  HOLE3(5, "oooo ", "ooooo", " o oo", " o  o", "ooooo"), //
  HOLE4a(5, "oooo ", "ooooo", "oo  o", " o  o", "ooooo"), // 18
  HOLE4c(5, " oooo", "ooooo", "oo  o", "oo  o", " oooo"), //
  HOLE6a(5, " oooo", "oo  o", "oo  o", "oo  o", "ooooo"), //
  HOLE6b(5, "oooooo", "oo  o", "oo  o", "oo  o", "ooooo"), //
  SHOE1a(5, "oooooo", "oo  o", "oo   ", "oo  o", "ooooo"), //
  SHOE1b(5, "oooooo", "oo  oo", "oo   ", "oo  oo", "ooooo"), //
  /** only 2 solutions */
  SHOE1c(5, "oooooo", "oo  oo", " o   ", "oo  oo", "ooooo"), //
  /** only 1 unique solution */
  PYRAMID1(5, "  ooo ", " oooo", "ooooo", " ooo", " oo"), //
  /** 2 solutions */
  PYRAMID2(5, "   o  ", "  ooo ", " oooo", "ooooo", " ooo", " oo"), //
  // ---
  MASKDIFF(6, "  o    ", " ooooo ", "o ooo o", "ooooooo", " ooooo "),
  /***************************************************/
  /************ BATCH 4 ******************************/
  MODERN_1(4, "  oo ", "oooo ", " ooo ", " ooo ", "oo oo"), //
  AUTOMOB1(4, "  oo  ", " ooooo", "oooooo", " o  o "), //
  AUTOMOB2(5, "  oo   ", "ooooooo", "ooooooo", " oo oo "), //
  AUTOMOB3(5, " oo    ", "ooooooo", "ooooooo", " oo oo "), //
  FACTORY1(4, " o     ", " oo    ", " oo    ", " ooooo ", "ooo ooo"), //
  FACTORY2(5, " o     ", " o   o ", "oo   o ", "oooooo ", "ooo ooo"), //
  NKAPPE_1(5, "  o    ", "  oo   ", "o ooo o", "ooooooo", " ooooo "), //
  TERRIER1(5, "  o    ", " ooo   ", "ooooooo", "  ooooo", "  o  oo", "  o   o"), //
  MASKEYES(5, "  o   ", " ooooo", "o oo o", "oooooo", " oooo "), //
  MODERN_2(5, "oo oo", "oooo ", " oooo", " ooo ", "ooooo"), //
  HOLGRAIL(5, " oo  ", "ooooo", " ooo ", "  o  ", " oooo", "ooooo"), //
  // BROTHER1(5, " ooo ", " ooo ", " ooo ", " o ", " oooo", "ooooo"), //
  SICHEL_1(5, "  ooo", " oooo", "ooo  ", "oo   ", " oo  ", "  oo ", "  oo "), //
  SICHAL_2(5, "  ooo", " ooo ", "ooo  ", "oo   ", " oo  ", " ooo ", " ooo "), //
  GIRLHAT1(5, "  o  ", " ooo ", "ooooo", "  ooo", "ooooo", " ooo "), //
  GIRLHAT2(5, " oo  ", " ooo ", "oo oo", "  ooo", "ooooo", " ooo "), //
  MICKEY_3(6, "oo  oo", "ooo oo", " oooo", " oooo", " o oo", "  oo  "), //
  FREIGHT1(6, "    o    ", "   ooo o ", "oo  o  oo", " oooooooo", "  oooooo "), //
  CHRISTMT(6, "  o   ", "  oo  ", " oooo ", "oooo  ", " oooo ", "oooooo", "  o   ", "  oo  "), //
  KERZENH1(6, "o o o o", "ooooooo", " ooooo ", " oooo  ", "  oo   "), //
  // ---
  // dont use:
  LETTERA2(5, " ooo ", "oo oo", "o   o", "ooooo", "o  oo", "o  oo"), //
  /***************************************************/
  /************ BATCH 5 ******************************/
  KEYLOCK1(4, "ooooooo", "o o  oo", "ooo  oo"), //
  SHOEBLK1(4, " ooo  ", "ooooo ", "o  ooo", "o  ooo"), //
  PRINTED1(4, " o   ", " oo  ", " oo o", "ooooo", "ooooo"), //
  LETTERA1(5, "ooooo", "oo  o", "oo  o", "ooooo", "o  oo", "o  oo"), //
  LETTER_C(5, " oooo ", "ooo oo", "o     ", "o     ", "oo  oo", " oooo "), //
  LETTER_G(5, "oooo ", "oo   ", "o    ", "o ooo", "o  oo", "ooooo"), //
  LETTER_I(5, "ooooo", "  oo ", "  oo ", "  oo ", " ooo ", "ooooo"), //
  LETTER_J(5, " oooo", "   oo", "   oo", "o   o", "oo oo", "ooooo"), //
  LETTER_M(5, "o   oo", "oooooo", "o oo o", "o    o", "o   oo", "o   oo"), //
  LETTER_P(5, "ooooo", "oo  o", "oo  o", "ooooo", "oo   ", "oo   "), //
  RINGRI_1(5, " ooooo", " o  oo", " o   o", " o  oo", "ooooo "), //
  RINGRI_2(5, " ooooo", " o  oo", " o   o", " o  oo", "oooooo"), //
  RINGRI_3(5, "ooooo ", "oo oo ", "o   o ", "o  oo ", "oooooo"), //
  RINGRI_4(5, "ooooo ", "oo oo ", "o   o ", "oo oo ", "oooooo"), //
  RINGRI_5(5, " ooo  ", "oo oo ", "o   o ", "oo oo ", "oooooo"), //
  CHAPEL_1(5, "oooo ", "oo oo", "o   o", "oo oo", "oo oo"), //
  CHAPEL_2(5, "  o  ", "oooo ", "oo oo", "o   o", "oo oo", "oo oo"), //
  ARROWHD1(5, "oo ooo  ", "oo   oo ", "oooooooo", "     oo ", "    oo  "), //
  FACTORY3(5, " oooo   ", " oooooo ", "oo o  oo", "oooo  oo"), //
  FACTORY4(5, "ooooo  ", "ooooo  ", "o  oooo", "o  oooo"), //
  AIRPLAN1(5, "   o  o", " oooooo", "ooooooo", "  oo  o", "   o   "), //
  CACTUS_1(5, "  o  ", "o o o", "o ooo", "ooooo", " ooo ", "  oo ", "  oo "), //
  CHAPEL_3(6, "  o   ", "ooooo ", "oo oo ", "o   oo", "oo ooo", "oo ooo"), //
  BATMAN_1(6, " o   o ", " oo oo ", " ooooo ", " o o oo", "oooooo ", " ooooo "), //
  LETTER_S(6, " oooo", "oo  o", "ooo  ", " oooo", "   oo", "ooooo"), //
  // ---
  ALIENFC1(5, "  o   ", " oooo ", "oo oo ", " ooooo", " oooo ", "   oo "), //
  PRALINE1(5, " oooo", " o oo", "ooooo", "o o o", "ooooo"), //
  PRALINE2(5, "ooooo", " o oo", "ooooo", "o o o", "ooooo"), //
  PRALINE3(5, "ooooo", "oooo ", "ooooo", "o o  ", "ooo  "), //
  SOMETRE1(5, "  o  ", " oooo", "  oo ", "ooooo", " ooo ", "  oo ", " ooo "), //
  UMBRELL1(5, "  oo  ", " oooo ", "oooooo", "o  o o", "   o  ", "   o  ", "  oo  "), //
  SKULLED1(5, "o  o ", "ooooo", "o o o", "ooooo", " ooo ", " ooo "), //
  SKULLED2(5, " oo  ", "oooo ", "oo o ", "ooooo", " ooo ", " oo  "), //
  ROOFED_1(5, "  o  ", "ooooo", " ooo ", "oooo ", "ooo  ", "ooo  "), //
  ROOFED_2(5, "  o  ", "ooooo", " ooo ", " ooo ", " oooo", "ooooo"), //
  HYDRANT5(5, "   o ", "  ooo", " oooo", "oooo ", " ooo ", "  ooo", "  oo "), //
  FLOWER_1(5, " oo  ", "oooo ", " ooo ", "  oo ", "  oo ", " oooo", "  oo "), //
  FLOWER_2(5, " o  ", "ooo ", "oooo", "ooo ", " ooo", "oooo", " oo "), //
  FLOWER_3(5, " oo ", "ooo ", "oooo", "ooo ", " ooo", "oooo", " oo "), //
  FLOWER_4(5, "  ooo ", " oooo ", "ooooo ", " ooooo", " oooo ", "  o   "), //
  SKULLED3(6, " ooo  ", "ooooo ", "oo ooo", "oo  oo", "ooooo ", "  oo  "), //
  SKULLED4(6, " ooo  ", "ooooo ", "oo ooo", "oo  oo", "ooooo ", " ooo  "), //
  HYDRANT1(6, "  oo ", " oooo", "  ooo", "ooooo", " ooo ", "  oo ", " oooo"), //
  HYDRANT2(6, " oooo ", " ooooo", " ooooo", "ooooo ", "  oo  ", "  ooo ", "  oo  "), //
  HYDRANT3(6, "  ooo ", " ooooo", " ooooo", "ooooo ", "  oo  ", "  ooo ", "  oo  "), //
  HYDRANT4(6, "   oo ", "  oooo", " ooooo", "ooooo ", "  oo  ", "  ooo ", "  oo  "), //
  // ---
  MODERN_3(5, "  ooo", "ooooo", "ooooo", " oooo", "oo   "), //
  MODERN_4(5, "  ooo", "ooooo", "ooooo", " oooo", " o  o"), //
  MODERN_5(6, "  ooo  ", "ooooo o", "ooooooo", " ooooo ", " o  o  "), //
  MOUSEGT1(6, "oo    ", "oo oo ", " oooo ", " ooo  ", "ooooo ", "oo ooo"), //
  TOWERBR1(6, "  o   o  ", "  oo  o  ", " ooo ooo ", "ooooooooo", " oo   oo "), //
  HELMET_1(6, " ooo ", "ooooo", "oo oo", "oo oo", " oooo", "oo  o"), //
  ;

  private final int use;
  private final UbongoBoard ubongoBoard;

  private UbongoBoards(int use, String... strings) {
    this.use = use;
    ubongoBoard = UbongoBoard.of(strings);
  }

  public UbongoBoard board() {
    return ubongoBoard;
  }

  public List<List<UbongoEntry>> solve() {
    return ubongoBoard.filter0(use);
  }
}
