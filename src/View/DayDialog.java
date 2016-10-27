package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import Manager.ScheduleManager;
import Model.Schedule;

public class DayDialog extends JDialog {
	private static final long serialVersionUID = 4205905965953117359L;

	private ArrayList<JPanel> scheduleJPanels;

	private Component frameBody;
	private JLabel addButton;

	private JPanel headerPanel;
	private JScrollPane scrollPanel;
	private JPanel scrollContent;

	private Date startDate;
	private Date endDate;

	private int yearNumber;
	private int monthNumber;
	private int dayNumber;

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

			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE || (keyMatch1 && keyMatch2))
				setVisible(false);
		}
	};

	private ScheduleManager.Callbacks lastCallback;

	public DayDialog(JFrame body, int year, int month, int day) {
		frameBody = this;

		scheduleJPanels = new ArrayList<JPanel>();

		setSize(500, 440);
		setLocation(body.getX() + (body.getSize().width - getSize().width) / 2,
				body.getY() + (body.getSize().height - getSize().height) / 2);
		setBackground(Color.WHITE);
		setResizable(false);
		setModal(true);

		lastCallback = ScheduleManager.sharedInstance().getEventListener();

		yearNumber = year;
		monthNumber = month;
		dayNumber = day;

		try {
			startDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
					.parse(String.format("%04d%02d%02d 00:00:00", yearNumber, monthNumber, dayNumber));
			endDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
					.parse(String.format("%04d%02d%02d 23:59:59", yearNumber, monthNumber, dayNumber));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		headerPanel = new JPanel();
		headerPanel.setLayout(new BorderLayout());
		headerPanel.setBorder(null);
		headerPanel.setBackground(new Color(0xDD9787)); // Background Color of
														// Header
		JLabel timeLabel = new JLabel();
		timeLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
		timeLabel.setText(String.format("%04d. %02d. %02d.", yearNumber, monthNumber, dayNumber));
		timeLabel.setFont(new Font("THEJung110", 0, 24));
		timeLabel.setForeground(Color.WHITE);
		headerPanel.add(timeLabel, BorderLayout.WEST);

		ImageIcon imgIcon = new ImageIcon("add.png");
		addButton = new JLabel(new ImageIcon(imgIcon.getImage().getScaledInstance(26, 26, Image.SCALE_DEFAULT)));
		addButton.setPreferredSize(new Dimension(50, 26));
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				AddDialog dialog = new AddDialog(frameBody, yearNumber, monthNumber, dayNumber);
				dialog.setVisible(true);
			}
		});
		headerPanel.add(addButton, BorderLayout.EAST);

		this.add(headerPanel, BorderLayout.NORTH);

		scrollContent = new JPanel();
		scrollContent.setLayout(null);

		scrollPanel = new JScrollPane();
		scrollPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
		scrollPanel.setHorizontalScrollBar(null);
		scrollPanel.setVerticalScrollBar(new JScrollBar());
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setViewportView(scrollContent);
		this.add(scrollPanel, BorderLayout.CENTER);

		addAllContent();

		this.addKeyListener(keyListener);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				ScheduleManager.sharedInstance().setEventListener(lastCallback);
			}
		});

		ScheduleManager.sharedInstance().setEventListener(new ScheduleManager.Callbacks() {
			@Override
			public void onUpdatedSchedule(Schedule schedule) {
				removeAllContent();
				addAllContent();
				for (int i = 0; i < scheduleJPanels.size(); i++) {
					if (Integer.parseInt(scheduleJPanels.get(i).getName()) == schedule.getIndex()) {
						updateContent(scheduleJPanels.get(i), schedule);
						break;
					}
				}

				lastCallback.onUpdatedSchedule(schedule);
			}

			@Override
			public void onAddedSchedule(Schedule schedule) {
				removeAllContent();
				addAllContent();

				lastCallback.onAddedSchedule(schedule);
			}

			@Override
			public void onDeletedSchedule(Schedule schedule) {
				removeAllContent();
				addAllContent();

				lastCallback.onDeletedSchedule(schedule);
			}
		});
	}

	private void updateLayout(Dimension dimension) {
		headerPanel.setPreferredSize(new Dimension(dimension.width, 50));
		scrollPanel.setPreferredSize(new Dimension(dimension.width, dimension.height - 55));

		for (int i = 0; i < scheduleJPanels.size(); i++) {
			JPanel p = scheduleJPanels.get(i);
			p.setBounds(5, 55 * i, dimension.width - 25, 50);
		}
		scrollContent.setPreferredSize(new Dimension(dimension.width, 55 * scheduleJPanels.size() + 20));
	}

	public void updateContent(JPanel content, Schedule s) {
		MouseListener buttonListner = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("TOUCH START");
				Schedule s = ScheduleManager.sharedInstance().getSchedule(Integer.parseInt(e.getComponent().getName()));

				AddDialog dialog = new AddDialog(frameBody, s);
				dialog.setVisible(true);

				System.out.println("TOUCH END");
			}
		};

		content.removeAll();

		content.setLayout(new BorderLayout());
		content.setBackground(new Color(0xE4ACA0)); // new Schedule Color

		JLabel sLabel = new JLabel();
		sLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		// sLabel.setText(String.format(new SimpleDateFormat("HH:mm").format(s
		// .getStartDate())
		// + "-"
		// + new SimpleDateFormat("HH:mm").format(s.getEndDate())));
		// sLabel.setFont(new Font("THEJung130", 0, 14));
		// sLabel.setForeground(Color.WHITE);
		sLabel.setBackground(new Color(0x049dd9));
		content.add(sLabel, BorderLayout.WEST);

		JPanel subjectPanel = new JPanel();
		subjectPanel.setLayout(new BorderLayout());
		subjectPanel.setBackground(Color.WHITE);
		subjectPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0x0E4ACA0))); // Border
																					// Color
																					// of
																					// new
																					// Schedule

		JLabel subjectLabel = new JLabel(s.getSubject());
		subjectLabel.setForeground(Color.BLACK);
		subjectLabel.setFont(new Font("THEJung130", 0, 14));
		subjectLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
		subjectPanel.add(subjectLabel);

		content.add(subjectPanel, BorderLayout.CENTER);
		content.setName(Integer.toString(s.getIndex()));
		content.addMouseListener(buttonListner);

		scrollPanel.setVisible(false);
		scrollPanel.setVisible(true);
	}

	public void addAllContent() {
		ArrayList<Schedule> list = ScheduleManager.sharedInstance().getSchedules(startDate, endDate);

		if (list.size() > 0) {
			for (Schedule s : list) {
				JPanel sPanel = new JPanel();

				updateContent(sPanel, s);

				scrollContent.add(sPanel);
				scheduleJPanels.add(sPanel);
			}
		} else {
			JPanel sPanel = new JPanel();
			sPanel.setLayout(new BorderLayout());
			sPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0xDD9787))); // Border
																				// of
																				// Text
																				// Box
																				// "Tap
																				// add
																				// button~"
			sPanel.setBackground(Color.WHITE);

			JLabel subjectLabel = new JLabel("Tap add button to start your planner!");
			subjectLabel.setForeground(Color.BLACK);
			subjectLabel.setFont(new Font("THEJung130", 0, 14));
			subjectLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
			subjectLabel.setHorizontalAlignment(JLabel.CENTER);
			sPanel.add(subjectLabel, BorderLayout.CENTER);

			scrollContent.add(sPanel);
			scheduleJPanels.add(sPanel);
		}
		updateLayout(getSize());
	}

	public void removeAllContent() {
		scheduleJPanels.clear();
		scrollContent.removeAll();
	}
}
