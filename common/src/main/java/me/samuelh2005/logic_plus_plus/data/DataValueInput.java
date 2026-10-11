package me.samuelh2005.logic_plus_plus.data;

/**
 * A data input exposed on ONE side of a block.
 * A block that returns one of these for a side is saying "this face takes data".
 * Whatever feeds the input calls onInputChanged() when the data behind it may have changed.
 */
@FunctionalInterface
public interface DataValueInput {
    void onInputChanged(int newValue);
}