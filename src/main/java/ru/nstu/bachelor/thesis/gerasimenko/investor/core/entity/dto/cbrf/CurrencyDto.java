package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.cbrf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrencyDto {

    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    private String id;

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "EngName")
    private String engName;

    @JacksonXmlProperty(localName = "Nominal")
    private String nominal;

    @JacksonXmlProperty(localName = "ParentCode")
    private String parentCode;

    @JacksonXmlProperty(localName = "ISO_Num_Code")
    private String isoNumCode;

    @JacksonXmlProperty(localName = "ISO_Char_Code")
    private String isoCharCode;

    public String getFormattedNumCode() {
        if (isoNumCode == null || isoNumCode.isEmpty()) {
            return "000";
        }
        try {
            int code = Integer.parseInt(isoNumCode);
            return String.format("%03d", code);
        } catch (NumberFormatException e) {
            return "000";
        }
    }
}