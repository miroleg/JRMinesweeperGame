package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private int countFlags;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private boolean isGameStopped = false; 
    private int countClosedTiles = SIDE * SIDE;
    private int  score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        boolean isMine;
        //isGameStopped = false;
        for (int y = 0; y < SIDE; y++) 
            for (int x = 0; x < SIDE; x++)
                setCellValue(x, y, "");
            
        
        
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                isMine = getRandomNumber(10) == 1;
                if (isMine)
                    countMinesOnField++;
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
        
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    
    private void countMineNeighbors() {
         for (int x = 0; x < SIDE; x++)
            for (int y = 0; y < SIDE; y++) {
               if (!gameField[y][x].isMine) {  // no mines
                  List<GameObject> listNeighbors = getNeighbors(gameField[y][x]);
                  gameField[y][x].countMineNeighbors = 0;
                  for (int i = 0; i < listNeighbors.size(); i++) 
                      if (listNeighbors.get(i).isMine) 
                        gameField[y][x].countMineNeighbors++;
               }
            }    
    }
    
    private void openTile(int x, int y) {
        if (!gameField[y][x].isOpen && 
            !gameField[y][x].isFlag &&
            !isGameStopped)
        {    
            
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE); 
                gameOver();                                 //   КОНЕЦ ИГРЫ
            }    
            else {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors); 
                gameField[y][x].isOpen = true;   
                score =  score + 5;
                setScore(score);
                countClosedTiles--; 
                setCellColor(x, y, Color.BLUE);
                if (countClosedTiles == countMinesOnField)
                    win();                                      // WIN !!!
                if (gameField[y][x].countMineNeighbors == 0) {
                    setCellValue(x, y, "");
                    // gameField[y][x].isOpen = true;
                    List<GameObject> listNeighbors = getNeighbors(gameField[y][x]);
                    for (int i = 0; i < listNeighbors.size(); i++) 
                        if (!listNeighbors.get(i).isOpen) // если ячейка не открыта
                            openTile(listNeighbors.get(i).x, listNeighbors.get(i).y);
                }
            } 
        }    
    }
    
    private void markTile(int x, int y) {
        if (!(gameField[y][x].isOpen) && !((countFlags == 0) 
            && !gameField[y][x].isFlag )
            && !isGameStopped) // ячейка не открыта и не флаг и не СТОП
        {
           if(!gameField[y][x].isFlag )
           {
                gameField[y][x].isFlag = true;
                countFlags--;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);
           }
           else
           {
               gameField[y][x].isFlag =  false; 
               countFlags++;
               setCellValue(x, y, "");
               setCellColor(x, y, Color.ORANGE);
           }
        }
    }
    
    public void onMouseLeftClick(int x, int y) {
          // родительского класса Game.
        if (isGameStopped){
            restart();
            return;
        }
        openTile(x , y);
    }

    public void onMouseRightClick(int x, int y) {
        markTile(x , y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.NONE, "GAME OVER", Color.GREEN, 75);
    }
    
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.NONE, "WIN !", Color.RED, 75);
    }

    

    private void restart() {
        isGameStopped = false; //исходные значения полей 
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore( score);
        createGame();
       // showMessageDialog(Color.NONE, "ПЕРЕСТРОЙКА !!!", Color.BLUERED, 75);
    }
    
}