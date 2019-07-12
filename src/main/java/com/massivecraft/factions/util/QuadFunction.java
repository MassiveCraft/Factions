package com.massivecraft.factions.util;

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {
    R apply(T t, U u, V v, W w);
}
