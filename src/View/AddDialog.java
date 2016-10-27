package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import Manager.FriendsList;
import Manager.ScheduleManager;
import Manager.StickerManager;
import Model.Schedule;

public class AddDialog extends JDialog {
	private static final long serialVersionUID = -1436447139996767847L;

	private int yearNumber;
	private int monthNumber;
	private int dayNumber;

	private Schedule scheduleObject = null;

	private JTextField titleField;
	private JTextArea memoArea;

	private JButton stickerButton;
	private JButton startDayButton;
	private JButton endDayButton;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton cancelButton;

	private JButton tagAtButton;
	private JLabel friendListLabel;

	private Date startDate;
	private Date endDate;
	private int sticker;

	private Component parentComp;

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

	public AddDialog(Component comp, Schedule schedule) {
		scheduleObject = schedule;

		initialize(comp, -1, -1, -1);
	}

	public AddDialog(Component comp, int year, int month, int day) {
		initialize(comp, year, month, day);
	}

	private void initialize(Component comp, int year, int month, int day) {
		parentComp = comp;

		setLayout(new BorderLayout());
		setSize(450, 300);
		setLocation(comp.getX() + (comp.getSize().width - getSize().width) / 2,
				comp.getY() + (comp.getSize().height - getSize().height) / 2);
		setResizable(false);
		setModal(true);

		if (scheduleObject == null) {
			yearNumber = year;
			monthNumber = month;
			dayNumber = day;
		} else {
			yearNumber = Integer.parseInt(new SimpleDateFormat("yyyy").format(scheduleObject.getStartDate()));
			monthNumber = Integer.parseInt(new SimpleDateFormat("MM").format(scheduleObject.getStartDate()));
			dayNumber = Integer.parseInt(new SimpleDateFormat("dd").format(scheduleObject.getStartDate()));
		}

		JPanel subjectPanel = new JPanel();
		JPanel emptyPanel = new JPanel();
		subjectPanel.setLayout(new BorderLayout());
		subjectPanel.setBorder(new EmptyBorder(0, 0, 0, 5));
		emptyPanel.setPreferredSize(new Dimension(10, 10));
		subjectPanel.add(emptyPanel, BorderLayout.NORTH);

		JLabel titleLabel = new JLabel();
		titleLabel.setText("  Subject ");
		titleLabel.setFont(new Font("THEJung130", 0, 12));
		subjectPanel.add(titleLabel, BorderLayout.WEST);

		titleField = new JTextField();
		if (scheduleObject != null)
			titleField.setText(scheduleObject.getSubject());
		titleField.addKeyListener(keyListener);
		subjectPanel.add(titleField, BorderLayout.CENTER);

		JPanel buttonDayPanel = new JPanel();
		buttonDayPanel.setLayout(new GridLayout(1, 2));
		buttonDayPanel.setBorder(new EmptyBorder(0, 5, 5, 5));

		if (scheduleObject != null) {
			startDate = scheduleObject.getStartDate();
			endDate = scheduleObject.getEndDate();
			sticker = scheduleObject.getStiker();
		} else {
			try {
				startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(String.format("%04d-%02d-%02d 00:00:00", yearNumber, monthNumber, dayNumber));
				endDate = startDate;
				sticker = 0;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		startDayButton = new JButton();
		startDayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DaySelector daySelector = new DaySelector(parentComp, new DaySelector.Callbacks() {

					@Override
					public void onUpdatedDate(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
						String textFormat = String.format("%04d-%02d-%02d %02d:%02d:00", year, month, day, hour,
								minute);

						startDayButton.setText(textFormat);
						try {
							startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(textFormat);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}, startDate);
				daySelector.setVisible(true);
			}
		});
		startDayButton.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate));
		startDayButton.addKeyListener(keyListener);
		buttonDayPanel.add(startDayButton);

