package robatortas.code.files;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Random;

public class Driver extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 500;
	private static final String name = "RAYCASTING";
	
	private MouseInput mouse = new MouseInput();;
	
	public Thread thread;
	public boolean running = false;
	
	private Random random = new Random();
	
	private static final int numLines = 16;
	private LinkedList<Line2D.Float> lines;
	
	public Driver() {
		lines = makeLines();
		new Screen(WIDTH, HEIGHT, name, this);
		
		addMouseMotionListener(mouse);
	}
	
	//creates lines in random positions
	private LinkedList<Line2D.Float> makeLines() {
		LinkedList<Line2D.Float> lines = new LinkedList<Line2D.Float>();
		for(int i = 0; i < numLines; i++) {
			int x1 = random.nextInt(WIDTH);
			int y1 = random.nextInt(HEIGHT);
			int x2 = random.nextInt(WIDTH);
			int y2 = random.nextInt(HEIGHT);
			lines.add(new Line2D.Float(x1, y1, x2, y2));
		}
		return lines;
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	int i2 = 0;
	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		running = false;
	}
	
	public void run() {
		while(running) {
			tick();
			render();
			i2++;
		}
	}
	
	private void tick() {
		
	}
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		Color color = new Color(random.nextInt(0xffffff));
		g.setColor(color.green);
		for(Line2D.Float line : lines) {
			g.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int) line.y2);
		}
//		for(int i = 0; i < 1000; i++) {
//			g.drawLine(random.nextInt(getWidth()), random.nextInt(getHeight()), random.nextInt(getWidth()), random.nextInt(getHeight()));
//		}
		
		g.setColor(color.white);
		LinkedList<Line2D.Float> rays = calcRays(lines, mouse.mouseX, mouse.mouseY, 100, 100);
		for(Line2D.Float ray : rays) {
			g.drawLine((int) ray.x1, (int) ray.y1, (int) ray.x2, (int) ray.y2);
			
			LinkedList<Line2D.Float> bounces = bounce((int) ray.x1, (int) ray.y1, (int) ray.x1 + 10, (int) ray.y1 + 10);
			for(Line2D.Float bounce : bounces) {
				g.drawLine((int) bounce.x1, (int) bounce.y1, (int) bounce.x2, (int) bounce.y2);
			}
		}
		
		g.setColor(Color.yellow);
		int rectSize = 10;
		g.fillRect(mouse.mouseX - rectSize/2, mouse.mouseY - rectSize/2, rectSize, rectSize);
		g.drawString("RAYCAST TEST", 0, 10);
		
		g.dispose();
		bs.show();
	}
	
	private LinkedList<Float> calcRays(LinkedList<Line2D.Float> lines, int x, int y, int resolution, int maxDist) {
		LinkedList<Line2D.Float> rays = new LinkedList<>();
		for(i2 = 0; i2 < resolution; i2++) {
			System.out.println(i2);
			double FOV = resolution;
			double dir = (Math.PI*2) * ((double)i2 / FOV);
			float minDist = maxDist;
			for(Line2D.Float line : lines) {
				// calculates distance between rays and hit
				float dist = getRayCast(x, y, x + (float) Math.cos(dir) * maxDist, y + (float) Math.sin(dir) * maxDist, line.x1, line.y1, line.x2, line.y2);
				if(dist < minDist && dist > 0) {
					// Makes the dist of the lines the dist of the ray to the bounce
					minDist = dist;
				}
			}
			rays.add(new Line2D.Float(x, y, x + (float) Math.cos(dir) * minDist, y + (float) Math.sin(dir) * minDist));
		}
		return rays;
	}

	public static float dist(float x1, float y1, float x2, float y2) {
	    return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	public static float getRayCast(float p0_x, float p0_y, float p1_x, float p1_y, float p2_x, float p2_y, float p3_x, float p3_y) {
	    float s1_x, s1_y, s2_x, s2_y;
	    /* p0_x && p0_y are the mouse positions
	     * p1_x && p1_y is the distance from the ray start and hit
	     * p2_x, p2_y, p3_x, p3_y are the line points
	     */
	    s1_x = p1_x - p0_x;
	    s1_y = p1_y - p0_y;
	    s2_x = p3_x - p2_x;
	    s2_y = p3_y - p2_y;
	    
	    float s, t;
	    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
	    t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

	    if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
	        //Collision detected
	        float x = p0_x + (t * s1_x);
	        float y = p0_y + (t * s1_y);
	        
	        LinkedList<Float> bounces = bounce((int) p0_x, (int) p0_y, (int) p0_x + 10, (int) p0_y + 10);
	        
	        return dist(p0_x, p0_y, x, y);
	    }

	    return -1; // No collision
	}
	
	public static LinkedList<Float> bounce(float p0_x, float p0_y, float p1_x, float p1_y) {
		LinkedList<Line2D.Float> rays = new LinkedList<>();
		
		return rays;
	}
	
	public static void main(String[] args) {
		new Driver();
	}
}