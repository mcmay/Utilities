/**
 * An application that records the start and
 * finish time for recharging my iPhone
 * @version started 0.1 25/07/2020
 * @author Michael Mei
 */
package rechargeRecorder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.net.*;

/**
 * Latest TODOs
 * 1) Finish modifyRecords
 * 2) Finish searchByDate
 * 3) Finish searchByChargedPercent of current device 
 */

public class DeviceBatteryRechargeRecorder {
	public static final int CAPACITY = 1000;
	public static final String timezone = "Australia/Melbourne";
	private static File recordsFile;
	private static File lastLogin;
	private static ArrayList<Record> records = new ArrayList<>(CAPACITY);
	private static String userName;
	private static String deviceName;
	private static Scanner in = new Scanner(System.in);
	private static Boolean modified = false;

	public static void main(String[] args) {
		int choice = -1;

		login();
		while (choice != 11) {
			System.out.println("\nI wish to: ");
			System.out.println("1) Create a new recharge record for the current device");
			System.out.println("2) Create a new recharge record for a different device");
			System.out.println("3) Finish last recharge record for the current device");
			System.out.println("4) Finish last recharge record for a different device");
			System.out.println("5) Search for a record");
			System.out.println("6) Modify a record");
			System.out.println("7) Delete unfinished records");
			System.out.println("8) Display records");
			System.out.println("9) Export reocrds to text file.");
			System.out.println("10) Import reocrds from text file.");
			System.out.println("11) Quit");
			System.out.print("Enter your choice: ");
			try {
				choice = in.nextInt(); 
				in.nextLine();
				switch (choice) {
					case 1:
						addRecord(true);
						break;
					case 2:
						addRecord(false);
						break;
					case 3:
						finishRecord(true);
						break;
					case 4:
						finishRecord(false);
						break;
					case 5:
						searchRecords();
						break;
					case 6:
						modifyRecords();
						break;
					case 7:
						deleteRecord();
						break;
					case 8:
						displayRecords();
						break;
					case 9:
						exportRecords(records);
						break;
					case 10:
						importRecords();
						break;
					case 11:
						break;
					default:
						System.out.println("Invalid input.");
						break;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Invalid choice.");
			}
		}
		quit("Bye, " + userName + "!");
	}
	private static void login () {
		String user = null, device = null;
		Boolean needAsk = false;

		lastLogin = new File("lastLogin.txt");
		if (lastLogin.exists()) {
			try (Scanner in = new Scanner(new FileInputStream(lastLogin))) {
				if (in.hasNextLine()) {
					user = in.nextLine();
					device = in.nextLine();
				}
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
			if (user != null && device != null) {
				System.out.println("Log in last user " + user + " and device " + device + "? (Y/n)");
				if (getStringInput(true).equals("Y")) {
					userName = user;
					deviceName = device;
				}
				else // if user does not want to log in last user and device ask for user name and device
					needAsk = true;
			}
		} 
		else // if lastLogin file does not exist, ask for user name and device name
			needAsk = true;	
		if (needAsk) {
			System.out.print("Enter your user name: ");
			userName = getStringInput(false);
			System.out.println("Welcome, " + userName + "!");
			System.out.print("Now enter the name of your device: ");
			deviceName = getStringInput(false);
		}
		recordsFile = new File("records.dat");
		// When the first user of this program runs it, the record file has not existed yet
		if (!recordsFile.exists()) 
			askUserToAddAccount();
		else { 
			ObjectInputStream ois = null;
			try  {
				ois = new ObjectInputStream(new FileInputStream(recordsFile));
				Record[] recs = (Record[]) ois.readObject();
				if (recs.length > 0) {
					for (Record rec : recs) {// collect substantial records from recs
						if (rec == null)
							break;
						records.add(rec);
					} // The statement below is a silent internal search for current user's current device.
					ArrayList<Record> fndRecs = searchByDeviceUnderUser(records, true, true); 
					// When user not found
					if (fndRecs == null || fndRecs.size() == 0) 
						askUserToAddAccount();	
				}
				else { // when empty record file encountered
					System.out.println("No record in the file.");
					askUserToAddAccount();
				}
			}
			catch (ClassNotFoundException cnfEx) {
				cnfEx.printStackTrace();
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}
			try {
				ois.close();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}
		}
	}
	private static String getStringInput (Boolean toUpperCase) {
		String input = in.nextLine().trim();	

		while (input.isEmpty()) {
			System.out.print("Invalid input. Please enter again: ");
			input = in.nextLine().trim();
		}
		if (toUpperCase)
			input = input.toUpperCase();

		return input;
	}
	private static void askUserToAddAccount () {
		System.out.println("Cannot find your user name or device.");
		System.out.println("Start a new account and create your first record of device battery recharge? (Y/n)");
		String response = getStringInput(true);
		if (response.equals("Y")) 
			addRecord(true);
		else if (response.equals("N")) {
			quit("That's ok. Maybe next time.");
		}
		else 
			System.out.println("Invalid input. Please make sure before you enter your choice.");
	}
	private static void addRecord (Boolean internal) {
		String device;
		Boolean afterUse = false;

		if (records.size() == CAPACITY) {
			System.out.println("Maximum records reached. Cannot add record.");
			return;
		}
		if (!internal) {
			System.out.print("Enter the name of your device: ");
			device = getStringInput(false);
		}
		else
			device = deviceName;
		System.out.print("Enter the percent of power left in your device: ");
		int startPercent = in.nextInt();
		in.nextLine(); // Mop up new line left from entering int
		System.out.print("Have you just finished using your device? (Y/n): ");
		if (getStringInput(true).equals("Y"))
			afterUse = true;
		Record record = new Record(userName, device, new Date(), startPercent, afterUse);
		System.out.println("You're all set!");
		System.out.println("Now leave your device on recharge and come back to finish your first record later.");
		records.add(record);
		modified = true;
	}
	private static void finishRecord (Boolean internal) {
		if (records.size() == 0) {
			System.out.println("No records in the file.");
			return;
		}
		final long expiresIn = 8 * 3600 * 1000;
		ArrayList<Record> unfinishedRecs = new ArrayList<>();
		ArrayList<Record> fndRecs = new ArrayList<>();
		long diff;
		int finishPercent;
		Date now = new Date();

		for (Record r : records) {
			diff = now.getTime() - r.getStart().getTime();
			if (r.getFinish() == null && diff <= expiresIn)
				unfinishedRecs.add(r);
		}
		if (unfinishedRecs.size() > 0) {
			if (internal) { // The statement below is a silent internal search for current user's current device.
				fndRecs = searchByDeviceUnderUser(unfinishedRecs, true, true); 
				if (fndRecs != null && fndRecs.size() > 0) {
					fndRecs.get(fndRecs.size() - 1).setFinish(new Date()); // set finish time of the last element in fndRecs
					System.out.print("Enter battery percent after recharge: ");
					try {
						finishPercent = in.nextInt();
						in.nextLine();
						fndRecs.get(fndRecs.size() - 1).setFinishPercent(finishPercent);
						System.out.println("Now the unfinished record has been completed.");
						modified = true;
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid choice.");
					} 
				}
				else
					System.out.println(userName + "'s " + deviceName + " has no unfinished records.");
			}
			else {
				System.out.println("Unfinished records within the last 8 hours:");
				displayRecords(unfinishedRecs, false);
				System.out.println("Which record you'd like to finish? Or enter 'q' to quit.");
				String choice = getStringInput(false);
				if (!choice.toLowerCase().equals("q")) {
					try {
						int option = Integer.parseInt(choice);
						if (option  <= unfinishedRecs.size()) {
							unfinishedRecs.get(option - 1).setFinish(new Date());
							System.out.println("Enter battery percent after recharge: ");
							finishPercent = in.nextInt();
							in.nextLine();
							unfinishedRecs.get(option - 1).setFinishPercent(finishPercent);
							System.out.println("Now the unfinished record has been completed.");
							modified = true;
						}
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid choice.");
					}
				}	
			}
		}
		else
			System.out.println("No records unfinished in the past 8 hours found.");
		
	}
	private static void searchRecords () {
		int option = -1; 
		final int QUIT = 15;
		ArrayList<Record> results = null;

		while (option != QUIT) {
			System.out.println("\nSearch by: ");
			System.out.println("1) User name");
			System.out.println("2) Device name");
			System.out.println("3) A range of start dates");
			System.out.println("4) A device under current user name");
			System.out.println("5) A device under other user name");
			System.out.println("6) A start date range under a user name");
			System.out.println("7) A start date range for a device");
			System.out.println("8) A start date range for a device under a user name");
			System.out.println("9) A range of start or finished percents under a user name");
			System.out.println("10) A range of start or finished percents for a device");
			System.out.println("11) A range of start or finished percents for a device under a user");
			System.out.println("12) A range of charged percents under a user name");
			System.out.println("13) A range of charged percents for a device");
			System.out.println("14) A range of charged percents for a device under a user name");
			System.out.println("15) Return");
			System.out.print("Enter your choice: ");
			try {
				option = in.nextInt();
				in.nextLine();
				switch (option) {
					case 1:
						results = searchByUser(records, false);
						break;
					case 2:
						results = searchByDevice(records, false);
						break;
					case 3:
						results = searchByStartDateRange(records);
						break;
					case 4: // search for current user, maybe current device or other device, not internal
						results = searchByDeviceUnderUser(records, true, false); 
						break;
					case 5: // search for different user's device, not internal
						results = searchByDeviceUnderUser(records, false, false);
						break;
					case 6:
						results = searchByStartDateRangeUnderUser(records);
						break;
					case 7:
						results = searchByStartDateRangeForDevice(records);
						break;
					case 8:
						results = searchByStartDateRangeForDeviceUnderUser(records);
						break;
					case 9:
						results = searchByStartOrFinishedPercentRangeUnderUser(records);
					case 10:
						results = searchByStartOrFinishedPercentRangeForDevice(records);
						break;
					case 11:
						results = searchByStartOrFinishedPercentForDeviceUnderUser(records);
						break;
					case 12:
						results = searchByChargedPercentUnderUser(records);
						break;
					case 13:
						results = searchByChargedPercentForDevice(records);
						break;
					case 14:
						results = searchByChargedPercentForDeviceUnderUser(records);
						break;
					case 15:
						break;
					default:
						System.out.println("Invalid input.");
				}
				if (option != QUIT)
					if (results != null && results.size() > 0) {
						System.out.println("Found:");
						displayRecords(results, false);
					} else
						System.out.println("No records found.");
			} catch (NumberFormatException nfe) {
				System.out.println("Invalid choice.");
			}
		}
	}
	private static ArrayList<Record> searchByUser (List<Record> recs, Boolean internal) {
		String name;

		if (internal)
			name = userName;
		else{
			System.out.print("Enter the user name (enter q to return): ");
			name = getStringInput(false);
		}
		if (name.toLowerCase().equals("q"))
			return null;
		return searchByUser(recs, name);
	}
	private static ArrayList<Record> searchByUser (List<Record> recs, String name) {
		List<Record> fndRecs = null;
		Record query = new Record(name, true); // here only name is of interest
		// Search a sorted array for a record by user name
		Record[] recsForQuery = recs.toArray(new Record[recs.size()]);
		SortByUserName sbun = new SortByUserName();
		Arrays.sort(recsForQuery, sbun);
		int index = Arrays.binarySearch(recsForQuery, query, sbun);
		int [] ends = null;
		if (index >= 0) {
			ends = getIndexeBounds(recsForQuery, UserDeviceStartCAU.USER, query.getUserName(), index);
			fndRecs = Arrays.asList(recsForQuery).subList(ends[0], ends[1]);
		}
		if (fndRecs == null)
			return null;
		return new ArrayList<Record>(fndRecs);
	}
	private static ArrayList<Record> searchByDevice (List<Record> recs, Boolean internal) {
		String device;

		if (internal)
			device = deviceName;
		else{
			System.out.print("Enter the device name (enter q to return): ");
			device = getStringInput(false);
		}
		if (device.toLowerCase().equals("q"))
			return null;
		return searchByDevice(recs, device);
	}
	private static ArrayList<Record> searchByDevice (List<Record> recs, String device) {
		List<Record> fndRecs = null;
		Record query = new Record(device, false); // here only device is of interest
		// Search a sorted array for a record by user name
		Record[] recsForQuery = recs.toArray(new Record[recs.size()]);
		SortByDeviceName sbdn = new SortByDeviceName();
		Arrays.sort(recsForQuery, sbdn);
		int index = Arrays.binarySearch(recsForQuery, query, sbdn);
		int [] ends = null;
		if (index >= 0) {
			ends = getIndexeBounds(recsForQuery, UserDeviceStartCAU.DEVICE, query.getDeviceName(), index);
			fndRecs = Arrays.asList(recsForQuery).subList(ends[0], ends[1]);
		}
		if (fndRecs == null)
			return null;
		return new ArrayList<Record>(fndRecs);
	}
	private static int[] getIndexeBounds (Record[] recs, UserDeviceStartCAU udsc, String queryString, int index) {
		int start = 0, end = 0;
		int i, j;

		if (udsc == UserDeviceStartCAU.USER) {// search user
			for (i = index; i >= 0;  i--)
				if (!fuzzyStringMatch(recs[i].getUserName(), queryString)) 
					break;
			for (j = index; j < recs.length;  j++)
				if (!fuzzyStringMatch(recs[j].getUserName(), queryString)) 
					break;
			start = i + 1; // in all three cases, when search reaches the mismatch which is not the top, 
						  // or when search reaches top of list, mismatch is not found or mismatch is the top itself
			end = j;
		}
		else if (udsc == UserDeviceStartCAU.DEVICE) {
			for (i = index; i >= 0;  i--)
				if (!fuzzyStringMatch(recs[i].getDeviceName(), queryString)) 
					break;
			for (j = index; j < recs.length;  j++)
				if (!fuzzyStringMatch(recs[j].getDeviceName(), queryString))
					break;
			start = i + 1;
			end = j;
		}
		else if (udsc == UserDeviceStartCAU.START) { // unidirection search, downwards only
			String source;
			for (j = index; j < recs.length; j++) {
				source = makeTimeString(recs[j].getStart(), Mode.YEAR_MONTH_DAY);
				if (source.compareTo(queryString) != 0) 
					break;		
			}
			start = index;
			end = j;
		}
		else {
			System.out.println("Invalid udsc category.");
			return null;
		}
		return new int[] {start, end};
	}
	private static Boolean fuzzyStringMatch (String a, String b) {
		final int FIRSTNCHAR = 4;
		String aLow = a.toLowerCase();
		String bLow = b.toLowerCase();
		Boolean match = true;
		
		for (int i = 0; i < FIRSTNCHAR; i++) 
			if (aLow.charAt(i) != bLow.charAt(i)) {
				match = false;
				break;
			}
		return match;
	}
	private static ArrayList<Record> searchByStartDateRange (List<Record> recs) {
		System.out.println("\nPlease enter a date range.");
		System.out.println("For particular day in a month in a year, e.g. 18/8/2020 or 2020/8/18");
		System.out.println("For a day in a month in the current year, e.g. 15/4, September 19, or 8 Feb");
		System.out.println("For a day in current month, just enter the day, e.g. 20\n");
		System.out.println("For a range of days across years, e.g. 20/09/2020-14/05/2021, or 2020/09/20-2021/05/14");
		System.out.println("For a range of days within a year, e.g. 13/2-22/06/2020, or 2020/2/13-06/12, or May 20-July 20, 2020 or 20 May-20 July, 2020");
		System.out.println("For a range of days within a month, e.g. 1-21/05/2020, 2020/05/1-21"); 
		System.out.println("For a range of days across months in current year, e.g. Mar 10-June 20, or 10 March-10 April");
		System.out.println("For a range of days within a month of current year, e.g. 3-25/06");
		System.out.println("For a range of days within current month, e.g. 2-30\n");
		System.out.println("For a month in a year, e.g. August, 2020, or Oct, 2020 or 2020/4, or 5/2020");
		System.out.println("For current year, just enter the month, e.g. July, or Aug. No digits should be used for representing month alone.");
		System.out.println("Free standing digits are used for representing a day of a month.\n");
		System.out.println("For a of month range, e.g. Feb-May, 2020, or 4-8/2020, or 2020/4-8.");
		System.out.println("For current year, just enter a month range, e.g. March-June, Apr-Sep.\n");
		System.out.println("For a particular year, e.g. 2020.");
		System.out.println("For a range of years, e.g. 2020-2021, 2020-22\n");
		System.out.println("Enter 0 to return");
		String dateString = getStringInput(false); 
		if (dateString.equals("0"))
				return null;
	
		return searchByStartDateRange (recs, dateString);
	}
	private static ArrayList<Record> searchByStartDateRange (List<Record> recs, String dateString) {
		String[] queryString = getDateStringInput(dateString);
		Record[] recsForQuery = recs.toArray(new Record[recs.size()]);
		List<Record> fndRecs = null;
		int[] ends;

		if (queryString[0].equals(queryString[1]))  // single date
			ends = getStartDateIndexBounds(recsForQuery, queryString[0]);
		else { // an interval of two different dates 
			int start, end;
			ends = getStartDateIndexBounds(recsForQuery, queryString[0]);
			start = ends[0];
			ends = getStartDateIndexBounds(recsForQuery, queryString[1]);
			end = ends[1];
			ends[0] = start;
			ends[1] = end;
		}
		if (ends != null)
			fndRecs = Arrays.asList(recsForQuery).subList(ends[0], ends[1]);
		if (fndRecs == null)
			return null;
		return new ArrayList<Record>(fndRecs);
	}
	private static int[] getStartDateIndexBounds (Record[] recs, String queryString) {
		int[] ends = null;
		int index, start, end;
		SortByStartDate sbsd = new SortByStartDate();
		sbsd.setDateString(queryString); // set dateString for the compare() mehtod in SortByDate class
		Arrays.sort(recs, sbsd);
		// dateString must be year/month/day
		int[] yMD = getDateComponents(queryString, "/");
		Date date = new GregorianCalendar(yMD[0], yMD[1], yMD[2]).getTime(); // dateString 
		Record queryDate = new Record(date); // here only date is of interest

		index = Arrays.binarySearch(recs, queryDate, sbsd);
		if (index >= 0) 
			ends = getIndexeBounds(recs, UserDeviceStartCAU.START, queryString, index);
		return ends;
	}
	private static String[] getDateStringInput (String dateString) { // Unfinished
		String datePatternName = getPatternName(dateString);
		if (datePatternName == null) {
			System.out.println("Date pattern not found.");
			return null;
		}
		String[] dateStringPair = null, dateStringTrio = null, monthDayName;
		String timeString, firstString = null, lastString = null;
		Boolean externalMade = false;
		int[] monthDayNumeric = null;
		int year, month, days;
		if (datePatternName.equals("year")) { // a whole year
			firstString = dateString + "/" + "1" + "/" + "1";
			lastString = dateString + "/" + "12" + "/" + "31"; 
		} else if (datePatternName.equals("monthName")) { // a month name in current year
			year = Integer.parseInt(makeTimeString(new Date(), Mode.YEAR));
			monthDayNumeric = getMonthDayFromMonthName(dateString, year);
			firstString = year + "/" + monthDayNumeric[0] + "/" + "1"; // start of month in current year
			lastString = year + "/" + monthDayNumeric[0] + "/" + monthDayNumeric[1]; // end of month
		} else if (datePatternName.equals("day")) { // a day in current month of current year
			dateString = normalize(dateString);
			timeString = makeTimeString(new Date(), Mode.YEAR_MONTH); 
			timeString = timeString + "/" + dateString;
			firstString = timeString;
			lastString = timeString;
		} else if (datePatternName.equals("dayToDay")) { // range of days in current month
			dateStringPair = dateString.split("-");
			timeString = makeTimeString(new Date(), Mode.YEAR_MONTH);
			dateStringPair[0] = normalize(dateStringPair[0]);
			dateStringPair[1] = normalize(dateStringPair[1]);
			firstString = timeString + "/" + dateStringPair[0];
			lastString = timeString + "/" + dateStringPair[1];
		} else if (datePatternName.equals("monthToMonthName")) { // range of months (name) in current year
			dateStringPair = dateString.split("-");
			year = Integer.parseInt(makeTimeString(new Date(), Mode.YEAR));
			monthDayNumeric = getMonthDayFromMonthName(dateStringPair[0], year);
			firstString = year + "/" + monthDayNumeric[0] + "/" + "1";
			monthDayNumeric = getMonthDayFromMonthName(dateStringPair[1], year);
			lastString = year + "/" + monthDayNumeric[0] + "/" + monthDayNumeric[1];
		} else if (datePatternName.equals("yearToYear")) {
			dateStringPair = dateString.split("-");
			firstString = dateStringPair[0] + "/" + "1" + "/" + "1";
			lastString = dateStringPair[1] + "/" + "12" + "/" + "31";
		} else if (datePatternName.equals("monthDayNumeric")) { // day and month (num) in cur year
			dateStringPair = dateString.split("/");
			dateStringPair[0] = normalize(dateStringPair[0]); // day
			dateStringPair[1] = normalize(dateStringPair[1]); // month
			year = Integer.parseInt(makeTimeString(new Date(), Mode.YEAR));
			firstString = year + "/" + dateStringPair[1] + "/" + dateStringPair[0];
			lastString = firstString;
		} else if (datePatternName.equals("monthDayName")) { // day and month (name) in curr year
			monthDayName = getMonthDayFromMonthDayName(dateString);
			year =  Integer.parseInt(makeTimeString(new Date(), Mode.YEAR));
			firstString = year + "/" + monthDayName[0] + "/" + monthDayName[1];
			lastString = firstString;
		} else if (datePatternName.equals("yearMonthNumeric")) { // month (num) in same year
			dateStringPair = dateString.split("/");
			if (dateStringPair[0].length() == 4) { // year/month
				dateStringPair[1] = normalize(dateStringPair[1]);
				firstString = dateStringPair[0] + "/" + dateStringPair[1] + "/" + "1";
				days = getDaysInMonth(Integer.parseInt(dateStringPair[0]), Integer.parseInt(dateStringPair[1]));
				lastString = dateStringPair[0] + "/" + dateStringPair[1] + "/" + days;
			}
			else {// month/year
				dateStringPair[0] = normalize(dateStringPair[0]);
				firstString = dateStringPair[1] + "/" + dateStringPair[0] + "/" + "1";
				days = getDaysInMonth(Integer.parseInt(dateStringPair[1]), Integer.parseInt(dateStringPair[0]));
				lastString = dateStringPair[1] + "/" + dateStringPair[0] + "/" + days;
			}
		} else if (datePatternName.equals("yearMonthName")) { // monthName, year or monthName,year
			dateString = dateString.replaceAll("\\s", "");
			dateStringPair = dateString.split(",");
			monthDayNumeric = getMonthDayFromMonthName(dateStringPair[0],  Integer.parseInt(dateStringPair[1]));
			firstString = dateStringPair[1] + "/" + monthDayNumeric[0] + "/" + "1";
			lastString = dateStringPair[1] + "/" +  monthDayNumeric[0] + "/" + monthDayNumeric[1];
		} else if (datePatternName.equals("yearMonthDayNumeric")) {  
			dateStringTrio = dateString.split("/");
			if (dateStringTrio[0].length() == 4) {//year/month/day
				dateStringTrio[1] = normalize(dateStringTrio[1]);
				dateStringTrio[2] = normalize(dateStringTrio[2]);
				firstString = dateStringTrio[0] + "/" + dateStringTrio[1] + "/" + dateStringTrio[2];
			}
			else {//or day/month/year
				dateStringTrio[0] = normalize(dateStringTrio[0]);
				dateStringTrio[1] = normalize(dateStringTrio[1]);
				firstString = dateStringTrio[2] + "/" + dateStringTrio[1] + "/" + dateStringTrio[0];
			}
			lastString = firstString;
		} else if (datePatternName.equals("yearMonthDayName")) { // day month name, year or month name day, year
			dateStringPair = dateString.split(",");
			dateStringPair[1] = dateStringPair[1].replaceAll("\\s", "");
			monthDayName = getMonthDayFromMonthDayName(dateStringPair[0]);
			firstString = dateStringPair[1] + "/" + monthDayName[0] + "/" + monthDayName[1];
			lastString = firstString;
		}
		// TODO
		if (externalMade)
			return dateStringPair;
		return new String[] {firstString, lastString};
	}
	private static String normalize (String dateString) {
		if (dateString.length() == 2 && dateString.charAt(0) == '0')
			dateString = dateString.substring(1, dateString.length());
		return dateString;
	}
	private static int[] getMonthDayFromMonthName (String dateString, int year) {
		int month = parseMonthName(dateString);
		int daysInMonth = getDaysInMonth(year, month);

		return new int[] {month, daysInMonth};
	}
	private static String[] getMonthDayFromMonthDayName (String dateString) {
		String day;
		String[] dateStringPair = dateString.split(" ");
		int month;
		if (dateStringPair[0].length() < 3) {// day month
			day = normalize(dateStringPair[0]);
			month = parseMonthName(dateStringPair[1]);
		}
		else {	//month day
			day = normalize(dateStringPair[1]);
			month = parseMonthName(dateStringPair[0]);
		}
		return new String[] {("" + month), day};
	}
	/*private static String[] getDateStringPairForMonthToMonthNumeric (String dateString, int year) { // Unused
		String[] dsPair = dateString.split("-");
		int daysInMonth = getDaysInMonth(year, Integer.parseInt(dsPair[1]));
		String firstString = year + "/" + dsPair[0] + "/" + "1";
		String lastString = year + "/" + dsPair[1] + "/" + daysInMonth;

		return new String[] {firstString, lastString};
	}*/
	private static int getDaysInMonth (int year, int month) {
		int daysInMonth;
		final int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

		if (((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) && month == 2)
			daysInMonth = days[month-1] + 1;
		else
			daysInMonth = days[month-1];
		return daysInMonth;
	}
	private static int parseMonthName (String monthName) {
		int month = -1;
			switch (monthName.toLowerCase()) {
				case "jan":
				case "january":
					month = 1;
					break;
				case "feb":
				case "february":
					month = 2;
					break;
				case "mar":
				case "march":
					month = 3;
					break;
				case "apr":
				case "april":
					month = 4;
					break;
				case "may":
					month = 5;
					break;
				case "june":
					month = 6;
					break;
				case "july":
					month = 7;
					break;
				case "aug":
				case "august":
					month = 8;
					break;
				case "sep":
				case "september":
					month = 9;
					break;
				case "oct":
				case "october":
					month = 10;
					break;
				case "nov":
				case "november":
					month = 11;
					break;
				case "dec":
				case "december":
					month = 12;
					break;
				default:
					break;
			}
		return month;
	}
	private static String getPatternName (String dateString) {
		final String year = "(20?[2-5][0-9])";
		final String monthName = "((?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|June?|July?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?))";
		final String monthNumeric = "((0?[1-9])|(1[0-2]))";
		final String day = "((0?[1-9])|([1-2][0-9])|(3[0-1]))";

		final String yearMonthNumeric = "(" + "(" + year + "/" + monthNumeric + ")" + "|" + 
										"(" + monthNumeric + "/" + year + ")" + ")";
		final String yearMonthName = "(" + monthName + "," + "\\s?" + year + ")";
		final String monthDayNumeric = "(" + day + "/" + monthNumeric + ")";
										
		final String monthDayName = "(" + "(" + monthName + "\\s" + day + ")" + "|" + 
									"(" + day + "\\s" + monthName + ")" + ")";
		final String yearMonthDayNumeric = "(" + "(" + year + "/" + monthNumeric + "/" + day + ")" + "|" +
										   "(" + monthDayNumeric + "/" + year + ")" + ")";
		final String yearMonthDayName = "(" + monthDayName + "," + "\\s?" + year + ")";

		final String yearToYear = "(" + year + "-" + year + ")";
		final String yearToYearShort = "(" + year + "-" + "([2-5][0-9])" + ")";
		final String monthToMonthNumeric = "(" + monthNumeric + "-" + monthNumeric + ")";
		final String monthToMonthName = "(" + monthName + "-" + monthName + ")";
		final String dayToDay = "(" + day + "-" + day + ")";
		final String yearMonthToYearMonthNumeric = "(" + yearMonthNumeric + "-" + yearMonthNumeric + ")";
		final String yearMonthToYearMonthName = "(" + yearMonthName + "-" + yearMonthName + ")";
		final String monthDayToMonthDayNumeric = "(" + monthDayNumeric + "-" + monthDayNumeric + ")";
		final String monthDayToMonthDayName = "(" + monthDayName + "-" + monthDayName + ")";
		final String yearMonthDayToYearMonthDayNumeric = yearMonthDayNumeric + "-" + yearMonthDayNumeric;
		final String yearMonthDayToYearMonthDayName = yearMonthDayName + "-" + yearMonthDayName; 
		
		final String sameYearMonthToMonthNumeric = "(" + year + "/" + monthToMonthNumeric+ ")" + "|" + 
												   "(" + monthToMonthNumeric + "/" + year + ")";
		final String sameYearMonthToMonthName = monthToMonthName + "," + "\\s?" + year;
		final String sameMonthDayToDayNumeric = "(" + monthNumeric + "/" + dayToDay + ")" + "|" + "(" + dayToDay + "/" + monthNumeric + ")";
		final String sameMonthDayToDayName = "(" + monthName + "\\s" + dayToDay + ")" + "|" + "(" + dayToDay + "\\s" + monthName + ")";
		final String sameYearMonthDayToMonthDayNumeric = monthDayToMonthDayNumeric + "/" + year; 
		final String sameYearMonthDayToMonthDayName = monthDayToMonthDayName + "," + "\\s?" + year;  
		final String sameYearSameMonthDayToDay = "(" + dayToDay + "/" + monthNumeric + "/" + year + ")" + "|" + "(" + year + "/" + monthNumeric + "/" + dayToDay + ")" ;
		
		final String[] datePatterns = {year, monthName, monthNumeric, day, yearMonthNumeric, yearMonthName, 
										monthDayNumeric, monthDayName, yearMonthDayNumeric, yearMonthDayName, 
										yearToYear, yearToYearShort, monthToMonthName, dayToDay,
										yearMonthToYearMonthNumeric, yearMonthToYearMonthName, monthDayToMonthDayNumeric,
										monthDayToMonthDayName, yearMonthDayToYearMonthDayNumeric, yearMonthDayToYearMonthDayName,
										sameYearMonthToMonthNumeric, sameYearMonthToMonthName, sameMonthDayToDayNumeric, 
										sameMonthDayToDayName, sameYearMonthDayToMonthDayNumeric, sameYearMonthDayToMonthDayName,
										sameYearSameMonthDayToDay};
		final String[] keys = {"year", "monthName", "monthNumeric", "day", "yearMonthNumeric", "yearMonthName", 
								"monthDayNumeric", "monthDayName", "yearMonthDayNumeric", "yearMonthDayName", 
								"yearToYear", "yearToYearShort", "monthToMonthName", "dayToDay", 
								"yearMonthToYearMonthNumeric", "yearMonthToYearMonthName", 
								"monthDayToMonthDayNumeric", "monthDayToMonthDayName",
								"yearMonthDayToYearMonthDayNumeric", "yearMonthDayToYearMonthDayName",
								"sameYearMonthToMonthNumeric", "sameYearMonthToMonthName",
								"sameMonthDayToDayNumeric", "sameMonthDayToDayName",
								"sameYearMonthDayToMonthDayNumeric", "sameYearMonthDayToMonthDayName",
								"sameYearSameMonthDayToDay"};
		final int INIT_CAPACITY = 27;
		Map<String, String> dateMap = new HashMap<>(INIT_CAPACITY);
		for (int i = 0; i < INIT_CAPACITY; i++)
			dateMap.put(keys[i], datePatterns[i]);
		Pattern pattern = null; 
		Matcher matcher = null;
		String patternName = null;
		for (Map.Entry<String, String> entry : dateMap.entrySet()) {
			pattern = Pattern.compile(entry.getValue());
			matcher = pattern.matcher(dateString);
			if (matcher.matches()) {
				patternName = entry.getKey();
				break;
			}
		}
		return patternName;
	}
	private static int[] getDateComponents (String dateString, String separator) {
		int item1, item2, item3;
		int[] firstHalf, lastHalf, result = null;

		if (separator.equals(" ")) {
			String[] fullComponents = dateString.split(" ");
			firstHalf = getDateComponents(fullComponents[0], "/");
			lastHalf = getDateComponents(fullComponents[1], ":");
			result = new int[firstHalf.length + lastHalf.length];
			System.arraycopy(firstHalf, 0, result, 0, firstHalf.length);
			System.arraycopy(lastHalf, 0, result, firstHalf.length, lastHalf.length);
		}
		else if (separator.equals("/") || separator.equals(":")) {
			String[] components = dateString.split(separator);
			item1 = Integer.parseInt(components[0]);
			item2 = Integer.parseInt(components[1]);
			item3 = Integer.parseInt(components[2]);	
			result = new int[] {item1, item2, item3};
		}
		return result;
	}
	private static int[] getDateComponents (Date date) {
		int year, month, day, hour, minute, second;
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timezone));
		cal.setTime(date);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		second = cal.get(Calendar.SECOND);

		return new int[] {year, month, day, hour, minute, second}; 
	}
	private static ArrayList<Record> searchByDeviceUnderUser (List<Record> recs, Boolean currentUser, Boolean internal) {
		String name = null, device = null;
		ArrayList<Record> fndRecs = null;

		if (!currentUser && !internal) {
			System.out.print("Enter the user name: ");
			name = getStringInput(false);
			System.out.print("Enter the device name: ");
			device = getStringInput(false);
		}
		if (currentUser && !internal) {
			name = userName;
			System.out.print("Search for records of current device? (Y/n): ");
			if (getStringInput(true).equals("Y"))
				device = deviceName;
			else {
				System.out.println("Enter the device name: ");
				device = getStringInput(false);
			}
		}
		if (internal) {
			name = userName;
			device = deviceName;
		}
		fndRecs = searchByUser(recs, name);
		if (fndRecs != null) 
			fndRecs = searchByDevice(fndRecs, device);
		return fndRecs;
	}
	private static ArrayList<Record> searchByStartDateRangeForDevice (List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByStartDateRangeUnderUser (List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByStartDateRangeForDeviceUnderUser (List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByStartOrFinishedPercentRangeUnderUser(List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByStartOrFinishedPercentRangeForDevice(List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByStartOrFinishedPercentForDeviceUnderUser(List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByChargedPercentUnderUser(List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByChargedPercentForDevice(List<Record> recs) {
		return null;
	}
	private static ArrayList<Record> searchByChargedPercentForDeviceUnderUser(List<Record> recs) {
		return null;
	}
	private static void modifyRecords () {
		ArrayList<Record> fndRecs = null;
		int choice = -1;
		
		while (choice != 5) {
			System.out.println("Modify: ");
			System.out.println("1) A user's device name");
			System.out.println("2) A device's user name");
			System.out.println("3) A device's CAU state");
			System.out.println("4) A start date");
			System.out.println("5) Return");
			choice = in.nextInt();
			in.nextLine();
			switch (choice) {
				case 1:
					fndRecs = searchForItems(records, UserDeviceStartCAU.USER);
					if (fndRecs != null) 
						modifyRecords(fndRecs, UserDeviceStartCAU.DEVICE);
					break;
				case 2:
					fndRecs = searchForItems(records, UserDeviceStartCAU.DEVICE);
					if (fndRecs != null)
						modifyRecords(fndRecs, UserDeviceStartCAU.USER);
					break;
				case 3:
					fndRecs = searchForItems(records, UserDeviceStartCAU.DEVICE);
					if (fndRecs != null)
						modifyRecords(fndRecs, UserDeviceStartCAU.CAU);
					break;
				case 4:
					fndRecs = searchForItems(records, UserDeviceStartCAU.START);
					if (fndRecs != null)
						modifyRecords(fndRecs, UserDeviceStartCAU.START);
					break;
				default:
					System.out.println("Invalid choice.");
			}	
		}
	}
	private static void modifyRecords (List<Record> recs, UserDeviceStartCAU udsc) {
		ArrayList<Integer> itemsForMod = null;
		String option = getModificationOption(recs);
		Boolean success = false;

		if (!(option.toLowerCase().equals("q"))) {
			itemsForMod = getItemsForModification(option, recs.size());
			if (itemsForMod == null) 
				System.out.println("Invalid selection.");
			else {
				if (itemsForMod.size() == 1)
					success = modifyRecord(recs.get(itemsForMod.get(0)-1), itemsForMod.get(0), udsc);
				else {
					System.out.print("Batch modification? (Y/n): ");
					option = getStringInput(true);
					if (option.equals("Y")) 
						success = batchModifyRecords(recs, itemsForMod, udsc);
					else if (option.equals("N"))
						for (int i : itemsForMod) {//index in fndRecs starts at 0, so it should be i - 1
							success = modifyRecord(recs.get(i-1), i, udsc); 
							if (!success) {
								System.out.println("An unsuccessful modification occurred. Not all records modified.");
								break;
							}
						} 	
					else 
						System.out.println("Invliad option.");
				}
			}
		}
		if (success) {
			System.out.println("Device information has been modified.");
			modified = true;
		}
		else 
			System.out.println("Modifcation unsuccessful.");
	}
	private static ArrayList<Record> searchForItems (List<Record> recs, UserDeviceStartCAU udsc) {
		ArrayList<Record> fndRecs = null;
		String user, device, start, item = null; 

		if (udsc == UserDeviceStartCAU.USER) {
			System.out.print("Enter user name: ");
			user = getStringInput(false);
			fndRecs = searchByUser(records, user);
			item = user;
		}
		else if (udsc == UserDeviceStartCAU.DEVICE) {
			System.out.print("Enter device name: ");
			device = getStringInput(false);
			fndRecs = searchByDevice(records, device);
			item = device;
		}		
		else if (udsc == UserDeviceStartCAU.START) {
			System.out.print("Enter start date (e.g. 2020/7/10):" );
			start = getStringInput(false);
			fndRecs = searchByStartDateRange(recs, start);
			item = start;
		}
		if (fndRecs == null)
			System.out.println("No records of " + item + " found.");
		return fndRecs;	
	}
	private static Boolean modifyRecord (Record rec, int index, UserDeviceStartCAU udsc) {
		String response;
		Boolean success = false;

		System.out.println("Current record to modify: ");
		System.out.println(getDisplayString(rec, index, false));
		if(udsc == UserDeviceStartCAU.DEVICE) {
			System.out.print("Enter the new name of the device (q to quit): ");
			response = getStringInput(false);
			if (!response.toLowerCase().equals("q")) {
				rec.setDeviceName(response);
				success = true;
			}
		}
		else if (udsc == UserDeviceStartCAU.USER) {
			System.out.print("Enter the new name of the user (q to quit): ");
			response = getStringInput(false);
			if (!response.toLowerCase().equals("q")) {
				rec.setUserName(getStringInput(false));
				success = true;
			}
		}
		else if (udsc == UserDeviceStartCAU.CAU) {
			System.out.print("Enter the new state of CAU (enter T for \"true\" or F for \"false\"): ");
			response = getStringInput(true).substring(0, 1);
			if (!response.toLowerCase().equals("q")) {
				if (!response.equals("T") && !response.equals("F")) 
					System.out.println("Invliad input.");
				else {
					rec.setAfterUse(response.equals("T"));
					success = true;
				}
			}
		}
		else if (udsc == UserDeviceStartCAU.START) {
			final String YMDHMS = "YMDHMS";
			final String[] FULLDATE = {"year", "month", "day of month", "hour", "minute", "second"};
			int[] dateComponents = getDateComponents(rec.getStart());
			System.out.println("Modify: ");
			System.out.println("(Y)ear/(M)onth/(D)ay or (H)our:(M)inute:(S)econd?");
			response = getStringInput(true);
			if (!response.toLowerCase().equals("q")) {
				int entry = YMDHMS.indexOf(response);
				if (entry >= 0) {
					System.out.print("Enter a new " + FULLDATE[entry] + ": ");
					dateComponents[entry] = in.nextInt();
					in.nextLine();
					GregorianCalendar cal = new GregorianCalendar(dateComponents[0], dateComponents[1], dateComponents[2], 
											dateComponents[3], dateComponents[4], dateComponents[5]);
					cal.setTimeZone(TimeZone.getTimeZone(timezone));
					rec.setStart(cal.getTime());
					success = true;
				}
			}
		}
		return success;
	}
	private static Boolean batchModifyRecords (List<Record> recs, List<Integer> items, UserDeviceStartCAU udsc) {
		String user, device, cau;

		if (udsc == UserDeviceStartCAU.DEVICE) {
			System.out.print("Enter the new name of the device: ");
			device = getStringInput(false);
			for (int i : items)
				recs.get(i-1).setDeviceName(device);
		}
		else if (udsc == UserDeviceStartCAU.USER) {
			System.out.print("Enter the new use name: ");
			user = getStringInput(false);
			for (int i : items)
				recs.get(i-1).setUserName(user);
		}
		else if (udsc == UserDeviceStartCAU.CAU) {
			System.out.print("Enter the new CAU state (enter T for \"true\" or F for \"false\"): ");
			cau = getStringInput(true).substring(0, 1); 
			if (!cau.equals("T") && !cau.equals("F")) {
				System.out.println("Invliad input.");
				return false;
			}
			for (int i : items)
				recs.get(i-1).setAfterUse(cau.equals("T"));
		}
		else if (udsc == UserDeviceStartCAU.START) {
			//TODO
		}
		return true;
	}
	private static String getModificationOption (List<Record> recs) {
		String entry;
		displayRecords(recs, false);
		System.out.println("\nWhich record(s) would you like to modify?");
		System.out.println("You may enter a single record, e.g. 4,");
		System.out.println("or several individual records, e.g. 1, 3, 6,");
		System.out.println("or several continuous records, e.g. 2-5,");
		System.out.println("or a combination of the two, e.g. 2, 7, 9-13.");
		System.out.println("If you change your mind, enter q to quit.");
		System.out.print("Please enter your selection(s): ");
		entry = getStringInput(false);
		return entry.replaceAll("\\s", "");
	}
	private static ArrayList<Integer> getItemsForModification(String opt, int limit) {
		ArrayList<Integer> items = new ArrayList<>();
		String[] parts = null;
		
		if (opt.indexOf(",") < 0 && opt.indexOf("-") < 0) // modify a single record
			items.add(Integer.parseInt(opt));
		else {
			if (opt.indexOf(",") > 0 && opt.indexOf("-") > 0) { //  range and enumerated
				parts = opt.split(",");
				for (int i = 0; i < parts.length - 1; i++)
					items.add(Integer.parseInt(parts[i]));
				parts = parts[parts.length-1].split("-");
				for (int i = Integer.parseInt(parts[0]); i <= Integer.parseInt(parts[1]); i++)
					items.add(i);
			}
			else if (opt.indexOf("-") > 0) { // range, e.g. 3-7
				parts = opt.split("-");
				for (int i = Integer.parseInt(parts[0]); i <= Integer.parseInt(parts[1]); i++)
					items.add(i);
			}
			else if (opt.indexOf(",") > 0) { // enumerated individuals, 2, 4, 10
				parts = opt.split(",");
				for (int i = 0; i < parts.length; i++)
					items.add(Integer.parseInt(parts[i]));
			}
			else {
				System.out.println("Invalid selection for modification.");
				return null;
			}
		}
		if (items.size() == 0 || outOfBounds(items, limit))
			return null;
		return items;
	}
	private static Boolean outOfBounds (List<Integer> items, int limit) {
		Boolean outOfBounds = false;

		for (int i : items) 
			if (i < 1 || i > limit) {
				outOfBounds = true;
				break;
			}
		return outOfBounds;
	}
	private static void deleteRecord () {

	}
	private static void exportRecords (List<Record> recs) {
		int option;
		Boolean success = false;

		System.out.println("Export to:");
		System.out.println("1) local drive");
		System.out.println("2) network");
		System.out.println("3) Return");
		System.out.print("Your choice: ");
		option = in.nextInt();
		in.nextLine();
		if (option == 3)
			return;
		if (option == 1) 
			success = exportToLocal(recs);
		else if (option == 2) 
			success = exportToNet(recs);
		if (success && option == 1)
			System.out.println("All records have been exported to output.txt.");
		else if (success && option == 2)
			System.out.println("All records have been exported to remote host.");
		else
			System.out.println("One or more errors occurred. Records export unsuccessful.");
	}
	private static Boolean exportToLocal (List<Record> recs) {
		int i = 1;
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File("output.txt")))) {
			for (Record r : recs) {
				pw.println(getDisplayString(r, i, true));
				i += 1;
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		}
		return true;
	}
	private static Boolean exportToNet (List<Record> recs) {
		final int PORT = 1234;
		final String address = "192.168.1.108";
		InetAddress host;
		Socket link = null;
		int recCnt = 0;
		
		if (!exportToLocal(recs)) {
			System.out.println("Export to local drive failed. Can't get data from local drive for export.");
			return false;
		}
		try {
			host = InetAddress.getByName(address);
		} catch (UnknownHostException uhEx) {
			System.out.println("Host ID not found!");
			return false;
		}
		try {
			link = new Socket(host, PORT);
			Scanner input = new Scanner(link.getInputStream());
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			Scanner recordIn = new Scanner(new FileInputStream(new File("output.txt")));
			while (recordIn.hasNextLine()) {
				output.println(recordIn.nextLine());
				recCnt++;
			}
			output.println("***CLOSE***");
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			return false;
		} finally {
			try {
				System.out.println(recCnt + " records transferred.");
				link.close();
			} catch (IOException ioEx) {
				System.out.println("Unable to disconnect!");
			}
		}
		return true;
	}
	private static void importRecords () {
		ArrayList<Record> imports = null;
		int option;

		System.out.println("Import from:");
		System.out.println("1) local drive");
		System.out.println("2) network");
		System.out.println("3) Return");
		System.out.print("Your choice: ");
		option = in.nextInt();
		in.nextLine();
		if (option == 3)
			return;
		if (option == 1) 
			imports = importFromLocal();
		else if (option == 2) 
			imports = importFromNet();
		if (imports == null) {
			System.out.println("One or more errors occured. No records imported.");
			return;
		}
		//displayRecords(imports, false);
		System.out.println("1) Overwrite current record list");
		System.out.println("2) Append to current record list");
		System.out.println("3) Return");
		System.out.print("Your choice: ");
		option = in.nextInt();
		in.nextLine();
		if (option == 3)
			return;
		if (option == 1) 	
			records = new ArrayList<>(imports);
		else if (option == 2)
			records.addAll(imports);
		modified = true;
	}
	private static ArrayList<Record> importFromLocal () {
		ArrayList<Record> imports = null;

		try {
			List<String> allLines = Files.readAllLines(Paths.get("output.txt"));
			if (allLines.size() > 0) {
				imports = new ArrayList<Record>();
				for (String line : allLines) 
					imports.add(parseRecordString(line));
			}
			else
				System.out.println("File contains no records.");
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
		return imports;
	}
	private static ArrayList<Record> importFromNet () {
		ArrayList<Record> imports = null;
		ServerSocket serverSocket = null;
		Socket link = null;
		final int PORT = 1234;
		int numMsg = 0;

		System.out.println("Opening port...\n");
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException ioEx) {
			System.out.println("Unable to attach to port!");
			return null;
		}
		try {
			link = serverSocket.accept();
			Scanner input = new Scanner(link.getInputStream());
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			String line = null;
			imports = new ArrayList<>();
			do {
				line = input.nextLine();
				if (!line.equals("***CLOSE***"))
					imports.add(parseRecordString(line));
				numMsg++;
			} while (!line.equals("***CLOSE***"));
			output.println(numMsg + " messages received.");
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			return null;
		} finally {
			try {
				link.close();
			} catch (IOException ioEx) {
				System.out.println("Unable to disconnect!");
			}
		}
		return imports;
	}
	private static Record parseRecordString (String recStr) {
		String[] recordComponents = recStr.split("\\|");
		String userName, deviceName, start, startPercent, finish, finishPercent;
		Record record = null;
		int[] dateComponentsStart, dateComponentsFinish;
		Date startDate = null, finishDate = null;
		int startPercentInt, finishPercentInt;
		Boolean afterUse;
		
		if (recordComponents.length == 4) {
			userName = recordComponents[0];
			deviceName = recordComponents[1];
			start = recordComponents[2];
			startPercent = recordComponents[3]; 
			dateComponentsStart = getDateComponents(start, " ");
			startDate = new GregorianCalendar(dateComponentsStart[0], dateComponentsStart[1], dateComponentsStart[2], 
										dateComponentsStart[3], dateComponentsStart[4], dateComponentsStart[5]).getTime();
			startPercentInt = Integer.parseInt(startPercent);
			record = new Record(userName, deviceName, startDate, startPercentInt);
		}
		else if (recordComponents.length == 8) {
			userName = recordComponents[0];
			deviceName = recordComponents[1];
			start = recordComponents[2];
			startPercent = recordComponents[3]; 
			finish = recordComponents[4];
			finishPercent = recordComponents[5];
			dateComponentsStart = getDateComponents(start, " ");
			startDate = new GregorianCalendar(dateComponentsStart[0], dateComponentsStart[1], dateComponentsStart[2], 
										dateComponentsStart[3], dateComponentsStart[4], dateComponentsStart[5]).getTime();
			dateComponentsFinish = getDateComponents(finish, ":");
			finishDate = new GregorianCalendar(dateComponentsStart[0], dateComponentsStart[1], dateComponentsStart[2], 
										dateComponentsFinish[0], dateComponentsFinish[1], dateComponentsFinish[2]).getTime();
			startPercentInt = Integer.parseInt(startPercent);
			finishPercentInt = Integer.parseInt(finishPercent);
			record = new Record(userName, deviceName, startDate, startPercentInt, finishDate, finishPercentInt);
		}
		else if (recordComponents.length == 9) {
			userName = recordComponents[0];
			deviceName = recordComponents[1];
			start = recordComponents[2];
			startPercent = recordComponents[3]; 
			afterUse = (recordComponents[4] == "true")? true : false;
			finish = recordComponents[5];
			finishPercent = recordComponents[6];
			dateComponentsStart = getDateComponents(start, " ");
			startDate = new GregorianCalendar(dateComponentsStart[0], dateComponentsStart[1], dateComponentsStart[2], 
										dateComponentsStart[3], dateComponentsStart[4], dateComponentsStart[5]).getTime();
			dateComponentsFinish = getDateComponents(finish, ":");
			finishDate = new GregorianCalendar(dateComponentsStart[0], dateComponentsStart[1], dateComponentsStart[2], 
										dateComponentsFinish[0], dateComponentsFinish[1], dateComponentsFinish[2]).getTime();
			startPercentInt = Integer.parseInt(startPercent);
			finishPercentInt = Integer.parseInt(finishPercent);
			record = new Record(userName, deviceName, startDate, startPercentInt, afterUse, finishDate, finishPercentInt);
		}
		else 
			System.out.println("Data corrupted.");	
		return record;
	}
	private static void displayRecords () {
		displayRecords(records, false);
	}
	private static void displayRecords (List<Record> recs, Boolean internal) {
		int i = 1;

		for (Record r : recs) {
			System.out.println(getDisplayString(r, i, internal));
			i += 1;
		} 
	}
	private static String getDisplayString (Record r, int index, Boolean internal) {
		String user, device, start, finish, startString, finishString, diffString, result;
		int startPercent, finishPercent, percentDiff;
		Boolean afterUse;
		long[] timeDiff; 

		if (r == null) {
			return "Corrupted date string encountered.";
		}
		user = r.getUserName();
		device = r.getDeviceName();
		start = makeTimeString(r.getStart(), Mode.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND); 
		startPercent = r.getStartPercent();
		afterUse = r.getAfterUse();
		if (internal) {
			if (afterUse != null) 
				startString = user + "|" + device + "|" + start + "|" + startPercent + "|" + afterUse;
			else
				startString = user + "|" + device + "|" + start + "|" + startPercent;
		}
		else {
			if (afterUse != null)
				startString = user + "'s " + device + " s: " + start + " @: " + startPercent + "%" + " cau: " + afterUse;
			else
				startString = user + "'s " + device + " s: " + start + " @: " + startPercent + "%";
		}
		if (r.getFinish() == null)
			result = startString;
		else {
			finish = makeTimeString(r.getFinish(), Mode.HOUR_MINUTE_SECOND); 
			finishPercent = r.getFinishPercent();
			if (internal)
				finishString = "|" + finish + "|" + finishPercent;
			else
				finishString = " f: " + finish + " @: " + finishPercent + "%";
			timeDiff = getTimeDifference(r.getFinish().getTime() - r.getStart().getTime());
			percentDiff = finishPercent - startPercent;
			if (internal)
				diffString = "|" + percentDiff + "|" + timeDiff[0] + ":" + timeDiff[1] + ":" + timeDiff[2];
			else
				diffString = " c: " + percentDiff + " % in: " + timeDiff[0] + " h " + 
							timeDiff[1] + " m" + " and " + timeDiff[2] + " s";
			result = startString + finishString + diffString;
		}
		return index + ") " + result;
	}
	private static long[] getTimeDifference (long milliseconds) {
		long inSeconds = milliseconds / 1000;
		long hours, minutes, seconds;

		hours = inSeconds / 3600;
		inSeconds = inSeconds % 3600;
		minutes = inSeconds / 60;
		seconds = inSeconds % 60;

		return new long[] {hours, minutes, seconds};
	}
	public static String makeTimeString (Date date, Mode mode) {
		String timeString = null;
		int [] dateComponents = getDateComponents(date);

		switch (mode) {
			case HOUR_MINUTE_SECOND:
				timeString = "" + dateComponents[3] + ":" + dateComponents[4] + ":" + dateComponents[5];
				break;
			case YEAR_MONTH_DAY:	
				timeString = "" + dateComponents[0] + "/" + dateComponents[1] + "/" + dateComponents[2];
				break;
			case YEAR_MONTH_DAY_HOUR_MINUTE_SECOND:
				timeString = "" + dateComponents[0] + "/" + dateComponents[1] + "/" + dateComponents[2] + " " + 
							dateComponents[3] + ":" + dateComponents[4] + ":" + dateComponents[5];
				break;
			case YEAR_MONTH:
				timeString = "" + dateComponents[0] + "/" + dateComponents[1];
				break;
			case YEAR:
				timeString = "" + dateComponents[0];
				break;
			default:
				System.out.println("Invalid mode.");
		}	
		return timeString;
	}
	private static void giveTotalsOfDeviceUnderUser (String user, String device) {
		//ArrayList<Record> fndRecs = searchByDeviceUnderUser(user);

		System.out.println("Totals of " + user + "'s " + device + ":");
		//TODO

	}
	private static void quit (String bye) {
		ObjectOutputStream oos = null;
		// ArrayList<T> cannot be properly written to file at once with writeObject(Object<T>)
		// So, Record[] are created to hold ArrayList<Record> and written to file.
		if (modified) {
			Record[] recs = new Record[CAPACITY];
			recs = records.toArray(recs);
			try {
				oos = new ObjectOutputStream(new FileOutputStream(recordsFile));
				recordsFile.setWritable(true);
				oos.writeObject(recs);
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}
			try {
				oos.close();
				in.close();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}
		}
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(lastLogin))) {
			pw.println(userName);
			pw.println(deviceName);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		System.out.println(bye);
		System.exit(0);
	}
}
enum Mode {
	YEAR,
	YEAR_MONTH,
	YEAR_MONTH_DAY,
	HOUR_MINUTE_SECOND,
	YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;
}
enum UserDeviceStartCAU {
	USER,
	DEVICE,
	START,
	CAU;
}
class SortByUserName implements Comparator<Record> {
	public int compare (Record a, Record b) {
		return a.getUserName().compareToIgnoreCase(b.getUserName());
	}
}
class SortByDeviceName implements Comparator<Record> {
	public int compare (Record a, Record b) {
		return a.getDeviceName().compareToIgnoreCase(b.getDeviceName());
	}
}
class SortByStartDate implements Comparator<Record> {
	private String dateString;

	public int compare (Record a, Record b) { // customized date comparison
		String strA = DeviceBatteryRechargeRecorder.makeTimeString(a.getStart(), Mode.YEAR_MONTH_DAY);
		String strB = DeviceBatteryRechargeRecorder.makeTimeString(b.getStart(), Mode.YEAR_MONTH_DAY);
		
		return strA.compareTo(strB);
	}
	private String getDateString () {
		return dateString;
	}
	public void setDateString (String dateString) {
		this.dateString = dateString;
	}
}
class SortByFinishPercent implements Comparator<Record> {
	public int compare (Record a, Record b) {
		return a.getFinishPercent() - b.getFinishPercent();
	}
}
class Record implements Serializable {
	private String userName;
	private String deviceName;
	private Date start;
	private Date finish;
	private int startPercent;
	private int finishPercent;
	private Boolean afterUse;
	private static final long serialVersionUID = -4410570930041306783L;

	public Record (String name, Boolean isUser) {
		if (isUser)
			this.userName = name;
		else
			this.deviceName = name;
	}
	public Record (Date start) {
		this.start = start;
	}
	public Record (String userName, String deviceName) {
		this.userName = userName;
		this.deviceName = deviceName;
	}
	public Record (String userName, String deviceName, Date start, int startPercent) {
		this.userName = userName;
		this.deviceName = deviceName;
		this.start = start;
		this.startPercent = startPercent;
	}
	public Record (String userName, String deviceName, Date start, int startPercent,
					Boolean afterUse) {
		this.userName = userName;
		this.deviceName = deviceName;
		this.start = start;
		this.startPercent = startPercent;
		this.afterUse = afterUse;
	}
	public Record (String userName, String deviceName, Date start, 
					int startPercent, Date finish, int finishPercent) {
		this.userName = userName;
		this.deviceName = deviceName;
		this.start = start;
		this.startPercent = startPercent;
		this.finish = finish;
		this.finishPercent = finishPercent;
	}
	public Record (String userName, String deviceName, Date start, 
					int startPercent, Boolean afterUse, Date finish, 
					int finishPercent) {
		this.userName = userName;
		this.deviceName = deviceName;
		this.start = start;
		this.startPercent = startPercent;
		this.afterUse = afterUse;
		this.finish = finish;
		this.finishPercent = finishPercent;
	}
	public String getUserName () {
        return userName;
    }
    public String getDeviceName () {
        return deviceName;
    }
	public Date getStart () {
        return start;
    }
    public Date getFinish () {
        return finish;
    }
    public int getStartPercent () {
        return startPercent;
    }
    public int getFinishPercent () {
        return finishPercent;
    }
    public Boolean getAfterUse () {
    	return afterUse;
    }
    public void setUserName (String userName) {
    	this.userName = userName;
    }
    public void setDeviceName (String deviceName) {
    	this.deviceName = deviceName;
    }
    public void setStart (Date start) {
        this.start = start;
    }
    public void setFinish (Date finish) {
        this.finish = finish;
    }
    public void setStartPercent (int startPercent) {
        this.startPercent = startPercent;
    }
    public void setFinishPercent (int finishPercent) {
        this.finishPercent = finishPercent;
    }
    public void setAfterUse (Boolean afterUse) {
    	this.afterUse = afterUse;
    }
}