package skypro.internetshopsocks.models;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Socks {

private ColorSocks colorSocks; // цвет носков
private SizeSocks sizeSocks; // размер носков

    @NotNull
    @Min(10)
    @Max(100)
private int cottonPart; // процентное содержание хлопка в составе носков

}
