// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.List;

/* package */ enum UbongoBoards {
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
