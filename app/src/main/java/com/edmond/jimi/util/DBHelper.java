package com.edmond.jimi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.entity.Customer;
import com.edmond.jimi.entity.Order;
import com.edmond.jimi.entity.OrderItem;
import com.edmond.jimi.entity.Product;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DBHelper {
    private BigDecimal scoreRate = new BigDecimal(0.01);
    private KangmeiApplication app;
    private static DBHelper dbHelper;
    private static SQLiteDatabase db;
    private ContentValues content;
    private SimpleDateFormat format;
    private SimpleDateFormat sformat;

    private DBHelper(KangmeiApplication app) {
        this.app = app;
        format = new SimpleDateFormat("yyyyMMdd HHmmss");
        initDB();
    }

    public static DBHelper getInstance(KangmeiApplication app) {
        dbHelper = dbHelper == null ? new DBHelper(app) : dbHelper;
        return dbHelper;
    }

    private boolean initDB() {

        File dbFile = new File(app.dbpath + "/kangmei.db");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
                FileOutputStream out = new FileOutputStream(dbFile);
                InputStream in = app.getAssets().open("kangmei.db");
                int i = in.read();
                while (i != -1) {
                    out.write(i);
                    i = in.read();
                }
                out.flush();
                out.close();
                in.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(app, "没找到数据库文件，请检查存储设备是否正常", Toast.LENGTH_SHORT)
                        .show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(app, "无法打开数据库文件，请检查存储设备是否正常", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        try {
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
            content = new ContentValues();
        } catch (Exception sqlExc) {
            sqlExc.printStackTrace();
            Toast.makeText(app, "无法打开数据库，请检查存储设备是否正常", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    public ArrayList<Order> getOrders() {
        ArrayList<Order> list = new ArrayList<Order>();
        StringBuffer sb = new StringBuffer();

        HashMap<String, ArrayList<OrderItem>> map = new HashMap<String, ArrayList<OrderItem>>();
        sb.append("select orderitem.id,orderitem.ordercode,orderitem.product,")
                .append("products.price,orderitem.quantity,orderitem.flag,products.image ")
                .append("from orderitem ,products  where orderitem.product=products.product ");
        System.out.println(sb.toString());
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {

            OrderItem item = new OrderItem();
            item.id = itemResult.getInt(0);
            item.product = itemResult.getString(2);
            item.price = itemResult.getDouble(3);
            item.orderCode = itemResult.getString(1);
            item.quantity = itemResult.getInt(4);
            item.flag = itemResult.getInt(5);
            item.image=itemResult.getString(6);

            ArrayList<OrderItem> itemList = map.get(item.orderCode);
            if (itemList == null) {
                itemList = new ArrayList<OrderItem>();
                map.put(item.orderCode, itemList);
            }
            itemList.add(item);

            itemResult.moveToNext();
        }

        sb.delete(0, sb.length() - 1);
        sb.append(
                "select orders.id,orders.code,orders.ordertime,customers.name")
                .append(",orders.total,orders.salesman,customers.address,customers.phone")
                .append(" from orders, customers where orders.customer= customers.id")
                .append(" order by orders.ordertime asc");
        Cursor result = db.rawQuery(sb.toString(), null);

        result.moveToFirst();
        while (!result.isAfterLast()) {

            Order order = new Order();
            order.id = result.getInt(0);
            order.code = result.getString(1);
            order.customer = result.getString(3);
            order.total = result.getDouble(4);
            order.address = result.getString(6);
            order.customerphone = result.getString(7);
            order.salesman = result.getString(5);
            try {
                order.orderTime = format.parse(result.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            order.items = map.get(order.code);
            list.add(order);
            result.moveToNext();
        }

        itemResult.close();
        result.close();
        return list;
    }

    public Order getOrderHead(String orderid) {
        Order order = new Order();
        StringBuffer sb = new StringBuffer();
        sb.append("select id,code,ordertime,customer,total,salesman,address,customerphone ");
        sb.append(" from orders where id='").append(orderid).append("'");
        Cursor result = db.rawQuery(sb.toString(), null);

        result.moveToFirst();
        while (!result.isAfterLast()) {
            order.id = result.getInt(0);
            order.code = result.getString(1);
            order.customer = result.getString(3);
            order.total = result.getDouble(4);
            order.address = result.getString(6);
            order.customerphone = result.getString(7);
            order.salesman = result.getString(5);
            try {
                order.orderTime = format.parse(result.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            result.moveToNext();
        }
        return order;
    }

    public boolean deleteOrders(ArrayList<Order> list) {
        StringBuffer sqlcode = new StringBuffer();
        for (Order order : list) {
            sqlcode.append("'").append(order.code).append("',");
        }
        if (sqlcode.charAt(sqlcode.length() - 1) == ',') {
            sqlcode.delete(sqlcode.length() - 1, sqlcode.length());
        }

        StringBuffer sql = new StringBuffer(
                "delete from orderitem where ordercode in (");
        sql.append(sqlcode).append(")");
        db.beginTransaction();
        db.execSQL(sql.toString());
        sql.delete(0, sql.length());

        sql.append("delete from orders where code in (").append(sqlcode)
                .append(")");
        db.execSQL(sql.toString());
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }

    public boolean saveOrder(Order order) {
        BigDecimal price;
        BigDecimal qty;
        BigDecimal total = new BigDecimal(0);

        db.beginTransaction();
        StringBuffer sql = new StringBuffer();
        String code = null;
        sql.append("select code,total from orders where customer='")
                .append(order.customer).append("'");
        Cursor rs = db.rawQuery(sql.toString(), null);
        rs.moveToFirst();
        if (!rs.isAfterLast()) {
            code = rs.getString(0);
            total = new BigDecimal(rs.getDouble(1));
            rs.moveToNext();
        }
        if (code != null) {
            order.code = code;
        } else {
            content.clear();
            content.put("code", order.code);
            content.put("ordertime", format.format(order.orderTime));
            content.put("customer", order.customer);
            content.put("total", order.total);
            content.put("salesman", order.salesman);
            content.put("address", order.address);
            content.put("customerphone", order.customerphone);
            db.insert("orders", null, content);
        }
        for (OrderItem item : order.items) {
            content.clear();
            content.put("ordercode", order.code);
            content.put("product", item.product);
            content.put("price", item.price);
            content.put("quantity", item.quantity);
            content.put("flag", item.flag);
            db.insert("orderitem", null, content);
            System.out.println(content.toString());
            // order.total += (item.price * item.quantity);
            price = new BigDecimal(item.price);
            qty = new BigDecimal(item.quantity);
            total = total.add(price.multiply(qty).setScale(2,
                    BigDecimal.ROUND_HALF_EVEN));
            order.total = total.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        }

        // sql.delete(0, sql.length());
        // sql.append("select score from customers where name='");
        // sql.append(order.customer).append("'");
        // rs = db.rawQuery(sql.toString(), null);
        // if (rs.getCount() == 0) {
        // content.clear();
        // content.put("name", order.customer);
        // content.put("phone", order.customerphone);
        // content.put("address", order.address);
        // // content.put("score", order.total * scoreRate);
        // BigDecimal score = total.multiply(scoreRate);
        // content.put("score", score.setScale(3, BigDecimal.ROUND_HALF_UP)
        // .toString());
        // System.out.println(score.toString());
        // System.out.println(score.setScale(3, BigDecimal.ROUND_HALF_UP)
        // .toString());
        // db.insert("customers", null, content);
        // } else {
        // rs.moveToFirst();
        // BigDecimal score = new BigDecimal(rs.getDouble(0));
        // content.clear();
        // content.put(
        // "score",
        // score.add(total.multiply(scoreRate))
        // .setScale(3, BigDecimal.ROUND_HALF_UP).toString());
        // System.out.println(score.toString());
        // System.out.println(score.setScale(3, BigDecimal.ROUND_HALF_UP)
        // .toString());
        // db.update("customers", content, "name=?",
        // new String[] { order.customer });
        // }
        // rs.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public boolean updateOrders(Order order) {
        BigDecimal price;
        BigDecimal qty;
        BigDecimal total = new BigDecimal(0);

        db.beginTransaction();

        StringBuffer sql = new StringBuffer();
        String code = null;
        sql.append("select code,total from orders where customer='")
                .append(order.customer).append("'");
        Cursor rs = db.rawQuery(sql.toString(), null);
        rs.moveToFirst();
        if (!rs.isAfterLast()) {
            code = rs.getString(0);
            total = new BigDecimal(rs.getDouble(1));
            rs.moveToNext();
        }
        if (code != null) {
            order.code = code;
        } else {
            content.clear();
            content.put("code", order.code);
            content.put("ordertime", format.format(order.orderTime));
            content.put("customer", order.customer);
            content.put("total", order.total);
            content.put("salesman", order.salesman);
            content.put("address", order.address);
            content.put("customerphone", order.customerphone);
            db.insert("orders", null, content);
        }
        for (OrderItem item : order.items) {
            sql.delete(0, sql.length());

            sql.append("select id from orderitem where ordercode='")
                    .append(order.code).append("' and product='")
                    .append(item.product).append("'");
            rs = db.rawQuery(sql.toString(), null);
            if (rs.getCount() > 0) {
                rs.moveToFirst();
                content.clear();
                content.put("ordercode", order.code);
                content.put("product", item.product);
                content.put("price", item.price);
                content.put("quantity", item.quantity);
                content.put("flag", item.flag);
                db.update("orderitem", content, "id=?",
                        new String[] { rs.getString(0) });
            } else {
                content.clear();
                content.put("ordercode", order.code);
                content.put("product", item.product);
                content.put("price", item.price);
                content.put("quantity", item.quantity);
                content.put("flag", item.flag);
                db.insert("orderitem", null, content);
            }
            rs.close();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public ArrayList<Customer> getCustomers(String orderField) {
        ArrayList<Customer> list = new ArrayList<Customer>();
        StringBuffer sb = new StringBuffer();

        sb.append("select id,name,phone,address,score from customers ");
        if (orderField != null) {
            sb.append(" order by ").append(orderField).append(" desc ");
        }
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {

            Customer customer = new Customer();
            customer.id = itemResult.getInt(0);
            customer.name = itemResult.getString(1);
            customer.phone = itemResult.getString(2);
            customer.address = itemResult.getString(3);
            customer.score = itemResult.getDouble(4);

            list.add(customer);
            itemResult.moveToNext();
        }
        itemResult.close();
        return list;
    }

    public boolean saveCustomer(Customer customer) {
        content.clear();
        content.put("name", customer.name);
        content.put("phone", customer.phone);
        content.put("address", customer.address);
        content.put("score", customer.score);

        if (customer.id == 0) {

            db.insert("customers", null, content);
        } else {
            db.update("customers", content, "id=?",
                    new String[] { String.valueOf(customer.id) });
        }
        StringBuffer sql = new StringBuffer(
                "select id from customers where name='");
        sql.append(customer.name).append("'");

        Cursor rs = db.rawQuery(sql.toString(), null);
        if (rs.getCount() > 0) {
            rs.moveToFirst();
            customer.id = rs.getInt(0);
        }
        return true;
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> list = new ArrayList<Product>();
        StringBuffer sb = new StringBuffer();

        sb.append("select id,product,price,memo,image,purchasePrice from products order by image ");
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {

            Product product = new Product();
            product.id = itemResult.getInt(0);
            product.product = itemResult.getString(1);
            product.price = itemResult.getDouble(2);
            product.memo = itemResult.getString(3);
            product.image = itemResult.getString(4);
            product.purchasePrice=itemResult.getDouble(5);
            list.add(product);
            itemResult.moveToNext();
        }
        itemResult.close();
        return list;
    }

    public Product getProduct(String product) {
        Product p = null;
        StringBuffer sb = new StringBuffer();

        sb.append(
                "select id,product,price,memo,image,purchasePrice from products where product='")
                .append(product).append("'");
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {

            p = new Product();
            p.id = itemResult.getInt(0);
            p.product = itemResult.getString(1);
            p.price = itemResult.getDouble(2);
            p.memo = itemResult.getString(3);
            p.image = itemResult.getString(4);
            p.purchasePrice=itemResult.getDouble(5);
            itemResult.moveToNext();
        }
        itemResult.close();
        return p;
    }

    public String getProductByImage(String image) {
        String p = null;
        StringBuffer sb = new StringBuffer();

        sb.append(
                "select product from products where image='")
                .append(image).append("'");
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {


            p = itemResult.getString(0);

            itemResult.moveToNext();
        }
        itemResult.close();
        return p;
    }

    public ArrayList<OrderItem> genOrderItemList() {
        ArrayList<OrderItem> list = new ArrayList<OrderItem>();
        StringBuffer sb = new StringBuffer();

        sb.append("select id,product,price,image,purchasePrice from products order by image ");
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {
            OrderItem product = new OrderItem();
            product.id = itemResult.getInt(0);
            product.product = itemResult.getString(1);
            product.price = itemResult.getDouble(2);
            product.image = itemResult.getString(3);
            list.add(product);
            itemResult.moveToNext();
        }
        itemResult.close();
        return list;
    }

    public boolean saveProduct(Product product) {
        content.clear();
        content.put("product", product.product);
        content.put("price", product.price);
        content.put("image", product.image);
        content.put("memo", product.memo);
        content.put("purchasePrice",product.purchasePrice);
        if (product.id == 0) {
            db.insert("products", null, content);
        } else {
            db.update("products", content, "id=?",
                    new String[] { String.valueOf(product.id) });
        }
        return true;
    }

    public boolean saveOrderItem(OrderItem item) {
        content.clear();
        content.put("quantity", item.quantity);
        db.update("orderitem", content, "id=?",
                new String[] { String.valueOf(item.id) });
        return true;
    }

    // public String newOrderCode(Date orderTime) {
    // int count = 0;
    // StringBuffer sb = new StringBuffer();
    // SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
    // sb.append("select count(id) from orders where ordertime like '")
    // .append(f.format(orderTime)).append("%'");
    // Cursor itemResult = db.rawQuery(sb.toString(), null);
    // itemResult.moveToFirst();
    // while (!itemResult.isAfterLast()) {
    // count = itemResult.getInt(0);
    // itemResult.moveToNext();
    // }
    // itemResult.close();
    // if (count == 0)
    // count = 1;
    // return f.format(orderTime)
    // + fillString(new StringBuffer(String.valueOf(count)));
    // }

    private String fillString(StringBuffer sb) {
        sb.insert(0, '0');
        if (sb.length() < 3) {
            return fillString(sb);
        }
        return sb.toString();
    }

    public String[] getCustomerNames() {
        ArrayList<String> list = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();

        sb.append("select name from customers ");
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {

            list.add(itemResult.getString(0));
            itemResult.moveToNext();
        }
        itemResult.close();
        return list.toArray(new String[list.size()]);
    }

    public Customer getCustomer(String name) {
        Customer c = null;
        StringBuffer sb = new StringBuffer();

        sb.append(
                "select id,name,phone,address,score from customers where name='")
                .append(name).append("'");
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {
            c = new Customer();
            c.id = itemResult.getInt(0);
            c.name = itemResult.getString(1);
            c.phone = itemResult.getString(2);
            c.address = itemResult.getString(3);
            c.score = itemResult.getDouble(4);
            itemResult.moveToNext();
        }
        itemResult.close();
        return c;
    }

    public void deleteCustomer(String customer) {
        db.delete("customers", "name=?", new String[] { customer });
    }

    public void deleteProduct(String product) {
        db.delete("products", "product=?", new String[] { product });
    }

    public void deleteOrderItem(String id) {
        db.delete("orderitem", "id=?", new String[] { id });
    }

    public void deleteOrder(String string) {
        StringBuffer sb = new StringBuffer("select code from orders where id='");
        sb.append(string).append("'");
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        String code = "";
        while (!itemResult.isAfterLast()) {
            code = itemResult.getString(0);
            itemResult.moveToNext();
        }
        itemResult.close();

        db.delete("orderitem", "ordercode=?", new String[] { code });
        db.delete("orders", "code=?", new String[] { code });
    }

    public ArrayList<OrderItem> genOrderItemListForModify(String orderid) {
        ArrayList<OrderItem> list = new ArrayList<OrderItem>();
        StringBuffer sb = new StringBuffer();

        sb.append("select id,ordercode,product,price,quantity,flag from orderitem ");
        sb.append(" where ordercode=(select code from orders where id='")
                .append(orderid).append("')");
        ArrayList<OrderItem> modifyList = new ArrayList<OrderItem>();
        Cursor itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {

            OrderItem item = new OrderItem();
            item.id = itemResult.getInt(0);
            item.product = itemResult.getString(2);
            item.price = itemResult.getDouble(3);
            item.orderCode = itemResult.getString(1);
            item.quantity = itemResult.getInt(4);
            item.flag = itemResult.getInt(5);

            modifyList.add(item);
            itemResult.moveToNext();
        }
        itemResult.close();

        sb.delete(0, sb.length());
        sb.append("select id,product,price,image from products  order by image  ");
        itemResult = db.rawQuery(sb.toString(), null);
        itemResult.moveToFirst();
        while (!itemResult.isAfterLast()) {
            OrderItem product = null;
            for (OrderItem item : modifyList) {
                if (item.product.equals(itemResult.getString(1))) {
                    product = item;
                    modifyList.remove(item);
                    break;
                }
            }
            if (product == null) {
                product = new OrderItem();
                product.id = itemResult.getInt(0);
                product.product = itemResult.getString(1);
                product.price = itemResult.getDouble(2);
                product.image = itemResult.getString(3);
            }
            list.add(product);
            itemResult.moveToNext();
        }
        itemResult.close();
        return list;
    }
}
