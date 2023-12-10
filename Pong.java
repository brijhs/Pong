import java.util.*;
import java.util.Random; 

class Pong extends App {
    static class Player {
        Vector2 position;  
        double width; 
        double height; ; 
        Vector3 color; 
        Vector2 upLeftCorner; 
        Vector2 downRightCorner; 
        int score; 
        Player() {
            this.width = 2.0; 
            this.height = 10.0; 
            this.color = new Vector3(Vector3.white);  
        }
    }
    
    static class Ball {
        Vector2 position; 
        Vector2 direction; 
        Vector3 color; 
        double radius;
        double speed;
        Ball() {
            this.color = new Vector3(Vector3.white); 
            this.position = new Vector2(0,0); 
            this.radius = 3.0; 
            this.speed = 3.0; 
        }
    }
    
    Player leftPlayer = new Player();
    Player rightPlayer = new Player(); 
    Player[] playerArray = {leftPlayer, rightPlayer}; 
    Ball ball = new Ball(); 
    int sleeper = 5;
    
    //Define variables to draw the board and detect collisions 
    int width = 200; 
    int height = 128; 
    int rightBorder = width/2 - 1; 
    int leftBorder = -width/2 + 2; 
    int upBorder = height/2 - 2; 
    int downBorder = -height/2 + 8; 
    int scorePause = 100;  
    boolean winDetected; 
    boolean regPhysics;
    boolean drawMenu = true; 
    double change = 1.5;
    
    static void randomStart(Ball ball) {
        Random rand = new Random(); 
        double x = (rand.nextInt(2) + 1) * 2  - 3; 
        double y = rand.nextDouble() * 2 - 1; 
        Vector2 direction = new Vector2(x, y); 
        ball.direction = standardize(direction, 2); 
        return; 
    }
    
    
    void setup() {
        //Player & ball init
        leftPlayer.position = new Vector2(-width/2 + 10, 0); 
        rightPlayer.position = new Vector2(width/2 - 10, 0);
        updateCorners(leftPlayer); 
        updateCorners(rightPlayer); 
        
        //Probably want to choose a random here 
        randomStart(ball);  
        return; 
    }

    static void wallChange(Ball ball) {
        ball.direction.y = -ball.direction.y; 
        return; 
    }
    
    static void simpleBounce(Ball ball, int choice) {
        if (choice == 0) {ball.direction.y = -ball.direction.y; return;}
        else {ball.direction.x = -ball.direction.x; return;}
    }
    
    static void playerBounce(Ball ball, Player player, int bounceTemp) {
        //bounce is off a paddle 
        double position_relative_to_paddle = (ball.position.y - player.position.y) * 2 / player.height; 
        double exit_angle = 90 - 30 * position_relative_to_paddle; 
        exit_angle = Math.toRadians(exit_angle); 
        double y = Math.sqrt(2) * Math.cos(exit_angle); 
        double x = Math.sqrt(2) * Math.sin(exit_angle);
        if (bounceTemp == 1) { x = -Math.abs(x);} //Bounces off rightPlayer, should head left
        if (bounceTemp == 2) { x = Math.abs(x); } //Bounces off leftPlayer, should head right 
        
        ball.direction = new Vector2(x, y); 
        
        
        return; 
    }
    
    
    //To make sure that the magnitude of the vector remains the same to ensure inverse speed works as intended
    static Vector2 standardize(Vector2 ballDirection, double magnitude) {
        double x = ballDirection.x; 
        double y = ballDirection.y; 
        double shrink = Math.sqrt(x * x + y * y); 
        x /= shrink/magnitude; 
        y /= shrink/magnitude;  
        return new Vector2(x,y); 
    }
    
    static void updateCorners(Player player){
        player.upLeftCorner = new Vector2(player.position.x - player.width, player.position.y + player.height);
        player.downRightCorner = new Vector2(player.position.x + player.width, player.position.y - player.height); 
        return; 
    }
    
