package Manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.ArrayList;
import java.util.Scanner;

import Model.Schedule;

public class ScheduleManager {
	private static ScheduleManager singleton = new ScheduleManager();

	static final String fileName = "Scheduler.dat";
	private ArrayList<Schedule> scheduleList;
	private Callbacks scheduleCallback;

	private Date lastSyncTime;

	private ScheduleManager() {
		scheduleList = new ArrayList<Schedule>();
		scheduleCallback = null;
		lastSyncTime = new Date(0);
	}

	static public ScheduleManager sharedInstance() {
		return singleton;
	}

	public void loadSchedule() {
		Scanner scanner;
		File file = new File(fileName);
		if (file.exists()) {
			try {
				scanner = new Scanner(file);

				int count = scanner.nextInt();
				scanner.nextLine();
				for (int i = 0; i < count; i++) {
					Schedule schedule = new Schedule();
					schedule.setServerId(String.valueOf(scanner.nextInt()));
					schedule.setIndex(i);
					scanner.nextLine();
					schedule.setSubject(scanner.nextLine());
					schedule.setStartDate(new Date(scanner.nextLong()));
					schedule.setEndDate(new Date(scanner.nextLong()));
					schedule.setCreateDate(new Date(scanner.nextLong()));
					schedule.setUpdateDate(new Date(scanner.nextLong()));
					schedule.setSticker(scanner.nextInt());
					scanner.nextLine();
					schedule.setContent(scanner.nextLine());
					schedule.setIsDeleted(scanner.nextInt() != 0);
					scanner.nextLine();
					String taggedFriends = scanner.nextLine();
					String[] taggedFriendsArray = taggedFriends.split("!@#");
					ArrayList<String> taggedFriendsList = new ArrayList<>();
					for (int j = 0; j < taggedFriendsArray.length; j++) {
						if (taggedFriendsArray[0].isEmpty())
							break;
						taggedFriendsList.add(taggedFriendsArray[j]);
					}
					schedule.setTaggedFriends(taggedFriendsList);

					scheduleList.add(schedule);
				}

				lastSyncTime = new Date(scanner.nextLong());

				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveSchedule() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new File(fileName));

			writer.write(new Integer(scheduleList.size()).toString() + "\n");
			for (int i = 0; i < scheduleList.size(); i++) {
				Schedule schedule = scheduleList.get(i);
				writer.write(schedule.getServerId() + "\n");
				writer.write(schedule.getSubject() + "\n");
				writer.write(new Long(schedule.getStartDate().getTime()).toString() + "\n");
				writer.write(new Long(schedule.getEndDate().getTime()).toString() + "\n");
				writer.write(new Long(schedule.getCreateDate().getTime()).toString() + "\n");
				writer.write(new Long(schedule.getUpdateDate().getTime()).toString() + "\n");
				writer.write(String.valueOf(schedule.getStiker()) + "\n");
				writer.write(schedule.getContent() + "\n");
				writer.write(String.valueOf(schedule.getIsDeleted() ? 1 : 0) + "\n");
				ArrayList<String> taggedFriendsIdList = new ArrayList<>();
				taggedFriendsIdList = schedule.getTaggedFriendsIdArrayList();
				for (int j = 0; j < taggedFriendsIdList.size(); j++) {
					writer.write(taggedFriendsIdList.get(j) + "!@#");
				}
				writer.write("\n");
			}
			writer.write(new Long(lastSyncTime.getTime()).toString() + "\n");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setLastSyncTime(Date date) {
		lastSyncTime = date;
		saveSchedule();
	}

	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public Schedule getScheduleAtServerID(String id) {
		Schedule schedule = null;
		for (Schedule s : scheduleList) {
			if (s.getServerId().equals(id)) {
				schedule = s.copy();
				break;
			}
		}
		return schedule;
	}

	public Schedule getSchedule(int index) {
		Schedule schedule = null;

		if (0 <= index && index < scheduleList.size())
			schedule = scheduleList.get(index).copy();

		return schedule;
	}

	public ArrayList<Schedule> getSchedules() {
		return scheduleList;
	}

	public ArrayList<Schedule> getSchedules(Date startDate, Date endDate) {
		ArrayList<Schedule> returnList = new ArrayList<Schedule>();

		for (Schedule s : scheduleList) {
			if (s.checkInsetDate(startDate, endDate))
				returnList.add(s.copy());
		}

		for (int i = 0; i < scheduleList.size() - 1; i++) {
			for (int j = i + 1; j < scheduleList.size(); j++) {
				if (scheduleList.get(i).getStartDate().compareTo(scheduleList.get(j).getStartDate()) > 0) {
					Schedule s = scheduleList.get(i);
					scheduleList.set(i, scheduleList.get(j));
					scheduleList.set(j, s);
				}
			}
		}
		return returnList;
	}

	public Schedule addSchedule(Schedule schedule) {
		if (schedule == null)
			return null;

		Schedule newSchedule = new Schedule();
		newSchedule.setServerId(schedule.getServerId());
		newSchedule.setIndex(scheduleList.size());
		newSchedule.setSubject(schedule.getSubject());
		newSchedule.setStartDate(schedule.getStartDate());
		newSchedule.setEndDate(schedule.getEndDate());
		newSchedule.setCreateDate(new Date(System.currentTimeMillis()));
		newSchedule.setUpdateDate(new Date(System.currentTimeMillis()));
		newSchedule.setSticker(schedule.getStiker());
		newSchedule.setContent(schedule.getContent());
		ArrayList<String> taggedFriendsIdList = new ArrayList<>();
		taggedFriendsIdList = schedule.getTaggedFriendsIdArrayList();
		newSchedule.setTaggedFriends(taggedFriendsIdList);

		scheduleList.add(newSchedule);

		saveSchedule();

		if (scheduleCallback != null)
			scheduleCallback.onAddedSchedule(newSchedule);

		return newSchedule.copy();
	}

	public void updateSchedule(Schedule schedule) {
		if (schedule == null)
			return;

		schedule.update();
		saveSchedule();
	}

	public void deleteSchedule(Schedule schedule2bDeleted) {
		if (schedule2bDeleted == null)
			return;

		for (Schedule schedule : scheduleList) {
			if (schedule.getIndex() == schedule2bDeleted.getIndex()) {
				schedule.setIsDeleted(true);
				schedule.setUpdateDate(new Date(System.currentTimeMillis()));
			}
		}

		saveSchedule();

		if (scheduleCallback != null)
			scheduleCallback.onDeletedSchedule(schedule2bDeleted);
	}

	public void setEventListener(Callbacks callback) {
		scheduleCallback = callback;
	}

	public Callbacks getEventListener() {
		return scheduleCallback;
	}

	public interface Callbacks {
		public void onAddedSchedule(Schedule schedule);

		public void onUpdatedSchedule(Schedule schedule);

		public void onDeletedSchedule(Schedule schedule);
	}

	public static void Callbacks() {

	}
}