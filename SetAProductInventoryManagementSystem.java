package pckExer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SetAProductInventoryManagementSystem {

    private JFrame frame;
    private boolean isManagerLoggedIn = false;
    private JTextField txtId, txtName, txtPrice, txtQuantity, txtBuyId, txtBuyQty;
    private JTable managerTable, customerTable;
    private DefaultTableModel managerTableModel, customerTableModel;
    private Inventory inventory;
    private JPanel managerCardPanel; 
    private CardLayout managerCardLayout;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SetAProductInventoryManagementSystem window = new SetAProductInventoryManagementSystem();
                window.frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public SetAProductInventoryManagementSystem() {
        inventory = new Inventory();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Product Inventory Management System");
        frame.setBounds(100, 100, 900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        //Home Tab
        JPanel homePanel = new JPanel(new BorderLayout());
        
        homePanel.add(new JLabel("<html><center><h1>Product Inventory Management System Home</h1>"
                + "<p>Welcome! Browse the Customer Store to see our stock.<br>"
                + "<i>(The Home tab is currently under construction.)</i></p></center></html>", SwingConstants.CENTER));
                
        tabbedPane.addTab("Home", homePanel);

        //Customer Store
        JPanel customerPanel = new JPanel(new BorderLayout(10, 10));
        customerTableModel = new DefaultTableModel(new String[]{"Product ID (Hidden)", "Name", "Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.removeColumn(customerTable.getColumnModel().getColumn(0));
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        
        JPanel cBtnP = new JPanel();
        JButton btnCustView = new JButton("View Product Details");
        JButton btnPurchase = new JButton("Purchase Product");
        btnPurchase.setBackground(new Color(50, 205, 50)); btnPurchase.setForeground(Color.WHITE);
        cBtnP.add(btnCustView); cBtnP.add(new JLabel("Product ID:"));
        txtBuyId = new JTextField(5); cBtnP.add(txtBuyId);
        cBtnP.add(new JLabel("Quantity:")); txtBuyQty = new JTextField(3); cBtnP.add(txtBuyQty);
        cBtnP.add(btnPurchase);
        customerPanel.add(cBtnP, BorderLayout.SOUTH);
        tabbedPane.addTab("Customer Store", customerPanel);

        //Inventory Manager (Authorization required)
        managerCardLayout = new CardLayout();
        managerCardPanel = new JPanel(managerCardLayout);
        
        JPanel lockP = new JPanel(new GridBagLayout());
        JButton btnLogin = new JButton("Admin Login Required!");
        lockP.add(btnLogin);
        managerCardPanel.add(lockP, "LOCKED!");

        JPanel dashP = new JPanel(new BorderLayout(5, 5));
        JPanel inP = new JPanel(new GridLayout(4, 2, 5, 5));
        inP.add(new JLabel("Product ID:")); txtId = new JTextField(); inP.add(txtId);
        inP.add(new JLabel("Name:")); txtName = new JTextField(); inP.add(txtName);
        inP.add(new JLabel("Price:")); txtPrice = new JTextField(); inP.add(txtPrice);
        inP.add(new JLabel("Quantity:")); txtQuantity = new JTextField(); inP.add(txtQuantity);
        dashP.add(inP, BorderLayout.NORTH);

        managerTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        managerTable = new JTable(managerTableModel);
        dashP.add(new JScrollPane(managerTable), BorderLayout.CENTER);

        JPanel mBtns = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton btnAdd = new JButton("Add Product");
        JButton btnPrice = new JButton("Update Price");
        JButton btnQty = new JButton("Update Quantity");
        JButton btnDel = new JButton("Delete Product");
        JButton btnOut = new JButton("Logout");
        mBtns.add(btnAdd); mBtns.add(btnPrice); mBtns.add(btnQty); mBtns.add(btnDel); mBtns.add(btnOut);
        dashP.add(mBtns, BorderLayout.SOUTH);
        managerCardPanel.add(dashP, "DASHBOARD");
        tabbedPane.addTab("Inventory Manager", managerCardPanel);

        //Logic
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2 && !isManagerLoggedIn) {
                managerCardLayout.show(managerCardPanel, "LOCKED!"); // Added exclamation mark
            }
        });
        managerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && managerTable.getSelectedRow() != -1) {
                int selectedRow = managerTable.getSelectedRow();
                
                txtId.setText(managerTableModel.getValueAt(selectedRow, 0).toString());
                txtName.setText(managerTableModel.getValueAt(selectedRow, 1).toString());
                txtPrice.setText(managerTableModel.getValueAt(selectedRow, 2).toString());
                txtQuantity.setText(managerTableModel.getValueAt(selectedRow, 3).toString());
            }
        });
        
        btnLogin.addActionListener(e -> {
            JPanel p = new JPanel(new GridLayout(2, 2));
            JTextField u = new JTextField(); 
            JPasswordField ps = new JPasswordField();
            p.add(new JLabel("Username:")); 
            p.add(u); 
            p.add(new JLabel("Password:")); 
            p.add(ps);
            
            if (JOptionPane.showConfirmDialog(frame, p, "Login", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                if (inventory.authenticateManager(u.getText(), new String(ps.getPassword()))) {
                    isManagerLoggedIn = true;
                    managerCardLayout.show(managerCardPanel, "DASHBOARD");
                    refreshTables();
                } else {
                    JOptionPane.showMessageDialog(frame, "Wrong username and password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCustView.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(frame, "Select a product first!"); return; }
            try {
                int id = (int) customerTableModel.getValueAt(row, 0);
                Product prod = inventory.getProduct(id);
                JOptionPane.showMessageDialog(frame, prod.getFormattedDetails(), "Product Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
        });

        //Purchase logic with confirmation
        btnPurchase.addActionListener(e -> {
            if (txtBuyId.getText().isEmpty() || txtBuyQty.getText().isEmpty()) { 
                JOptionPane.showMessageDialog(frame, "Enter Product ID and Quantity."); return; 
            }
            try {
                int id = Integer.parseInt(txtBuyId.getText());
                int q = Integer.parseInt(txtBuyQty.getText());
                
                if (q <= 0) {
                    JOptionPane.showMessageDialog(frame, "Quantity must be positive.");
                    return; 
                }

                Product p = inventory.getProduct(id);
                
                if (q <= p.getQuantity()) {
                    double total = p.getPrice() * q;
                    
                    //Pre-purchase confirmation
                    String prompt = String.format("You're purchasing %s (%d units) at $%.2f each.\nTotal: $%.2f\n\nProceed to buy?", 
                                                 p.getProductName(), q, p.getPrice(), total);
                    
                    int confirmAction = JOptionPane.showConfirmDialog(frame, prompt, "Confirm Purchase", JOptionPane.YES_NO_OPTION);
                    
                    if (confirmAction == JOptionPane.YES_OPTION) {
                        //Transaction
                        inventory.updateProductQuantity(id, p.getQuantity() - q); 
                        
                        String receipt = String.format(
                            "----- OFFICIAL RECEIPT -----\n" +
                            "Item:       %s\n" +
                            "Quantity:   %d\n" +
                            "Total Cost: $%.2f\n" +
                            "---------------------------\n" +
                            "Thank you for your purchase!", 
                            p.getProductName(), q, total);
                        
                        JOptionPane.showMessageDialog(frame, receipt);
                        refreshTables();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient quantity available");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Invalid input. Check Product ID and Quantity."); }
        });

        btnAdd.addActionListener(e -> {
            if (txtId.getText().isEmpty() || txtName.getText().isEmpty()) { JOptionPane.showMessageDialog(frame, "Empty fields!"); return; }
            try {
                int id = Integer.parseInt(txtId.getText());
                if (id < 1) { JOptionPane.showMessageDialog(frame, "ID must start at 1!"); return; }
                inventory.addProduct(new Product(id, txtName.getText(), Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQuantity.getText())));
                refreshTables();
                clearManagerFields();
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
        });

        btnDel.addActionListener(e -> {
            if (txtId.getText().isEmpty()) { 
                JOptionPane.showMessageDialog(frame, "Please enter a Product ID to delete."); 
                return; 
            }
            
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try { 
                    int id = Integer.parseInt(txtId.getText());
                    
                    inventory.deleteProduct(id); 
                    
                    refreshTables(); 
                    clearManagerFields();
                    JOptionPane.showMessageDialog(frame, "Product deleted successfully.");
                } catch (Exception ex) { 
                    JOptionPane.showMessageDialog(frame, ex.getMessage()); 
                }
            }
        });
        
        btnPrice.addActionListener(e -> {
            try { inventory.updateProductPrice(Integer.parseInt(txtId.getText()), Double.parseDouble(txtPrice.getText())); refreshTables(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
        });

        btnQty.addActionListener(e -> {
            try { inventory.updateProductQuantity(Integer.parseInt(txtId.getText()), Integer.parseInt(txtQuantity.getText())); refreshTables(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
        });

        btnOut.addActionListener(e -> {
            isManagerLoggedIn = false;
            managerCardLayout.show(managerCardPanel, "LOCKED");
            tabbedPane.setSelectedIndex(0);
        });

        refreshTables();
    }

    private void clearManagerFields() {
        txtId.setText(""); txtName.setText(""); txtPrice.setText(""); txtQuantity.setText("");
    }

    private void refreshTables() {
        managerTableModel.setRowCount(0); customerTableModel.setRowCount(0);
        List<Product> list = inventory.getProductsList();
        for (Product p : list) {
            managerTableModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getPrice(), p.getQuantity()});
            if(p.getQuantity() > 0) customerTableModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getPrice()});
        }
    }
}