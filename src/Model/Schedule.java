package Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Manager.ScheduleManager;

public class Schedule {
	private Schedule parent;
	private String serverId;
	private int Index;
	private String Subject;
	private String Content;
	private Date StartDate;
	private Date EndDate;
	private Date CreateDate;
	private Date UpdateDate;
	private Boolean isDeleted;
	private int sticker;
	private JSONArray taggedFriends;

	public Schedule() {
		parent = null;
		serverId = "-1";
		Index = new Random().nextInt();
		Subject = new String();
		Content = new String();
		StartDate = new Date(0);
		EndDate = new Date(0);
		CreateDate = new Date(0);
		UpdateDate = new Date(0);
		isDeleted = false;
		sticker = 0;
		taggedFriends = new JSONArray();
	}

	public Schedule(Schedule schedule) {
		parent = schedule;
		serverId = schedule.serverId;
		Index = schedule.Index;
		Subject = schedule.Subject;
		Content = schedule.Content;
		StartDate = schedule.StartDate;
		EndDate = schedule.EndDate;
		CreateDate = schedule.CreateDate;
		UpdateDate = schedule.UpdateDate;
		isDeleted = schedule.isDeleted;
		sticker = schedule.sticker;
		taggedFriends = schedule.taggedFriends;
	}

	public Schedule copy() {
		return new Schedule(this);
	}

	public String getServerId() {
		return serverId;
	}

	public int getIndex() {
		return Index;
	}

	public String getSubject() {
		return Subject;
	}

	public String getContent() {
		return Content;
	}

	public Date getStartDate() {
		return StartDate;
	}

	public Date getEndDate() {
		return EndDate;
	}

	public Date getCreateDate() {
		return CreateDate;
	}

	public Date getUpdateDate() {
		return UpdateDate;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public int getStiker() {
		return sticker;
	}

	public JSONArray getTaggedFriends() {
		return taggedFriends;
	}
	
	public ArrayList<String> getTaggedFriendsIdArrayList() {
		ArrayList<String> idList = new ArrayList<>();
		for (int i = 0; i < taggedFriends.size(); i++) {
			JSONObject taggedFriendObj = (JSONObject) taggedFriends.get(i);
			idList.add((String) taggedFriendObj.get("user_id"));
		}
		return idList;
	}

	public void setServerId(String id) {
		serverId = id;
	}

	public void setIndex(int index) {
		Index = index;
	}

	public void setSubject(String subject) {
		Subject = subject;
	}

	public void setContent(String content) {
		Content = content;
	}

	public void setStartDate(Date date) {
		StartDate = date;
	}

	public void setEndDate(Date date) {
		EndDate = date;
	}

	public void setCreateDate(Date date) {
		CreateDate = date;
	}

	public void setUpdateDate(Date date) {
		UpdateDate = date;
	}

	public void setIsDeleted(Boolean deleted) {
		isDeleted = deleted;
	}

	public void setSticker(int icon) {
		sticker = icon;
	}
	
	public void setTaggedFriendsDirect(JSONArray friendsArray) {
		taggedFriends = friendsArray;
	}

	@SuppressWarnings("unchecked")
	public void setTaggedFriends(ArrayList<String> friendsIdList) {
		for (int i = 0; i < friendsIdList.size(); i++) {
			JSONObject obj = new JSONObject();
			obj.put("user_id", friendsIdList.get(i));
			obj.put("is_facebook", 1);
			taggedFriends.add(obj);
		}
	}

	public boolean checkInsetDate(Date date) {
		if (isDeleted)
			return false;

		if (StartDate.getTime() <= date.getTime() && date.getTime() <= EndDate.getTime())
			return true;
		return false;
	}

	public boolean checkInsetDate(Date date1, Date date2) {
		if (isDeleted)
			return false;

		long maxDist = Math.abs(date1.getTime() - date2.getTime()) + Math.abs(StartDate.getTime() - EndDate.getTime());
		long dist = Math.max(Math.abs(date1.getTime() - EndDate.getTime()),
				Math.abs(date2.getTime() - StartDate.getTime()));

		if (dist <= maxDist)
			return true;
		return false;
	}

	public boolean update() {
		if (parent == null)
			return false;

		if (isEqual(parent))
			return false;

		parent.serverId = serverId;
		parent.Subject = Subject;
		parent.Content = Content;
		parent.StartDate = StartDate;
		parent.EndDate = EndDate;
		parent.CreateDate = CreateDate;
		parent.UpdateDate = new Date(System.currentTimeMillis());
		parent.isDeleted = isDeleted;
		parent.sticker = sticker;
		parent.taggedFriends = taggedFriends;

		if (!parent.update()) {
			if (ScheduleManager.sharedInstance().getEventListener() != null)
				ScheduleManager.sharedInstance().getEventListener().onUpdatedSchedule(this);
		}
		return true;
	}

	public boolean isEqual(Schedule schedule) {
		if (parent.serverId != serverId)
			return false;

		if (parent.Subject != Subject)
			return false;

		if (parent.Content != Content)
			return false;

		if (parent.StartDate != StartDate)
			return false;

		if (parent.EndDate != EndDate)
			return false;

		if (parent.CreateDate != CreateDate)
			return false;

		if (parent.isDeleted != isDeleted)
			return false;

		if (parent.sticker != sticker)
			return false;

		if (parent.taggedFriends != taggedFriends)
			return false;

		return true;
	}

	@Override
	public String toString() {
		return new String("{ Schedule:{ Subject:\"" + Subject + "\", Content:\"" + Content + "\", StartDate:\""
				+ StartDate.toString() + "\", EndDate:\"" + EndDate.toString() + "\" } }");
	}
}
