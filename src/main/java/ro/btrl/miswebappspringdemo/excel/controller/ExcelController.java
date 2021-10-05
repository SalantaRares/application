package ro.btrl.miswebappspringdemo.excel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.btrl.miswebappspringdemo.excel.export.ExcelGenerator;
import ro.btrl.miswebappspringdemo.excel.model.ExportClassExample;
import ro.btrl.miswebappspringdemo.utils.DataGenerator;


@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @GetMapping("/exports")
    private ModelAndView getExportExcel() {
        return new ModelAndView(new ExcelGenerator(), ExcelGenerator.EXPORT_LIST_NAME, DataGenerator.generatePopulatedObjects(ExportClassExample.class,10));
    }
}