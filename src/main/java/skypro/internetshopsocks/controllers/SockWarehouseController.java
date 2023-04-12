package skypro.internetshopsocks.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skypro.internetshopsocks.models.ColorSocks;
import skypro.internetshopsocks.models.SizeSocks;
import skypro.internetshopsocks.models.SockWarehouse;
import skypro.internetshopsocks.models.Socks;
import skypro.internetshopsocks.services.SockWarehouseService;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "Носки", description = "CRUD операции с носками")
public class SockWarehouseController {

    private final SockWarehouseService sockWarehouseService;

    public SockWarehouseController(SockWarehouseService sockWarehouseService) {
        this.sockWarehouseService = sockWarehouseService;
    }

    @PostMapping
    @Operation(summary = "Поступление новых носков", description = "Регистрирует приход товара на склад")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Socks> addSocks(@RequestBody SockWarehouse sockWarehouse) {
        Socks addSocks = sockWarehouseService.addSocks(sockWarehouse);
        return ResponseEntity.ok().build();
    }



    @GetMapping
    @Operation(summary = "Запрос носков по параметрам", description = "Получение ноcков, " +
            "отсортированных по цвету, размеру, максимальному значению содержания хлопка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<Socks, Long>> getFilterSockWarehouse(
            @RequestParam("выбрать цвет") ColorSocks colorSocks,
            @RequestParam("выбрать размер") SizeSocks sizeSocks,
            @RequestParam("укажите содержание х/б") int cottonPart)
             {
            return ResponseEntity.ok(sockWarehouseService.getFilterSockWarehouse(colorSocks, sizeSocks, cottonPart));
    }




    @PutMapping
    @Operation(summary = "Отпуск носков со склада по параметрам", description = "Уменьшение общего количества носков")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<Socks, Long>> editFilterSockWarehouse(
            @RequestParam("выдать по цвету") ColorSocks colorSocks,
            @RequestParam("выдать по размеру") SizeSocks sizeSocks,
            @RequestParam("выдать по составу х/б") int cottonPart)
     {
        return ResponseEntity.ok(sockWarehouseService.editFilterSockWarehouse(colorSocks, sizeSocks, cottonPart));
    }



    @DeleteMapping
    @Operation(summary = "Списание бракованных носков", description = "Уменьшение общего количества носков")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Long> deleteSockWarehouse(@RequestBody SockWarehouse sockWarehouse) {
       sockWarehouseService.deleteSockWarehouse(sockWarehouse);
            return ResponseEntity.ok().build();
    }


}