		endDayButton = new JButton();
		endDayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DaySelector daySelector = new DaySelector(parentComp, new DaySelector.Callbacks() {

					@Override
					public void onUpdatedDate(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
						String textFormat = String.format("%04d-%02d-%02d %02d:%02d:00", year, month, day, hour,
								minute);

						endDayButton.setText(textFormat);
						try {
							endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(textFormat);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}, endDate);
				daySelector.setVisible(true);
			}
		});
		endDayButton.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate));
		endDayButton.addKeyListener(keyListener);
		buttonDayPanel.add(endDayButton);

		JPanel firstBodyRight = new JPanel();
		firstBodyRight.setLayout(new BorderLayout());
		firstBodyRight.add(subjectPanel, BorderLayout.NORTH);
		firstBodyRight.add(buttonDayPanel, BorderLayout.CENTER);

		stickerButton = new JButton();
		stickerButton.setPreferredSize(new Dimension(56, 56));
		if (sticker != 0) {
			stickerButton.setIcon(new ImageIcon(StickerManager.sharedInstance().getStickerURL(sticker)));
		}
		stickerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StickerSelector stickerSelector = new StickerSelector(parentComp, new StickerSelector.Callbacks() {

					@Override
					public void onUpdatedDate(Integer selectedID) {
						stickerButton.setIcon(new ImageIcon(StickerManager.sharedInstance().getStickerURL(selectedID)));
						sticker = selectedID;
					}
				});
				stickerSelector.setVisible(true);
			}
		});
		stickerButton.addKeyListener(keyListener);

		JPanel firstBody = new JPanel();
		firstBody.setLayout(new FlowLayout());

		firstBody.add(stickerButton);
		firstBody.add(firstBodyRight);
		firstBodyRight.setPreferredSize(new Dimension(getWidth() - 80, 66));

		this.add(firstBody, BorderLayout.NORTH);

		JPanel memoPanel = new JPanel();
		memoPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
		memoPanel.setLayout(new BorderLayout());

		memoArea = new JTextArea();
		if (scheduleObject != null)
			memoArea.setText(scheduleObject.getContent());
		memoArea.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0xAAAAAA)));
		memoArea.addKeyListener(keyListener);
		memoPanel.add(memoArea, BorderLayout.CENTER);
		this.add(memoPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());

		JPanel tagPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tagAtButton = new JButton("@");
		tagAtButton.setPreferredSize(new Dimension(35, 35));
		tagPanel.add(tagAtButton);
		friendListLabel = new JLabel();
		tagPanel.add(friendListLabel);

		if (year == -1) {
			ArrayList<String> taggedFriendsId = new ArrayList<>();
			FriendsList friendsList = new FriendsList();
			taggedFriendsId = scheduleObject.getTaggedFriendsIdArrayList();
			String names = "";
			for (int i = 0; i < taggedFriendsId.size(); i++) {
				names += friendsList.getFriendName(taggedFriendsId.get(i)) + ",";
			}
			friendListLabel.setText(names);
		}

		tagAtButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tagListView tagFriendsDialog = new tagListView(parentComp, new tagListView.Callbacks() {

					@Override
					public void onUpdatedDate(ArrayList<String> selectedFriends) {
						String friendNames = "";
						for (int i = 0; i < selectedFriends.size(); i++) {
							friendNames += selectedFriends.get(i) + ",";
						}
						friendListLabel.setText(friendNames);
						// System.out.println(friendListLabel.getText());
					}
				});
				tagFriendsDialog.setVisible(true);
			}
		});
		tagAtButton.addKeyListener(keyListener);

		bottomPanel.add(tagPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		if (scheduleObject != null)
			buttonPanel.setLayout(new GridLayout(1, 3));
		else
			buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.setBorder(new EmptyBorder(0, 5, 5, 5));

		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Schedule schedule = new Schedule();
				schedule.setSubject(titleField.getText());
				schedule.setStartDate(startDate);
				schedule.setEndDate(endDate);
				schedule.setContent(memoArea.getText());
				schedule.setSticker(sticker);
				ArrayList<String> taggedFriendsIdList = new ArrayList<>();
				String[] friendNames = friendListLabel.getText().split(",");
				FriendsList friendsList = new FriendsList();
				for (int i = 0; i < friendNames.length; i++) {
					if (friendNames[i].isEmpty())
						continue;
					taggedFriendsIdList.add(friendsList.getFriendID(friendNames[i]));
				}
				schedule.setTaggedFriends(taggedFriendsIdList);

				if (schedule.getSubject().length() < 1) {
					JOptionPane.showMessageDialog(null, "Subject is empty!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (schedule.getEndDate().compareTo(schedule.getStartDate()) < 0) {
					JOptionPane.showMessageDialog(null, "Start-time is more faster then End-time!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (scheduleObject != null) {
					scheduleObject.setSubject(schedule.getSubject());
					scheduleObject.setStartDate(schedule.getStartDate());
					scheduleObject.setEndDate(schedule.getEndDate());
					scheduleObject.setContent(schedule.getContent());
					scheduleObject.setSticker(sticker);
					ArrayList<String> taggedFriendsID = scheduleObject.getTaggedFriendsIdArrayList();
					scheduleObject.setTaggedFriends(taggedFriendsID);
					scheduleObject.update();
				} else {
					ScheduleManager.sharedInstance().addSchedule(schedule);
				}
				ScheduleManager.sharedInstance().saveSchedule();

				setVisible(false);
			}
		});
		saveButton.addKeyListener(keyListener);
		buttonPanel.add(saveButton);

		if (scheduleObject != null) {
			deleteButton = new JButton("Delete");
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ScheduleManager.sharedInstance().deleteSchedule(scheduleObject);
					setVisible(false);
				}
			});
			deleteButton.addKeyListener(keyListener);
			buttonPanel.add(deleteButton);
		}

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		cancelButton.addKeyListener(keyListener);
		buttonPanel.add(cancelButton);

		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}
}
