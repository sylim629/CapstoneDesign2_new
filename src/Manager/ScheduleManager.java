package Manager;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;

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

	public void deleteScheduleFile() {
		File file = new File(fileName);
		if (file.delete()) {
			System.out.println("File deleted successfully");
		}
	}
	
	public void setLastSyncTime(Date date) {
		lastSyncTime = date;
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
		// JSONArray taggedFriends = schedule.getTaggedFriends();
		ArrayList<String> taggedFriendsIdList = new ArrayList<>();
		taggedFriendsIdList = schedule.getTaggedFriendsIdArrayList();
		// for (int i = 0; i < taggedFriends.size(); i++) {
		// JSONObject taggedFriendObj = (JSONObject) taggedFriends.get(i);
		// taggedFriendsIdList.add((String) taggedFriendObj.get("user_id"));
		// }
		newSchedule.setTaggedFriends(taggedFriendsIdList);

		scheduleList.add(newSchedule);

		if (scheduleCallback != null)
			scheduleCallback.onAddedSchedule(newSchedule);

		return newSchedule.copy();
	}

	public void updateSchedule(Schedule schedule) {
		if (schedule == null)
			return;

		schedule.update();
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