package com.github.mrgalaxy.bitmodule;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.github.mrgalaxy.bitmodule.gfx.Screen;
import com.github.mrgalaxy.bitmodule.gfx.SpriteSheet;
import com.github.mrgalaxy.bitmodule.lib.Image;
import com.github.mrgalaxy.bitmodule.lib.Reference;

/**
 * BitModule.
 * 
 * main game Engine Class
 * 
 * @author Mr_Galaxy
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class BitModule extends Canvas implements Runnable
{
    private static final long serialVersionUID = 1L;
    
    public static int WIDTH =           300;                    // 300
    public static int HEIGHT =          WIDTH / 16 * 9;         // = 168 // WIDTH / 16 * 9
    public static int SCALE =           3;                      // From 1 - 5
    public static BitModule             _bitengine;
    private JFrame                      _frame;
    
    public boolean running =            false;
    
    public int tickCount =              0;

    private BufferedImage image =       new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels =              ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    
    private Screen                      _screen;
    
    public BitModule()
    {
        setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        
        _frame = new JFrame(Reference.TITLE); // Window Title - Title Name
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setLayout(new BorderLayout());
        
        _frame.add(this, BorderLayout.CENTER);
        _frame.pack();
        _frame.setResizable(false);
        _frame.setLocationRelativeTo(null);
        
        _frame.setVisible(true);
    }
    
    public void init()
    {
        _screen = new Screen(WIDTH, HEIGHT, new SpriteSheet (Image.SHEET_1));
    }
    
    public synchronized void start()
    {
        running = true;
        new Thread(this).start();
    }
    
    public synchronized void stop()
    {
        running = false;
    }
    
    public void run()
    {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / 60D;
        
        int ticks = 0;
        int frames = 0;
        
        long lastTimer = System.currentTimeMillis();
        double delta = 0;
        
        init();
        
        while (running)
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = true;
            
            while (delta >= 1)
            {
                ticks++;
                tick();
                delta -= 1;
                
                shouldRender = true;
            }
            
            try
            {
                Thread.sleep(2);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            
            if (shouldRender)
            {
                frames++;
                render();
            }
            
            if (System.currentTimeMillis() - lastTimer >= 1000)
            {
                lastTimer += 1000;
                
                System.out.println( Reference.ENGINE_NAME + ": " + ticks + " Ticks, " + frames + " FPS" );
                
                frames = 0;
                ticks = 0;
            }
        }
    }
    
    public void tick()
    {
        tickCount++;
    }
    
    public void render()
    {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null)
        {
            createBufferStrategy(4);
            return;
        }
        
        _screen.render(pixels, 0, WIDTH);
        
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        
        //g.setColor(Color.white); // TODO: Remove
        //g.fillRect(0, 0, getWidth(), getHeight()); // TODO: Remove
        
        g.dispose();
        bs.show();
    }
    
}