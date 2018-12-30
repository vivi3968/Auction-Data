import java.io.Serializable;

/* Vivian Lam
 * ID: 111549991
 * vivian.lam@stonybrook.edu
 * Homework 6
 * CSE214, R11 (Reed Gantz)
 */ 

/**
 * @author vivi3
 * Thrown if the auction is closed and no more bids can be placed
 */
public class ClosedAuctionException extends Exception implements Serializable  {
	
	/**
	 * Constructor for ClosedAuctionException
	 */
	public ClosedAuctionException() {
		super("Auction is closed");
	}
	
	/**
	 * Constructor for ClosedAuctionException with message
	 * @param message
	 */
	public ClosedAuctionException(String message) {
		super(message);
	}
}
