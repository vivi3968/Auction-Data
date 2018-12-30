import java.io.Serializable;

/* Vivian Lam
 * ID: 111549991
 * vivian.lam@stonybrook.edu
 * Homework 6
 * CSE214, R11 (Reed Gantz)
 */ 

/**
 * @author vivi3
 * Represents an active auction currently in the database
 */
public class Auction implements Serializable {
	
	private int timeRemaining;
	private double currentBid;
	private String auctionId, sellerName, buyerName, itemInfo;
	
	/**
	 * Constructor for Auction
	 */
	public Auction() {
	}
	
	/**
	 * Constructor with parameters for auction
	 * 
	 * @param timeRemaining
	 * @param auctionId
	 * @param sellerName
	 * @param buyerName
	 * @param itemInfo
	 */
	public Auction(int timeRemaining, String auctionId, String sellerName, String buyerName,
			String itemInfo) {
		this.timeRemaining = timeRemaining;
		this.currentBid = currentBid;
		this.auctionId = auctionId;
		this.sellerName = sellerName;
		this.buyerName = buyerName;
		this.itemInfo = itemInfo;
	}
	
	/**
	 * Constructor with parameters for auction
	 * 
	 * @param timeRemaining
	 * @param currentBid
	 * @param auctionId
	 * @param sellerName
	 * @param buyerName
	 * @param itemInfo
	 */
	public Auction(int timeRemaining, double currentBid, String auctionId, String sellerName, String buyerName,
			String itemInfo) {
		this.timeRemaining = timeRemaining;
		this.currentBid = currentBid;
		this.auctionId = auctionId;
		this.sellerName = sellerName;
		this.buyerName = buyerName;
		this.itemInfo = itemInfo;
	}

	/**
	 * Accessor method for time remaining
	 * @return remaining time for the auction
	 */
	public int getTimeRemaining() {
		return timeRemaining;
	}

	/**
	 * Accessor method for current bid
	 * @return what the current bid is
	 */
	public double getCurrentBid() {
		return currentBid;
	}

	/**
	 * Accesor method for auction 
	 * @return auction id 
	 */
	public String getAuctionId() {
		return auctionId;
	}

	/**
	 * Accessor method for seller name
	 * @return seller name
	 */
	public String getSellerName() {
		return sellerName;
	}

	/**
	 * Accessor method for buyer name
	 * @return name of buyer
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * Info pertaining to memory, hard drive, and cpu
	 * @return item info 
	 */
	public String getItemInfo() {
		return itemInfo;
	}
	
	/**
	 * Decreases the time remaining for this auction by the specified amount
	 * @param time to decrement by
	 */
	public void decrementTimeRemaining(int time) {
		if (time > this.timeRemaining) 
			this.timeRemaining = 0;
		else
			this.timeRemaining -= time;
	}
	
	/**
	 * Makes a new bid on this auction
	 * @param bidderName
	 * @param bidAmt
	 * @throws ClosedAuctionException Thrown if the auction is closed and no more bids can be placed 
	 */
	public void newBid(String bidderName, double bidAmt) throws ClosedAuctionException {
		if (this.timeRemaining > 0){
			if (bidAmt > this.currentBid) {
				this.currentBid = bidAmt;
				this.buyerName = bidderName;
			}
		}
		
		else if (this.timeRemaining <= 0)
			throw new ClosedAuctionException(); 
	}
	
	/** (non-Javadoc)
	 * string of data members in tabular form.
	 */
	public String toString() {
		return "";
	}

	
}
