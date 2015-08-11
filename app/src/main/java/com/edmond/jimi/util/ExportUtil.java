package com.edmond.jimi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import android.widget.Toast;

import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.entity.Order;
import com.edmond.jimi.entity.OrderItem;

public class ExportUtil {
    private static ArrayList<Order> orderList;
    private static TreeMap<String, TreeMap<String, Double>> groupMap = new TreeMap<String, TreeMap<String, Double>>();
    private static String salesman;
    private static String salesmanPhone;
    private static String devPhone;
    private static String company;

    public static boolean exportExcel(KangmeiApplication app) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        company= PrefrenceTool.getStringValue("kangmei", "apptitle", app);
        salesman = PrefrenceTool.getStringValue("kangmei", "salesman", app);
        salesmanPhone = PrefrenceTool.getStringValue("kangmei", "phone", app);
        devPhone = PrefrenceTool
                .getStringValue("kangmei", "deliveryphone", app);
        groupMap = new TreeMap<String, TreeMap<String, Double>>();
        orderList = DBHelper.getInstance(app).getOrders();
        if (orderList != null && orderList.size() > 0) {
            HSSFWorkbook wb = new HSSFWorkbook();
            CreationHelper createHelper = wb.getCreationHelper();
            deliveryBill(wb, createHelper);
            detailBill(wb, createHelper);
            statisticalBill(wb, createHelper, app);

            try {
                File xlsfile = new File(app.excelpath
                        + format.format(new Date(System.currentTimeMillis()))
                        + ".xls");
                if (!xlsfile.exists()) {
                    xlsfile.createNewFile();
                }
                FileOutputStream fileOut = new FileOutputStream(xlsfile, true);
                wb.write(fileOut);
                fileOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            DBHelper.getInstance(app).deleteOrders(orderList);
            Toast.makeText(app, "数据导出成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(app, "没有数据需要导出", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public static void deliveryBill(HSSFWorkbook wb, CreationHelper createHelper) {
        BigDecimal price;
        BigDecimal qty;
        BigDecimal total;

        HSSFSheet sheet = wb.createSheet("送货单");

        HSSFDataFormat format = wb.createDataFormat();

        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 16);

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerCellStyle.setFont(font);

        CellStyle currencyStyle = wb.createCellStyle();
        currencyStyle.setDataFormat(format.getFormat("#,##0.00"));
        currencyStyle.setBorderBottom(CellStyle.BORDER_THIN);
        currencyStyle.setBorderLeft(CellStyle.BORDER_THIN);
        currencyStyle.setBorderRight(CellStyle.BORDER_THIN);
        currencyStyle.setBorderTop(CellStyle.BORDER_THIN);
        currencyStyle.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setBorderBottom(CellStyle.BORDER_THIN);
        borderStyle.setBorderLeft(CellStyle.BORDER_THIN);
        borderStyle.setBorderRight(CellStyle.BORDER_THIN);
        borderStyle.setBorderTop(CellStyle.BORDER_THIN);
        borderStyle.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle borderLeftStyle = wb.createCellStyle();
        borderLeftStyle.setAlignment(CellStyle.ALIGN_LEFT);

        CellStyle totalStyle = wb.createCellStyle();
        totalStyle.setBorderBottom(CellStyle.BORDER_THIN);
        totalStyle.setBorderLeft(CellStyle.BORDER_THIN);
        totalStyle.setBorderRight(CellStyle.BORDER_THIN);
        totalStyle.setBorderTop(CellStyle.BORDER_DOUBLE);

        int row_no = 0;
        for (Order order : orderList) {
            if (order == null) {
                continue;
            }
            row_no++;
            total = new BigDecimal(0);
            HSSFRow row = sheet.createRow((short) row_no);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(company+"送货单");
            cell.setCellStyle(headerCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(row_no, row_no, 0, 4));
            row.setHeightInPoints(35);

            row_no++;
            row = sheet.createRow((short) row_no);
            cell = row.createCell(0);
            cell.setCellValue("客户名称：" + order.customer);
            cell.setCellStyle(borderLeftStyle);
            row.createCell(1).setCellStyle(borderLeftStyle);
            row.createCell(2).setCellStyle(borderLeftStyle);
            sheet.addMergedRegion(new CellRangeAddress(row_no, row_no, 0, 2));
            cell = row.createCell(3);
            cell.setCellValue("订单号：");
            cell.setCellStyle(borderLeftStyle);
            cell = row.createCell(4);
            cell.setCellValue(order.code);
            cell.setCellStyle(borderLeftStyle);

            row_no++;
            row = sheet.createRow((short) row_no);
            cell = row.createCell(0);
            cell.setCellValue("地址：" + order.address);
            cell.setCellStyle(borderLeftStyle);
            cell = row.createCell(1);
            sheet.addMergedRegion(new CellRangeAddress(row_no, row_no, 0, 2));
            cell.setCellStyle(borderLeftStyle);
            row.createCell(2).setCellStyle(borderLeftStyle);
            cell.setCellStyle(borderLeftStyle);
            cell = row.createCell(3);
            cell.setCellValue("电话：");
            cell.setCellStyle(borderLeftStyle);
            cell = row.createCell(4);
            cell.setCellValue(order.customerphone == null ? ""
                    : order.customerphone);
            cell.setCellStyle(borderLeftStyle);

            row_no++;
            row = sheet.createRow((short) row_no);
            cell = row.createCell(0);
            cell.setCellValue("货品名称");
            cell.setCellStyle(borderStyle);
            cell = row.createCell(1);
            cell.setCellValue("数量");
            cell.setCellStyle(borderStyle);
            cell = row.createCell(2);
            cell.setCellValue("单价");
            cell.setCellStyle(borderStyle);
            cell = row.createCell(3);
            cell.setCellValue("金额");
            cell.setCellStyle(borderStyle);
            cell = row.createCell(4);
            cell.setCellValue("备注");
            cell.setCellStyle(borderStyle);

            for (OrderItem item : order.items) {
                price = new BigDecimal(item.price);
                qty = new BigDecimal(item.quantity);
                row_no++;
                row = sheet.createRow((short) row_no);
                cell = row.createCell(0);
                cell.setCellValue(item.product);
                cell.setCellStyle(borderStyle);

                if (item.flag == 1 || item.flag == 3 || item.flag == 4) {
                    cell = row.createCell(4);
                    if (item.flag == 1) {
                        cell.setCellValue("换货");
                    } else if (item.flag == 3) {
                        cell.setCellValue("赠送");
                    } else if (item.flag == 4) {
                        cell.setCellValue("补货");
                    }
                    cell = row.createCell(1);
                    cell.setCellValue(item.quantity);
                    cell.setCellStyle(borderStyle);
                } else if (item.flag == 2) {
                    cell = row.createCell(4);
                    cell.setCellValue("退货");
                    cell.setCellStyle(borderStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(item.quantity * -1);
                    cell.setCellStyle(borderStyle);
                    cell = row.createCell(2);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(item.price);
                    cell = row.createCell(3);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(price.multiply(qty).doubleValue() * -1);

                } else {
                    cell = row.createCell(1);
                    cell.setCellValue(item.quantity);
                    cell.setCellStyle(borderStyle);
                    cell = row.createCell(2);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(item.price);
                    cell = row.createCell(3);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(price.multiply(qty).doubleValue());
//                    cell.setCellFormula("B"+(row_no+1)+"*C"+(row_no+1));
                    row.createCell(4).setCellStyle(borderStyle);
                }
                if (item.flag == 2) {
                    total = total.subtract(price.multiply(qty));
                } else if (item.flag == 1||item.flag==3||item.flag==4) {

                } else {
                    total = total.add(price.multiply(qty));
                }
            }

            row_no++;
            row = sheet.createRow((short) row_no);

            cell = row.createCell(0);
            cell.setCellValue("合计");
            cell.setCellStyle(borderStyle);
            cell = row.createCell(3);
            currencyStyle.setDataFormat(format.getFormat("￥#,##0.00"));
            cell.setCellValue(total.doubleValue());
            cell.setCellStyle(currencyStyle);
            row.createCell(1).setCellStyle(borderStyle);
            row.createCell(2).setCellStyle(borderStyle);
            sheet.addMergedRegion(new CellRangeAddress(row_no, row_no, 0, 2));
            row.createCell(4).setCellStyle(borderStyle);

            row_no++;
            row = sheet.createRow((short) row_no);
            cell = row.createCell(0);
            cell.setCellValue("业务员电话：" + salesmanPhone + " （" + salesman
                    + ")                         送货电话：" + devPhone);
            sheet.addMergedRegion(new CellRangeAddress(row_no, row_no, 0, 4));

            row_no++;
        }
        sheet.setColumnWidth(0, 17 * 512);
        sheet.setColumnWidth(3, 8 * 512);
        sheet.setColumnWidth(4, 9 * 512);
    }

    public static void detailBill(HSSFWorkbook wb, CreationHelper createHelper) {
        TreeMap<String, Double> flagProductMap = null;
        HSSFSheet sheet = wb.createSheet("订货详单");

        CellStyle dataCellStyle = wb.createCellStyle();
        dataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
                "yyyy年MM月dd日"));

        CellStyle currencyStyle = wb.createCellStyle();
        currencyStyle.setDataFormat((short) 5);
        currencyStyle
                .setDataFormat(wb.createDataFormat().getFormat("#,###.00"));
        currencyStyle.setBorderBottom(CellStyle.BORDER_THIN);
        currencyStyle.setBorderLeft(CellStyle.BORDER_THIN);
        currencyStyle.setBorderRight(CellStyle.BORDER_THIN);
        currencyStyle.setBorderTop(CellStyle.BORDER_THIN);

        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setBorderBottom(CellStyle.BORDER_THIN);
        borderStyle.setBorderLeft(CellStyle.BORDER_THIN);
        borderStyle.setBorderRight(CellStyle.BORDER_THIN);
        borderStyle.setBorderTop(CellStyle.BORDER_THIN);

        HSSFCell cell = null;
        // Create a row and put some cells in it. Rows are 0 based.
        HSSFRow row = sheet.createRow((short) 0);
        cell = row.createCell(0);
        cell.setCellValue("日期");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(1);
        cell.setCellValue("店名");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(2);
        cell.setCellValue("货品名称");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(3);
        cell.setCellValue("数量");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(4);
        cell.setCellValue("单价");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(5);
        cell.setCellValue("金额");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(6);
        cell.setCellValue("备注");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(7);
        cell.setCellValue("地址");
        cell.setCellStyle(borderStyle);

        int row_no = 1;
        for (Order order : orderList) {
            for (OrderItem item : order.items) {
                row = sheet.createRow((short) row_no);
                cell = row.createCell(0);
                cell.setCellValue(order.orderTime);
                cell.setCellStyle(dataCellStyle);
                cell = row.createCell(1);
                cell.setCellValue(order.customer);
                cell.setCellStyle(borderStyle);
                cell = row.createCell(2);
                cell.setCellValue(item.product);
                cell.setCellStyle(borderStyle);
                if (item.flag == 1||item.flag==3||item.flag==4) {
                    cell = row.createCell(6);
                    if (item.flag == 1) {
                        cell.setCellValue("换货");
                    } else if (item.flag == 3) {
                        cell.setCellValue("赠送");
                    } else if (item.flag == 4) {
                        cell.setCellValue("补货");
                    }
                    cell.setCellStyle(borderStyle);
                    cell = row.createCell(3);
                    cell.setCellValue(item.quantity);
                    cell.setCellStyle(borderStyle);
                } else if (item.flag == 2) {
                    cell = row.createCell(6);
                    cell.setCellValue("退货");
                    cell.setCellStyle(borderStyle);
                    cell = row.createCell(3);
                    cell.setCellValue(item.quantity * -1);
                    cell.setCellStyle(borderStyle);

                    cell = row.createCell(4);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(item.price);
                    cell = row.createCell(5);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(item.price * item.quantity * -1);
                } else {
                    cell = row.createCell(3);
                    cell.setCellValue(item.quantity);
                    cell.setCellStyle(borderStyle);
                    cell = row.createCell(4);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(item.price);
                    cell = row.createCell(5);
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(item.price * item.quantity);
                    row.createCell(6).setCellStyle(borderStyle);
                }
                cell = row.createCell(7);
                cell.setCellValue(order.address);
                cell.setCellStyle(borderStyle);

                flagProductMap = groupMap.get(item.image);
                if (flagProductMap == null) {
                    flagProductMap = new TreeMap<String, Double>();
                    groupMap.put(item.image, flagProductMap);
                }

                Double qty = null;
                if (item.flag == 2) {

                } else {
                    qty = flagProductMap.get("0");
                }
                if (qty == null) {
                    qty = new Double(0);
                }
                qty = new Double(qty.doubleValue() + item.quantity);
                flagProductMap.put(item.flag == 2 ? "2" : "0", qty);

                row_no++;
            }
        }
        sheet.setColumnWidth(0, 9 * 512);
    }

