package Manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Model.Schedule;

public class SyncManager {

	static private SyncManager sharedInstance = new SyncManager();

	static public SyncManager sharedInstance() {
		return sharedInstance;
	}

	public SyncManager() {

	}

	@SuppressWarnings("unchecked")
	public Boolean synchronizeServer() {
		Date syncTime = ScheduleManager.sharedInstance().getLastSyncTime();
		ArrayList<Schedule> scheduleList = ScheduleManager.sharedInstance().getSchedules();
		ArrayList<Schedule> syncList = new ArrayList<Schedule>();

		// Insert Data
		StringBuffer buffer = null;
		JSONArray jsonArray = new JSONArray();
		for (Schedule s : scheduleList) {
			if (s.getUpdateDate().after(syncTime)) {
				JSONObject obj = new JSONObject();
				obj.put("id", s.getServerId());
				obj.put("title", s.getSubject());
				obj.put("content", s.getContent());
				obj.put("startdate", s.getStartDate().getTime());
				obj.put("enddate", s.getEndDate().getTime());
				obj.put("created", s.getCreateDate().getTime());
				obj.put("updated", s.getUpdateDate().getTime());
				obj.put("is_deleted", s.getIsDeleted());
				obj.put("sticker", s.getStiker());
				JSONArray taggedFriends = s.getTaggedFriends();
				ArrayList<String> taggedFriendsIdList = new ArrayList<>();
				for (int i = 0; i < taggedFriends.size(); i++) {
					JSONObject taggedFriendObj = (JSONObject) taggedFriends.get(i);
					taggedFriendsIdList.add((String) taggedFriendObj.get("user_id"));
				}
				obj.put("tagged", (Object) s.getTaggedFriends());
				// JSONObject objTaggedFriends = new JSONObject();
				// for (String[] str : s.getTaggedFriends) {
				// objTaggedFriends.put("user_id", str[0]);
				// }
				jsonArray.add(obj);
				// jsonArray.add(objTaggedFriends);
				syncList.add(s);
			}
		}
		buffer = new StringBuffer(JSONValue.toJSONString(jsonArray));

		// Synchronize Request
		try {
			URL u = new URL("http://leannelim0629.cafe24.com/sync/");
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("charset", "utf-8");

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write("update_time=" + String.valueOf(syncTime.getTime() / 1000) + "&session_key="
					+ LoginManager.sharedInstance().getSessionKey() + "&sync_data=" + buffer.toString());
			wr.flush();
			// 여기까지 서버로 정보를 전달 후 php가 알아서 처리. ㅇㅇ.

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			buffer = new StringBuffer();
			String line = null;
			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			wr.close();
			rd.close();
			conn.disconnect();

			System.out.println(buffer);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Remove Old Schedule
		for (Schedule s : syncList) {
			if (s.getServerId().equals("-1"))
				ScheduleManager.sharedInstance().deleteSchedule(s);
		}
		// System.out.print(buffer);
		// Update Schedule
		try {
			JSONParser parser = new JSONParser();
			JSONObject dataObject = (JSONObject) parser.parse(buffer.toString());

			if (Long.parseLong((String) dataObject.get("code")) == 200) {
				dataObject = (JSONObject) dataObject.get("data");

				JSONArray serverSchedule = (JSONArray) dataObject.get("schedule");
				for (int i = 0; i < serverSchedule.size(); i++) {
					JSONObject obj = (JSONObject) serverSchedule.get(i);
					Schedule sc = ScheduleManager.sharedInstance().getScheduleAtServerID((String) obj.get("id"));
					if (sc == null)
						sc = ScheduleManager.sharedInstance().addSchedule(new Schedule());

					sc.setServerId((String) obj.get("id"));
					sc.setSubject((String) obj.get("title"));
					sc.setContent((String) obj.get("content"));
					sc.setStartDate(new Date(Long.parseLong((String) obj.get("startdate"))));
					sc.setEndDate(new Date(Long.parseLong((String) obj.get("enddate"))));
					sc.setCreateDate(new Date(Long.parseLong((String) obj.get("created_date"))));
					sc.setUpdateDate(new Date(Long.parseLong((String) obj.get("updated_date"))));
					sc.setIsDeleted(Long.parseLong((String) obj.get("is_deleted")) != 0);
					sc.setSticker(Integer.parseInt((String) obj.get("sticker")));
					JSONArray taggedFriendsArray = (JSONArray) dataObject.get("tagged");
					ArrayList<String> friendsArrayList = new ArrayList<String>();
					for (int j = 0; j < taggedFriendsArray.size(); j++) {
						JSONObject friendIdObj = (JSONObject) taggedFriendsArray.get(j);
						friendsArrayList.add((String) friendIdObj.get("user_id"));
					}
					sc.setTaggedFriends(friendsArrayList);
					ScheduleManager.sharedInstance().updateSchedule(sc);
				}

				ScheduleManager.sharedInstance().setLastSyncTime(new Date((Long) dataObject.get("sync_date") * 1000));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return true;
	}
}
