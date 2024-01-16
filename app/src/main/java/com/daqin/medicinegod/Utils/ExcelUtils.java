package com.daqin.medicinegod.Utils;


import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jxl.Cell;
import jxl.Image;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


/**
 * Excel导出工具
 */
public class ExcelUtils {

    public static WritableFont asongti12font_bold = null;
    public static WritableFont asongti12font_normal = null;
    public static WritableFont asongti12font_red = null;
    public static WritableFont asongti12font_black = null;
    public static WritableFont asongti12font_red_italic = null;
    public static WritableCellFormat songti12format_bold = null;
    public static WritableCellFormat songti12format_normal = null;
    public static WritableCellFormat songti12format_red = null;
    public static WritableCellFormat songti12format_black = null;
    public static WritableCellFormat songti12format_italic = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";


    public static void format() throws WriteException {
        asongti12font_bold = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
        asongti12font_normal = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
        asongti12font_red = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
        asongti12font_black = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        asongti12font_red_italic = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, true, UnderlineStyle.NO_UNDERLINE, Colour.RED);
        songti12format_bold = new WritableCellFormat(asongti12font_bold);
        songti12format_normal = new WritableCellFormat(asongti12font_normal);
        songti12format_red = new WritableCellFormat(asongti12font_red);
        songti12format_black = new WritableCellFormat(asongti12font_black);
        songti12format_italic = new WritableCellFormat(asongti12font_red_italic);

        songti12format_bold.setAlignment(jxl.format.Alignment.CENTRE);//设置居中
        songti12format_normal.setAlignment(jxl.format.Alignment.CENTRE);//设置居中
        songti12format_italic.setAlignment(jxl.format.Alignment.CENTRE);//设置居中
        songti12format_red.setAlignment(Alignment.LEFT);//设置居左
        songti12format_bold.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//垂直居中
        songti12format_normal.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//垂直居中
        songti12format_red.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//垂直居中
        songti12format_italic.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//垂直居中

