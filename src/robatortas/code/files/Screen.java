package robatortas.code.files;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Screen {
	
	public Screen(int WIDTH, int HEIGHT, String name, Driver driver) {
		JFrame frame = new JFrame();
		
		Dimension size = new Dimension(WIDTH, HEIGHT);
		
		frame.setTitle(name);
		frame.setSize(size);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.add(driver);
		driver.start();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
