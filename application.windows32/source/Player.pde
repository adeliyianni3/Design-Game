class Player {
  String name;
  color myColor;
  ArrayList<Grid> visible = new ArrayList<Grid>();
  public Player(String name, color col) {
    this.name = name;
    myColor = col;
  }
  String myName() {
    return name;
  }
  color getColor() {
    return myColor;
  }
  void addObstacle(Grid o) {
    visible.add(o);
  }
  void removeObstacle(Grid o) {
    visible.remove(o);
  }
  boolean contains(Grid o) {
    return visible.contains(o);
  }
  void obsClear() {
    visible.clear();
  }
}