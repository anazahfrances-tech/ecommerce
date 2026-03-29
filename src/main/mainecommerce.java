import service.ProductService;
import service.CartService;
import model.Product;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ProductService productService = new ProductService();
        CartService cartService = new CartService();
        Scanner scanner = new Scanner(System.in);

        while(true) {

            System.out.println("\n--- ECOMMERCE MENU ---");
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();

            switch(choice) {

                case 1:
                    for(Product p : productService.getAllProducts()) {
                        System.out.println(
                                p.getId() + " " +
                                        p.getName() + " $" +
                                        p.getPrice()
                        );
                    }
                    break;

                case 2:
                    System.out.print("Enter Product ID: ");
                    int id = scanner.nextInt();

                    System.out.print("Enter Quantity: ");
                    int qty = scanner.nextInt();

                    Product product = productService.getProductById(id);

                    if(product != null) {
                        cartService.addToCart(product, qty);
                        System.out.println("Product added to cart");
                    } else {
                        System.out.println("Product not found");
                    }
                    break;

                case 3:
                    cartService.viewCart();
                    break;

                case 4:
                    System.exit(0);
            }
        }
    }
}
