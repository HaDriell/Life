package fr.hadriel.life;


import java.awt.event.*;

/**
 * Created by glathuiliere on 21/12/2015.
 */

/*
* This is the Input Handler class.
*
* Whenever an AWT Component is focussed by the cursor, focussed, and the user hits a Key / use the Mouse, AWT will call
* one of all those methods as Callbacks (Thanks Java for not having Callbacks... ).
*
* */
public class Inputs implements KeyListener, MouseListener, MouseMotionListener {

    private LifeProgram simulation;

    private int stateSpreading;
    private int cursorX, cursorY;
    private boolean spreading;

    public Inputs(LifeProgram simulation) {
        this.simulation = simulation;
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                simulation.setPause(!simulation.isPaused()); // swaps paused by pressing P.
                break;

            case KeyEvent.VK_UP:
                simulation.setSpeed(simulation.getSpeed() + 1);
                break;

            case KeyEvent.VK_DOWN:
                simulation.setSpeed( simulation.getSpeed() - 1);
                break;

            case KeyEvent.VK_ESCAPE:
                int width = simulation.getwidth();
                int height= simulation.getheight();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        simulation.setCell(x,y,0);
                    }
                }
                break;
            case KeyEvent.VK_1:
                simulation.setPenState(0);
                break;
            case KeyEvent.VK_2:
                simulation.setPenState(1);
                break;
            case KeyEvent.VK_3:
                simulation.setPenState(2);
                break;
            case KeyEvent.VK_4:
                simulation.setPenState(3);
                break;

        }
        simulation.updatetitle();
    }

    public void mouseClicked(MouseEvent e) {
        cursorX = e.getX() / LifeProgram.CELLSIZE;
        cursorY = e.getY() / LifeProgram.CELLSIZE;
        switch (simulation.getPenState()){
            case 0:
                simulation.setCell(cursorX, cursorY, stateSpreading);
                break;
            case 1:
                simulation.addGlider(cursorX,cursorY);
                break;
            case 2:
                simulation.addBlinker(cursorX, cursorY);
                break;
            case 3:
                simulation.addBlinkerB(cursorX, cursorY);
                break;
        }
    }

    public void mouseDragged(MouseEvent e) {
        cursorX = e.getX() / LifeProgram.CELLSIZE;
        cursorY = e.getY() / LifeProgram.CELLSIZE;
        if(simulation.getPenState()==0|e.getButton() != MouseEvent.BUTTON1){
            simulation.setCell(cursorX, cursorY, stateSpreading);
        }

    }

    public void mousePressed(MouseEvent e) {
        if((simulation.getPenState()==0)|e.getButton() != MouseEvent.BUTTON1){
            if(!spreading) {
                stateSpreading = (e.getButton() == MouseEvent.BUTTON1 ? 1 : 0);
                spreading = true;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        spreading = false;
    }



    // I don't use that for controls.
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}

}