    public static void statisticalBill(Workbook wb,
                                       CreationHelper createHelper, KangmeiApplication app) {
        Sheet sheet = wb.createSheet("订货统计");
        TreeMap<String, Double> flagProductMap = null;

        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setBorderBottom(CellStyle.BORDER_THIN);
        borderStyle.setBorderLeft(CellStyle.BORDER_THIN);
        borderStyle.setBorderRight(CellStyle.BORDER_THIN);
        borderStyle.setBorderTop(CellStyle.BORDER_THIN);
        borderStyle.setAlignment(CellStyle.ALIGN_CENTER);

        Cell cell;
        Row row = sheet.createRow((short) 0);
        cell = row.createCell(0);
        cell.setCellValue("货品名称");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(1);
        cell.setCellValue("数量");
        cell.setCellStyle(borderStyle);
        cell = row.createCell(2);
        cell.setCellValue("备注");
        cell.setCellStyle(borderStyle);

        int row_no = 1;
        Iterator<String> ite = groupMap.keySet().iterator();
        while (ite.hasNext()) {
            String image = ite.next();
            flagProductMap = groupMap.get(image);

            Iterator<String> flagIte = flagProductMap.keySet().iterator();
            while (flagIte.hasNext()) {
                String flag = flagIte.next();
                Double qty = flagProductMap.get(flag);
                if (!flag.equals("2")) {
                    row = sheet.createRow((short) row_no);
                    cell = row.createCell(0);
                    cell.setCellValue(DBHelper.getInstance(app)
                            .getProductByImage(image));
                    cell.setCellStyle(borderStyle);
                    if (flag.equals("1")||flag.equals("3")||flag.equals("4")) {
                        cell = row.createCell(2);
                        if (flag.equals("1")) {
                            cell.setCellValue("换货");
                        } else if (flag.equals("3")) {
                            cell.setCellValue("赠送");
                        } else if (flag.equals("4")) {
                            cell.setCellValue("补货");
                        }
                        cell.setCellStyle(borderStyle);
                        cell = row.createCell(1);
                        cell.setCellValue(qty);
                        cell.setCellStyle(borderStyle);
                    } else {
                        cell = row.createCell(1);
                        cell.setCellValue(qty);
                        cell.setCellStyle(borderStyle);
                        row.createCell(2).setCellStyle(borderStyle);
                    }
                    row_no++;
                }
            }

        }
        sheet.setColumnWidth(0, 15 * 512);
    }

}
