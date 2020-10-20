// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.List;

/* package */ enum UbongoBoards {
  STANDARD(3, "oooo", "oooo", "ooo", " o"), //
  CARPET1(5, "o oo ", "ooooo", " oooo", " oooo", "ooooo"), //
  CARPET2(5, "oooo ", "ooooo", " oooo", " oooo", "ooooo"), //
  HOLE1(5, "oooo ", "ooooo", " oooo", " oo o", "ooooo"), //
  HOLE2(5, "oooo ", "ooooo", " oooo", " o  o", "ooooo"), //
  HOLE3(5, "oooo ", "ooooo", " o oo", " o  o", "ooooo"), //
  HOLE4a(5, "oooo ", "ooooo", "oo  o", " o  o", "ooooo"), //
  HOLE4b(5, "oooo ", "ooooo", "oo  o", "oo  o", "ooooo"), //
  HOLE4c(5, " oooo", "ooooo", "oo  o", "oo  o", " oooo"), //
  HOLE6a(5, " oooo", "oo  o", "oo  o", "oo  o", "ooooo"), //
  HOLE6b(5, "oooooo", "oo  o", "oo  o", "oo  o", "ooooo"), //
  SHOE1a(5, "oooooo", "oo  o", "oo   ", "oo  o", "ooooo"), //
  SHOE1b(5, "oooooo", "oo  oo", "oo   ", "oo  oo", "ooooo"), //
  /** only 2 solutions */
  SHOE1c(5, "oooooo", "oo  oo", " o   ", "oo  oo", "ooooo"), //
  SHOE1d(5, "oooooo", "oo  oo", "o   ", "oo  oo", "ooooo"), //
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
