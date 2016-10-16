package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.MenuSelectionManager;
import javax.swing.MenuElement;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import Manager.ScheduleManager;
import Model.Schedule;

public class MonthView extends JPanel {
	private static final long serialVersionUID = -1988315654293058919L;
	private static String[] monthNames = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };

	private int selectedRow = -1;
	private int selectedColumn = -1;

	private JFrame frameBody;
	private JLabel headerYearLabel;
	private JLabel headerMonthLabel;
	private JPopupMenu popupYearMenu;
	private JPopupMenu popupMonthMenu;

	private JPanel headerFrame;
	private JPanel contentFrame;

	private JTable headCalendar;
	private JTable contentCalendar;
	private CalendarModel model = new CalendarModel();

	private ActionListener menuYearActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			setCalendarMonth(Integer.valueOf(e.getActionCommand()),
					model.getMonth());
			popupYearMenu.setVisible(false);
		}
	};
	private ActionListener menuMonthActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int monthIndex = -1;
			for (int i = 0; i < 12; i++) {
				if (monthNames[i].equals(e.getActionCommand())) {
					monthIndex = i;
					break;
				}
			}
			if (monthIndex != -1)
				setCalendarMonth(model.getYear(), monthIndex + 1);

			popupMonthMenu.setVisible(false);
		}

	};

	public MonthView(JFrame body, int year, int month) {
		frameBody = body;
		model.setMonth(year, month);
		selectedRow = -1;
		selectedColumn = -1;

		headerFrame = new JPanel();
		headerFrame.setLayout(new BorderLayout());
		headerFrame.setBackground(new Color(0xCD8777));										// Background color next to Month Name

		JPanel headerText = new JPanel();
		headerText.setBackground(new Color(0xCD8777));										// Header Text Background color
		headerText.setLayout(new BorderLayout());
		headerYearLabel = new JLabel(Integer.toString(model.getYear()));
		headerYearLabel.setFont(new Font("THEJung110", 0, 30));
		headerYearLabel.setPreferredSize(new Dimension(headerYearLabel
				.getPreferredSize().width + 12, headerYearLabel
				.getPreferredSize().height + 15));
		headerYearLabel.setHorizontalAlignment(JLabel.RIGHT);
		headerYearLabel.setVerticalAlignment(JLabel.CENTER);
		headerYearLabel.setForeground(Color.WHITE);
		headerYearLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				popupYearMenu.removeAll();
				for (int i = model.getYear() - 10; i < model.getYear() + 10; i++) {
					JMenuItem item = new JMenuItem(Integer.toString(i));
					item.addActionListener(menuYearActionListener);
					popupYearMenu.add(item);
				}
				popupYearMenu.show(e.getComponent(),
						headerYearLabel.getX() + 60,
						headerYearLabel.getY() + 20);
				MenuSelectionManager.defaultManager().setSelectedPath(
						new MenuElement[] { popupYearMenu,
								(JMenuItem) popupYearMenu.getComponent(10) });
			}
		});
		
		headerText.add(headerYearLabel, BorderLayout.WEST);
		headerMonthLabel = new JLabel();
		headerMonthLabel.setText(monthNames[model.getMonth() - 1]);
		headerMonthLabel.setFont(new Font("THEJung150", 0, 26));
		headerMonthLabel.setPreferredSize(new Dimension(headerMonthLabel
				.getPreferredSize().width + 6, headerMonthLabel
				.getPreferredSize().height));
		headerMonthLabel.setHorizontalAlignment(JLabel.RIGHT);
		headerMonthLabel.setVerticalAlignment(JLabel.CENTER);
		headerMonthLabel.setForeground(Color.WHITE);
		headerMonthLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				popupMonthMenu.show(e.getComponent(),
						headerMonthLabel.getX() - 40,
						headerMonthLabel.getY() + 20);
				MenuSelectionManager.defaultManager().setSelectedPath(
						new MenuElement[] {
								popupMonthMenu,
								(JMenuItem) popupMonthMenu.getComponent(model
										.getMonth() - 1) });
			}
		});

		popupYearMenu = new JPopupMenu();
		popupMonthMenu = new JPopupMenu();
		for (String str : monthNames) {
			JMenuItem item = new JMenuItem(str);
			item.addActionListener(menuMonthActionListener);
			popupMonthMenu.add(item);
		}
		headerText.add(headerMonthLabel, BorderLayout.EAST);
		headerFrame.add(headerText, BorderLayout.WEST);

		headCalendar = new JTable(new CalendarHeadModel());
		headCalendar.getTableHeader().setReorderingAllowed(false);
		headCalendar.setColumnSelectionAllowed(false);
		headCalendar.setRowSelectionAllowed(false);
		headCalendar.setIntercellSpacing(new Dimension(0, 0));
		headCalendar.setDefaultRenderer(headCalendar.getColumnClass(0),
				new CalendarCellRenderer(true));
		headCalendar.setRowHeight(0, 30);
		headCalendar.setFont(new Font("THEJung130", 0, 14));
		headCalendar
				.setBorder(new MatteBorder(1, 0, 0, 0, new Color(0xD58F7F)));				// Borderline under Header
		headerFrame.add(headCalendar, BorderLayout.SOUTH);

		contentFrame = new JPanel();
		contentFrame.setLayout(null);
		contentCalendar = new JTable(model);
		contentCalendar.getTableHeader().setReorderingAllowed(false);
		contentCalendar.setColumnSelectionAllowed(false);
		contentCalendar.setRowSelectionAllowed(false);
		contentCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentCalendar.setIntercellSpacing(new Dimension(0, 0));
		contentCalendar.setDefaultRenderer(contentCalendar.getColumnClass(0),
				new CalendarCellRenderer(false));
		ListSelectionListener selectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = contentCalendar.getSelectedRow();
				int col = contentCalendar.getSelectedColumn();

				if (row == -1 || col == -1)
					return;

				DayView value = (DayView) contentCalendar.getValueAt(row, col);
				if (value.isPrevMonth())
					setCalendarMonth(model.getYear(), model.getMonth() - 1);
				if (value.isNextMonth())
					setCalendarMonth(model.getYear(), model.getMonth() + 1);

				setPreferredSize(getPreferredSize());
			}
		};
		contentCalendar.getSelectionModel().addListSelectionListener(
				selectionListener);
		contentCalendar.getColumnModel().getSelectionModel()
				.addListSelectionListener(selectionListener);
		contentCalendar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				int row = contentCalendar.getSelectedRow();
				int col = contentCalendar.getSelectedColumn();

				System.out.printf("%d %d\n", row, col);

				if (row == -1 || col == -1)
					return;

				if (selectedRow == row && selectedColumn == col) {
					DayDialog dialog = new DayDialog(frameBody,
							model.getYear(), model.getMonth(), ((DayView) model
									.getValueAt(row, col)).getDayNumber());
					dialog.setVisible(true);
				}

				selectedRow = row;
				selectedColumn = col;
			}
		});
		contentFrame.add(contentCalendar);

		this.setLayout(new BorderLayout());
		this.add(headerFrame, BorderLayout.NORTH);
		this.add(contentFrame, BorderLayout.CENTER);

		ScheduleManager.sharedInstance().setEventListener(
				new ScheduleManager.Callbacks() {
					@Override
					public void onUpdatedSchedule(Schedule schedule) {
						setCalendarMonth(model.getYear(), model.getMonth());
					}

					@Override
					public void onAddedSchedule(Schedule schedule) {
						setCalendarMonth(model.getYear(), model.getMonth());
					}

					@Override
					public void onDeletedSchedule(Schedule schedule) {
						setCalendarMonth(model.getYear(), model.getMonth());
					}
				});
	}

	private void setCalendarMonth(int year, int month) {
		model.setMonth(year, month);
		selectedRow = -1;
		selectedColumn = -1;

		headerYearLabel.setText(Integer.toString(model.getYear()));
		headerMonthLabel.setText(monthNames[model.getMonth() - 1]);

		headerYearLabel.setPreferredSize(new Dimension(headerYearLabel
				.getFontMetrics(headerYearLabel.getFont()).stringWidth(
						headerYearLabel.getText()) + 12, headerYearLabel
				.getPreferredSize().height));
		headerMonthLabel.setPreferredSize(new Dimension(headerMonthLabel
				.getFontMetrics(headerMonthLabel.getFont()).stringWidth(
						headerMonthLabel.getText()) + 6, headerMonthLabel
				.getPreferredSize().height));

		setPreferredSize(getPreferredSize());
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);

		int height = (preferredSize.height - headerFrame.getPreferredSize().height) - 39;
		int cellHeight = height / 6;

		contentCalendar.setBounds(0, 0, contentFrame.getSize().width, height);

		contentCalendar.setRowHeight(cellHeight);
		model.setRowSize(new Dimension(preferredSize.width / 7, cellHeight));

		contentCalendar.setRowHeight(5, height - cellHeight * 5);
		model.setRowSize(5, new Dimension(preferredSize.width / 7, height
				- cellHeight * 5));
	}

	public void setYear(int year) {
		model.setYear(year);
	}

	public void setMonth(int month) {
		model.setMonth(month);
	}

	public int getYear() {
		return model.getYear();
	}

	public int getMonth() {
		return model.getMonth();
	}
}

