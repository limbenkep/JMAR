import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportToExcel <T>{
    private final Stage stage;
    public ExportToExcel(Stage stage) {
        this.stage= stage;
    }

    public void export(final TableView<T> tableView, final String workbook) {

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Sheet1");
        HSSFRow firstRow = hssfSheet.createRow(0);

        ///set titles of columns
        for (int i = 0; i < tableView.getColumns().size(); i++) {

            firstRow.createCell(i).setCellValue(tableView.getColumns().get(i).getText());

        }


        for (int row = 0; row < tableView.getItems().size(); row++) {

            HSSFRow hssfRow = hssfSheet.createRow(row + 1);

            for (int col = 0; col < tableView.getColumns().size(); col++) {

                Object celValue = tableView.getColumns().get(col).getCellObservableValue(row).getValue();

                try {
                    if (celValue != null && Double.parseDouble(celValue.toString()) != 0.0) {
                        hssfRow.createCell(col).setCellValue(Double.parseDouble(celValue.toString()));
                    }
                } catch (NumberFormatException e) {
                    hssfRow.createCell(col).setCellValue(celValue.toString());
                }

            }

        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Excel file", "*.xls", "*.xlsx"));
        File excelFile = fileChooser.showSaveDialog(stage);
        //save Excel file and close the workbook
        try {
            hssfWorkbook.write(new FileOutputStream(excelFile));
            hssfWorkbook.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
