package Manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
					Schedule obj = new Schedule();
					obj.setServerId(String.valueOf(scanner.nextInt()));
					obj.setIndex(i);
					scanner.nextLine();
					obj.setSubject(scanner.nextLine());
					obj.setStartDate(new Date(scanner.nextLong()));
					obj.setEndDate(new Date(scanner.nextLong()));
					obj.setCreateDate(new Date(scanner.nextLong()));
					obj.setUpdateDate(new Date(scanner.nextLong()));
					obj.setSticker(scanner.nextInt());
					scanner.nextLine();
					obj.setContent(scanner.nextLine());
					obj.setIsDeleted(scanner.nextInt() != 0);
					String taggedFriends = scanner.nextLine();
					String[] taggedFriendsArray = taggedFriends.split("!@#");
					ArrayList<String> taggedFriendsList = new ArrayList<>();
					for (int j = 0; j < taggedFriendsArray.length; j++) {
						taggedFriendsList.add(taggedFriendsArray[j]);
					}
					obj.setTaggedFriends(taggedFriendsList);

					scheduleList.add(obj);
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
				Schedule obj = scheduleList.get(i);
				writer.write(obj.getServerId() + "\n");
				writer.write(obj.getSubject() + "\n");
				writer.write(new Long(obj.getStartDate().getTime()).toString() + "\n");
				writer.write(new Long(obj.getEndDate().getTime()).toString() + "\n");
				writer.write(new Long(obj.getCreateDate().getTime()).toString() + "\n");
				writer.write(new Long(obj.getUpdateDate().getTime()).toString() + "\n");
				writer.write(String.valueOf(obj.getStiker()) + "\n");
				writer.write(obj.getContent() + "\n");
				writer.write(String.valueOf(obj.getIsDeleted() ? 1 : 0) + "\n");
//				writer.write(obj.getTaggedFriends() + "\n");
				JSONArray taggedFriends = obj.getTaggedFriends();
				ArrayList<String> taggedFriendsIdList = new ArrayList<>();
				for (int j = 0; j < taggedFriends.size(); j++) {
					JSONObject taggedFriendObj = (JSONObject) taggedFriends.get(j);
					taggedFriendsIdList.add((String) taggedFriendObj.get("user_id"));
				}
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

		if (0 <= index && index < scheduleList.size()) {
			schedule = scheduleList.get(index).copy();
		}

		return schedule;
	}

	public ArrayList<Schedule> getSchedules() {
		ArrayList<Schedule> returnList = new ArrayList<Schedule>();

		for (Schedule s : scheduleList)
			returnList.add(s.copy());

		return returnList;
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

		Schedule obj = new Schedule();
		obj.setServerId(schedule.getServerId());
		obj.setIndex(scheduleList.size());
		obj.setSubject(schedule.getSubject());
		obj.setStartDate(schedule.getStartDate());
		obj.setEndDate(schedule.getEndDate());
		obj.setCreateDate(new Date(System.currentTimeMillis()));
		obj.setUpdateDate(new Date(System.currentTimeMillis()));
		obj.setSticker(schedule.getStiker());
		obj.setContent(schedule.getContent());
		JSONArray taggedFriends = schedule.getTaggedFriends();
		ArrayList<String> taggedFriendsIdList = new ArrayList<>();
		for (int i = 0; i < taggedFriends.size(); i++) {
			JSONObject taggedFriendObj = (JSONObject) taggedFriends.get(i);
			taggedFriendsIdList.add((String) taggedFriendObj.get("user_id"));
		}
		obj.setTaggedFriends(taggedFriendsIdList);

		scheduleList.add(obj);

		saveSchedule();

		if (scheduleCallback != null)
			scheduleCallback.onAddedSchedule(obj);

		return obj.copy();
	}

	public void updateSchedule(Schedule schedule) {
		if (schedule == null)
			return;

		schedule.update();

		saveSchedule();
	}

	public void deleteSchedule(Schedule schedule) {
		if (schedule == null)
			return;

		// ArrayList<Schedule> deleteList = new ArrayList<Schedule>();

		for (Schedule s : scheduleList) {
			if (s.getIndex() == schedule.getIndex()) {
				s.setIsDeleted(true);
				s.setUpdateDate(new Date(System.currentTimeMillis()));
			}
			// deleteList.add(s);
		}
		// for (Schedule s : deleteList) {
		// scheduleList.remove(s);
		// }

		saveSchedule();

		if (scheduleCallback != null)
			scheduleCallback.onDeletedSchedule(schedule);
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
		// TODO Auto-generated method stub

	}
}