class CalendarHeadModel extends AbstractTableModel {
	private static final long serialVersionUID = 8654378532415125861L;

	String[] days = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public String getColumnName(int column) {
		return days[column];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return days[columnIndex];
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		days[columnIndex] = (String) aValue;
	}
}

class CalendarModel extends AbstractTableModel {
	private static final long serialVersionUID = -1892399927942291308L;

	private int yearNumber;
	private int monthNumber;

	int[] numDays = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	DayView[][] calendar = new DayView[6][7];

	public CalendarModel() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				calendar[i][j] = new DayView();
			}
		}
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public int getRowCount() {
		return 6;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return calendar[rowIndex][columnIndex];
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		calendar[rowIndex][columnIndex] = (DayView) aValue;// (String)aValue;
	}

	public void setRowSize(Dimension dimension) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				calendar[i][j].setPreferredSize(dimension);
			}
		}
	}

	public void setRowSize(int row, Dimension dimension) {
		for (int i = 0; i < 7; i++) {
			calendar[row][i].setPreferredSize(dimension);
		}
	}

	public void setYear(int year) {
		setMonth(year, monthNumber);
	}

	public void setMonth(int month) {
		setMonth(yearNumber, month);
	}

	public int getYear() {
		return yearNumber;
	}

	public int getMonth() {
		return monthNumber;
	}

	public void setMonth(int year, int month) {
		if (month < 1) {
			year--;
			month = 12;
		}
		if (month > 12) {
			year++;
			month = 1;
		}
		yearNumber = year;
		monthNumber = month;

		Date currentTime = Calendar.getInstance().getTime();
		Integer todayYear = new Integer(
				new SimpleDateFormat("yyyy").format(currentTime)).intValue();
		Integer todayMonth = new Integer(
				new SimpleDateFormat("MM").format(currentTime)).intValue();
		Integer todayDay = new Integer(
				new SimpleDateFormat("dd").format(currentTime)).intValue();

		int offset = getStartDay(yearNumber, monthNumber);
		int num = daysInMonth(yearNumber, monthNumber);
		int prevNum = daysInMonth(yearNumber, monthNumber - 1);
		int prevStart = prevNum - offset % 7;
		for (int i = prevStart; i < prevNum; ++i) {
			calendar[0][i - prevStart].setDate(year, month - 1, i + 1);
			calendar[0][i - prevStart].setPrevMonth(true);
			calendar[0][i - prevStart].setNextMonth(false);
			if (todayYear == year && todayMonth == month - 1
					&& i + 1 == todayDay)
				calendar[0][i - prevStart].setToday(true);
			else
				calendar[0][i - prevStart].setToday(false);
		}
		for (int i = 0; i < num; ++i, ++offset) {
			calendar[offset / 7][offset % 7].setDate(year, month, i + 1);
			calendar[offset / 7][offset % 7].setPrevMonth(false);
			calendar[offset / 7][offset % 7].setNextMonth(false);
			if (todayYear == year && todayMonth == month && i + 1 == todayDay)
				calendar[offset / 7][offset % 7].setToday(true);
			else
				calendar[offset / 7][offset % 7].setToday(false);
		}
		for (int i = 0; offset < 6 * 7; ++i, ++offset) {
			calendar[offset / 7][offset % 7].setDate(year, month + 1, i + 1);
			calendar[offset / 7][offset % 7].setPrevMonth(false);
			calendar[offset / 7][offset % 7].setNextMonth(true);
			if (todayYear == year && todayMonth == month + 1
					&& i + 1 == todayDay)
				calendar[offset / 7][offset % 7].setToday(true);
			else
				calendar[offset / 7][offset % 7].setToday(false);
		}
		fireTableDataChanged();
	}

	private int getStartDay(int year, int month) {
		final int START_DAY_FOR_JAN_1_1800 = 3;
		int totalNumberOfDays = getTotalNumberOfDays(year, month);

		return (totalNumberOfDays + START_DAY_FOR_JAN_1_1800) % 7;
	}

	private int getTotalNumberOfDays(int year, int month) {
		int total = 0;

		int yearCount = year - 1800;
		total = yearCount * 365 + yearCount / 400 + yearCount / 4 - yearCount
				/ 100 + (isLeapYear(year) ? 0 : 1);

		for (int i = 1; i < month; i++)
			total = total + daysInMonth(year, i);

		return total;
	}

	private boolean isLeapYear(int year) {
		return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
	}

	private int daysInMonth(int year, int month) {
		if (month < 1) {
			year--;
			month = 12;
		}
		if (month > 12) {
			year++;
			month = 1;
		}
		int days = numDays[month];
		if (month == 2 && isLeapYear(year))
			++days;
		return days;
	}
}

class CalendarCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 5214495388243952251L;

	private boolean isHeadCell = false;

	public CalendarCellRenderer(boolean isHead) {
		isHeadCell = isHead;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean selected, boolean focused, int row, int column) {
		if (isHeadCell) {
			setHorizontalAlignment(JLabel.CENTER);
			setBackground(new Color(0xD58F7F));											// Weekday Backgound Color
			setForeground(Color.WHITE);

			super.getTableCellRendererComponent(table, value, false, false,
					row, column);

			return this;
		} else {
			if (row == table.getSelectedRow()
					&& column == table.getSelectedColumn())
				focused = true;

			if (value != null)
				((DayView) value).setFocused(focused);

			return (Component) value;
		}
	}
}