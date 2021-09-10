package cn.tml.dbtool.dbdict.engine;

import static cn.smallbun.screw.core.constant.DefaultConstants.MAC;
import static cn.smallbun.screw.core.constant.DefaultConstants.WINDOWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.smallbun.screw.core.engine.AbstractTemplateEngine;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.exception.ProduceException;
import cn.smallbun.screw.core.metadata.model.ColumnModel;
import cn.smallbun.screw.core.metadata.model.DataModel;
import cn.smallbun.screw.core.metadata.model.TableModel;
import cn.smallbun.screw.core.util.ExceptionUtils;
import cn.smallbun.screw.core.util.StringUtils;
import cn.tml.dbtool.dbdict.constant.ExcelConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelTemplateEngine extends AbstractTemplateEngine {

    /**
     * 
     */
    private static final long serialVersionUID = 9013626158732805456L;

    private static final String SHEET_CONTENT = "目录";
    private static final String SHEET_TABLE_STRUCT = "表结构";
    private static final String FILE_SUFFIX = ".xlsx";

    public ExcelTemplateEngine(EngineConfig engineConfig) {
        super(engineConfig);
    }

    @Override
    public void produce(DataModel info, String docName)
            throws ProduceException {
        Workbook wb = new XSSFWorkbook();
        Sheet contentSheet = wb.createSheet(SHEET_CONTENT);
        Sheet structSheet = wb.createSheet(SHEET_TABLE_STRUCT);

        // 设置目录页面列宽度
        contentSheet.setColumnWidth(ExcelConstant.CONTENT_DB_INDEX, 20 * 256);
        contentSheet.setColumnWidth(ExcelConstant.CONTENT_TBCNNAME_INDEX,
                20 * 256);
        contentSheet.setColumnWidth(ExcelConstant.CONTENT_TBENNAME_INDEX,
                30 * 256);
        contentSheet.setColumnWidth(ExcelConstant.CONTENT_DISCRP_INDEX,
                60 * 256);
        // 设置表结构页列宽度
        structSheet.setColumnWidth(ExcelConstant.STRUCT_FIELDCNNAME, 20 * 256);
        structSheet.setColumnWidth(ExcelConstant.STRUCT_FIELDENNAME, 20 * 256);
        structSheet.setColumnWidth(ExcelConstant.STRUCT_FIELDTYPE, 20 * 256);
        structSheet.setColumnWidth(ExcelConstant.STRUCT_FIELDREMARKS, 40 * 256);
        structSheet.setColumnWidth(ExcelConstant.STRUCT_FIELDPRIMAYKEY,
                10 * 256);
        structSheet.setColumnWidth(ExcelConstant.STRUCT_FIELDNULL, 10 * 256);
        structSheet.setColumnWidth(ExcelConstant.STRUCT_FIELDDEFAULT, 10 * 256);

        Row headRow = contentSheet.createRow(0);

        Cell topicCell = headRow.createCell(ExcelConstant.CONTENT_DB_INDEX);
        topicCell.setCellValue("数据库");
        setCellStyleWithFillColor(wb, topicCell);
        Cell tbCnNameCell = headRow
                .createCell(ExcelConstant.CONTENT_TBCNNAME_INDEX);
        tbCnNameCell.setCellValue("表中文名");
        setCellStyleWithFillColor(wb, tbCnNameCell);
        Cell tbEnNameCell = headRow
                .createCell(ExcelConstant.CONTENT_TBENNAME_INDEX);
        tbEnNameCell.setCellValue("表英文名");
        setCellStyleWithFillColor(wb, tbEnNameCell);
        Cell discrpCell = headRow
                .createCell(ExcelConstant.CONTENT_DISCRP_INDEX);
        discrpCell.setCellValue("说明");
        setCellStyleWithFillColor(wb, discrpCell);

        int contentRowNum = 1;
        Row contentRow;
        int structRowNum = 0;
        Row structRow;
        for (TableModel table : info.getTables()) {
            contentRow = contentSheet.createRow(contentRowNum++);
            Cell dataBaseCell = contentRow
                    .createCell(ExcelConstant.CONTENT_DB_INDEX);
            dataBaseCell.setCellValue(info.getDatabase());
            setCellStyleAlignCenter(wb, dataBaseCell);
            tbCnNameCell = contentRow
                    .createCell(ExcelConstant.CONTENT_TBCNNAME_INDEX);
            tbCnNameCell.setCellValue(table.getTableName());
            setHyperLinkCellStyle(wb, tbCnNameCell);

            tbEnNameCell = contentRow
                    .createCell(ExcelConstant.CONTENT_TBENNAME_INDEX);
            tbEnNameCell.setCellValue(table.getTableName());
            setCellStyle(wb, tbEnNameCell);

            discrpCell = contentRow
                    .createCell(ExcelConstant.CONTENT_DISCRP_INDEX);
            discrpCell.setCellValue(table.getRemarks());
            setCellStyle(wb, discrpCell);

            structRow = structSheet.createRow(structRowNum++);
            Cell tbCnNameCellExt = structRow
                    .createCell(ExcelConstant.STRUCT_TBCNNAME_INDEX);
            tbCnNameCellExt.setCellValue(table.getTableName());
            setCellStyleWithFillColor(wb, tbCnNameCellExt);
            Cell tbEnNameCellExt = structRow
                    .createCell(ExcelConstant.STRUCT_TBENNAME_INDEX);
            tbEnNameCellExt.setCellValue(table.getTableName());
            setCellStyleWithFillColor(wb, tbEnNameCellExt);
            Cell discrpCellExt = structRow
                    .createCell(ExcelConstant.STRUCT_DISCRP_INDEX);
            discrpCellExt.setCellValue(table.getRemarks());
            setCellStyleWithFillColor(wb, discrpCellExt);

            // 合并单元格
            CellRangeAddress structCra = new CellRangeAddress(structRowNum - 1,
                    structRowNum - 1, 2, 6);
            structSheet.addMergedRegion(structCra);

            // 使用RegionUtil类为合并后的单元格添加边框
            RegionUtil.setBorderBottom(BorderStyle.THIN, structCra,
                    structSheet); // 下边框
            RegionUtil.setBorderLeft(BorderStyle.THIN, structCra, structSheet); // 左边框
            RegionUtil.setBorderRight(BorderStyle.THIN, structCra, structSheet); // 右边框
            RegionUtil.setBorderTop(BorderStyle.THIN, structCra, structSheet); // 上边框

            structRow = structSheet.createRow(structRowNum++);
            Cell fieldCnNameCell = structRow
                    .createCell(ExcelConstant.STRUCT_FIELDCNNAME);
            fieldCnNameCell.setCellValue("字段中文名");
            setCellStyle(wb, fieldCnNameCell);
            Cell fieldEnNameCell = structRow
                    .createCell(ExcelConstant.STRUCT_FIELDCNNAME);
            fieldEnNameCell.setCellValue("字段英文名");
            setCellStyle(wb, fieldEnNameCell);
            Cell fieldTypeCell = structRow
                    .createCell(ExcelConstant.STRUCT_FIELDTYPE);
            fieldTypeCell.setCellValue("字段类型");
            setCellStyle(wb, fieldTypeCell);
            Cell fieldRemarksCell = structRow
                    .createCell(ExcelConstant.STRUCT_FIELDREMARKS);
            fieldRemarksCell.setCellValue("注释");
            setCellStyle(wb, fieldRemarksCell);
            Cell fieldPrimayKeyCell = structRow
                    .createCell(ExcelConstant.STRUCT_FIELDPRIMAYKEY);
            fieldPrimayKeyCell.setCellValue("是否主键");
            setCellStyle(wb, fieldPrimayKeyCell);
            Cell fieldNullCell = structRow
                    .createCell(ExcelConstant.STRUCT_FIELDNULL);
            fieldNullCell.setCellValue("允许空值");
            setCellStyle(wb, fieldNullCell);
            Cell fieldDefaultCell = structRow
                    .createCell(ExcelConstant.STRUCT_FIELDDEFAULT);
            fieldDefaultCell.setCellValue("默认值");
            setCellStyle(wb, fieldDefaultCell);

            // 链接跳转
            CreationHelper creationHelper = wb.getCreationHelper();
            Hyperlink hyperlink = creationHelper
                    .createHyperlink(HyperlinkType.DOCUMENT);
            hyperlink.setAddress(
                    SHEET_TABLE_STRUCT + "!" + "A" + (structRowNum - 1));
            tbCnNameCell.setHyperlink(hyperlink);

            for (ColumnModel column : table.getColumns()) {
                structRow = structSheet.createRow(structRowNum++);
                fieldCnNameCell = structRow
                        .createCell(ExcelConstant.STRUCT_FIELDCNNAME);
                fieldCnNameCell.setCellValue(column.getColumnName());
                setCellStyle(wb, fieldCnNameCell);
                fieldEnNameCell = structRow
                        .createCell(ExcelConstant.STRUCT_FIELDENNAME);
                fieldEnNameCell.setCellValue(column.getColumnName());
                setCellStyle(wb, fieldEnNameCell);
                fieldTypeCell = structRow
                        .createCell(ExcelConstant.STRUCT_FIELDTYPE);
                fieldTypeCell.setCellValue(column.getTypeName() + "("
                        + column.getColumnSize() + ")");
                setCellStyle(wb, fieldTypeCell);
                fieldRemarksCell = structRow
                        .createCell(ExcelConstant.STRUCT_FIELDREMARKS);
                fieldRemarksCell.setCellValue(column.getRemarks());
                setCellStyle(wb, fieldRemarksCell);
                fieldPrimayKeyCell = structRow
                        .createCell(ExcelConstant.STRUCT_FIELDPRIMAYKEY);
                fieldPrimayKeyCell.setCellValue(column.getPrimaryKey());
                setCellStyle(wb, fieldPrimayKeyCell);
                fieldNullCell = structRow
                        .createCell(ExcelConstant.STRUCT_FIELDNULL);
                fieldNullCell.setCellValue(column.getNullable());
                setCellStyle(wb, fieldNullCell);
                fieldDefaultCell = structRow
                        .createCell(ExcelConstant.STRUCT_FIELDDEFAULT);
                fieldDefaultCell.setCellValue(column.getColumnDef());
                setCellStyle(wb, fieldDefaultCell);
            }

            structRowNum++;
        }

        // 合并单元格
        CellRangeAddress contentCra = new CellRangeAddress(1,
                (contentRowNum - 1), 0, 0);
        contentSheet.addMergedRegion(contentCra);

        // 使用RegionUtil类为合并后的单元格添加边框
        RegionUtil.setBorderBottom(BorderStyle.THIN, contentCra, contentSheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, contentCra, contentSheet); // 左边框
        RegionUtil.setBorderRight(BorderStyle.THIN, contentCra, contentSheet); // 有边框
        RegionUtil.setBorderTop(BorderStyle.THIN, contentCra, contentSheet); // 上边框

        // create file
        File file = getFile(docName);

        try (OutputStream outpout = new FileOutputStream(file)) {
            wb.write(outpout);
        } catch (IOException e) {
            log.error("ExcelTemplateEngine output error!", e);
        }

        // open the output directory
        openOutputDir();

    }

    private void setCellStyle(Workbook wb, Cell cell) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        cell.setCellStyle(cellStyle);
    }

    private void setCellStyleWithFillColor(Workbook wb, Cell cell) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex());

        cell.setCellStyle(cellStyle);
    }

    private void setCellStyleAlignCenter(Workbook wb, Cell cell) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        cell.setCellStyle(cellStyle);
    }

    private void setHyperLinkCellStyle(Workbook wb, Cell cell) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setUnderline(XSSFFont.U_DOUBLE);
        font.setColor(IndexedColors.BLUE_GREY.getIndex());
        cellStyle.setFont(font);

        cell.setCellStyle(cellStyle);
    }

    @Override
    protected File getFile(String docName) {
        File file;
        // 如果没有填写输出路径，默认当前项目路径下的doc目录
        if (StringUtils.isBlank(getEngineConfig().getFileOutputDir())) {
            String dir = System.getProperty("user.dir");
            file = new File(dir + "/doc");
        } else {
            file = new File(getEngineConfig().getFileOutputDir());
        }
        // 不存在创建
        if (!file.exists()) {
            // 创建文件夹
            boolean mkdir = file.mkdirs();
        }
        // 文件后缀
        file = new File(file, docName + FILE_SUFFIX);
        // 设置文件产生位置
        getEngineConfig().setFileOutputDir(file.getParent());
        return file;
    }

    @Override
    protected void openOutputDir() {
        // 是否打开，如果是就打开输出路径
        if (getEngineConfig().isOpenOutputDir() && StringUtils
                .isNotBlank(getEngineConfig().getFileOutputDir())) {
            try {
                // 获取系统信息
                String osName = System.getProperty("os.name");
                if (osName != null) {
                    if (osName.contains(MAC)) {
                        Runtime.getRuntime().exec(
                                "open " + getEngineConfig().getFileOutputDir());
                    } else if (osName.contains(WINDOWS)) {
                        Runtime.getRuntime().exec("explorer "
                                + getEngineConfig().getFileOutputDir());
                    }
                }
            } catch (IOException e) {
                throw ExceptionUtils.mpe(e);
            }
        }
    }

}
