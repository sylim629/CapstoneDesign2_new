package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import Manager.ScheduleManager;
import Model.Schedule;

public class DayView extends JPanel {
	private static final long serialVersionUID = -8145021500441323353L;

	private JPanel scheduleBox;
	private JLabel dayLabel;
	private JPanel bottomBox;

	private ArrayList<JPanel> scheduleList;

	private int yearNumber;
	private int monthNumber;
	private int dayNumber;
	private boolean prevMonth;
	private boolean nextMonth;
	private boolean isFocused;
	private boolean isToday;

	private Dimension lastDimension;

	public DayView() {
		this.setLayout(new BorderLayout());
		this.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0x36b1e1)));

		scheduleBox = new JPanel();
		this.add(scheduleBox, BorderLayout.NORTH);

		scheduleList = new ArrayList<JPanel>();

		dayLabel = new JLabel();
		dayLabel.setForeground(Color.BLACK);
		dayLabel.setHorizontalAlignment(JLabel.CENTER);
		dayLabel.setVerticalAlignment(JLabel.BOTTOM);
		this.add(dayLabel, BorderLayout.CENTER);

		bottomBox = new JPanel();
		bottomBox.setPreferredSize(new Dimension(15, 15));
		this.add(bottomBox, BorderLayout.SOUTH);

		isToday = false;
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);

		lastDimension = preferredSize;
	}

	private void updateLayout() {
		dayLabel.setText(Integer.toString(dayNumber));
		if (lastDimension != null) {
			scheduleBox.setPreferredSize(new Dimension(
					lastDimension.width - 20, lastDimension.height - 35));
			dayLabel.setPreferredSize(new Dimension(dayLabel.getFontMetrics(
					dayLabel.getFont()).stringWidth(dayLabel.getText()), 15));

			int widthCount = (int) Math.ceil(lastDimension.width
					/ ((lastDimension.width - 40)));
			int maxCount = scheduleBox.getPreferredSize().height / 12
					* widthCount;

			for (int i = 0; i < maxCount && i < scheduleList.size(); i++) {
				JPanel scPanel = scheduleList.get(i);
				scPanel.setPreferredSize(new Dimension(
						lastDimension.width - 40, 6));
				scPanel.setVisible(true);

				if (isFocused)
					scPanel.setBackground(new Color(0x049dd9));
				else
					scPanel.setBackground(new Color(0x82ceec));
			}
			for (int i = maxCount; i < scheduleList.size(); i++) {
				JPanel scPanel = scheduleList.get(i);
				scPanel.setVisible(false);
			}
		}

		if (isToday)
			dayLabel.setFont(new Font("THEJung150", 0, 14));

		if (isFocused) {
			setBackground(new Color(0xffffff));
			dayLabel.setForeground(new Color(0x049dd9));
			dayLabel.setFont(new Font("THEJung150", 0, 14));
		} else {
			setBackground(new Color(0x049dd9));

			dayLabel.setForeground(Color.WHITE);
			dayLabel.setFont(new Font("THEJung130", 0, 14));
		}

		if (prevMonth || nextMonth) {
			dayLabel.setForeground(new Color(0x43b6e3));
			dayLabel.setFont(new Font("THEJung110", 0, 14));
		}

		scheduleBox.setBackground(getBackground());

		if (isToday)
			bottomBox.setBackground(new Color(0xa8be37));
		else
			bottomBox.setBackground(getBackground());
	}

	private void addSchedule() {
		JPanel dayBox = new JPanel();
		dayBox.setBackground(new Color(0x82ceec));
		dayBox.setPreferredSize(new Dimension(100, 6));
		scheduleBox.add(dayBox);

		scheduleList.add(dayBox);
	}

	private void reloadSchedule() {
		scheduleBox.removeAll();
		scheduleList.clear();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date firstDate = null;
		Date endDate = null;
		try {
			firstDate = formatter.parse(String.format("%04d%02d%02d 00:00:00",
					yearNumber, monthNumber, dayNumber));
			endDate = formatter.parse(String.format("%04d%02d%02d 23:59:59",
					yearNumber, monthNumber, dayNumber));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ArrayList<Schedule> scList = ScheduleManager.sharedInstance()
				.getSchedules(firstDate, endDate);
		for (int i = 0; i < scList.size(); i++)
			addSchedule();
	}

	public void setDate(int year, int month, int day) {
		if (month <= 0) {
			year--;
			month = 12;
		}
		if (month > 12) {
			year++;
			month = 1;
		}

		yearNumber = year;
		monthNumber = month;
		setDayNumber(day);

		reloadSchedule();
	}

	private void setDayNumber(int number) {
		dayNumber = number;
		updateLayout();
	}

	public void setPrevMonth(boolean prev) {
		prevMonth = prev;
		updateLayout();
	}

	public void setNextMonth(boolean next) {
		nextMonth = next;
		updateLayout();
	}

	public void setFocused(boolean focused) {
		isFocused = focused;

		updateLayout();
	}

	public void setToday(boolean today) {
		isToday = today;
		updateLayout();
	}

	public int getYearNumber() {
		return yearNumber;
	}

	public int getMonthNumber() {
		return monthNumber;
	}

	public int getDayNumber() {
		return dayNumber;
	}

	public boolean isPrevMonth() {
		return prevMonth;
	}

	public boolean isNextMonth() {
		return nextMonth;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public boolean isToday() {
		return isToday;
	}
}
