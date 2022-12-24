package jworkspace.exif;

import javax.persistence.Entity;
import java.util.Date;

import jworkspace.BaseEntity;
import org.apache.commons.math3.fraction.Fraction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * The tags important for weather analysis:
 * <ul>
 * <li>[Exif SubIFD] Date/Time Original - 2022:07:11 09:58:21</li>
 * <li>[Exif SubIFD] Exposure Time - 1/2062 sec</li>
 * <li>[Exif SubIFD] Shutter Speed Value - 1/2061 sec</li>
 * <li>[Exif SubIFD] Aperture Value - f/1.6</li>
 * <li>[Exif SubIFD] Brightness Value - 9.031</li>
 * </ul>
 * <p>
 * Specific to iPhone 13 EXIF
 * </p>
 * <ul>
 * <li>[ICC Profile] Media White Point - (0.9642, 1, 0.8249)</li>
 * <li>[ICC Profile] Red Colorant - (0.5151, 0.2412, 65536)</li>
 * <li>[ICC Profile] Green Colorant - (0.292, 0.6922, 0.0419)</li>
 * <li>[ICC Profile] Blue Colorant - (0.1571, 0.0666, 0.7841)</li>
 * <li>[ICC Profile] Red TRC - para (0x70617261): 32 bytes</li>
 * <li>[ICC Profile] Blue TRC - para (0x70617261): 32 bytes</li>
 * <li>[ICC Profile] Green TRC - para (0x70617261): 32 bytes</li>
 * <li>[ICC Profile] Chromatic Adaptation - sf32 (0x73663332): 44 bytes</li>
 * <li>[ICC Profile] XYZ values - 0.964 1 0.825</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@ToString
public class WeatherImage extends BaseEntity<Long> {

    private Date date;

    private String path;

    private Fraction exposureTime;

    private Double brightnessValue;


}
