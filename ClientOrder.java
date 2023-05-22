package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class ClientOrder implements Runnable{
	
	protected TreeMap<String, Integer> numOrders;
	protected TreeMap<String, Double> costs;
	private File fileOrder;
	
	
	public ClientOrder(TreeMap<String, Double> itemCosts, String fileName) {
		numOrders = new TreeMap<String, Integer>();
		costs = itemCosts;
		fileOrder = new File(fileName);
	}
	

	
	public double getTotal() {
		double totalNum = 0.0;
		
		Set<String> order = numOrders.keySet();
		for(String orderName : order) {
			totalNum += (numOrders.get(orderName) * costs.get(orderName));
		}
		
		return totalNum;
	}
	
	public String getDetails() {
		StringBuilder details = new StringBuilder();
		
		Set<String> orderItems = numOrders.keySet();
		for(String item : orderItems) {
			double cost = costs.get(item);
			int quantity = numOrders.get(item);
			double totalCost = cost * quantity;
			
			details.append("Item's name: ").append(item)
			.append(", Cost per item: ").append(NumberFormat.getCurrencyInstance().format(cost))
			.append(", Quantity: ").append(quantity).append(", Cost: ").append(NumberFormat.getCurrencyInstance().format(totalCost))
			.append("\n");
			
		}
		
		details.append("Order Total: " + NumberFormat.getCurrencyInstance().format(this.getTotal()) + "\n");
		
		return details.toString();
	}
	
	@Override
	public void run() {
		Scanner scanner;
		try {
			scanner = new Scanner(fileOrder);
			scanner.next();
			
			System.out.println("Reading order for client with id: " + scanner.nextInt());
			
			while(scanner.hasNext()) {
				String currItem = scanner.next();
				
				if(numOrders.containsKey(currItem)) {
					numOrders.replace(currItem, numOrders.get(currItem) + 1);
				}else {
					numOrders.put(currItem,  1);
				}
				
				scanner.next();
			}
			
			scanner.close();
			
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
