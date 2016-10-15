package Manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class StickerManager {
	private static StickerManager singleton = new StickerManager();

	static final String fileName = "Data/sticker.dat";

	private ArrayList<String> typeList = new ArrayList<String>();
	private ArrayList<HashMap<Integer, String>> stickerList = null;

	static public StickerManager sharedInstance() {
		return singleton;
	}

	// private HashMap<Integer, String> stickerList = new HashMap<Integer, String>();

	private void loadStickers() {
		if (stickerList != null)
			return;

		stickerList = new ArrayList<HashMap<Integer, String>>();

		Scanner scanner;
		File file = new File(fileName);
		System.out.print(file.getAbsoluteFile());
		System.out.print(file.isFile());

		if (file.exists()) {
			try {
				scanner = new Scanner(file);
				Integer line;
				String typeName;

				while (scanner.hasNext()) {
					line = scanner.nextInt();
					typeName = scanner.nextLine().substring(1);

					typeList.add(typeName);
					stickerList.add(new HashMap<Integer, String>());
					for (int i = 0; i < line; i++) {
						Integer index = scanner.nextInt();
						String url = "Data/" + scanner.nextLine().substring(1);

						stickerList.get(stickerList.size() - 1).put(index, url);
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<HashMap<Integer, String>> getStickerList() {
		loadStickers();

		return stickerList;
	}

	public ArrayList<String> getStickerTypes() {
		loadStickers();

		return typeList;
	}

	public String getStickerURL(int stickerID) {
		loadStickers();

		for (int i = 0; i < stickerList.size(); i++) {
			if (stickerList.get(i).get(stickerID) != null)
				return stickerList.get(i).get((Integer) stickerID);
		}
		return null;
	}
}
