package info.hb.apriori.simple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TxtReader {

	public List<List<String>> getRecord() {
		List<List<String>> record = new ArrayList<List<String>>();
		try {
			String encoding = "GBK";
			File file = new File("data/simple.txt");
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = bufferedReader.readLine()) != null) {
					String[] lineString = lineTXT.split("	");
					List<String> lineList = new ArrayList<String>();
					for (int i = 0; i < lineString.length; i++) {
						if (lineString[i].endsWith("T") || lineString[i].endsWith("YES"))
							lineList.add(record.get(0).get(i));
						else if (lineString[i].endsWith("F") || lineString[i].endsWith("NO"))
							;
						else
							lineList.add(lineString[i]);
					}
					record.add(lineList);
				}
				read.close();
			} else {
				System.out.println("XX");
			}
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		return record;
	}

}
