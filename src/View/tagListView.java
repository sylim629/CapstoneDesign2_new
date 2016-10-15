package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.border.LineBorder;

import Manager.FriendsList;

import com.restfb.types.User;

public class tagListView extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7737057288376292273L;
	
	private Callbacks friendCallback = null;
	
	private ArrayList<String> selectedFriends = new ArrayList<String>();
	
	private KeyListener keyListener = new KeyListener() {
		private boolean keyMatch1 = false;
		private boolean keyMatch2 = false;

		@Override
		public void keyTyped(KeyEvent arg0) {
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			if (arg0.getKeyCode() == 157)
				keyMatch1 = false;

			if (arg0.getKeyCode() == 87)
				keyMatch2 = false;
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == 157)
				keyMatch1 = true;

			if (arg0.getKeyCode() == 87)
				keyMatch2 = true;

			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE
					|| (keyMatch1 && keyMatch2))
				setVisible(false);
		}
	};

	public tagListView(Component comp, Callbacks callback) {
		friendCallback = callback;
		
		List<User> friends = new FriendsList().getFriendsList();

		setSize(new Dimension(500, 200));
		setLocation(comp.getX() + (comp.getSize().width - getSize().width) / 2,
				comp.getY() + (comp.getSize().height - getSize().height) / 2);
		setResizable(false);
		setModal(true);

		this.setLayout(new FlowLayout());

		for (int i = 0; i < friends.size(); i++) {
			User user = friends.get(i);
			
			String friendName = user.getName();
			
			JButton friendButton = new JButton(friendName);
			friendButton.setPreferredSize(new Dimension(500, 40));
			friendButton.setName(friendName);
			friendButton.setBackground(Color.WHITE);
			friendButton.setForeground(Color.BLACK);
			friendButton.setBorder(LineBorder.createGrayLineBorder());
			friendButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					JButton label = (JButton) e.getSource();
					String name = label.getName();
					if (label.getBackground().equals(Color.WHITE))
					{
						label.setBackground(Color.GRAY);
						label.setOpaque(true);
						selectedFriends.add(name);
//						System.out.println(selectedFriends.get(0));
					}
					else
					{
						label.setBackground(Color.WHITE);
						label.setOpaque(true);
						selectedFriends.remove(name);
					}
				}
			});
			friendButton.addKeyListener(keyListener);
			
			this.add(friendButton);
			
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					friendCallback.onUpdatedDate(selectedFriends);
					super.windowClosed(e);
				}
			});
		}
	}
	
	public interface Callbacks {
		public void onUpdatedDate(ArrayList<String> selectedNames);
	}
}
