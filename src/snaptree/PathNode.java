package snaptree;

/**
 */
class PathNode {
  public PathNode(BPage page, int position) {
    this.page = page;
    this.position = position;
  }

  public BPage page() {
    return page;
  }

  public int position() {
    return position;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("(" + page  + ":" + position() + ")");
    return sb.toString();
  }
  private BPage page;
  private int position;

}

