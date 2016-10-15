package View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DaySelector extends JDialog {
	private static final long serialVersionUID = 6196959467796789548L;

	private JButton selectButton;
	private JButton cancelButton;
	private JComboBox<String> yearCombo;
	private JComboBox<String> monthCombo;
	private JComboBox<String> dayCombo;
	private JComboBox<String> hourCombo;
	private JComboBox<String> minuteCombo;

	private Callbacks dateCallback;

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

	public DaySelector(Component comp, Callbacks callback, Date date) {
		// public DaySelector(Component comp, Callbacks callback, Integer year,
		// Integer month,
		// Integer day) {
		Integer year = Integer.valueOf(new SimpleDateFormat("yyyy")
				.format(date));
		Integer month = Integer
				.valueOf(new SimpleDateFormat("MM").format(date));
		Integer day = Integer.valueOf(new SimpleDateFormat("dd").format(date));
		Integer hour = Integer.valueOf(new SimpleDateFormat("HH").format(date));
		Integer minute = Integer.valueOf(new SimpleDateFormat("mm")
				.format(date));

		setSize(300, 160);
		setLocation(comp.getX() + (comp.getSize().width - getSize().width) / 2,
				comp.getY() + (comp.getSize().height - getSize().height) / 2);
		setResizable(false);
		setModal(true);

		dateCallback = callback;

		this.setLayout(new BorderLayout());

		JPanel datePanel = new JPanel();
		datePanel.setLayout(new GridLayout(2, 5, 0, 5));
		this.add(datePanel, BorderLayout.CENTER);

		JPanel buttonSelectPanel = new JPanel();
		buttonSelectPanel.setLayout(new GridLayout(1, 2));
		buttonSelectPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
		this.add(buttonSelectPanel, BorderLayout.SOUTH);

		JLabel yearLabel = new JLabel();
		yearLabel.setText("Year");
		yearLabel.setFont(new Font("THEJung130", 0, 12));
		yearLabel.setAlignmentX(CENTER_ALIGNMENT);
		datePanel.add(yearLabel);

		JLabel monthLabel = new JLabel();
		monthLabel.setText("Month");
		monthLabel.setFont(new Font("THEJung130", 0, 12));
		monthLabel.setAlignmentX(CENTER_ALIGNMENT);
		datePanel.add(monthLabel);

		JLabel dayLabel = new JLabel();
		dayLabel.setText("Day");
		dayLabel.setFont(new Font("THEJung130", 0, 12));
		dayLabel.setAlignmentX(CENTER_ALIGNMENT);
		datePanel.add(dayLabel);

		JLabel hourLabel = new JLabel();
		hourLabel.setText("Hour");
		hourLabel.setFont(new Font("THEJung130", 0, 12));
		hourLabel.setAlignmentX(CENTER_ALIGNMENT);
		datePanel.add(hourLabel);

		JLabel minuteLabel = new JLabel();
		minuteLabel.setText("Minute");
		minuteLabel.setFont(new Font("THEJung130", 0, 12));
		minuteLabel.setAlignmentX(CENTER_ALIGNMENT);
		datePanel.add(minuteLabel);

		ActionListener comboChanged = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Integer selectedYear = Integer.valueOf((String) yearCombo
						.getSelectedItem());
				Integer selectedMonth = Integer.valueOf((String) monthCombo
						.getSelectedItem());
				Boolean isLeapYear = false;
				if (selectedYear % 4 == 0 && selectedYear % 100 != 0
						|| selectedYear % 400 == 0)
					isLeapYear = true;

				Integer monthArray[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
						31, 30, 31 };

				dayCombo.removeAllItems();
				if (selectedMonth == 2) {
					for (int i = 1; i <= 28 + (isLeapYear ? 1 : 0); i++)
						dayCombo.addItem(String.format("%02d", i));
				} else {
					for (int i = 1; i <= monthArray[selectedMonth - 1]; i++)
						dayCombo.addItem(String.format("%02d", i));
				}
			}
		};

		yearCombo = new JComboBox<String>();
		for (int i = -50; i <= 50; i++)
			yearCombo.addItem(String.format("%04d", year + i));
		yearCombo.addActionListener(comboChanged);
		yearCombo.addKeyListener(keyListener);
		datePanel.add(yearCombo);

		monthCombo = new JComboBox<String>();
		for (int i = 1; i <= 12; i++)
			monthCombo.addItem(String.format("%02d", i));
		monthCombo.addActionListener(comboChanged);
		monthCombo.addKeyListener(keyListener);
		datePanel.add(monthCombo);

		dayCombo = new JComboBox<String>();
		datePanel.add(dayCombo);

		yearCombo.setSelectedItem(String.format("%04d", year));
		monthCombo.setSelectedIndex(month - 1);
		dayCombo.setSelectedIndex(day - 1);

		hourCombo = new JComboBox<String>();
		for (int i = 0; i < 24; i++)
			hourCombo.addItem(String.format("%02d", i));
		hourCombo.setSelectedIndex(hour);
		hourCombo.addKeyListener(keyListener);
		datePanel.add(hourCombo);

		minuteCombo = new JComboBox<String>();
		minuteCombo.setBorder(new EmptyBorder(0, 0, 0, 5));
		for (int i = 0; i < 60; i++)
			minuteCombo.addItem(String.format("%02d", i));
		minuteCombo.setSelectedIndex(minute);
		minuteCombo.addKeyListener(keyListener);
		datePanel.add(minuteCombo);

		selectButton = new JButton("Select");
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dateCallback.onUpdatedDate(
						Integer.valueOf((String) yearCombo.getSelectedItem()),
						monthCombo.getSelectedIndex() + 1,
						dayCombo.getSelectedIndex() + 1,
						hourCombo.getSelectedIndex(),
						minuteCombo.getSelectedIndex());
				setVisible(false);
			}
		});
		selectButton.addKeyListener(keyListener);
		buttonSelectPanel.add(selectButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		cancelButton.addKeyListener(keyListener);
		buttonSelectPanel.add(cancelButton);
	}

	public void setEventListener(Callbacks callback) {
		dateCallback = callback;
	}

	public Callbacks getEventListener() {
		return dateCallback;
	}

	public interface Callbacks {
		public void onUpdatedDate(Integer year, Integer month, Integer day,
				Integer hour, Integer minute);
	}
}
