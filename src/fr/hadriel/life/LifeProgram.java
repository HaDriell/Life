package fr.hadriel.life;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

/**
 * Created by glathuiliere on 21/12/2015.
 */
public class LifeProgram extends Thread {

    public static final int CELLSIZE = 10;
    public static final double MIN_SPEED = 1D;
    public static final double MAX_SPEED = 100D;
    private int penState=0;


    private Inputs inputs = new Inputs(this);
    private Frame frame;
    private Canvas canvas;
    private BufferStrategy bs;
    private Graphics2D g;

    //Simulation variables
    private boolean paused;
    int[][] matrix;
    private int width;
    private int height;
    private double period;

    public void configure(int width, int height) {
        setSpeed(MIN_SPEED * 2);
        //this is the Simulation configuration part :
        this.matrix = new int[width][height];
        this.width = width;
        this.height = height;
        //this block is used if you just recreated a new Grid from the same LifeProgram. It's cleaning up the old frame.
        if(frame != null) {
            frame.dispose();
        }

        //create our GUI components
        frame = new Frame();
        canvas = new Canvas();
        canvas.setSize(width * CELLSIZE, height * CELLSIZE);
        canvas.setIgnoreRepaint(true); // we will manually repaint canvas. ignore OS draw calls.

        //Bind our Inputs to our Canvas, so AWT will call our callbacks on input events
        canvas.addMouseMotionListener(inputs);
        canvas.addMouseListener(inputs);
        canvas.addKeyListener(inputs);


        //set canvas the only content of the frame.
        frame.add(canvas);
        frame.pack();

        //Default window stuff (center, close operation, show)
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
        setPause(true); // pause before generation
        canvas.requestFocusInWindow();
    }

    /*
    * Speed is the number of updates per second.
    * */
    public void setSpeed(double speed) {
        speed = Math.min(speed, MAX_SPEED);
        speed = Math.max(speed, MIN_SPEED);
        this.period = 1000 / speed;
    }

    public double getSpeed() {
        return 1000 / this.period;
    }
    public int getwidth() {
        return this.width;
    }
    public int getheight() {
        return this.height;
    }
    public boolean isPaused() {
        return paused;
    }

    public void setPause(boolean paused) {
        this.paused = paused;
        updatetitle();
    }
    public void updatetitle(){
        frame.setTitle( "Conway's Game of Life: "+(paused ? "[PAUSED]" : "[RUNNING]")+getPenString()+" - ["+(int)getSpeed()+":GPS]");
    }
    public void setPenState(int state) {
        penState=state;
    }
    public int getPenState(){
        return penState;
    }
    private String getPenString() {
        String penstring="";
        switch(penState){
            case 0:
                penstring=" - [Pen]";
                break;
            case 1:
                penstring=" - [Glider]";
                break;
            case 2:
                penstring=" - [Blinker A]";
                break;
            case 3:
                penstring=" - [Blinker B]";
                break;
        }
        return penstring;
    }
    public boolean addGlider(int x, int y) {
    	/*
    	*  ..#
    	*  #.#
    	*  .##
    	*/
        setCell((x-1),(y+0),1);
        setCell((x+0),(y+1),1);
        setCell((x+1),(y-1),1);
        setCell((x+1),(y+0),1);
        setCell((x+1),(y+1),1);
        return true;

    }
    public void addBlinkerB(int x, int y) {
        final int[][] miniMatrixBlinkerB ={
                {0,0,1,1,0,0,0,0,0,1,1,0,0},
                {0,0,0,1,1,0,0,0,1,1,0,0,0},
                {1,0,0,1,0,1,0,1,0,1,0,0,1},
                {1,1,1,0,1,1,0,1,1,0,1,1,1},
                {0,1,0,1,0,1,0,1,0,1,0,1,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,1,1,0,0},
                {0,1,0,1,0,1,0,1,0,1,0,1,0},
                {1,1,1,0,1,1,0,1,1,0,1,1,1},
                {1,0,0,1,0,1,0,1,0,1,0,0,1},
                {0,0,0,1,1,0,0,0,1,1,0,0,0},
                {0,0,1,1,0,0,0,0,0,1,1,0,0}};
		/*
		 *  ..##.....##..
		 *  ...##...##...
		 *  #..#.#.#.#..#
		 *  ###.##.##.###
		 *  .#.#.#.#.#.#.
		 *  ..###...###..
		 *  ......X......
		 *  ..###...###..
		 *  .#.#.#.#.#.#.
		 *  ###.##.##.###
		 *  #..#.#.#.#..#
		 *  ...##...##...
		 *  ..##.....##..
		 */
        int bWidth=12;
        int bHeight=12;
        paintSticker(x,y,miniMatrixBlinkerB,bWidth, bHeight);

    }
    private void paintSticker(int x, int y, int[][] mMatrix,int width, int height){
        int state=0;
        int hh=(height/2);
        int hw=(width/2);
        for(int xx=0;xx<=width;xx++){
            for(int yy=0;yy<=height;yy++){
                state=mMatrix[xx][yy];
                setCell((xx+x-hw),(yy+y-(hh)),state);
            }
        }
        return;
    }
    public boolean addBlinker(int x, int y) {
    	/*
    	*  ...
    	*  ###
    	*  ...
    	*/
        setCell((x-1),(y+0),1);
        setCell((x+0),(y+0),1);
        setCell((x+1),(y+0),1);
        return true;
    }
    public void setCell(int x, int y, int state) {
        if(x < 0 || x >= width || y < 0 || y >= height) return;
        matrix[x][y] = state;
    }