        songti12format_bold.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        songti12format_bold.setBackground(Colour.GREY_50_PERCENT);
        songti12format_normal.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        songti12format_italic.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);


    }


    /**
     * 返回根路径
     *
     * @return filePath  系统根路径 /storage/emulated/0/
     */
    public static String getRootPath() {
        return "/storage/emulated/0/";
    }

    /**
     * 初始化表格，包括文件名、sheet名、各列的名字
     *
     * @param filePath  文件路径
     * @param sheetName sheet名
     * @param colName   各列的名字
     */
    public static void initExcel(Context context, String filePath, String sheetName, String[] colName) throws IOException, WriteException {
        format();
        WritableWorkbook workbook = null;

        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet(sheetName, 0);

        sheet.addCell((WritableCell) new Label(0, 0, filePath, songti12format_bold));
        sheet.addCell(new Label(0, 0, "注意事项：1.示例请自行删除，行与行之前不要留空白，否则导入失败！2.图片请输入无（因为技术原因无法识别到图片位置，请后续自行导入）。3.除ID与图片外不允许其他项目留空（留空请填无，否则导入失败）。4.请严格按照格式填入导入，请勿留有空行。", songti12format_red));

        for (int col = 0; col < colName.length; col++) {
            sheet.addCell(new Label(col, 1, colName[col], songti12format_bold));
            switch (col) {
                case 1:
                    WritableImage writableImage = new WritableImage(col, 2, 1, 1, ((byte[]) Utils.getBytesFromBitmap(Utils.getBitmapFromResourse(context, R.mipmap.add_imgdefault))));
                    sheet.addImage(writableImage);
                    sheet.addCell(new Label(col, 2, "图片请填无，后续自行导入", songti12format_italic));
                    break;
                case 3:
                    sheet.addCell(new Label(col, 2, "如：" + Utils.getDate(), songti12format_italic));//过期时间 格式：XXXX-XX-XX
                    break;
                case 4:
                    sheet.addCell(new Label(col, 2, "如：非处方药-绿(红)/处方药/无", songti12format_italic));
                    break;
                case 6:
                    sheet.addCell(new Label(col, 2, "条码，必须为13位条码", songti12format_italic));//条码
                case 7:
                    sheet.addCell(new Label(col, 2, "余量，请保证必为数字", songti12format_italic));
                case 8:
                    sheet.addCell(new Label(col, 2, "药效标签(如：感冒@@发烧)，多个请用@@分隔", songti12format_italic));
                case 9:
                    sheet.addCell(new Label(col, 2, "如：1-包-3-次-1天 （代表一天三次一次一包，请自行修改单位）", songti12format_italic));
                case 10:
                    sheet.addCell(new Label(col, 2, "如：家/学校等（请自行修改，填无代表默认）", songti12format_italic));
                default:
                    sheet.addCell(new Label(col, 2, colName[col] + "示例", songti12format_italic));//默认
                    break;
            }
        }


        setColumnAutoSize(sheet);
        workbook.write();
        workbook.close();

    }

    private static void setColumnAutoSize(WritableSheet ws) {
        // 获取本列的最宽单元格的宽度
        for (int i = 0; i < ws.getColumns(); i++) {
            int colWith = 0;
            for (int j = 1; j < ws.getRows(); j++) {
                String content = ws.getCell(i, j).getContents().toString();
                // int cellWith = content.length();
                byte[] bstrLength = content.getBytes();
                int cellWith = bstrLength.length;
                if (colWith < cellWith) {
                    colWith = cellWith;
                }
            }
            // 设置单元格的宽度为最宽宽度+额外宽度
            ws.setColumnView(i, colWith + 20);
            ws.setColumnView(1, 25);
        }
    }

    /**
     * 将药品数据写入Excel表格
     *
     * @param medicines 药品数据
     * @param filePath  写出文件名（*.xls）
     * @param colName   列名
     * @return 导出成功
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int exportMedicineToExcel(List<Map<String, Object>> medicines, String filePath, String[] colName, String[] colRigin) throws WriteException, IOException {

        if (medicines != null && medicines.size() > 0) {
            int count = 0;
            File file = new File(filePath);
            String sheetName = "药神导出数据" + Utils.getTime();
            WritableWorkbook workbook = null;

            format();
            WorkbookSettings setEncode = new WorkbookSettings();
            setEncode.setEncoding(UTF8_ENCODING);
            setEncode.setWriteAccess(null);
            setEncode.setUseTemporaryFileDuringWrite(true);
            setEncode.setTemporaryFileDuringWriteDirectory(new File(getRootPath()));//临时文件夹的位置

            workbook = Workbook.createWorkbook(file);
            // 创建sheet页:name属性为sheet页的名称，index为第几页
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], songti12format_bold));
            }
            for (int j = 0; j < medicines.size(); j++) {
                Map<String, Object> one = medicines.get(j);
                //禁止导出网络药品
                if ((Objects.requireNonNull(one.get(Constant.COLUMN_M_FROMWEB)).toString().contains("0"))) {

                    for (int i = 0; i < colName.length; i++) {

//                        String img = Utils.getBase64FromImg((byte[]) one.get(Constant.COLUMN_M_IMAGE));
                        Label cell = new Label(
                                i,
                                j,
                                (i == 1 ? "图片显示的位置(比例无影响)" :
                                        (i == 3 ? Utils.getStringFromDate(Long.parseLong(Objects.requireNonNull(one.get(colRigin[i])).toString()))
                                                : String.valueOf(one.get(colRigin[i])))),
                                songti12format_black);
//                        Label cell = new Label(i, k, (i == 2 ? img : String.valueOf(one.get(colRigin[i]))), songti12format_black);
                        sheet.addCell(cell);
                    }
                    WritableImage writableImage = new WritableImage(1, j, 1, 1, ((byte[]) one.get(Constant.COLUMN_M_IMAGE)));
                    sheet.addImage(writableImage);
                    count++;

                }

            }

//                writebook.write();
            //写入数据
            setColumnAutoSize(sheet);
            sheet.setColumnView(2, 30);
            sheet.setColumnView(7, 30);

            workbook.write();
            //一定要关闭，不然不会写入文件内。
            workbook.close();

            return count;
        } else {
            return -1;
        }
    }


    public static String[] getSheetNames(String filePath) {
        File file = new File(filePath);
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            return workbook.getSheetNames();
        } catch (IOException | BiffException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取excel readExcelAll
     *
     * @param filePath    文件路径
     * @param sheetName   文件簿
     * @param columnNames 列名
     * @return 集合
     * @throws Exception 文件错误
     */
    public static List<Map<String, Object>> readExcelAll(String filePath, String sheetName, String[] columnNames) throws Exception {

        File file = new File(filePath);
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            //3. 获取指定的sheet页码   通过指定的Sheet页的名字获取指定的Sheet页，也可以通过索引获取Sheet
            Sheet sheet = workbook.getSheet(sheetName);//Sheet sheet = workbook.getSheet("表格1");
            //4.循环获取每行每列的单元格内容


            for (int i = 2; i < sheet.getRows(); i++) {//行
                Map<String, Object> map = new HashMap<>();
                int nullCOunt = 0;

//                + sheet.getDrawing(i).getImageData().length
//                System.out.println("获取了" + sheet.getDrawing(i).getRow() + "-" );

                for (int j = 0; j < sheet.getColumns(); j++) {//列
//                        Cell cell = sheet.getCell(j, i);//这里注意，是(j,i)
                    String contents = sheet.getCell(j, i).getContents().trim();

                    if (!contents.equals("")) {
                        map.put(columnNames[j], contents);
                    } else {
                        nullCOunt++;
                    }
                }


                map.put("isSuccess", 1);
                if (nullCOunt != columnNames.length) {
                    res.add(map);
                } else break;


            }
            workbook.close();    //将工作簿的资源关闭

            return res;
        } catch (IOException | BiffException e) {
            e.printStackTrace();
            return null;
        }
    }




}