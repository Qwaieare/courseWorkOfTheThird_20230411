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

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;
import javax.validation.constraints.PositiveOrZero;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SockWarehouseImpl implements SockWarehouseService {
    private static Map<Socks, Long> batchOfSsocks = new HashMap<>();
    private final FileService fileService;

    @PositiveOrZero(message = "Количество должно быть больше нуля")
    private long quantity;
    public SockWarehouseImpl(FileService fileService) {
         this.fileService = fileService;
    }



    @Override
    public Socks addSocks(SockWarehouse sockWarehouse) { // поступление носков на склад
        Socks socks = sockWarehouse.getSocks();
        if (batchOfSsocks.containsKey(socks)) {
            batchOfSsocks.replace(socks, batchOfSsocks.get(socks) + sockWarehouse.getQuantity());
        } else {
            batchOfSsocks.put(socks, sockWarehouse.getQuantity());
        }
        saveToFile();
        return socks;
    }

    @Override
    public Map<Socks, Long> getAll() {
        return batchOfSsocks;
     }


     @Override
    public Map<Socks, Long> getFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks,
                                                   int cottonPart, long quantity)
                                                   throws ValidationException {
        if (batchOfSsocks.containsValue(quantity)) {

            Map<Socks, Long> socksFilter = batchOfSsocks.entrySet()
                    .stream()
                    .filter(map -> map.getKey().getColorSocks().equals(colorSocks))
                    .filter(map -> map.getKey().getSizeSocks().equals(sizeSocks))
                    .filter(map -> map.getKey().getCottonPart() == cottonPart)
                    .filter(map -> "quantity".equals(map.getValue()))
                    .collect(Collectors
                            .toMap(Map.Entry::getKey,
                                    Map.Entry::getValue)
                    );
            return socksFilter;
        }
        return null;
    }


    @Override
    public Socks editFilterSockWarehouse(Socks socks, long quantity) {
        if (batchOfSsocks.containsValue(quantity)) {
            long defectiveSocks = batchOfSsocks.get(socks) - quantity;
            if (defectiveSocks > 0) {
                batchOfSsocks.merge(socks, quantity, (a, b) -> a - b);
                batchOfSsocks.putIfAbsent(socks, quantity);
            }
            saveToFile();
        } else {
            throw new IllegalArgumentException();
        }
        return socks;
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


    @PostConstruct
    private void init() {
        try {
            readFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
