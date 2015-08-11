package com.edmond.jimi.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order extends Entity {
	public String code = "";
	public Date orderTime;
	public String customer = "";
	public double total;
	public String salesman = "";
	public String address = "";
	public String customerphone = "";

	public List<OrderItem> items = new ArrayList<OrderItem>();

	public String getTotal() {
		BigDecimal t = new BigDecimal("0");
		BigDecimal price;
		BigDecimal qty;
		for (OrderItem item : items) {
			// total += (item.price * item.quantity);
			price = new BigDecimal(item.price);
			qty = new BigDecimal(item.quantity);
			if (item.flag == 2) {
				t = t.subtract(price.multiply(qty));
			}else if(item.flag==1||item.flag==3||item.flag==4){
				
			}
			else {
				t = t.add(price.multiply(qty));
			}
		}
		return t.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
    public String getTotalProfit() {
        BigDecimal t = new BigDecimal("0");
        BigDecimal profit;
        BigDecimal qty;
        for (OrderItem item : items) {
            // total += (item.price * item.quantity);
            profit = new BigDecimal(item.profit);
            qty = new BigDecimal(item.quantity);
            if (item.flag == 2) {
                t = t.subtract(profit.multiply(qty));
            }else if(item.flag==1||item.flag==3||item.flag==4){

            }
            else {
                t = t.add(profit.multiply(qty));
            }
        }
        return t.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
