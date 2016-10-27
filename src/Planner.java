import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;

import Manager.FriendsList;
import Manager.LoginManager;
import Manager.ScheduleManager;
import Manager.SyncManager;
import View.MonthView;

import java.util.Timer;
import java.util.TimerTask;

public class Planner extends JFrame {
	private static final long serialVersionUID = 7594201658394504244L;

	private MonthView monthView;

	private int yearNumber;
	private int monthNumber;
	private static Timer timer = null;

	public Planner() {
		Date currentTime = Calendar.getInstance().getTime();
		yearNumber = new Integer(new SimpleDateFormat("yyyy").format(currentTime)).intValue();
		monthNumber = new Integer(new SimpleDateFormat("MM").format(currentTime)).intValue();

		monthView = new MonthView(this, yearNumber, monthNumber);
		this.add(monthView);

		this.setTitle(new Integer(yearNumber).toString() + " Planner");

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				monthView.setSize(getSize());
				monthView.setPreferredSize(getSize());
			}
		});
	}

	static public void main(String[] args) {
		ScheduleManager.sharedInstance().loadSchedule();

		Planner planner = new Planner();
		planner.pack();
		planner.setSize(800, 600);
		planner.setMinimumSize(new Dimension(340, 510));
		planner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		planner.setVisible(true);

		planner.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}
		});

		LoginManager.sharedInstance().loginFacebook();
		new FriendsList().printfriend();

		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				SyncManager.sharedInstance().synchronizeServer();
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), 20000); // 20초마다 동기화
	}
}