    // Program Logic : main loop
    public void run() {
        Delta delta = new Delta();
        double acc = 0;
        while (true) {
            acc += delta.getDelta(); // this accumulates time elapsed through loops.
            // acc is given in ms.

            // convert "real time" to "simulation time"
            while(acc > period) {
                acc -= period;
                if (!paused)
                    update();
            }

            //Render is done as fast as it can.
            render();
            // Slow down the thread because it will sleep a lot.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {}
        }
    }

    public void update() {
        int[][] next = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                next[x][y] = updateCell(x, y, matrix, width, height);
            }
        }
        matrix = next;
    }

    public static int updateCell(int x, int y, int[][] matrix, int width, int height) {
        int currentState = matrix[x][y];
        int neighborCount = 0;

        for(int xx = -1; xx <= 1; xx++) {
            int dx = x + xx;
            if(dx < 0 || dx >= width) continue; // skip the check if dx is out of bounds
            for(int yy = -1; yy <= 1; yy++) {
                int dy = y + yy;
                if(dy < 0 || dy >= height) continue; // skip the check if dx is out of bounds

                //skip that cell, this is the x, y we're checking
                if(dx == x && dy == y) continue;

                // count neighbors that are alive
                if(matrix[dx][dy] != 0)
                    neighborCount++;
            }
        }

        if(neighborCount == 3 && currentState == 0) currentState = 1; // n = 3 && dead = alive
        else if(neighborCount < 2 && currentState != 0) currentState = 0; // n < 2 && alive = dead
        else if(neighborCount > 3 && currentState != 0) currentState = 0; // n > 3 && alive = dead

        return currentState;
    }

    public void render() {
        beginRendering();

        g.setTransform(new AffineTransform()); // reset any random transformation that could blow up our image.
        //Render every Cell
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                drawCell(g, x, y, matrix[x][y]);

        endRendering();
    }

    //Must be called to get the correct Graphics object. We're playing with multiple stacked graphics
    private void beginRendering() {
        if(canvas.getBufferStrategy() == null) canvas.createBufferStrategy(3);
        bs = canvas.getBufferStrategy();
        g = (Graphics2D) bs.getDrawGraphics();
    }

    //Must be called to swap the Canvas Buffers
    private void endRendering() {
        g.dispose();
        bs.show();
    }

    // Super Tool function to render the Cells
    private void drawCell(Graphics2D g, int x, int y, int state) {
        if (state != 0)
            g.setColor(Color.blue); // alive color
        else
            g.setColor(Color.white); // dead color
        g.fillRect(x * CELLSIZE, y * CELLSIZE, CELLSIZE, CELLSIZE);
        g.setColor(Color.black);
        g.drawRect(x * CELLSIZE, y * CELLSIZE, CELLSIZE, CELLSIZE);
    }


    public static void main(String[] args) {
        LifeProgram lifeProgram = new LifeProgram();
        lifeProgram.configure(50, 50);
        lifeProgram.start();
    }


}