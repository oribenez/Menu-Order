package com.example.menuapp;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Class that handles the Graphic stage of this menu app
 */
public class MenuController {

    @FXML
    private GridPane gridMenu;

    @FXML
    private Label txtTotalPrice;

    @FXML
    private Button btnPlaceOrder;

    @FXML
    private Label lblMessage;

    @FXML
    private Rectangle rectToast;

    private int gridMenuRowCount;
    private Menu menu;
    private Order order;
    private boolean lockActionEvent = false;
    final String MENU_FILENAME = "menu.txt";

    /**
     * Function which initialize the graphical menu grid using GridPane and reads the menu from a given file
     */
    public void initialize(){
        this.menu = new Menu();
        this.order = new Order();
        gridMenuRowCount = 1; //starting at 1 cor cosmetic reasons
        btnPlaceOrder.setDisable(true);

        //disable toast
        lblMessage.setVisible(false);
        rectToast.setVisible(false);

        try{
            this.menu.readMenuFromFile(MENU_FILENAME);

            ArrayList<String> categories = this.menu.getAllCategories(); // list of all categories
            for (String category : categories){
                //add items to gridPane for every category
                addItemsToMenuList(category, menu);
            }

        } catch (FileNotFoundException | InvalidInputFileException e){
            Alert errAlert = new Alert(Alert.AlertType.ERROR);
            String errMessage;
            if(e instanceof FileNotFoundException) errMessage = "The file \"" + MENU_FILENAME + "\" not found.";
            else errMessage = "The input file is not in the correct structure.";

            errAlert.setTitle("Error");
            errAlert.setContentText(errMessage);
            Optional<ButtonType> result = errAlert.showAndWait();
            if(result.isPresent()){
                System.exit(0);
            }
        }
    }

    /**
     * Function which generates title block for the menu such as Starters, Main Meals, Beverages... and add it to the GridPane
     * @param strTitle the name of the title
     * @param row the row to add it to the grid
     */
    private void addTitleMenuList(String strTitle, int row){
        Rectangle rect = new Rectangle(gridMenu.getPrefWidth(),30);
        Label lblTitle = new Label(strTitle);
        lblTitle.setStyle("-fx-padding: 5px 0 0 5px; -fx-font-size: 14px; -fx-font-family: Verdana; -fx-font-weight: bold;");
        AnchorPane ap = new AnchorPane(rect, lblTitle);

        rect.setFill(Color.web("#d6b0ff"));
        gridMenu.add(ap, 0, row);
    }

    /**
     * enum which represents the indexes of each node in the grid row
     */
    public enum MenuFields{
        CK_ISACTIVE, LBL_ITEMNAME, LBL_PRICE,CMB_QTY, LBL_TOTAL
    }

