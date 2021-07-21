package org.tdf.sim.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.tdf.sim.type.Personnel;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelUtils {

    /**
     * 导出excel
     *
     * @param excelName 导出的excel路径（需要带.xlsx)
     * @param headList  excel的标题字段（与数据中map中键值对应）
     * @param dataList  excel数据
     * @throws Exception
     */
    public static void createExcel(HttpServletResponse response, String excelName, String[] headList,
                                   List<Map<String, Object>> dataList)
            throws Exception {
        // 创建新的Excel 工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 在Excel工作簿中建一工作表，其名为缺省值
        XSSFSheet sheet = workbook.createSheet();
        // 在索引0的位置创建行（最顶端的行）
        XSSFRow row = sheet.createRow(0);
        // 设置excel头（第一行）的头名称
        for (int i = 0; i < headList.length; i++) {

            // 在索引0的位置创建单元格（左上端）
            XSSFCell cell = row.createCell(i);
            // 定义单元格为字符串类型
            cell.setCellType(CellType.STRING);
            // 在单元格中输入一些内容
            cell.setCellValue(headList[i]);
        }
        // ===============================================================
        //添加数据
        for (int n = 0; n < dataList.size(); n++) {
            // 在索引1的位置创建行（最顶端的行）
            XSSFRow row_value = sheet.createRow(n + 1);
            Map<String, Object> dataMap = dataList.get(n);
            // ===============================================================
            for (int i = 0; i < headList.length; i++) {

                // 在索引0的位置创建单元格（左上端）
                XSSFCell cell = row_value.createCell(i);
                // 定义单元格为字符串类型
                cell.setCellType(CellType.STRING);
                // 在单元格中输入一些内容
                cell.setCellValue((dataMap.get(headList[i])).toString());
            }
            // ===============================================================
        }
        // 把相应的Excel 工作簿存盘
        response.setContentType("application/octet-stream");
        //默认Excel名称
        response.setHeader("Content-Disposition", "attachment;fileName=" + excelName);
        workbook.write(response.getOutputStream());
    }

    private Workbook workbook;

    public ExcelUtils(InputStream is, String filename) throws IOException {
        try {
            if (filename.endsWith(".xls")) {
                // Excel2003及以前版本
                workbook = new HSSFWorkbook(is);
            } else {
                // Excel2007及以后版本
                workbook = new XSSFWorkbook(is);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 读取指定sheet页指定行数据
     *
     * @param sheetIx 指定sheet页，从0开始
     * @param start   指定开始行，从0开始
     * @param end     指定结束行，从0开始
     * @return List<List < String>>
     */
    public List<List<String>> read(int sheetIx, int start, int end) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        List<List<String>> rowList = new ArrayList<>();

        if (end > getRowCount(sheetIx)) {
            end = getRowCount(sheetIx);
        }
        // 第一行总列数
        int colNum = sheet.getRow(0).getLastCellNum();
        for (int i = start; i < end; i++) {
            List<String> colList = new ArrayList<>();
            Row row = sheet.getRow(i);
            for (int j = 0; j < colNum; j++) {
                if (row == null) {
                    colList.add(null);
                    continue;
                }
                colList.add(getCellValue(row.getCell(j)));
            }
            rowList.add(colList);
        }
        return rowList;
    }

    private String getCellValue(Cell cell) {
        String cellValue;
        if (cell == null){
            return "";
        }
        switch (cell.getCellType()) {
            // 数字
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cellValue = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                } else {
                    DataFormatter dataFormatter = new DataFormatter();
                    cellValue = dataFormatter.formatCellValue(cell);
                }
                break;
            // 字符串
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            // Boolean
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            // 公式
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            // 空值
            case BLANK:
                cellValue = null;
                break;
            // 错误
            case ERROR:
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }


    public Integer getRowCount(int sheetIx) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return 0;
        }
        return sheet.getLastRowNum() + 1;
    }


    public static void main(String[] args) throws Exception {
//        List<Map<String, Object>> dataList = new ArrayList<>();
//        Map<String, Object> map = new HashMap<>();
//        map.put("学号/工号（必填）","322132131");
//        map.put("姓名（必填）","322132131");
//        map.put("证件证号","322132131");
//        map.put("性别","男");
//        map.put("电话","18805177592");
//        map.put("院系","计算机系");
//        map.put("专业","计算机科学与技术");
//        map.put("年级","大二");
//        map.put("班级","二班");
//        map.put("职称（0学生,1教师）",0);
//        dataList.add(map);
//        dataList.add(map);
//        dataList.add(map);
//        ExcelUtils.createExcel("1.xlsx",new String[]{"学号/工号（必填）","姓名（必填）","证件证号","性别","电话","院系","专业","年级"
//                ,"班级","职称（0学生,1教师）"},dataList);
        File file = new File("src/main/resources/3.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        ExcelUtils utils = new ExcelUtils(inputStream, file.getName());
        System.out.println(utils.parsePersonnel(utils.read(0, 1, utils.getRowCount(0))));
    }

    public List<Personnel> parsePersonnel(List<List<String>> lists) {
        return lists.stream().map(x -> Personnel.builder()
                .id(x.get(0))
                .name(x.get(1))
                .identificationNumber(x.get(2))
                .gender(StringUtils.isEmpty(x.get(3)) || x.get(3).equals("男") ? 0 : 1)
                .phone(x.get(4))
                .department(x.get(5))
                .major(x.get(6))
                .grade(x.get(7))
                .classes(x.get(8))
                .type(Integer.parseInt(x.get(9)) == 0 ? 0 : 1)
                .build()).collect(Collectors.toList());
    }

}
