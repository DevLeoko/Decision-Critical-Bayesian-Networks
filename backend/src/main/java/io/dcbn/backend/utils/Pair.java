package io.dcbn.backend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<T, G> {
    private T key;
    private G value;
}
