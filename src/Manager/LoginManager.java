package Manager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LoginManager {

	private static LoginManager sharedInstance = new LoginManager();

	private String accessToken = "";
	private String sessionID = "";

	static public LoginManager sharedInstance() {
		return sharedInstance;
	}

	public LoginManager() {

	}

	public String getaccessTok() {
		return accessToken;
	}

	public String getSessionKey() {
		return sessionID;
	}

	public void loginFacebook() {
		accessToken = getAccessToken();
		URL url;
		try {
			url = new URL("http://leannelim0629.cafe24.com/login/");
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("charset", "utf-8");

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write("access_token=" + URLEncoder.encode(accessToken, "UTF-8"));
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			wr.close();
			rd.close();
			conn.disconnect();

			System.out.println(buffer);

			JSONParser parser = new JSONParser();
			JSONObject dataObject = null;
			try {
				dataObject = (JSONObject) parser.parse(buffer.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			sessionID = (String) ((JSONObject) dataObject.get("data")).get("session_key");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getAccessToken() {
		String url = null;
		try {
			// https://www.facebook.com/dialog/oauth?
			// client_id={app-id}
			// &redirect_uri={redirect-uri}
			url = "https://www.facebook.com/dialog/oauth?client_id=1805924592999036&redirect_uri="
					+ URLEncoder.encode("http://localhost:3306/", "UTF-8")
					+ "&client_secret=82d8278ef1aa1b46f619ae4faa4c4b4d&scope=user_friends";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String accessToken = "";
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(3306);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			String OS = System.getProperty("os.name").toLowerCase();
			if (OS.indexOf("win") >= 0) {
				String runURL = "cmd.exe /C \"start iexplore.exe \"" + url + "\"\"";
				Runtime.getRuntime().exec(runURL);
			} else
				Runtime.getRuntime().exec("open " + url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Socket socket = serverSocket.accept();

			InputStream in = socket.getInputStream();
			BufferedReader dis = new BufferedReader(new InputStreamReader(in));

			accessToken = dis.readLine().split(" ")[1].split("/?code=")[1];

			OutputStream out = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);

			dos.writeBytes("HTTP/1.1 200 OK\r\n");
			dos.writeBytes("Cache-Control: no-cache\r\n");
			dos.writeBytes("Connection: close\r\n");
			dos.writeBytes("Content-Type: text/html; charset=UTF-8\r\n");
			dos.writeBytes("\r\n");
			dos.writeBytes("<script type='text/javascript'>window.open('', '_self');window.close();</script>");

			dis.close();
			dos.close();
			socket.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// https://graph.facebook.com/v2.3/oauth/access_token?
			// client_id={app-id}
			// &redirect_uri={redirect-uri}
			// &client_secret={app-secret}
			// &code={code-parameter}
			url = "https://graph.facebook.com/v2.3/oauth/access_token?client_id=1805924592999036&redirect_uri="
					+ URLEncoder.encode("http://localhost:3306/", "UTF-8")
					+ "&client_secret=82d8278ef1aa1b46f619ae4faa4c4b4d&code=" + accessToken;
			HttpURLConnection conn;
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("charset", "utf-8");

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			rd.close();
			conn.disconnect();

			accessToken = buffer.toString().split("access_token\":\"")[1].split("\",\"token_type")[0];
			System.out.println(accessToken);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return accessToken;
	}

}