    static int bounceDetected(Ball ball, Player leftPlayer, Player rightPlayer, int upBorder,  int downBorder, int sleeper) {
 
        if (ball.position.y <= leftPlayer.position.y + leftPlayer.height/2 + ball.radius
                && ball.position.y >= leftPlayer.position.y - leftPlayer.height/2 - ball.radius
                && ball.position.x <= leftPlayer.position.x + leftPlayer.width/2 + ball.radius 
                && ball.position.x > leftPlayer.position.x + leftPlayer.width/2 
                && sleeper <= 0) {return 2;}
        
        if (ball.position.y <= rightPlayer.position.y + rightPlayer.height/2 + + ball.radius 
                && ball.position.y >= rightPlayer.position.y - rightPlayer.height/2 - ball.radius
                && ball.position.x >= rightPlayer.position.x - rightPlayer.width/2 - ball.radius 
                && ball.position.x < rightPlayer.position.x + rightPlayer.width/2 
                && sleeper <= 0) { return 1;}
        
        if (ball.position.y + ball.radius >= upBorder || ball.position.y - ball.radius <= downBorder) { return 0;} 
        return -1; 
    }
    
    static int scoreDetected(Ball ball, Player leftPlayer, Player rightPlayer, int leftBorder, int rightBorder) {
        if (ball.position.x < rightPlayer.position.x && ball.position.x > leftPlayer.position.x) {return -1;}
        else if (ball.position.x - ball.radius  <= leftBorder) {return 0;}
        else if (ball.position.x + ball.radius >= rightBorder) {return 1;}
        
        return -1;
    }
    
    static void scoreReset(Ball ball, int scoreSide, Player leftPlayer, Player rightPlayer) {
        ball.position = new Vector2(0,0); 
        ball.speed = 3.0;
        ball.color = new Vector3(Vector3.white); 
        if (scoreSide == 0) {rightPlayer.score++;}
        if (scoreSide == 1) {leftPlayer.score++;}
        randomStart(ball); 
        return; 
    }
    
    static int winCheck(Player leftPlayer, Player rightPlayer) {
        if (leftPlayer.score == 5) {return 0;}
        if (rightPlayer.score == 5) {return 1;}
        return -1; 
    }
    
