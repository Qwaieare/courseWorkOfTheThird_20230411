package skypro.internetshopsocks.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import skypro.internetshopsocks.models.ColorSocks;
import skypro.internetshopsocks.models.SizeSocks;
import skypro.internetshopsocks.models.SockWarehouse;
import skypro.internetshopsocks.models.Socks;
import skypro.internetshopsocks.services.FileService;
import skypro.internetshopsocks.services.SockWarehouseService;

import javax.validation.constraints.PositiveOrZero;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SockWarehouseImpl implements SockWarehouseService {

    private static Map<Socks, Long> batchOfSsocks = new HashMap<>();

    @PositiveOrZero(message = "Количество должно быть больше нуля")

    private final FileService fileService;

    public SockWarehouseImpl(FileService fileService) {
        this.fileService = fileService;
    }


    @Override
    public Socks addSocks(SockWarehouse sockWarehouse) { // поступление носков на склад
        Socks socks = sockWarehouse.getSocks();
        if (batchOfSsocks.containsKey(socks)) {
            batchOfSsocks.replace(socks, batchOfSsocks.get(socks) + sockWarehouse.getQuantity());
        } else {
            batchOfSsocks.putIfAbsent(socks, sockWarehouse.getQuantity());
        }
        return socks;
    }


    @Override
    public Map<Socks, Long>  getFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks, int cottonPart) {
        Map<Socks, Long> socksFilter = batchOfSsocks.entrySet()
                .stream()
                .filter(map -> map.getKey().getColorSocks().equals(colorSocks))
                .filter(map -> map.getKey().getSizeSocks().equals(sizeSocks))
                .filter(map -> map.getKey().getCottonPart() == cottonPart)
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue)
                        );
        saveToFile();
        return socksFilter;
    }


    @Override
    public Map<Socks, Long>  editFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks, int cottonPart) {
        Map<Socks, Long> socksFilterEdit = batchOfSsocks.entrySet()
                .stream()
                .filter(map -> map.getKey().getColorSocks().equals(colorSocks))
                .filter(map -> map.getKey().getSizeSocks().equals(sizeSocks))
                .filter(map -> map.getKey().getCottonPart() == cottonPart)
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue)
                );
        socksFilterEdit.remove(colorSocks);
        socksFilterEdit.remove(sizeSocks);
        socksFilterEdit.remove(cottonPart);
        saveToFile();
        return socksFilterEdit;
    }



    @Override
    public long deleteSockWarehouse(SockWarehouse sockWarehouse) {
        Socks socks = sockWarehouse.getSocks();
            long quantity = batchOfSsocks.get(socks);
               if (batchOfSsocks.containsKey(socks)) {
                   if (quantity > sockWarehouse.getQuantity()) {
                       batchOfSsocks.replace(socks, quantity - sockWarehouse.getQuantity());
                       return sockWarehouse.getQuantity();
                   } else {
                       batchOfSsocks.remove(socks);
                       return quantity;
                   }
               }
                saveToFile();
               return 0;
    }



    // метод принимает и сохраняет информацию
    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(batchOfSsocks);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // метод возвращает информацию, которую прочитали
    private void readFromFile() {
        String json = fileService.readFromFile();
        try {
            batchOfSsocks = new ObjectMapper().readValue(json, new TypeReference<HashMap<Socks, Long>>() {
            });
        } catch (JsonMappingException e) {

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



//
//    @PostConstruct
//    private void init() {
//        try {
//            readFromFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



}
