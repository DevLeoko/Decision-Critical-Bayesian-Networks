package io.dcbn.backend.graph;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ProbabilityConverter implements AttributeConverter<Double[][], String> {

    @Override
    public String convertToDatabaseColumn(Double[][] attribute) {
        return Arrays.stream(attribute)
                .map(array -> Arrays.stream(array)
                        .map(Object::toString)
                        .collect(Collectors.joining(";")))
                .collect(Collectors.joining("#"));
    }

    @Override
    public Double[][] convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split("#"))
                .map(array -> Arrays.stream(array.split(";"))
                        .map(Double::parseDouble)
                        .toArray(Double[]::new))
                .toArray(Double[][]::new);
    }
}
