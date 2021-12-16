package robatortas.code.files;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseMotionListener {
	
	public int mouseX, mouseY;
	
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
}
