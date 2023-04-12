package skypro.internetshopsocks.services;

import org.springframework.validation.annotation.Validated;
import skypro.internetshopsocks.models.ColorSocks;
import skypro.internetshopsocks.models.SizeSocks;
import skypro.internetshopsocks.models.SockWarehouse;
import skypro.internetshopsocks.models.Socks;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;

@Validated
public interface SockWarehouseService {

    Socks addSocks(SockWarehouse sockWarehouse);

    Map<Socks, Long>  getFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks, int cottonPart);

    Map<Socks, Long>  editFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks, int cottonPart);

    long deleteSockWarehouse(SockWarehouse sockWarehouse);

   }
