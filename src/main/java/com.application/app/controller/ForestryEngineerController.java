package com.application.app.controller;

import com.application.app.service.ForestryEngineerService;
import com.application.util.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ForestryEngineerController {
    private final ForestryEngineerService forestryEngineerService;

    public ForestryEngineerController(ForestryEngineerService forestryEngineerService) {
        this.forestryEngineerService = forestryEngineerService;
    }

    @GetMapping("/all-clients")
    public ResponseEntity getAllClients(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.getAllClientsEntity());
    }

    @GetMapping("/username")
    public ResponseEntity getUsername(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(Utils.getUserNameFromPrincipal(principal));
    }

//    @GetMapping("/all-clients/export")
//    public ModelAndView getReportAllClients(Principal principal) {
//        return new ModelAndView(new ExcelGenerator(), ExcelGenerator.EXPORT_LIST_NAME, forestryEngineerService.getAllClientsEntity("principal"));
//    }

    @GetMapping("/expire-today")
    public ResponseEntity getTodayExpire(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.getTodayExpire());
    }

//    @GetMapping("/expire-today/export")
//    public ModelAndView getReportTodayExpire(Principal principal) {
//        return new ModelAndView(new ExcelGenerator(), ExcelGenerator.EXPORT_LIST_NAME, forestryEngineerService.getTodayExpire("principal"));
//    }

    @GetMapping("/expire-by-given-date")
    public ResponseEntity getExpireByGivenDate(@RequestParam(name = "date") Long date, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.getExpireByGivenDate(date));
    }

//    @GetMapping("/expire-by-given-date/export")
//    public ModelAndView getReportExpirebyGivenDate(@RequestParam(name = "date") Long date, Principal principal) {
//        return new ModelAndView(new ExcelGenerator(), ExcelGenerator.EXPORT_LIST_NAME, forestryEngineerService.getExpireByGivenDate(date, "principal"));
//    }

//    @PostMapping("/new-client")
//    public ResponseEntity insertNewClient(@RequestBody AllClientsDto allClientsDto, Principal principal) {
//        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.insertNewClient(allClientsDto, "principal"));
//    }

    @PutMapping("/ridicare-portie")
    public ResponseEntity giveAllQuantity(@RequestParam(name = "CNP") String cnp, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.giveAllQuantity(cnp));
    }

    @PutMapping("/ridicare-bucata-portie")
    public ResponseEntity givePartQuantity(@RequestParam(name = "CNP") String cnp,
                                           @RequestParam(name = "portieMcPredata") BigDecimal portieMcPredata,
                                           @RequestParam(name = "anii_restanta") String aniiRestanta,
                                           Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.givePartQuantity(cnp, portieMcPredata));
    }


    @PutMapping("/actualizare-anii-restanta")
    public ResponseEntity updateRemainingYears(@RequestParam(name = "CNP") String cnp,
                                               @RequestParam(name = "aniiRestanta") String aniiRestanta,
                                               Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.updateRemainingYears(cnp, aniiRestanta));
    }

    @PutMapping("/edit-client")
    public ResponseEntity insertNewClient(
            @RequestParam(name = "nume", required = false) String nume,
            @RequestParam(name = "prenume", required = false) String prenume,
            @RequestParam(name = "cnp") String cnp,
            @RequestParam(name = "dataRidicarePortie", required = false) Long dataRidicarePortie,
            @RequestParam(name = "dataScadentaRidicarePortie", required = false) Long dataScadentaRidicarePortie,
            @RequestParam(name = "cantitateTotala", required = false) BigDecimal cantitateTotala,
            @RequestParam(name = "um", required = false) String um,
            @RequestParam(name = "imputernicit", required = false) String imputernicit,
            @RequestParam(name = "portieAn", required = false) BigDecimal portieAn,
            @RequestParam(name = "portieRamasaAniiAnteriori", required = false) BigDecimal portieRamasaAniiAnteriori,
            @RequestParam(name = "hectareDetinute", required = false) BigDecimal hectareDetinute,
            @RequestParam(name = "portieRestanta", required = false) BigDecimal portieRestanta,
            @RequestParam(name = "cnpImputernicit", required = false) String cnpImputernicit,
            @RequestParam(name = "aniiRestanta", required = false) String aniiRestanta,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.updateClient(nume, prenume, cnp, dataRidicarePortie,
                dataScadentaRidicarePortie, cantitateTotala, um, imputernicit, portieAn, portieRamasaAniiAnteriori, hectareDetinute,
                portieRestanta, cnpImputernicit, aniiRestanta));
    }

    @PostMapping(value = "/clients-from-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity importClientsFromExcel(@RequestParam(name = "file") MultipartFile file, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(forestryEngineerService.importClientsFromExcel(file));
    }

}
