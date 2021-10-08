package ro.btrl.miswebappspringdemo.excel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.btrl.miswebappspringdemo.excel.export.ExcelGenerator;
import ro.btrl.miswebappspringdemo.excel.model.ExportClassExample;
import ro.btrl.miswebappspringdemo.excel.model.IdExportClassExample;
import ro.btrl.miswebappspringdemo.utils.DataGenerator;

import java.util.*;


@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @GetMapping("/exports")
    private ModelAndView getExportExcel() {
        return new ModelAndView(new ExcelGenerator(), ExcelGenerator.EXPORT_LIST_NAME, DataGenerator.generatePopulatedObjects(ExportClassExample.class, 10));
    }

    @GetMapping("/exports/custom-attributes")
    private ModelAndView getExportExcelCustomAttributes() {
        Map<String, List> model = new HashMap<>();
        model.put(ExcelGenerator.EXPORT_LIST_NAME, DataGenerator.generatePopulatedObjects(ExportClassExample.class, 10));
        List<String> customAttributes = new ArrayList<>();
        customAttributes.add("attributeOneString");
        customAttributes.add("attributeFiveBoolean");
        customAttributes.add("attributeTenInt");
        customAttributes.add("attributeString");
        customAttributes.add("attributeDate");
        customAttributes.add("attributeFourteenFloat");
        customAttributes.add("attributeNineLong");
        customAttributes.add("attributeSixDate");
        customAttributes.add("attributeSevenInteger");
        customAttributes.add("attributeEightString");
        model.put(ExcelGenerator.EXPORT_CUSTOM_ATTRIBUTES, customAttributes);
        model.put(ExcelGenerator.EXPORT_SMALL_SIZE_COLUMNS, new ArrayList<>(Arrays.asList("ATTRIBUTE STRING", "DATA HIST", "HIST DATE", "CIF", "CIF CLIENT", "CATEGORIE CLIENT", "COD PRODUS", "DATA DESCHIDERE", "DATA INCHIDERE", "ID UNIT", "ID SUCU", "ID SUCURSALA")));
        return new ModelAndView(new ExcelGenerator(), model);
    }


    @GetMapping("/exports/multiple-lists")
    private ModelAndView getExportExcelMultipleLists() {
        Map<String, Object> model = new HashMap<>();
        Map<String, List> multipleList = new HashMap<>();
        multipleList.put("test1 shhet", DataGenerator.generatePopulatedObjects(ExportClassExample.class, 10));
        multipleList.put("test2 shhet", DataGenerator.generatePopulatedObjects(IdExportClassExample.class, 10));
        multipleList.put("test3 shhet", DataGenerator.generatePopulatedObjects(ExportClassExample.class, 100));
        model.put(ExcelGenerator.MULTIPLE_DATA_LIST, multipleList);
        model.put(ExcelGenerator.EXPORT_SMALL_SIZE_COLUMNS, new ArrayList<>(Arrays.asList("ATTRIBUTE STRING", "DATA HIST", "HIST DATE", "CIF", "CIF CLIENT", "CATEGORIE CLIENT", "COD PRODUS", "DATA DESCHIDERE", "DATA INCHIDERE", "ID UNIT", "ID SUCU", "ID SUCURSALA")));
        return new ModelAndView(new ExcelGenerator(), model);
    }
}