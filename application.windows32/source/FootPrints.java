import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import beads.*; 
import org.jaudiolibs.beads.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FootPrints extends PApplet {





//sound
AudioContext ac;
SamplePlayer song;
Gain gain;

//grid stuff
ArrayList<Grid> squares = new ArrayList<Grid>();
ArrayList<Grid> tempPath = new ArrayList<Grid>();
ArrayList<Grid> path = new ArrayList<Grid>();
Grid currGrid;
Grid endGrid;
Grid startGrid;

//players
Player player1;
Player player2;
Player currPlayer;

boolean draw;

//images
PImage map;

String footType;

PImage feet;

PImage left;
PImage right;
PImage up;
PImage down;

PImage upFoot;
PImage downFoot;
PImage leftFoot;
PImage rightFoot;
PImage upShoe;
PImage downShoe;
PImage leftShoe;
PImage rightShoe;
PImage counterImage;
  
//Until someone makes these visible they are out
/*PImage upHor;
PImage downHor;
PImage leftHor;
PImage rightHor;*/

PImage endTile;
PImage blank;

PImage desk;

PImage play1;
PImage play2;
PImage gameOver;

//aspects of game
int numVisObs; //number of visible obstacles for player 2
int counter; //number of obstacles player1 set that turn
int playerSteps; //number of steps given for player2
boolean switching = true;
Mode mode = Mode.STARTSCREEN;

//controls
ControlP5 cp5;
Button play;
Button start;
Button pauseMusic;
Textlabel counterLabel;
Slider pickSteps; 
int numOfTotalSteps;

public void setup() {
  
  ac = new AudioContext();
  cp5 = new ControlP5(this);
  //tiles
  upFoot = loadImage("upF.png");
  downFoot = loadImage("downF.png");
  leftFoot = loadImage("leftF.png");
  rightFoot = loadImage("rightF.png");
  
  upShoe = loadImage("upS.png");
  downShoe = loadImage("downS.png");
  leftShoe = loadImage("leftS.png");
  rightShoe = loadImage("rightS.png");
  counterImage = loadImage("player2Counter.png");
  
/*  upHor = loadImage("upH.png");
  downHor = loadImage("downH.png");
  leftHor = loadImage("leftH.png");
  rightHor = loadImage("rightH.png");*/
  
  blank = loadImage("blank.png");
  endTile = loadImage("end.png");
  feet = blank;
  
  counterLabel = new Textlabel(cp5, "x", displayWidth-288, 300);
  counterLabel.setColor(0);
  
  
  play = new Button(cp5, "GO!");
  play.setPosition(displayWidth-400,400);
  play.setImage(loadImage("go.png")).updateSize();
  play.hide();

  start = new Button(cp5, "Start Game");
  start.setPosition(displayWidth/2-100,displayHeight/2-30);
  start.setImage(loadImage("play.png")).updateSize();
  start.hide();
  pauseMusic= new Button(cp5, "pause music");
  pauseMusic.setPosition(displayWidth-200,0);
  pauseMusic.setImage(loadImage("pausemusic.png")).updateSize();
  
  pickSteps = new Slider(cp5, "Steps Player Two can Take this Round");
  pickSteps.setPosition(displayWidth-400, 300);
  pickSteps.setMin(1);
  pickSteps.hide();

  map = loadImage("Map.jpg");
  desk = loadImage("desk.png");
  play1 = loadImage("player1.png");
  play2 = loadImage("player2.png");
  gameOver = loadImage("gameover.png");
  initiateStart();
  
  song = getSamplePlayer("song.wav");
  song.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
  gain = new Gain(ac, 1);
  gain.addInput(song);
  ac.out.addInput(gain);
  ac.start();
}
public void setObstacles() {
  for (int i = 0; i < 30; i++) {
    int temp = (int)random(1,178);
    while (squares.get(temp).getType()!=0) {
      temp = (int)random(1,178);
    }
    squares.get(temp).setType(1);
    player1.addObstacle(squares.get(temp));
  }
}
public void startPoint() {
  currGrid = squares.get(0);
  currGrid.setType(-1);
  currGrid.setColor(rightFoot);
  startGrid = currGrid;
}
public void endPoint() {
  Grid temp = squares.get(squares.size()-1);
  temp.setColor(endTile);
  endGrid = temp;
}
public void draw() {
 if (mode == Mode.PLAYER2) {
   image(counterImage,displayWidth-295, 290);
   counterLabel.setText("Player Moves Left: "+playerSteps);
   counterLabel.draw();
    if(draw) {
      drawOnce();
    }
  }
 if(mode == Mode.SWITCH){
   image(desk,0,0,displayWidth, displayHeight);
 }
 if (mode == Mode.PLAYER1) {
    if(draw) {
      drawOnce();
    }
  }
}
public void initiateEnd() {
  mode = Mode.END;
  image(desk,0,0,displayWidth,displayHeight);
  drawOnce();
  image(gameOver,displayWidth-400, 100);
  currPlayer = player1;
  drawOnce();
}
public void drawOnce() {
  image(map, 100, 50, 15*50, 12*50);
  for( Grid a: squares) {
    if (currPlayer.contains(a)) {
      fill(0);
      rect(a.getX(), a.getY(), a.getSize(), a.getSize());
    } else {
      a.drawMe();
    }
  }
  draw = false;
  fill(0,255,0);
  rect(currGrid.getX() + currGrid.getSize()/2-10, currGrid.getY() + currGrid.getSize()/2-10, 20, 20);
}
public void initiateStart() {
  background(0);
  image(loadImage("start.png"),0,0,displayWidth, displayHeight);
  start.show();
}
public void initiatePlayer1() {
    numVisObs = 5;
    counter = 0;
    numOfTotalSteps = 60;
    pickSteps.setMax(numOfTotalSteps);
    pickSteps.setValue(1);
    pickSteps.snapToTickMarks(true);
    pickSteps.setNumberOfTickMarks(numOfTotalSteps);
    footType = "foot";
}
public void mousePressed() {
  if (pauseMusic.isPressed()) {
    if(song.isPaused()) {
      song.pause(false);
    } else {
      song.pause(true);
    }
  }
  if(start.isPressed()) {
    mode = Mode.PLAYER1;
    player1 = new Player("Player 1", color(0,0,255));
    player2 = new Player("Player 2", color(0,0,50));
    if(squares.size()> 0) {
      squares.clear();
    }
    for (int j = 2; j < 17; j++) {
      for (int i = 1; i< 13; i++) {
        int place = squares.size();
        squares.add(new Grid(place, 50*j, 50*i, 0, 50));
      }
    }
    startPoint();
    endPoint();
    path.clear();
    path.add(currGrid);
    currPlayer = player1;
    draw = true;
    start.hide();
    image(desk,0,0,displayWidth, displayHeight);
    image(play1,displayWidth-400, 100);
    setObstacles();
    play.show();
    pickSteps.show();
    initiatePlayer1();
  }
  if (mode == Mode.PLAYER2) {
      alterPath();
  }
  else if (mode == Mode.PLAYER1) {
      int temp = ((mouseY-50)/50)+(12*((mouseX-100)/50));
        if(temp < squares.size() && temp > -1) {
          Grid a = squares.get(temp);
          if(a.overRect(mouseX, mouseY)) {
            if (a.getType()==1) {
              if (counter < numVisObs) {
                if (!player2.contains(a)){
                  player2.addObstacle(a);
                  counter++;
                  stroke(0,255,0);
                  noFill();
                  rect(a.getX(), a.getY(), a.getSize(), a.getSize());
                } else {
                  player2.removeObstacle(a);
                  counter--;
                  stroke(0,0,0);
                  noFill();
                  rect(a.getX(), a.getY(), a.getSize(), a.getSize());
                }
              }  else {
                if (player2.contains(a)){
                  player2.removeObstacle(a);
                  counter--;
                  stroke(0,0,0);
                  noFill();
                  rect(a.getX(), a.getY(), a.getSize(), a.getSize());
              }
            }
          }
        }
      }
    if (play.isPressed()) {
      draw= true;
      mode = Mode.SWITCH;
      play.hide();
      pickSteps.hide();
    }
  } else if (mode == Mode.END) {
    mode = Mode.STARTSCREEN;
    initiateStart();
  } else if(mode == Mode.SWITCH) {
    if (currPlayer.equals(player1)){
      changeToPlayer2();
    } else {
      changeToPlayer1();
  }
  }
  
}  
public void changeToPlayer2() {
  draw = true;
  mode = Mode.PLAYER2;
  image(desk,0,0,displayWidth, displayHeight);
  currPlayer = player2;
  image(play2,displayWidth-400, 100);
  playerSteps = (int) pickSteps.getValue();
  numOfTotalSteps = numOfTotalSteps - playerSteps;
  if (footType.equals("foot")) {
    up = upFoot;
    down = downFoot;
    left = leftFoot;
    right = rightFoot;
  } else if (footType.equals("shoe")) {
    up = upShoe;
    down = downShoe;
    left = leftShoe;
    right = rightShoe;
  } /*else {
    up = upHor;
    down = downHor;
    left = leftHor;
    right = rightHor;
  }*/
}
public void swapFootPrint() {
  if (footType == "foot") {
    footType = "shoe";
  } else if (footType == "shoe") {
    /*footType = "horse";
  } else {*/
    footType = "foot";
  }
}

public void alterPath() {
  int temp = ((mouseY-50)/50)+(12*((mouseX-100)/50));
  if(temp < squares.size() && temp > -1) {
    Grid a = squares.get(temp);
    if(a.overRect(mouseX, mouseY)) {
      int v = a.getPlace() - currGrid.getPlace();
      if (v == 1|| v==-1|| v==12|| v== -12 || v == 0) {
        if (playerSteps > 0) {
          if(v==-1) {
            feet = up;
          } else if (v==1) {
            feet = down;
          } else if (v==12) {
            feet = right;
          } else {
            feet = left;
          }
          if (a.getType() != 1) {
            if (a.getType() != -1) {
                a.setType(-1);
                tempPath.add(a);
                playerSteps--;
                if (!currGrid.equals(endGrid)) {
                  currGrid.setColor(feet);
                }
                currGrid = tempPath.get(tempPath.size()-1);
                drawOnce();
                if (a.equals(endGrid)) {
                  initiateEnd();
                }
            } else {
              if (tempPath.size()-1 > 0 && tempPath.get(tempPath.size()-1).equals(a)) {
                tempPath.remove(a);
                playerSteps++;
                if (!a.equals(endGrid)) {
                  a.setColor(blank);
                }
                if (tempPath.size()-1 < 0) {
                  currGrid = startGrid;
                } else {
                  currGrid = tempPath.get(tempPath.size()-1);
                }
                drawOnce();
                fill(0,255,0);
                rect(currGrid.getX() + currGrid.getSize()/2 - 10, currGrid.getY() + currGrid.getSize()/2 - 10, 20, 20);
              } else {
                tempPath.add(a);
                playerSteps--;
                if (!currGrid.equals(endGrid)) {
                  currGrid.setColor(feet);
                }
                currGrid = tempPath.get(tempPath.size()-1);
                drawOnce();
              }
            }
          } else {
            if (numOfTotalSteps == 0) {
              initiateEnd();
            } else {
              currGrid = squares.get(0);
              swapFootPrint();
              mode = Mode.SWITCH;
            }
          }
        }
        if (playerSteps == 0) {
          if (numOfTotalSteps == 0) {
            initiateEnd();
          } else {
            mode = Mode.SWITCH;
          }
        }
      }
    }
  }
}
public void changeToPlayer1() {
  mode = Mode.PLAYER1;
  currPlayer = player1;
  player2.obsClear();
  play.show();
  pickSteps.setMax(numOfTotalSteps);
  pickSteps.setValue(1);
  pickSteps.snapToTickMarks(true);
  pickSteps.setNumberOfTickMarks(numOfTotalSteps);
  pickSteps.show();
  counter = 0;
  path.addAll(tempPath);
  tempPath.clear();
  startGrid = currGrid;
  drawOnce();
  image(play1,displayWidth-400, 100);
}
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
  public void setType(int newType) {
    type = newType;
  }
  public boolean overRect(float xNow, float yNow)  {
    if (xNow >= x && xNow <= x+size && 
        yNow >= y && yNow <= y+size) {
        return true;
      } else {
        return false;
    }
  }
  public int getX() {
    return x;
  }
  public int getY() {
    return y;
  }
  public int getSize() {
    return size;
  }
  public void drawMe() {
    stroke(0);
    noFill();
    rect(x, y, size, size);
    image(col, x, y, size, size);
  }
  public void setColor(PImage c) {
    col = c;
  }
  public PImage getColor() {
    return col;
  }
  public int getPlace() {
    return num;
  }
  public int getType() {
    return type;
  }
}
    
  
public enum Mode {
    STARTSCREEN,
    PLAYER1,
    SWITCH,
    PLAYER2,
    END;
}
class Player {
  String name;
  int myColor;
  ArrayList<Grid> visible = new ArrayList<Grid>();
  public Player(String name, int col) {
    this.name = name;
    myColor = col;
  }
  public String myName() {
    return name;
  }
  public int getColor() {
    return myColor;
  }
  public void addObstacle(Grid o) {
    visible.add(o);
  }
  public void removeObstacle(Grid o) {
    visible.remove(o);
  }
  public boolean contains(Grid o) {
    return visible.contains(o);
  }
  public void obsClear() {
    visible.clear();
  }
}
//helper functions

public Sample getSample(String fileName) {
 return SampleManager.sample(dataPath(fileName)); 
}

public SamplePlayer getSamplePlayer(String fileName, Boolean killOnEnd) {
  SamplePlayer player = null;
  try {
    player = new SamplePlayer(ac, getSample(fileName));
    player.setKillOnEnd(killOnEnd);
    player.setName(fileName);
  }
  catch(Exception e) {
    println("Exception while attempting to load sample: " + fileName);
    e.printStackTrace();
    exit();
  }
  
  return player;
}

public SamplePlayer getSamplePlayer(String fileName) {
  return getSamplePlayer(fileName, false);
}
  public void settings() {  size(displayWidth, displayHeight); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FootPrints" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
