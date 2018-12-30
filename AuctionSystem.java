import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;

public class AuctionSystem implements Serializable {
	
	public static void main(String[] args) {
		String username;
		String command;
		
		AuctionTable auctionTable = new AuctionTable();

		System.out.println("Starting...");
		String currentDirectory = System.getProperty("user.dir");
		boolean prevTable = new File(currentDirectory, "auction.obj").exists();

		if (prevTable) {
			try {
				FileInputStream file = new FileInputStream("auction.obj");
				ObjectInputStream inStream = new ObjectInputStream(file);
				auctionTable = (AuctionTable) inStream.readObject();
				
				inStream.close();
				file.close();
				
				System.out.println("Loading previous Auction Table.");
				
			} catch (IOException ex) {
				System.out.println("IO Exception");
			} catch (ClassNotFoundException ex) {
				System.out.println("ClassNotFoundException");
			}
		}
		
		else if (!prevTable) {
			System.out.println("No previous table detected.");
			System.out.println("Creating new table...");
			System.out.println();
		}
		
		
		System.out.print("Please enter a username: ");
		Scanner input = new Scanner(System.in);
		Scanner input2 = new Scanner(System.in);
		username = input.nextLine();
		
		do {	
			System.out.println();
			System.out.println("Menu: ");
			System.out.println("     (D) - Import Data from URL");
			System.out.println("     (A) - Create a New Auction");
			System.out.println("     (B) - Bid on an Item");
			System.out.println("     (I) - Get Info on Auction");
			System.out.println("     (P) - Print All Auctions");
			System.out.println("     (R) - Remove Expired Auctions");
			System.out.println("     (T) - Let Time Pass");
			System.out.println("     (Q) - Quit");
			System.out.println();
			System.out.print("Please select an option: ");

			command = input.next();
			
			if (command.equals("D")) {
				System.out.println();
				System.out.print("Please enter a URL: ");
				String inp = input.next();
				try {
					auctionTable = AuctionTable.buildFromURL(inp);
					System.out.println();
					System.out.println("Loading...");
					System.out.println("Auction data loaded successfully!");
				} catch (big.data.DataSourceException ex) {
					System.out.println();
				}
			}
			
			else if (command.equals("A")) {
				System.out.println();
				System.out.println("Creaing new Auction as " + username);
				System.out.print("Enter an Auction ID: ");
				String auctionID = input.next();
				System.out.print("Please enter an Auction time (hours): ");
				int auctionTime = input.nextInt();
				System.out.print("Please enter some Item Info: ");
				String itemInfo = input2.nextLine();
				
				Auction auc = new Auction(auctionTime, 0, auctionID, username, "", itemInfo);
				auctionTable.putAuction(auctionID, auc);
				
				System.out.println();
				System.out.println("Auction " + auctionID + " inserted into table");
			}
			
			else if (command.equals("B")) {
				System.out.println();
				System.out.print("Please enter an Auction ID: ");
				String id = input.next();
				System.out.println();
				boolean bidAccepted = false;
				
				if (auctionTable.getAuction(id).getTimeRemaining() > 0 ) {
					System.out.println("Auction " + id + " is OPEN");
					if (auctionTable.getAuction(id).getCurrentBid() != 0) 
						System.out.println("\tCurrent Bid: $ " + auctionTable.getAuction(id).getCurrentBid());
					else if (auctionTable.getAuction(id).getCurrentBid() == 0)
						System.out.println("\tCurrent Bid: NONE");
					
					System.out.println();
					System.out.print("What would you like to bid?: ");
					double bid = input.nextDouble();
					try {
						if (bid > auctionTable.getAuction(id).getCurrentBid()) {
							auctionTable.getAuction(id).newBid(username, bid);
							bidAccepted = true;
						}
						
						else 
							System.out.println("Bid must be greater than current bid.");
					} catch (ClosedAuctionException ex) {
						System.out.println("Auction is closed.");
					}
					
				}
				
				else if (auctionTable.getAuction(id).getTimeRemaining() <= 0) {
					System.out.println("Auction " + id + " is CLOSED");
					System.out.println("\tCurrent Bid: $ " + auctionTable.getAuction(id).getCurrentBid());
					System.out.println("You can no longer bid on this item.");
				}
				
				if (bidAccepted == true) 
					System.out.println("Bid accepted.");
			}
			
			else if (command.equals("I")) {
				System.out.println();
				System.out.print("Please enter an Auction ID: ");
				String auctionID = input.next();
				
				Auction auction = auctionTable.getAuction(auctionID);
				
				if (auction != null) {
					System.out.println();
					System.out.println("Auction " + auctionID);
					System.out.println("     Seller: " + auction.getSellerName());
					System.out.println("     Buyer: " + auction.getBuyerName());
					System.out.println("     Time: " + auction.getTimeRemaining() + " hours");
					System.out.println("     Info: " + auction.getItemInfo());
				}
				
				else if (auction == null) {
					System.out.println();
					System.out.println("Invalid Auction ID. Try again.");
				}
			}
			
			else if (command.equals("P")) {
				auctionTable.printTable();
			}
			
			else if (command.equals("R")) {
				auctionTable.removeExpiredAuctions();
				
				System.out.println();
				System.out.println("Removing expired auctions...");
				System.out.println("All expired auctions removed.");
			}
			
			else if (command.equals("T")) {
				System.out.println();
				System.out.print("How many hours should pass: ");
				int numHours = input.nextInt();
				auctionTable.letTimePass(numHours);
				
				System.out.println();
				System.out.println("Time passing...");
				System.out.println("Auction times updated.");
			}
			
			else if (command.equals("Q")) {
				try {
					FileOutputStream file = new FileOutputStream("auction.obj");
					ObjectOutputStream outStream = new ObjectOutputStream(file);
					outStream.writeObject(auctionTable);		
					
					outStream.close();
					file.close();
					
					System.out.println();
					System.out.println("Writing Auction Table to file...");
					System.out.println("Done!");
					
					
				} catch (IOException ex) {
					System.out.println("IO Exception.");
				}
				System.exit(0);
				System.out.println("Goodbye.");
			}
			
			else {
				System.out.println();
				System.out.println("Invalid input. Try again");
			}
			
		} while (command != "Q");
	}
}
