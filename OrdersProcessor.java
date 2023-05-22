package processor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class OrdersProcessor extends Thread {
    public static void main(String[] args) {
        TreeMap<String, Double> itemsCosts = new TreeMap<>();
        TreeMap<Integer, ClientOrder> orders = new TreeMap<Integer, ClientOrder>();
        
        Scanner input = new Scanner(System.in);
        
        System.out.println("Enter item's data file name: ");
        String fileName = input.nextLine();
        
        System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
        String character = input.nextLine();
        
        System.out.println("Enter numbers of orders to process: ");
        int numOrders = input.nextInt();
        
        input.nextLine();
        System.out.println("Enter order's base filename: ");
        String base = input.nextLine();
        
        System.out.println("Enter result's filename: ");
        String results = input.nextLine();
        
        input.close();
        
        long startTime = System.currentTimeMillis();
        
        try {
        	Scanner fileScanner = new Scanner(new File(fileName));
        	
        	while(fileScanner.hasNextLine()) {
        		itemsCosts.put(fileScanner.next(),fileScanner.nextDouble());
        		
        	}
        	
        	fileScanner.close();
        	
        }catch(FileNotFoundException e) {
        	e.printStackTrace();
        } 
   
        if(character.equalsIgnoreCase("y")) {
        	ArrayList<Thread> threads = new ArrayList<>();
        	
        	synchronized(orders) {
        		for(int i = 0; i < numOrders; i++) {
        			try {
        				String fName = base + Integer.toString(i+1) + ".txt";
        				Scanner baseFileScanner = new Scanner(new File(fName));
        				
        				baseFileScanner.next();
        				
        				int clientId = baseFileScanner.nextInt();
        				
        				ClientOrder clientOrder = new ClientOrder(itemsCosts, fName);
        				
        				Thread orderThread = new Thread(clientOrder);
        				threads.add(orderThread);
        				
        				orderThread.start();
        				
        				orders.put(clientId, clientOrder);
        				baseFileScanner.close();
        				
        			}catch(FileNotFoundException e) {
        				e.printStackTrace();
        			}
        		}
        	}
        	
        	for(Thread t : threads) {
        		try {
        			
        			t.join();
        			
        		}catch(InterruptedException e) {
        			e.printStackTrace();
        		}
        	}
        	
        } else {
        	for(int i = 0; i < numOrders; i++) {
        		try {
        			String nameOfFile = base + Integer.toString(i  +1) + ".txt";
        			Scanner baseFileScanner = new Scanner(new File(nameOfFile));
        			
        			baseFileScanner.next();
        			
        			int clientId = baseFileScanner.nextInt();
        			
        			ClientOrder order = new ClientOrder (itemsCosts, nameOfFile);
        			order.run();
        			
        			orders.put(clientId, order);
        			baseFileScanner.close();
        			
        		}catch(FileNotFoundException e) {
        			e.printStackTrace();
        		}
        	}
        }
        
        try { 
        	
        	FileWriter fileWrite = new FileWriter(results);
        	
        	Set<Integer> orderSet = orders.keySet();
        	for(Integer orderId : orderSet) {
        		fileWrite.write("----- Order details for client with Id: " + orderId + " -----\n");
        		fileWrite.write(orders.get(orderId).getDetails());
        		
        	}
        	
        	fileWrite.write("***** Summary of all orders *****\n");
        	
        	double total = 0.00;
        	
        	for(String item : itemsCosts.keySet()) {
        		int itemTotal = 0;
        		
        		for(Integer id : orders.keySet()) {
        			if(orders.get(id).numOrders.containsKey(item)) {
        				itemTotal += orders.get(id).numOrders.get(item);
        			}
        		}
        		
        		if(itemTotal > 0) {
        			fileWrite.write("Summary - Item's name: " + item + ", Cost per item: "
        					+ NumberFormat.getCurrencyInstance().format(itemsCosts.get(item))
        					+ ", Number sold: " + itemTotal + ", Item's Total: "
        					+ NumberFormat.getCurrencyInstance().format(itemTotal * itemsCosts.get(item))
        					+ "\n");
        			
        			total += itemTotal * itemsCosts.get(item);
        		}
        	}
        	
        	fileWrite.write("Summary Grand Total: " + NumberFormat.getCurrencyInstance().format(total) + "\n");
        	
        	fileWrite.close();
        
        }catch(IOException e) {
        	e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Processing time (msec): " + (endTime - startTime));;
        
        System.out.println("Results can be found in the file: " + results);
         
    }
     
}