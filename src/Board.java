
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author victoralonso
 */
public class Board extends javax.swing.JPanel {
    
    private Snake snake;
    private Snake snakeTwo;
    private List<Snake> snakes;
    private Food food;
    //private Food specialFood;
    private Timer snakeTimer;
    private Timer specialFoodTimer;
    private int specialFoodCounter;
    private int deltaTime;
    public  int numRows = 15;
    public  int numCols = 15;
    private final int INITIAL_DELTA_TIME = 300;
    private ScoreBoard scoreBoard;
    private ScoreBoard scoreBoardTwo;
    private List<Obstacle> obstacles;
    private int numberOfPlayers;
    
    /**
     * Creates new form Board
     */
    public Board(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        initComponents();
        snakes = new ArrayList<Snake>();
        if (numberOfPlayers == 2) {
            addSecondPlayer();
        }
        myInit();
        
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }
    
    public void setScoreBoardTwo(ScoreBoard scoreBoard) {
        this.scoreBoardTwo = scoreBoard;
    }
    
    public Food getFood() {
        return food;
    }
    
    private void myInit() {
        
        this.setBackground(Color.GREEN);
        obstacles = null;
        snake = new Snake(5, 5, 3, this, 1);
        snakes.add(snake);
        
        createFoodTimer();
        food = new Food(snakes, this);
        checkFood();
        
        deltaTime = INITIAL_DELTA_TIME;
       
        
        createTimer();
        
        MyKeyAdapter keyAdepter = new MyKeyAdapter();
        addKeyListener(keyAdepter);
        setFocusable(true);
       
        
    }
    
    private void addSecondPlayer() {
        snakeTwo = new Snake(5, 10, 3, this, 2);
        snakes.add(snakeTwo);
    }
    
