import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import big.data.DataSource;
import big.data.DataSourceException;

/* Vivian Lam
 * ID: 111549991
 * vivian.lam@stonybrook.edu
 * Homework 6
 * CSE214, R11 (Reed Gantz)
 */ 

/**
 * @author vivi3
 * The database of open auctions will be stored in a hash table to provide constant 
 * time insertion and deletion
 */
public class AuctionTable extends Hashtable<String, Auction> implements Serializable {
	
	/**
	 * Uses the BigData library to construct an AuctionTable from a remote data source.
	 * @param URL
	 * @return The AuctionTable constructed from the remote data source.
	 * @throws IllegalArgumentException Thrown if the URL does not represent a valid datasource 
	 * (can't connect or invalid syntax).
	 * @throws big.data.DataSourceException
	 */
	public static AuctionTable buildFromURL(String URL) throws IllegalArgumentException, big.data.DataSourceException {
		AuctionTable table = new AuctionTable();
		DataSource ds = null;
		try {
			ds = DataSource.connect(URL).load();
		} catch (big.data.DataSourceException ex) {
			System.out.println("error");
		}
		String[] listSellerName = ds.fetchStringArray("listing/seller_info/seller_name");
		String[] listSellerName2 = new String[listSellerName.length];
		for (int i = 0; i < listSellerName.length; i++) {
			if (listSellerName[i].contains("\n")) {
				String newstr = listSellerName[i].replaceAll("\n", " ");
				newstr = newstr.trim().replaceAll(" +", " ");
				listSellerName2[i] = newstr;
			}
			else
				listSellerName2[i] = listSellerName[i];
		}
		String[] listCurrentBid = ds.fetchStringArray("listing/auction_info/current_bid");
		double[] listCurrentBid2 = new double[listCurrentBid.length];
		for (int i = 0; i < listCurrentBid.length; i++) {
			if (listCurrentBid[i].contains("$")) {
				listCurrentBid[i] = listCurrentBid[i].substring(1);
				if (listCurrentBid[i].contains(","))
					listCurrentBid[i] = listCurrentBid[i].replaceAll(",", "");
			}
			double result = Double.parseDouble(listCurrentBid[i]);
			listCurrentBid2[i] = result;
		}
		String[] listAuctionID = ds.fetchStringArray("listing/auction_info/id_num");
		String[] listBuyer = ds.fetchStringArray("listing/auction_info/high_bidder/bidder_name");
		String[] listTime = ds.fetchStringArray("listing/auction_info/time_left");
		int[] listTime2 = new int[listTime.length];
		for (int i = 0; i < listTime.length; i++) {
			int time = 0;
			if (listTime[i].contains("day")) {
				time = Character.getNumericValue(listTime[i].charAt(0)) * 24;
				if (listTime[i].contains("hour") || listTime[i].contains("hr")) {
					String str = listTime[i].substring(listTime[i].lastIndexOf(", ")+2);
					String news = str.substring(str.indexOf(" "));
					String str1 = str.substring(0, str.length()-news.length()+1);
					str1 = str1.trim();
					time += Integer.parseInt(str1);
				}
			}
			else if (listTime[i].contains("hour") || listTime[i].contains("hr")) {
				time = Character.getNumericValue(listTime[i].charAt(0));
			}
			listTime2[i] = time;
		}
		String[] listmemory = ds.fetchStringArray("listing/item_info/memory");
		String[] listdrive = ds.fetchStringArray("listing/item_info/hard_drive");
		String[] listcpu = ds.fetchStringArray("listing/item_info/cpu");
		
		String[] itemInfo = new String[11];
		for (int i = 0; i < listSellerName2.length; i++) {
			if (listmemory[i].equals(""))
				listmemory[i] = "N/A";
			if (listcpu[i].equals(""))
				listcpu[i] = "N/A";
			if (listdrive[i].equals(""))
				listdrive[i] = "N/A";
			itemInfo[i] = listcpu[i] + ", " + listmemory[i] + ", " + listdrive[i];
		}
	
		for (int i =0; i < listSellerName2.length; i++) {
			Auction auc = new Auction(listTime2[i], listCurrentBid2[i], listAuctionID[i], listSellerName2[i],
					listBuyer[i], itemInfo[i]);
			table.put(listAuctionID[i], auc); 
		}
		return table;
	}
	
