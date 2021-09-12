package Game_2048;

import javafx.util.Pair;
import sun.lwawt.macosx.CSystemTray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

enum Direction {
    UP(0), DOWN(1), LEFT(2), RIGHT(3), NONE(4);
    int code;

    Direction(int code) {
        this.code = code;
    }

    static Direction getEnumFromNumber(int num){
        return Arrays.stream(Direction.values()).filter( direction -> direction.code == num).findFirst().orElse(null);
    }
}

class ActionParameter{
    Move move;

    ActionParameter(){

    }

    ActionParameter(Move move){
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}



class Move {
    Direction direction;

    public Move(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}


class Tile {
    int value;
    int color;

    public Tile(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}


interface BoardGame {
    int defaultRowSize = 4;
    int defaultColSize = 4;

    void resetBoard();

    boolean move(Move move);

    void printBoard();

    boolean isWon();
}

class Game_2048 implements BoardGame{
    private Tile[][] board;
    private int row = defaultRowSize;
    private int col = defaultColSize;
    private final int winningCount = 2048;
    private int maxGameCount = 2;

    Game_2048(){
        resetBoard();
    }

    Game_2048(int rows, int cols){
        this.row = rows;
        this.col = cols;
        resetBoard();
    }

    @Override
    public void resetBoard(){
        board = new Tile[row][col];
        for(int i =0;i<row;i++){
            for(int j=0;j<col;j++){
               board[i][j] = new Tile(0);
            }
        }
        this.board[row-1][0].value = 2;
    }

    /*

    0 2 2 4 8 -> 0 0 4 4 8
    0 2 2 0 0 -> 0 0 0 0 4
    2 0 0 0 4 -> 0 0 0 2 4

     */
    @Override
    public boolean move(Move move) {
        switch (move.getDirection()){
            case UP:
                for(int j=0 ; j<this.col ; j++){
                    int mergeIndex = 0;
                    for(int i = 1 ; i < this.row ; i++){
                        if(board[i][j].value == 0) continue;
                        if(board[i][j].value == board[mergeIndex][j].value){
                            board[mergeIndex][j].value = board[mergeIndex][j].value * 2;
                            this.maxGameCount = Math.max(this.maxGameCount, board[mergeIndex][j].value);
                            board[i][j].value = 0;
                            mergeIndex++;
                        }
                        else{
                            if(board[mergeIndex][j].value != 0)
                                mergeIndex++;
                            board[mergeIndex][j].value = board[i][j].value;
                            if(i!=mergeIndex)
                                board[i][j].value = 0;
                        }
                    }
                }
                break;
            case DOWN:
                for(int j=0 ; j<this.col ; j++){
                    int mergeIndex = this.row-1;
                    for(int i = this.row-2 ; i >= 0 ; i--){
                        if(board[i][j].value == 0) continue;
                        if(board[i][j].value == board[mergeIndex][j].value){
                            board[mergeIndex][j].value = board[mergeIndex][j].value * 2;
                            this.maxGameCount = Math.max(this.maxGameCount, board[mergeIndex][j].value);
                            board[i][j].value = 0;
                            mergeIndex--;
                        }
                        else{
                            if(board[mergeIndex][j].value != 0)
                                mergeIndex--;
                            board[mergeIndex][j].value = board[i][j].value;
                            if(i!=mergeIndex)
                                board[i][j].value = 0;
                        }
                    }
                }
                break;
            case LEFT:
                for(int i=0 ; i<this.row ; i++){
                    int mergeIndex = 0;
                    for(int j = 1 ; j < this.col ; j++){
                        if(board[i][j].value == 0) continue;
                        if(board[i][j].value == board[i][mergeIndex].value){
                            board[i][mergeIndex].value = board[i][mergeIndex].value * 2;
                            this.maxGameCount = Math.max(this.maxGameCount, board[mergeIndex][j].value);
                            board[i][j].value = 0;
                            mergeIndex++;
                        }
                        else{
                            if(board[i][mergeIndex].value != 0)
                                mergeIndex++;
                            board[i][mergeIndex].value = board[i][j].value;
                            if(j!=mergeIndex)
                                board[i][j].value = 0;
                        }
                    }
                }
                break;
            case RIGHT:
                for(int i=0 ; i<this.row ; i++){
                    int mergeIndex = this.col - 1;
                    for(int j = this.col - 2 ; j >=0 ; j--){
                        if(board[i][j].value == 0) continue;
                        if(board[i][j].value == board[i][mergeIndex].value){
                            board[i][mergeIndex].value = board[i][mergeIndex].value * 2;
                            this.maxGameCount = Math.max(this.maxGameCount, board[mergeIndex][j].value);
                            board[i][j].value = 0;
                            mergeIndex--;
                        }
                        else{
                            if(board[i][mergeIndex].value != 0)
                                mergeIndex--;
                            board[i][mergeIndex].value = board[i][j].value;
                            if(j!=mergeIndex)
                                board[i][j].value = 0;
                        }
                    }
                }
                break;
        }

        return this.fillRandomAvailableSpotWithTwo();
    }

    private boolean fillRandomAvailableSpotWithTwo(){
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        for(int i=0 ; i<this.row ; i++){
            for(int j = 0; j <this.col ; j++){
                if(board[i][j].value == 0) pairs.add(new Pair<>(i, j));
            }
        }
        if(pairs.isEmpty())
            return false;
        int randomPairIndex = new Random().nextInt(pairs.size());
        this.board[pairs.get(randomPairIndex).getKey()][pairs.get(randomPairIndex).getValue()].value = 2;
        return true;
    }

    @Override
    public void printBoard() {
        for(int i=0 ; i<this.row ; i++){
            for(int j = 0; j <this.col ; j++){
               System.out.print(this.board[i][j].value + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public boolean isWon() {
    if(this.winningCount == this.maxGameCount)
        return true;
        return false;
    }

}

class GameManager {

    public void resetGame(BoardGame game){
        game.resetBoard();
    }

    public boolean move(ActionParameter actionParameter, BoardGame game){
        return game.move(actionParameter.getMove());
    }

    public void printBoard(BoardGame game) {
       game.printBoard();
    }

    public boolean isWon(BoardGame game) {
        return game.isWon();
    }
}

class MainClass {

    public static void main(String[] args) throws InterruptedException {
        BoardGame game_2048 = new Game_2048(6,6);
        GameManager gameManager = new GameManager();
        gameManager.printBoard(game_2048);
        ActionParameter actionParameter = new ActionParameter(new Move(Direction.NONE));
        boolean isGameRemaining = true;
        while(isGameRemaining){
            actionParameter.getMove().setDirection(Direction.getEnumFromNumber(new Random().nextInt(4)));
            System.out.println("Moving Direction: " + actionParameter.getMove().getDirection().name());
            isGameRemaining = gameManager.move(actionParameter, game_2048);
            gameManager.printBoard(game_2048);
            if(gameManager.isWon(game_2048)){
                System.out.println("Congratulation on winning game!");
                break;
            }
//            Thread.sleep(1000);
        }
    }
}
