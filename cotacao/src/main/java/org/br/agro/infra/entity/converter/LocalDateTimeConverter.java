package org.br.agro.infra.entity.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

    /**
     * Formatador para dd/MM/yyyy HH:mm:ss. Exemplo: 06-09-2022 15:20:45
     */
    public static final DateTimeFormatter DD_MM_YYYY_HH_MM_SS_TRACO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convert(LocalDateTime localDate) {
        return localDate.format(DD_MM_YYYY_HH_MM_SS_TRACO);
    }

    @Override
    public LocalDateTime unconvert(String value) {
        return LocalDateTime.parse(value, DD_MM_YYYY_HH_MM_SS_TRACO);
    }

    public static LocalDateTime converteParaLocalDateTime(String input, DateTimeFormatter formatter) {
        return LocalDateTime.parse(input, formatter);
    }
}