    //Drawing loop 
    void loop() {
        //Draw the borders of the board 
        drawCornerRectangle(new Vector2(leftBorder - 1, upBorder + 1), new Vector2(rightBorder + 1, upBorder), new Vector3(Vector3.white));
        drawCornerRectangle( new Vector2(leftBorder - 1, upBorder + 1), new Vector2(leftBorder, downBorder) , new Vector3(Vector3.white));
        drawCornerRectangle(new Vector2(leftBorder - 1, downBorder - 1), new Vector2(rightBorder + 1, downBorder), new Vector3(Vector3.white));
        drawCornerRectangle(new Vector2(rightBorder + 1, upBorder + 1), new Vector2(rightBorder, downBorder), new Vector3(Vector3.white));
        if (drawMenu) {
            drawString("Welcome to Pong!", new Vector2(0, 50), new Vector3(Vector3.red), 30, true); 
            drawString("Rules:", new Vector2(0,30), new Vector3(Vector3.blue), 20, true); 
            drawString("First to five wins", new Vector2(0,20), new Vector3(Vector3.blue), 20, true); 
            drawString("The left player is controlled by 'w' and 's'", new Vector2(0,10), new Vector3(Vector3.blue), 20, true);
            drawString("The right player is controlled by mouse tracking", new Vector2(0,0), new Vector3(Vector3.blue), 20, true);
            drawString("Click to choose physics", new Vector2(0,-10), new Vector3(Vector3.white), 20, true);
            drawCornerRectangle(new Vector2(-60, -20), new Vector2(-10, -40), new Vector3(Vector3.white)); 
            drawCornerRectangle(new Vector2(60, -20), new Vector2(10, -40), new Vector3(Vector3.white)); 
            drawString("Regular Physics", new Vector2(-35, -30), new Vector3(Vector3.blue), 15, true); 
            drawString("Pong Physics", new Vector2(35, -30), new Vector3(Vector3.red), 15, true);   
            if (mousePressed && mousePosition.x > 10 && mousePosition.x < 60 && mousePosition.y < -20 && mousePosition.y > -40) {drawMenu = false;}
            if (mousePressed &&  mousePosition.x < -10 && mousePosition.x > -60 && mousePosition.y < -20 && mousePosition.y > -40) {
                regPhysics = true; 
                drawMenu = false;
            }
        }
        
        else if (!drawMenu) {
            //Move the players 
            if (keyHeld('W') && leftPlayer.position.y < upBorder - leftPlayer.height - 1.0) { leftPlayer.position.y += change; }
            if (keyHeld('S') && leftPlayer.position.y > downBorder + leftPlayer.height + 1) { leftPlayer.position.y -= change; }
            if (mousePosition.y < upBorder - rightPlayer.height && mousePosition.y > downBorder + rightPlayer.height) {rightPlayer.position.y = mousePosition.y; }
            updateCorners(leftPlayer); 
            updateCorners(rightPlayer); 
            
            //Draw the ball and player
            drawCornerRectangle(leftPlayer.upLeftCorner, leftPlayer.downRightCorner, leftPlayer.color);
            drawCornerRectangle(rightPlayer.upLeftCorner, rightPlayer.downRightCorner, rightPlayer.color);
            drawCircle(ball.position, ball.radius, ball.color); 
            
            sleeper--;
            
            //Physics 
            if (scorePause == 0 && !winDetected) {
                ball.position = new Vector2(ball.position.x + (ball.direction.x / ball.speed), ball.position.y + (ball.direction.y  / ball.speed)); }
            
            else {scorePause--;}
            
            //Check for scores 
            int scoreTemp = scoreDetected (ball, leftPlayer, rightPlayer, leftBorder, rightBorder);
            if (scoreTemp > -1) {
                scorePause = 50; 
                scoreReset(ball, scoreTemp, leftPlayer, rightPlayer); 
            }
            
            //Draw Scores 
            String score = "Score: ";
            drawString(score + leftPlayer.score, new Vector2(leftBorder, upBorder + 5), new Vector3(Vector3.red), 20, false);
            drawString(score + rightPlayer.score, new Vector2(rightBorder - 32, upBorder + 5), new Vector3(Vector3.red), 20, false);
            
            //Check for win
            int win = winCheck(leftPlayer, rightPlayer); 
            
            //Left win 
            if (win == 0) {drawString(" Left Wins!", new Vector2(0,0), new Vector3(Vector3.red), 50, true); winDetected = true; }
            
            //Right win 
            if (win == 1) {drawString("Right Wins!", new Vector2(0,0), new Vector3(Vector3.red), 50, true); winDetected = true; }
            
            if (winDetected) {
                drawString("Click to restart", new Vector2(0,-20), new Vector3(Vector3.red), 40, true);
            }
            if (mousePressed && winDetected) {
                leftPlayer.score = 0; 
                rightPlayer.score = 0; 
                winDetected = false;
                win = -1;
                randomStart(ball);  
                scorePause = 50; 
            }
            
            //Check for bounces; temp = -1 means no bounce, 0 means wall, 1 means rightPlayer, 2 means leftPlayer 
            int bounceTemp = bounceDetected(ball, leftPlayer, rightPlayer, upBorder, downBorder, sleeper);
            if (bounceTemp > -1) {
                if (bounceTemp > 0) {sleeper = 10; }
                if (regPhysics) {simpleBounce(ball, bounceTemp);}
                else {
                
                if (bounceTemp == 0) {wallChange(ball); }
                else if (bounceTemp == 1) { playerBounce(ball, rightPlayer, 1);}
                else if (bounceTemp == 2) {playerBounce(ball, leftPlayer, 2); }
                }
                
                
                //Increase speed on subsequent bounces 
                if (ball.speed > 2) {ball.speed /= 1.4;}
                else if (ball.speed <= 2 && ball.speed > 0.5) {ball.speed /= 1.1; ball.color = new Vector3(Vector3.red); change = 2.0; } 
                else if (ball.speed <= 0.5) {ball.color = new Vector3(Vector3.blue); change = 2.5;}
            }
        } 
    }
    

    public static void main(String[] arguments) {
        System.out.println("Welcome to Pong"); 
        System.out.println("The left player is moved up and down using 'w' and 's' respectively"); 
        System.out.println("The right player is moved using mouse tracking"); 
        System.out.println("First to five wins!");
        App app = new Pong();
        int width = 172; 
        int height = 128; 
        app.setWindowBackgroundColor(0.0, 0.0, 0.0);
        app.setWindowSizeInWorldUnits(width + 50, height + 50);
        app.setWindowCenterInWorldUnits(0.0, 0.0);
        app.setWindowHeightInPixels(528);
        app.setWindowTopLeftCornerInPixels(width/2, height/2);
        app.run();
    }
}