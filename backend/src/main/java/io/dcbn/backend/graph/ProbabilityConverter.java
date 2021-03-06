package io.dcbn.backend.graph;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This converter is used to convert a {@link String} into a 2 dimensional double array.
 */
public class ProbabilityConverter implements AttributeConverter<double[][], String> {

    /**
     * Converter from double[][] to string
     *
     * @param attribute the array to convert into a {@link String}
     * @return the {@link String} generated by the converter
     */
    @Override
    public String convertToDatabaseColumn(double[][] attribute) {
        return Arrays.stream(attribute)
                .map(array -> Arrays.stream(array)
                        .mapToObj(Double::toString)
                        .collect(Collectors.joining(";")))
                .collect(Collectors.joining("#"));
    }

    /**
     * Converter from {@link String} to double[][]
     *
     * @param dbData the {@link String} to convert into a double[][]
     * @return the double[][] generated by the converter
     */
    @Override
    public double[][] convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split("#"))
                .map(array -> Arrays.stream(array.split(";"))
                        .mapToDouble(Double::parseDouble)
                        .toArray())
                .toArray(double[][]::new);
    }
}
