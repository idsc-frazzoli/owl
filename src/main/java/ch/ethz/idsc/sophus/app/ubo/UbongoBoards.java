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
  /************ BATCH 2 ******************************/
  // interesting
  SPIRAL_1(5, "oooooo ", "  oooo", "  ooo ", "  oooo", "   oooo"),
  // interesting
  SPIRAL_2(6, "oooooo  ", " ooooo", "  ooo ", "  oooo", "   ooooo"),
  // interesting
  SPIRAL_3(5, "ooooo  ", "  oooo", "  ooo ", "  oooo", "   oooo"),
  // interesting
  SPIRAL_4(5, "ooooo  ", "oooooo", "  ooo ", "  oooo", "   oooo"),
  // 12 solutions
  SPIRAL_6(6, " o     ", "ooooo  ", "oooooo", "  ooo ", "  oooo", "   oooo"), //
  // 7 solutions
  SHOTGUN1(4, "oooooo", "ooooo", "ooooo", "oo"),
  // interesting
  SHOTGUN2(5, "ooooooo", "oooooo", "oooooo", "oo"),
  // 6 solutions
  SHOTGUN3(6, "oooooooo", " ooooooo", " oooooo", " oo"),
  // one solution
  SHOTGUN4(6, "ooooooo", "ooooooo", "oooooo", "oo"),
  // many solutions
  CORNER_1(4, "oooooo", "ooooo", "ooo", "o"),
  // 7 sol
  CORNER_2(5, "oooooo", "ooooo", "oooo", "oo", "o"),
  // many solutions
  CORNER_3(5, "oooooo", "ooooo", "oooo", "ooo", "o"),
  // many solutions
  CORNER_4(5, "oooooo", "ooooo", "oooo", "ooo", "oo"),
  // ...
  CORNER_5(6, "ooooooo", "oooooo", "oooo", "ooo", "oo", "o"),
  // ...
  CORNER_6(6, "ooooooo", "ooooo", "oooo", "ooooo", "oo"),
  // ---
  // only 3 solutions
  SPIRAL_5(6, "ooooo  ", "oooooo", "  ooo ", "  oooo", "   oooo"), //
  // maybe not very exciting
  HOOK_A3(5, "  oooo ", " oo o ", "ooo ", " oo oo", " ooooo"), //
  // 6 solutions
  GRID_A6(5, "  oooo ", " oo oo", "o ooo", "ooo oo", " oooo "), //
  // TODO mickey produces 12 solutions
  MICKEY_3(6, "oo  oo", "ooo oo", " oooo", " oooo", " o oo", "  oo  "), //
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
  ;

  private final int use;
  private final UbongoBoard ubongoBoard;

  private UbongoBoards(int use, String... strings) {
    this.use = use;
    ubongoBoard = new UbongoBoard(strings);
  }

  public UbongoBoard board() {
    return ubongoBoard;
  }

  public List<List<UbongoEntry>> solve() {
    return ubongoBoard.filter0(use);
  }
}
