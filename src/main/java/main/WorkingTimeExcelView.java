//package main;
//
//	import java.io.ByteArrayInputStream;
//	import java.io.File;
//	import java.io.IOException;
//	import java.io.InputStream;
//	import java.net.URLEncoder;
//	import java.nio.file.Files;
//	import java.util.List;
//	import java.util.Map;
//
//	import javax.servlet.http.HttpServletRequest;
//	import javax.servlet.http.HttpServletResponse;
//
//	import org.apache.poi.EncryptedDocumentException;
//	import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//	import org.apache.poi.ss.usermodel.Cell;
//	import org.apache.poi.ss.usermodel.Row;
//	import org.apache.poi.ss.usermodel.Sheet;
//	import org.apache.poi.ss.usermodel.Workbook;
//	import org.apache.poi.ss.usermodel.WorkbookFactory;
//	import org.slf4j.Logger;
//	import org.slf4j.LoggerFactory;
//	import org.springframework.beans.factory.annotation.Autowired;
//	import org.springframework.beans.factory.annotation.Value;
//	import org.springframework.stereotype.Component;
//	import org.springframework.web.servlet.view.document.AbstractXlsxView;
//
//	
//	@Component
//	public class WorkingTimeExcelView extends AbstractXlsxView {
//		
//		@Autowired
//		TimeCardRepository timeCardRepo;
//		@Autowired
//		UserInfoRepository userInfoRepo;
//
//		List<UserInfoEntity> userData;
//		List<WorkingTimeEntity> workingTimeList;
//
//	    private static final Logger LOGGER = LoggerFactory
//	            .getLogger(WorkingTimeExcelView.class);
//
//	    @Value("$C:/sample/kintai.xlsx}")
//	    private File excelTemplateFile;
//
////	    @Value("${app.report.resevation.pass:locked}")
////	    private String excelOpenPass;
//
//	    /**
//	     * ★ポイント3
//	     * <p>
//	     * excelTemplateFile で指定したExcelテンプレートを利用してWorkbookを作成する
//	     * <p>
//	     * このメソッドで返却したWorkbookのオブジェクトが、buildExcelDocumentメソッドの引数として渡される
//	     * @see org.springframework.web.servlet.view.document.AbstractXlsxView#createWorkbook(java.util.Map,
//	     *      javax.servlet.http.HttpServletRequest)
//	     */
//	    @Override
//	    protected Workbook createWorkbook(Map<String, Object> model,
//	            HttpServletRequest request) {
//	        Workbook workbook = null;
//	        try (InputStream is = new ByteArrayInputStream(
//	                Files.readAllBytes(excelTemplateFile.toPath()));) {
//	            workbook = WorkbookFactory.create(is);
//	            // workbook = WorkbookFactory.create(is, excelOpenPass);
//	            // ファイルにパスワードロックが掛かっている場合、jce_policy-8.zip の適用が必要
//	        } catch (IOException | EncryptedDocumentException
//	                | InvalidFormatException e) {
//	            LOGGER.error("create workbook error", e);
//	        }
//	        return workbook;
//	    }
//
//	    @Override
//	    protected void buildExcelDocument(Map<String, Object> model,
//	            Workbook workbook, HttpServletRequest request,
//	            HttpServletResponse response) throws Exception {
//	        // ★ポイント5
//	        // 1. Modelに格納されている帳票データを取得
//	    	WorkingTimeEntity workingTimeList;
//
//	        // ★ポイント6
//	        // 2. シートの選択
//	        Sheet sheet = workbook.getSheet("勤務表");
//
//	        // ★ポイント7
//	        // 3. セルにデータを設定
//	        // 5行F列に「予約番号」の値を設定
//	        getCell(sheet, 4, 5).setCellValue(reserve.getReserveNo());
//
//	        // ★ポイント8        
//	        // 4. responseヘッダにファイル名を設定
//	        String fileName = (String) model.get("fileName");
//	        String encodedFilename = URLEncoder.encode(fileName, "UTF-8");
//	        response.setHeader("Content-Disposition","attachment; filename*=UTF-8''" + encodedFilename);
//
//	        // ★ポイント7
//	        // 3. セルにデータを設定
//	        // 5行AA列に「予約日」の値を設定
//	        getCell(sheet, 4, 26).setCellValue(reserve.getReservedDay());
//	        // ツアー名
//	        getCell(sheet, 5, 5).setCellValue(tourInfo.getTourName());
//	        // 出発日
//	        getCell(sheet, 6, 5).setCellValue(tourInfo.getDepDay());
//	        // 日数
//	        getCell(sheet, 6, 26).setCellValue(tourInfo.getTourDays());
//	        // 出発地
//	        getCell(sheet, 7, 5).setCellValue(tourInfo.getDeparture().getDepName());
//	        // 目的地
//	        getCell(sheet, 7, 26).setCellValue(tourInfo.getArrival().getArrName());
//	        // 添乗員
//	        getCell(sheet, 8, 5).setCellValue(tourInfo.getConductor());
//	        // 宿泊施設
//	        getCell(sheet, 9, 5)
//	                .setCellValue(tourInfo.getAccommodation().getAccomName());
//	        // 連絡先
//	        getCell(sheet, 9, 26)
//	                .setCellValue(tourInfo.getAccommodation().getAccomTel());
//	        // 概要
//	        getCell(sheet, 10, 5).setCellValue(tourInfo.getTourAbs());
//	        // omitted
//	    }
//
//	    /**
//	     * <p>
//	     * 引数で指定されたシートの、行番号、列番号で指定したセルを取得して返却する
//	     * <p>
//	     * 行番号、列番号は0から開始する
//	     * <p>
//	     * Excelテンプレートで該当のセルを操作していない場合、NullPointerExceptionになる
//	     * @param sheet シート
//	     * @param rowIndex 行番号
//	     * @param colIndex 列番号
//	     * @return セル
//	     */
//	    private Cell getCell(Sheet sheet, int rowIndex, int colIndex) {
//	        Row row = sheet.getRow(rowIndex);
//	        return row.getCell(colIndex);
//	    }
//
//}