    private void createFoodTimer() {
        specialFoodTimer = new Timer(1000,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               
                specialFoodCounter++;
                

                if (specialFoodCounter == 4) {
                    food.blink();
                }
               
                if (specialFoodCounter == 6) {
                    
                    solidateFood(food.getPosition());
                    food.generateFood(snakes);
                    stopFoodTimer(true);
                }
                
                
            }

           

        });
    }
    
    private void solidateFood(Node position) {
        
        if (obstacles == null) {
            
            obstacles = new ArrayList<Obstacle>();
            obstacles.add(new Obstacle(position, this));
            
        } else {
            
            obstacles.add(new Obstacle(position));
            
        }
        
    }
    
    public Board(int numRows, int numCols) {
        
        initComponents();
        myInit();
        this.numRows = numRows;
        this.numCols = numCols;
                
    }
    
    public void checkColideFood(Snake snake) {
        
        Node node = food.getPosition();
        
        int row = snake.getNextRow();
        int col = snake.getNextCol();
        
        if (row == node.getRow() && col == node.getCol()) {
            
            foodEat(snake);
            
        }
        
    }
    
    public boolean colideObstacles(int row, int col) {
        
        if (obstacles != null) {
            
            for (Obstacle obstacle : obstacles) {
                Node obstaclePosition = obstacle.getPosition();
                
                if (obstaclePosition.getRow() == row && obstaclePosition.getCol() == col) {
                    return true;
                }
                
            }
            
        }
        
        return false;
        
    }
    
    private void foodEat(Snake snake) {
        stopFoodTimer(false);
        int score;
        
        if(deltaTime > 100) {
            deltaTime -= 20;
        }
        if (food.getIsSpecial()) {
            snake.addRemainingNodes(4);
            score = 50;
        } else {
            snake.addRemainingNodes(1);
            score = 10;
        }
        
        if (snake.getNumber() == 1 ) {
            scoreBoard.incrementScore(score);
        } else {
            scoreBoardTwo.incrementScore(score);
        }
        
        //createTimer();
        
        food.generateFood(snakes);
        checkFood();
    }
    
    private void stopFoodTimer(boolean created) {
        if (specialFoodTimer != null) {
           specialFoodTimer.stop();
           food.stopBlink();
       }
       if(created) {
           checkFood();
       }
    }
    
    private void checkFood() {
        
        if (food.getIsSpecial()) {
            startFoodTimer();
        }
    }
    
    private void startFoodTimer() {
        
        specialFoodCounter = 0;
        specialFoodTimer.start();
        
        
    }
    
    public int squareWidth() {
        return (int) Math.floor(getWidth()/numCols);
    }
    
    public int squareHeight() {
        return (int) Math.floor(getHeight()/numRows);
    }
    
    public boolean colideOtherSnake(int row, int col, int n) {
        if (numberOfPlayers == 2) {
        List<Node> nodes = new ArrayList<Node>();
        
        if (n == 1) {
            
            nodes = snakeTwo.getBody();
             
        } else {
            
            nodes = snake.getBody();
            
        }
        
        for (Node node: nodes) {
            if (node.getCol() == col && node.getRow() == row) {
                return true;
            }
        }
        
        return false;
        } else {
            
            return false;
            
        }
        
    }
    
    public void checkGameOver() {
        boolean losed = false;
        
        for (int i = 0; i < snakes.size() && !losed; i++) {
            Snake snake = snakes.get(i);
            losed = snake.getIsAlive();
        }
        
        if (!losed) {
            gameOver();
        }
        
    }
    
    public void gameOver() {
        snakeTimer.stop();
        
        String text = "Game Over, wanna play again?";
        
        if (numberOfPlayers == 2) {
           int one = scoreBoard.getScore();
           int two = scoreBoardTwo.getScore();
           if (one < two) {
               text = "Red Wins, wanna play again?";
           } else if(one > two) {
               text = "Blue Wins, wanna play again?";
           } else {
               text = "Tie, wanna play again?";
           }
               
        } 
        
         int gameOver = JOptionPane.showConfirmDialog(this, text, "Game over", 0);
        
        if (gameOver == 0) {
            restart();
        } else {
            System.exit(0);
        }
        
    }
    
    @Override 
    protected void paintComponent(Graphics g)  {
       
        super.paintComponent(g);
        
        
        for (Snake snake : snakes) {
            snake.paint(g);
        }
        food.paint(g);
        if (obstacles != null) {
            for(Obstacle obstacle : obstacles) {
                obstacle.paint(g);
            }
        }
        
    }
    
    
    private void restart() {
        
        obstacles = null;
        snake = new Snake(5, 5, 3, this, 1);
        snakes = new ArrayList<Snake>();
        snakes.add(snake);
        if(numberOfPlayers == 2) {
            snakeTwo = new Snake(5, 10, 3, this, 2);
            snakes.add(snakeTwo);
            scoreBoardTwo.resetScore();
        }
        food.generateFood(snakes);
        stopFoodTimer(true);
        deltaTime = INITIAL_DELTA_TIME;
        scoreBoard.resetScore();
        createTimer();
        
    }
    
    
    
    
    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    snake.setDirection(Direction.UP);
                    break;
                case KeyEvent.VK_D:
                    snake.setDirection(Direction.RIGHT);
                    break;
                case KeyEvent.VK_S:
                    snake.setDirection(Direction.DOWN);
                    break;
                case KeyEvent.VK_A:
                   snake.setDirection(Direction.LEFT);
                    break;
                case KeyEvent.VK_ESCAPE:
                   pause();
                    break;
                case KeyEvent.VK_UP:
                    if ( numberOfPlayers == 2) {
                        snakeTwo.setDirection(Direction.UP);
                    } else {
                        snake.setDirection(Direction.UP);
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if ( numberOfPlayers == 2) {
                        snakeTwo.setDirection(Direction.RIGHT);
                    } else {
                        snake.setDirection(Direction.RIGHT);
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if ( numberOfPlayers == 2) {
                        snakeTwo.setDirection(Direction.DOWN);
                    } else {
                        snake.setDirection(Direction.DOWN);
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if ( numberOfPlayers == 2) {
                        snakeTwo.setDirection(Direction.LEFT);
                    } else {
                        snake.setDirection(Direction.LEFT);
                    }
                    break;
            }
        }
    }
    
    private void pause() {
        
        snakeTimer.stop();
        specialFoodTimer.stop();
        food.pauseBlink();
        
        int option = JOptionPane.showConfirmDialog(this, "Continue?", "Pause", 0);
        
        if(option == 1) {
            System.exit(0);
        }
        
        snakeTimer.start();
        if(food.getIsSpecial()) {
            specialFoodTimer.start();
            food.blink();
        }
        
        
    }
    
     private void createTimer() {
       if (snakeTimer != null) {
           snakeTimer.stop();
       }
        snakeTimer = new Timer(deltaTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               
                moveSnakes();
                repaint();
                
                
            }

           

        });
        snakeTimer.start();
        
    }
     
    private void moveSnakes() {
        for (Snake snake: snakes) {
            if (snake.getIsAlive()) {
                snake.move();
            }
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

