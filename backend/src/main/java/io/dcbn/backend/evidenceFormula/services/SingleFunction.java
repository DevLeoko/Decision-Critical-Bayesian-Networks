package io.dcbn.backend.evidenceFormula.services;

@FunctionalInterface
public interface SingleFunction<A, R> {

    R apply(A a);

}
