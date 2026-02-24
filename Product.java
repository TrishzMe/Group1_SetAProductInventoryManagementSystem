package pckExer;

public class Product {
    private int productId;
    private String productName;
    private double price;
    private int quantity;

    public Product(int productId, String productName, double price, int quantity) 
            throws InvalidPriceException, InvalidQuantityException {
        if (price < 0) throw new InvalidPriceException("Price cannot be negative.");
        if (quantity < 0) throw new InvalidQuantityException("Quantity cannot be negative.");
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() {
    	return productId;
    	}
    
    public String getProductName() {
    	return productName;
    	}
    
    public double getPrice() {
    	return price;
    	}
    
    public int getQuantity() {
    	return quantity;
    	}

    public void setPrice(double price) throws InvalidPriceException {
        if (price < 0) throw new InvalidPriceException("Price cannot be negative.");
        this.price = price;
    }

    public void updateQuantity(int quantity) throws InvalidQuantityException {
        if (quantity < 0) throw new InvalidQuantityException("Quantity cannot be negative.");
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %-20s | Price: $%.2f | Qty: %d", 
                             productId, productName, price, quantity);
    }

    public String getFormattedDetails() {
        return "--- Product Details ---\n" +
               "Product ID:   " + productId + "\n" +
               "Product Name:  " + productName + "\n" +
               "Product Price:   $" + String.format("%.2f", price) + "\n" +
               "Quantity:     " + quantity + " units\n" +
               "-----------------------";
    }
}