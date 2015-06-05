package info.hb.apriori.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aprori {

	static boolean endTag = false;
	static Map<Integer, Integer> dCountMap = new HashMap<Integer, Integer>();
	static Map<Integer, Integer> dkCountMap = new HashMap<Integer, Integer>();
	static List<List<String>> record = new ArrayList<List<String>>();
	final static double MIN_SUPPORT = 0.2;
	final static double MIN_CONF = 0.8;
	static int lable = 1;
	static List<Double> confCount = new ArrayList<Double>();
	static List<List<String>> confItemset = new ArrayList<List<String>>();

	public static void main(String[] args) {
		record = getRecord();
		List<List<String>> cItemset = findFirstCandidate();
		List<List<String>> lItemset = getSupportedItemset(cItemset);

		while (endTag != true) {
			List<List<String>> ckItemset = getNextCandidate(lItemset);
			List<List<String>> lkItemset = getSupportedItemset(ckItemset);
			getConfidencedItemset(lkItemset, lItemset, dkCountMap, dCountMap);
			if (confItemset.size() != 0)
				printConfItemset(confItemset);
			confItemset.clear();
			cItemset = ckItemset;
			lItemset = lkItemset;
			dCountMap.clear();
			dCountMap.putAll(dkCountMap);
		}

	}

	private static void printConfItemset(List<List<String>> confItemset2) {
		System.out.print("*********Print***********\n");
		for (int i = 0; i < confItemset2.size(); i++) {
			int j = 0;
			for (j = 0; j < confItemset2.get(i).size() - 3; j++)
				System.out.print(confItemset2.get(i).get(j) + " ");
			System.out.print("-->");
			System.out.print(confItemset2.get(i).get(j++));
			System.out.print("" + confItemset2.get(i).get(j++));
			System.out.print("" + confItemset2.get(i).get(j++) + "\n");
		}
	}

	private static List<List<String>> getConfidencedItemset(List<List<String>> lkItemset, List<List<String>> lItemset,
			Map<Integer, Integer> dkCountMap2, Map<Integer, Integer> dCountMap2) {
		for (int i = 0; i < lkItemset.size(); i++) {
			getConfItem(lkItemset.get(i), lItemset, dkCountMap2.get(i), dCountMap2);

		}
		return null;
	}

	private static List<String> getConfItem(List<String> list, List<List<String>> lItemset, Integer count,
			Map<Integer, Integer> dCountMap2) {
		for (int i = 0; i < list.size(); i++) {
			List<String> testList = new ArrayList<String>();
			for (int j = 0; j < list.size(); j++)
				if (i != j)
					testList.add(list.get(j));
			int index = findConf(testList, lItemset);
			Double conf = count * 1.0 / dCountMap2.get(index);
			if (conf > MIN_CONF) {
				testList.add(list.get(i));
				Double relativeSupport = count * 1.0 / (record.size() - 1);
				testList.add(relativeSupport.toString());
				testList.add(conf.toString());
				confItemset.add(testList);
			}
		}
		return null;
	}

	private static int findConf(List<String> testList, List<List<String>> lItemset) {
		for (int i = 0; i < lItemset.size(); i++) {
			boolean notHaveTag = false;
			for (int j = 0; j < testList.size(); j++) {
				if (haveThisItem(testList.get(j), lItemset.get(i)) == false) {
					notHaveTag = true;
					break;
				}
			}
			if (notHaveTag == false)
				return i;
		}
		return -1;
	}

	private static boolean haveThisItem(String string, List<String> list) {
		for (int i = 0; i < list.size(); i++)
			if (string.equals(list.get(i)))
				return true;
		return false;
	}

	private static List<List<String>> getRecord() {
		TxtReader readRecord = new TxtReader();
		return readRecord.getRecord();
	}

	private static List<List<String>> getSupportedItemset(List<List<String>> cItemset) {
		boolean end = true;
		List<List<String>> supportedItemset = new ArrayList<List<String>>();
		int k = 0;
		for (int i = 0; i < cItemset.size(); i++) {
			int count = countFrequent(cItemset.get(i));
			if (count >= MIN_SUPPORT * (record.size() - 1)) {
				if (cItemset.get(0).size() == 1)
					dCountMap.put(k++, count);
				else
					dkCountMap.put(k++, count);
				supportedItemset.add(cItemset.get(i));
				end = false;
			}
		}
		endTag = end;
		return supportedItemset;
	}

	private static int countFrequent(List<String> list) {
		int count = 0;
		for (int i = 1; i < record.size(); i++) {
			boolean notHavaThisList = false;
			for (int k = 0; k < list.size(); k++) {
				boolean thisRecordHave = false;
				for (int j = 1; j < record.get(i).size(); j++) {
					if (list.get(k).equals(record.get(i).get(j)))
						thisRecordHave = true;
				}
				if (!thisRecordHave) {
					notHavaThisList = true;
					break;
				}
			}
			if (notHavaThisList == false)
				count++;
		}
		return count;
	}

	private static List<List<String>> getNextCandidate(List<List<String>> cItemset) {
		List<List<String>> nextItemset = new ArrayList<List<String>>();
		for (int i = 0; i < cItemset.size(); i++) {
			List<String> tempList = new ArrayList<String>();
			for (int k = 0; k < cItemset.get(i).size(); k++)
				tempList.add(cItemset.get(i).get(k));
			for (int h = i + 1; h < cItemset.size(); h++) {
				for (int j = 0; j < cItemset.get(h).size(); j++) {
					tempList.add(cItemset.get(h).get(j));
					if (isSubsetInC(tempList, cItemset)) {
						List<String> copyValueHelpList = new ArrayList<String>();
						for (int p = 0; p < tempList.size(); p++)
							copyValueHelpList.add(tempList.get(p));
						if (isHave(copyValueHelpList, nextItemset))
							nextItemset.add(copyValueHelpList);
					}
					tempList.remove(tempList.size() - 1);
				}
			}
		}

		return nextItemset;
	}

	private static boolean isHave(List<String> copyValueHelpList, List<List<String>> nextItemset) {
		for (int i = 0; i < nextItemset.size(); i++)
			if (copyValueHelpList.equals(nextItemset.get(i)))
				return false;
		return true;
	}

	private static boolean isSubsetInC(List<String> tempList, List<List<String>> cItemset) {
		boolean haveTag = false;
		for (int i = 0; i < tempList.size(); i++) {
			List<String> testList = new ArrayList<String>();
			for (int j = 0; j < tempList.size(); j++)
				if (i != j)
					testList.add(tempList.get(j));
			for (int k = 0; k < cItemset.size(); k++) {
				if (testList.equals(cItemset.get(k))) {
					haveTag = true;
					break;
				}
			}
			if (haveTag == false)
				return false;
		}

		return haveTag;
	}

	private static List<List<String>> findFirstCandidate() {
		List<List<String>> tableList = new ArrayList<List<String>>();
		List<String> lineList = new ArrayList<String>();

		int size = 0;
		for (int i = 1; i < record.size(); i++) {
			for (int j = 1; j < record.get(i).size(); j++) {
				if (lineList.isEmpty()) {
					lineList.add(record.get(i).get(j));
				} else {
					boolean haveThisItem = false;
					size = lineList.size();
					for (int k = 0; k < size; k++) {
						if (lineList.get(k).equals(record.get(i).get(j))) {
							haveThisItem = true;
							break;
						}
					}
					if (haveThisItem == false)
						lineList.add(record.get(i).get(j));
				}
			}
		}
		for (int i = 0; i < lineList.size(); i++) {
			List<String> helpList = new ArrayList<String>();
			helpList.add(lineList.get(i));
			tableList.add(helpList);
		}
		return tableList;
	}

}
