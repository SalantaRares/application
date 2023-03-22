package com.application.excel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.application.excel.export.ExcelGenerator;
import com.application.excel.model.ExcelParseExample;
import com.application.excel.model.ExcelParserExample2;
import com.application.excel.model.ExportClassExample;
import com.application.excel.model.IdExportClassExample;
import com.application.excel.parse.ExcelParser;
import com.application.utils.DataGenerator;

import java.io.IOException;
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


    @GetMapping("/exports/multiple-lists/exceptions")
    private ModelAndView getExportExcelMultipleListsEx() {
        Map<String, Object> model = new HashMap<>();
        Map<String, List> multipleList = new HashMap<>();
        List l = new ArrayList();
        l.add(new ExportClassExample());
        multipleList.put("test1 shhet", DataGenerator.generatePopulatedObjects(ExportClassExample.class, 0));
        multipleList.put("test2 shhet", DataGenerator.generatePopulatedObjects(IdExportClassExample.class, 10));
        multipleList.put("test3 shhet", null);
        multipleList.put("test4 shhet", l);
        model.put(ExcelGenerator.MULTIPLE_DATA_LIST, multipleList);
        return new ModelAndView(new ExcelGenerator(), model);
    }

    @GetMapping("/exports/custom-formats")
    private ModelAndView getExportExcelCustomFormats() {
        return new ModelAndView(new ExcelGenerator(), ExcelGenerator.EXPORT_LIST_NAME, DataGenerator.generatePopulatedObjects(ExportClassExample.class, 10));
    }


    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity parseExcel(MultipartFile file) throws IOException {
        ExcelParser excelParser = new ExcelParser(file);
        final List data = excelParser.getData(ExcelParseExample.class);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @PostMapping(value = "/parse2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity parseExcel2(MultipartFile file) throws IOException {
        ExcelParser excelParser = new ExcelParser(file);
        final List data = excelParser.getData(ExcelParserExample2.class);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}