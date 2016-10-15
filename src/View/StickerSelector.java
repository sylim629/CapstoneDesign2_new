package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import Manager.StickerManager;

public class StickerSelector extends JDialog {
	private static final long serialVersionUID = 3755107908467793653L;

	private Callbacks stickerCallback = null;

	private Integer selectedIndex = 0;
	private ArrayList<JButton> typeButtonList = new ArrayList<JButton>();
	private ArrayList<JLabel> stickerList = new ArrayList<JLabel>();

	private JPanel menuView = null;
	private JPanel contentBoxView = null;
	private JScrollPane scrollBarView = null;
	private JPanel contentView = null;

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

	public StickerSelector(Component comp, Callbacks callback) {
		stickerCallback = callback;

		setSize(360, 170);
		setLocation(comp.getX() + (comp.getSize().width - getSize().width) / 2,
				comp.getY() + (comp.getSize().height - getSize().height) / 2);
		setResizable(false);
		setModal(true);
		setBackground(Color.WHITE);
		addKeyListener(keyListener);
		this.setLayout(new BorderLayout());

		// Type List
		ArrayList<String> typeList = StickerManager.sharedInstance()
				.getStickerTypes();

		menuView = new JPanel();
		menuView.setPreferredSize(new Dimension(this.getWidth(), 50));
		menuView.setLayout(new GridLayout(1, typeList.size() + 1));
		menuView.setBackground(new Color(0xa8be37));
		this.add(menuView, BorderLayout.NORTH);

		for (int i = 0; i <= typeList.size(); i++) {
			JButton typeButton = new JButton(i == 0 ? "All"
					: typeList.get(i - 1));
			typeButton.setPreferredSize(new Dimension(this.getWidth()
					/ (typeList.size() + 1), 50));
			typeButton.setFont(new Font("TheJung110", Font.PLAIN, 10));
			typeButton.setBackground(new Color(0xa8be37));
			typeButton.setForeground(Color.WHITE);
			menuView.add(typeButton);
			typeButtonList.add(typeButton);

			typeButton.addKeyListener(keyListener);	
			typeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg) {
					selectedIndex = typeButtonList.indexOf(arg.getSource());
					reloadStickers();
				}
			});
		}

		// Content View
		contentBoxView = new JPanel();
		contentBoxView.setPreferredSize(new Dimension(this.getWidth(), this
				.getHeight() - 50));
		contentBoxView.setLayout(new BorderLayout());
		contentBoxView.setBackground(Color.WHITE);
		this.add(contentBoxView, BorderLayout.CENTER);

		contentView = new JPanel();
		contentView.setLayout(new FlowLayout());
		contentView.setBounds(0, 0, contentBoxView.getWidth(),
				contentBoxView.getHeight());
		contentBoxView.add(contentView);

		scrollBarView = new JScrollPane();
		// scrollBarView.setVerticalScrollBar(null);
		scrollBarView.setHorizontalScrollBar(new JScrollBar(
				JScrollBar.HORIZONTAL));
		scrollBarView
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollBarView
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollBarView.setViewportView(contentView);
		contentBoxView.add(scrollBarView);

		reloadStickers();
	}

	private void updateLayout(Dimension dimension) {
		Integer count = 0;
		for (JLabel sButton : stickerList) {
			Integer height = 10 + 66 * (int) (count / 4);
			sButton.setSize(56, 56);
			sButton.setPreferredSize(new Dimension(56, 56));
			if (count % 4 == 0)
				sButton.setBounds(new Rectangle(10, height, 56, 56));
			else if (count % 4 == 1)
				sButton.setBounds(new Rectangle(77, height, 56, 56));
			else if (count % 4 == 2)
				sButton.setBounds(new Rectangle(143, height, 56, 56));
			else
				sButton.setBounds(new Rectangle(210, height, 56, 56));
			sButton.setHorizontalTextPosition(JLabel.RIGHT);
			sButton.setVerticalTextPosition(JLabel.BOTTOM);
			sButton.setVisible(true);
		}
	}

	private void reloadStickers() {
		// Reset
		stickerList.clear();
		contentView.removeAll();

		// Add
		Integer start, end;
		ArrayList<HashMap<Integer, String>> list = StickerManager
				.sharedInstance().getStickerList();

		if (selectedIndex == 0) {
			start = 0;
			end = list.size();
		} else {
			start = selectedIndex - 1;
			end = selectedIndex;
		}

		for (int i = start; i < end; i++) {
			for (Integer key : list.get(i).keySet()) {
				String values = list.get(i).get(key);

				ImageIcon icon = new ImageIcon(values);
				JLabel sButton = new JLabel(icon);
				sButton.setName(key.toString());
				sButton.setBackground(Color.WHITE);
				sButton.setForeground(Color.WHITE);
				sButton.setBorder(LineBorder.createGrayLineBorder());
				sButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						JLabel label = (JLabel) e.getSource();
						String key = label.getName();
						stickerCallback.onUpdatedDate(Integer.parseInt(key));
						setVisible(false);
					}
				});
				sButton.addKeyListener(keyListener);

				contentView.add(sButton);
				stickerList.add(sButton);
			}
		}
		updateLayout(getSize());
	}

	public void setEventListener(Callbacks callback) {
		stickerCallback = callback;
	}

	public Callbacks getEventListener() {
		return stickerCallback;
	}

	public interface Callbacks {
		public void onUpdatedDate(Integer selectedID);
	}
}