	/**
	 * Manually posts an auction, and add it into the table.
	 * @param auctionID the unique key for this object
	 * @param auction The auction to insert into the table with the corresponding auctionID
	 * @throws IllegalArgumentException thrown if the given auctionID is already stored in the table.
	 */
	public void putAuction(String auctionID, Auction auction) throws IllegalArgumentException {
		if (!containsKey(auctionID))
			put(auctionID, auction);
		else
			throw new IllegalArgumentException();
	}
	
	/**
	 * Get the information of an Auction that contains the given ID as key
	 * @param auctionID the unique key for this object
	 * @return information of an Auction that contains the given ID as key
	 */
	public Auction getAuction(String auctionID) {
		if (containsKey(auctionID)) 
			return (Auction) get(auctionID);
		else 
			return null;
	}
	
	/**
	 * Simulates the passing of time. Decrease the timeRemaining of all Auction objects by 
	 * the amount specified.
	 * @param numHours
	 * @throws IllegalArgumentException If the given numHours is non positive
	 */
	public void letTimePass(int numHours) throws IllegalArgumentException {
		if (numHours < 0)
			throw new IllegalArgumentException();
		
		forEach((auctionID, auction) -> {
			((Auction) auction).decrementTimeRemaining(numHours);;
			replace(auctionID, auction);
		});	
	}
	
	/**
	 * Iterates over all Auction objects in the table and removes them if they are expired 
	 */
	public void removeExpiredAuctions() {
		
		Set<String> s = keySet();
		
		Iterator<String> itr = s.iterator();
		while (itr.hasNext()) {
			String auc = itr.next();
			Auction auction = get(auc);
			if (auction.getTimeRemaining() == 0)
				itr.remove();
		}
	}
	
	/**
	 * Prints the AuctionTable in tabular form.
	 */
	public void printTable() {
		System.out.printf("%-15s%-20s%-25s%-20s%-8s%-20s", "Auction ID |", "Bid   |", "Seller     |", 
				"Buyer       |", "Time   |", "  Item Info");
		System.out.println();
		System.out.println("============================================================================================================================================");
		forEach((auctionID, auction) -> {
			if (((Auction) auction).getItemInfo().length() > 48) {
				String newInfo = ((Auction) auction).getItemInfo().substring(0, 48);
				if (auction.getCurrentBid() == 0) {
					System.out.printf("%-13s%-10s%-25s%-27s%-15s%-20s", ((Auction) auction).getAuctionId(), "",
							((Auction) auction).getSellerName(), ((Auction) auction).getBuyerName(), ((Auction) auction).getTimeRemaining() + " hours", newInfo);
				}
				System.out.printf("%-13s%-10s%-25s%-27s%-15s%-20s", ((Auction) auction).getAuctionId(), "$" + ((Auction) auction).getCurrentBid(),
						((Auction) auction).getSellerName(), ((Auction) auction).getBuyerName(), ((Auction) auction).getTimeRemaining() + " hours", newInfo);
			}
			
			else {
				if (auction.getCurrentBid() == 0) {
					System.out.printf("%-13s%-10s%-25s%-27s%-15s%-20s", ((Auction) auction).getAuctionId(), "",
							((Auction) auction).getSellerName(), ((Auction) auction).getBuyerName(), ((Auction) auction).getTimeRemaining() + " hours", ((Auction) auction).getItemInfo());
				}
				else 
					System.out.printf("%-13s%-10s%-25s%-27s%-15s%-20s", ((Auction) auction).getAuctionId(), "$" + ((Auction) auction).getCurrentBid(),
					((Auction) auction).getSellerName(), ((Auction) auction).getBuyerName(), ((Auction) auction).getTimeRemaining() + " hours", ((Auction) auction).getItemInfo());
			}
			System.out.println();
		});	
	}
}
