package me.samuelh2005.logic_plus_plus.data;

/**
 * A readable data value exposed on ONE side of a block.
 * A block that returns one of these for a side is saying "you can read data from this face".
 */
@FunctionalInterface
public interface DataValueOutput {
    int getValue();
}