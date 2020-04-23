
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author victoralonso
 */
public class Food {
    private Node position;
    private boolean isSpecial;
    private Board mainBoard;
    
    public Food(Snake snake, Board mainBoard) { 
        // We pass Snake to the constructor because if the randomnly generated food
        // falls on the Snake you have to create another position for the food
        isSpecial = false;
        this.mainBoard = mainBoard;
        generateFood(snake);
        
        
    }

    public boolean getIsSpecial() {
        return isSpecial;
    }
    
    public Node getPosition() {
        return position;
    }
    
    
    
    public void generateFood(Snake snake) {
        
        isSpecial = false;
        
        boolean foodCreated = false;
        int y = 0;
        int x = 0;        
        
        
        while (!foodCreated) {            
            
            x = (int) (Math.random() * mainBoard.numCols);
            y = (int) (Math.random() * mainBoard.numRows);

            List<Node> body = snake.getBody();
            
            boolean canCreateFood = true;
            
            for(int i = 0; i < body.size() && canCreateFood; i++) {
                
                Node snakeNode = body.get(i);
                
                //System.out.println("Snakenode"+snakeNode.getRow()+", "+snakeNode.getCol());
                //System.out.println("foodnode"+x+", "+y);
                
                if (x == snakeNode.getRow()&& y == snakeNode.getCol()) {
                    
                    canCreateFood = false;
                    
                } 

            }
            
            if(canCreateFood) { 
                foodCreated = true;
                position = new Node(x, y);
            }
            
        }
        
        position  = new Node(x, y);
        
        int random = (int) (Math.random() * 8);
        //System.out.println(""+random);
        isSpecial = (random == 1);
        
    }
    
    
    public void paint(Graphics g) {
        
       Util.drawSquare(g, position.getRow(),position.getCol(), mainBoard.squareWidth(), mainBoard.squareHeight(), isSpecial?Color.YELLOW:Color.RED);
        
        
    }
    
    // Create all the methods you need here
}