    /**
     * Function that adds items to graphic menu, GridPane by a given category
     */
    private void addItemsToMenuList(String category, Menu menu){
        ArrayList<MenuItem> listMenu = menu.getListMenuByCategory(category);

        addTitleMenuList(category, gridMenuRowCount++); // create bold row title for category
        for (int i = 0; i < listMenu.size(); i++) {
            MenuItem currItem = listMenu.get(i);

            CheckBox ckItem = new CheckBox();
            ckItem.setPadding(new Insets(0, 0, 0, 7));
            ckItem.setUserData(currItem.getName());

            int changedRow = gridMenuRowCount;
            ckItem.setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if(lockActionEvent) return;
                    else lockActionEvent = true;

                    Node node = (Node) actionEvent.getTarget();
                    if (node instanceof CheckBox){
                        CheckBox ck = (CheckBox) node;
                        String strItemName = ck.getUserData().toString();
                        MenuItem mi = menu.getMenuItemByName(strItemName);
                        Label lblTotal = (Label)getNodeByCoordinate(changedRow, MenuFields.LBL_TOTAL.ordinal());
                        //no need to check if this a combobox since I am sure it is a combobox because of the well organized grid
                        ComboBox<Integer> cmbQty = (ComboBox<Integer>)getNodeByCoordinate(changedRow, MenuFields.CMB_QTY.ordinal());
                        if (ck.isSelected()){
                            OrderItem oi = new OrderItem(mi,1);
                            order.addDish(oi);
                            cmbQty.setValue(1);
                            btnPlaceOrder.setDisable(order.isEmpty());
                            lblTotal.setText(mi.getPriceWCurrency());
                        } else {
                            order.removeDishByName(strItemName);
                            cmbQty.setValue(0);
                            btnPlaceOrder.setDisable(order.isEmpty());
                            lblTotal.setText("-");
                        }
                        txtTotalPrice.setText(MenuItem.CURRENCY_SYMBOL + "" + order.getTotal());
                    }
                    lockActionEvent = false;
                }
            });

            Label lblItemName = new Label(currItem.getName());
            lblItemName.setLabelFor(ckItem);

            Label lblPrice = new Label(currItem.getPriceWCurrency());

            ComboBox<Integer> cmbQty = new ComboBox<>();
            cmbQty.setUserData(currItem.getName());
            cmbQty.setStyle("-fx-background-color: #dddddd; -fx-border-color: transparent;");
            final int MAX_QTY = 10;
            for (int j = 0; j < MAX_QTY; j++) {
                cmbQty.getItems().add(j);
            }
            cmbQty.setValue(0);
            cmbQty.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if(lockActionEvent) return;
                    else lockActionEvent = true;

                    Node node = (Node) actionEvent.getTarget();
                    if (node instanceof ComboBox){
                        ComboBox cmbClicked = (ComboBox) node;
                        String strItemName = cmbClicked.getUserData().toString();
                        MenuItem mi = menu.getMenuItemByName(strItemName);
                        int selectedQuantity = Integer.parseInt(cmbClicked.getValue().toString());
                        Label lblTotal = (Label)getNodeByCoordinate(changedRow, MenuFields.LBL_TOTAL.ordinal());
                        CheckBox ck = (CheckBox)getNodeByCoordinate(changedRow, MenuFields.CK_ISACTIVE.ordinal());
                        if (selectedQuantity == 0) {
                            ck.setSelected(false);
                            order.removeDishByName(strItemName);
                            btnPlaceOrder.setDisable(order.isEmpty());
                            lblTotal.setText("-");
                        } else {
                            ck.setSelected(true);
                            OrderItem oi = new OrderItem(mi, selectedQuantity);
                            order.removeDishByName(strItemName);
                            order.addDish(oi);
                            btnPlaceOrder.setDisable(order.isEmpty());
                            lblTotal.setText(MenuItem.CURRENCY_SYMBOL + "" + oi.getTotal());
                        }
                        txtTotalPrice.setText(MenuItem.CURRENCY_SYMBOL + "" + order.getTotal());
                    }

                    lockActionEvent = false;
                }
            });
            Label lblTotal = new Label("-");

            gridMenu.add(ckItem, 0, gridMenuRowCount);
            gridMenu.add(lblItemName, 1, gridMenuRowCount);
            gridMenu.add(lblPrice, 2, gridMenuRowCount);
            gridMenu.add(cmbQty, 3, gridMenuRowCount);
            gridMenu.add(lblTotal, 4, gridMenuRowCount);

            gridMenuRowCount++;
        }
    }

    /**
     * Function that gets a row and a column  and return the node in that specific coordinate in the gridpane
     * @param row row index
     * @param col column index
     */
    public Node getNodeByCoordinate(int row, int col){
        for (Node node : gridMenu.getChildren()){
            int currColInd = GridPane.getColumnIndex(node);
            int currRowInd = GridPane.getRowIndex(node);
            if(currColInd == col && currRowInd == row) return node;
        }
        return null;
    }
    @FXML
    void placeOrderPressed(ActionEvent event) {
        Alert alertPlaceOrder = new Alert(Alert.AlertType.CONFIRMATION);
        alertPlaceOrder.setTitle("Your order");
        alertPlaceOrder.setHeaderText("ORDER:");

        // Add custom buttons
        ButtonType btnPlaceOrder = new ButtonType("Place Order");
        ButtonType btnChangeOrder = new ButtonType("Change Order");
        ButtonType btnCancelOrder = new ButtonType("Cancel order");
        alertPlaceOrder.getButtonTypes().setAll(btnPlaceOrder, btnChangeOrder,btnCancelOrder);

        alertPlaceOrder.getDialogPane().setContent(generateGridWithOrder()); // add grid to alert

        Optional<ButtonType> result = alertPlaceOrder.showAndWait();

        //handle user's response
        if(result.isPresent()){
            if (result.get() == btnPlaceOrder){
                //User clicked Place Order
                confirmOrder();
            }else if (result.get() == btnCancelOrder){
                //User clicked Cancel Order
                resetOrder();
            }
        }
    }

    /**
     * Fucntion that handles the reset operation of the order.
     */
    private void resetOrder(){
        order.reset();
        btnPlaceOrder.setDisable(false);

        lockActionEvent = true;
        for (int i = 0; i < gridMenuRowCount; i++) {
            Node nodeCk = getNodeByCoordinate(i, MenuFields.CK_ISACTIVE.ordinal());
            if (nodeCk instanceof CheckBox){
                CheckBox ck = (CheckBox)nodeCk;
                ck.setSelected(false);
            }
            Node nodeQty = getNodeByCoordinate(i, MenuFields.CMB_QTY.ordinal());
            if (nodeQty instanceof ComboBox){
                ComboBox<Integer> cmbQty = (ComboBox<Integer>)nodeQty;
                cmbQty.setValue(0);
            }
            Node nodeTotal = getNodeByCoordinate(i, MenuFields.LBL_TOTAL.ordinal());
            if (nodeTotal instanceof Label){
                Label lblTotal = (Label)nodeTotal;
                lblTotal.setText("-");
            }
        }
        txtTotalPrice.setText(MenuItem.CURRENCY_SYMBOL + "" + order.getTotal());
        lockActionEvent = false;
    }

    /**
     * function that is called when the order is placed and now the user needs to type his details and save them to a file
     */
    private void confirmOrder() {
        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirm.setTitle("Client info");
        alertConfirm.setHeaderText("Please type your Name followed by your ID.\n ex. Ori208333444");

        TextField txtUserId = new TextField();
        Label lblErr = new Label();
        VBox vb = new VBox(txtUserId,lblErr);
        vb.setSpacing(2);
        alertConfirm.getDialogPane().setContent(vb);

        confirmAlertValidation: while (true) {
            Optional<ButtonType> result = alertConfirm.showAndWait();

            //handle user's response
            if(result.isPresent()){
                if (result.get() == ButtonType.OK){
                    //User clicked Ok

                    String client = txtUserId.getText().trim();
                    //validate clientId
                    final int NAME_MINLENGTH = 2;
                    final int ID_LENGTH = 9;
                    if(client.length() < NAME_MINLENGTH + ID_LENGTH) {
                        lblErr.setText("Too short. ID length is " + ID_LENGTH + " digits, Name is at least " + NAME_MINLENGTH + " characters");
                        continue;
                    }
                    String clientName = client.substring(0, client.length()-ID_LENGTH-1);
                    String clientIdNum = client.substring(client.length()-ID_LENGTH);

                    //check id validity
                    try{
                        Integer.parseInt(clientIdNum);
                    } catch (NumberFormatException e) {
                        lblErr.setText("ID is invalid or too short, it has to include only digits.");
                        continue;
                    }
                    //check name is at least at min length
                    if (clientName.length() < NAME_MINLENGTH) {
                        lblErr.setText("Name has to be at least " + NAME_MINLENGTH + " characters length");
                        continue;
                    }
                    //check name is at least at min length
                    if (clientIdNum.length() != ID_LENGTH) {
                        lblErr.setText("ID has to be at " + ID_LENGTH + " digits length");
                        continue;
                    }

                    //check if client name contains only letters
                    for (int i = 0; i < clientName.length(); i++) {
                        char c = clientName.charAt(i);
                        if (!Character.isLetter(c)) {
                            lblErr.setText("Name needs to include only letters");
                            continue confirmAlertValidation;
                        }
                    }

                    order.setClient(client);
                    saveOrderToFile(client);
                    resetOrder();
                    showToast("Order has been saved successfully.", 3);
                    break;
                }
                else if (result.get() == ButtonType.CANCEL) {
                    alertConfirm.close();
                    break confirmAlertValidation;
                }
            }
        }
    }

    /**
     * Function that handles showing a toast to screen
     * @param msg the message in the toast
     * @param secondsShow time shown on screen
     */
    private void showToast(String msg, int secondsShow){
        lblMessage.setVisible(true);
        rectToast.setVisible(true);
        lblMessage.setText(msg);

        PauseTransition pause = new PauseTransition(Duration.seconds(secondsShow));
        pause.setOnFinished(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                lblMessage.setVisible(false);
                rectToast.setVisible(false);
            }
        });

        pause.play();
    }

    /**
     * function that handels aving the order to a file
     * @param filename the name of the file with the order
     */
    private void saveOrderToFile(String filename){
        try{
            File fileOrder = new File(filename + ".txt");
            FileWriter fw = new FileWriter(fileOrder);
            fw.write(order.toString());
            fw.close();

            boolean success = fileOrder.createNewFile();
        } catch (IOException e){
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Function which generates a grid that includes al the info about this current order
     * this grid is readonly and can't be changed
     */
    private GridPane generateGridWithOrder() {
        GridPane gridOrder = new GridPane();
        gridOrder.setHgap(10); // Set the horizontal gap between columns
        gridOrder.setVgap(10); // Set the vertical gap between rows

        //generate titles
        String[] gridTitles = {"Item", "Price", "Qty", "Total"};
        int row = 0, col = 0;
        for (String title : gridTitles){
            Label lblTitle = new Label(title);
            gridOrder.add(lblTitle, col++, row);
        }

        //generate rows
        row = 1;
        for (OrderItem oi  : order.getListDishes()){
            col = 0;
            MenuItem mi = oi.getMenuItem();
            gridOrder.add(new Label(mi.getName()), col++, row);
            gridOrder.add(new Label(mi.getPrice()+""), col++, row);
            gridOrder.add(new Label(oi.getQuantity()+""), col++, row);
            gridOrder.add(new Label(oi.getTotal()+""), col, row);
            row++;
        }
        Separator sep = new Separator();
        gridOrder.add(sep,0,row++);

        Label lblGrandTotal = new Label("Total: " + MenuItem.CURRENCY_SYMBOL + "" + order.getTotal());
        gridOrder.add(lblGrandTotal, 0,row);

        return gridOrder;
    }
}





