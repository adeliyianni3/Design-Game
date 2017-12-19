class Grid {
  int x;
  int y;
  //boolean activate;
  int type; //0 is ok, 1 is obstacle, 2 is collectible, -1 is already used
  int num;
  int size;
  PImage col;
  
  public Grid(int number, int xCoor, int yCoor, int type2, int size2) {
   // activate = false;
    x = xCoor;
    size = size2;
    y = yCoor;
    type = type2;
    num = number;
    col = blank;
  }
  void setType(int newType) {
    type = newType;
  }
  boolean overRect(float xNow, float yNow)  {
    if (xNow >= x && xNow <= x+size && 
        yNow >= y && yNow <= y+size) {
        return true;
      } else {
        return false;
    }
  }
  int getX() {
    return x;
  }
  int getY() {
    return y;
  }
  int getSize() {
    return size;
  }
  void drawMe() {
    stroke(0);
    noFill();
    rect(x, y, size, size);
    image(col, x, y, size, size);
  }
  void setColor(PImage c) {
    col = c;
  }
  PImage getColor() {
    return col;
  }
  int getPlace() {
    return num;
  }
  int getType() {
    return type;
  }
}
    
  