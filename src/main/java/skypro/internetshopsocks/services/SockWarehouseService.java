package skypro.internetshopsocks.services;

import org.springframework.validation.annotation.Validated;
import skypro.internetshopsocks.models.ColorSocks;
import skypro.internetshopsocks.models.SizeSocks;
import skypro.internetshopsocks.models.SockWarehouse;
import skypro.internetshopsocks.models.Socks;

import javax.validation.ValidationException;
import java.util.Map;

@Validated
public interface SockWarehouseService {

    Socks addSocks(SockWarehouse sockWarehouse);

    Map<Socks, Long> getAll();

    Map<Socks, Long> getFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks,
                                            int cottonPart, long quantity)
                                                  throws ValidationException;

    Socks editFilterSockWarehouse(Socks socks, long quantity);

    long deleteSockWarehouse(SockWarehouse sockWarehouse);

   }
