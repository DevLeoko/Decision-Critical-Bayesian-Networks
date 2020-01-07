package io.dcbn.backend.graph;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ProbabilityConverter implements AttributeConverter<double[][], String> {

    @Override
    public String convertToDatabaseColumn(double[][] attribute) {
        return Arrays.stream(attribute)
                .map(array -> Arrays.stream(array)
                        .mapToObj(Double::toString)
                        .collect(Collectors.joining(";")))
                .collect(Collectors.joining("#"));
    }

    @Override
    public double[][] convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split("#"))
                .map(array -> Arrays.stream(array.split(";"))
                        .mapToDouble(Double::parseDouble)
                        .toArray())
                .toArray(double[][]::new);
    }
}
