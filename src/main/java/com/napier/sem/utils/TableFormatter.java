package com.napier.sem.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for formatting output tables in commands.
 * Provides methods to generate dynamic separator lines that match column widths.
 */
public class TableFormatter {

    /**
     * Generates a separator line with '=' characters matching the total width of columns
     * based on a printf format string.
     * 
     * @param formatString Printf format string (e.g., "%-30s %-30s %15s%n")
     * @return A string of '=' characters matching the total width
     */
    public static String generateSeparator(String formatString) {
        int totalWidth = calculateWidthFromFormat(formatString);
        return "=".repeat(totalWidth);
    }

    /**
     * Generates a separator line with '-' characters matching the total width of columns
     * based on a printf format string.
     * 
     * @param formatString Printf format string (e.g., "%-30s %-30s %15s%n")
     * @return A string of '-' characters matching the total width
     */
    public static String generateDashedSeparator(String formatString) {
        int totalWidth = calculateWidthFromFormat(formatString);
        return "-".repeat(totalWidth);
    }

    /**
     * Calculates the total width from a printf format string.
     * Parses format specifiers like %-30s, %15s, %,15d, etc.
     * 
     * @param formatString Printf format string
     * @return Total width of all columns plus spacing
     */
    private static int calculateWidthFromFormat(String formatString) {
        // Pattern to match printf width specifiers: %[-]?[,]?(\d+)[sdfl]
        Pattern pattern = Pattern.compile("%-?[,]?(\\d+)[sdfln]");
        Matcher matcher = pattern.matcher(formatString);
        
        int totalWidth = 0;
        int columnCount = 0;
        
        while (matcher.find()) {
            int width = Integer.parseInt(matcher.group(1));
            totalWidth += width;
            columnCount++;
        }
        
        // Add spacing between columns (n-1 spaces for n columns)
        if (columnCount > 1) {
            totalWidth += (columnCount - 1);
        }
        
        return totalWidth;
    }

    /**
     * Generates a separator line with '=' characters matching the total width of columns.
     * 
     * @param columnWidths Array of column widths (positive for left-align, negative for right-align)
     * @return A string of '=' characters matching the total width
     * @deprecated Use {@link #generateSeparator(String)} instead
     */
    @Deprecated
    public static String generateSeparator(int... columnWidths) {
        int totalWidth = calculateTotalWidth(columnWidths);
        return "=".repeat(totalWidth);
    }

    /**
     * Generates a separator line with '-' characters matching the total width of columns.
     * 
     * @param columnWidths Array of column widths (positive for left-align, negative for right-align)
     * @return A string of '-' characters matching the total width
     * @deprecated Use {@link #generateDashedSeparator(String)} instead
     */
    @Deprecated
    public static String generateDashedSeparator(int... columnWidths) {
        int totalWidth = calculateTotalWidth(columnWidths);
        return "-".repeat(totalWidth);
    }

    /**
     * Calculates the total width including all columns and spacing between them.
     * Assumes one space between each column.
     * 
     * @param columnWidths Array of column widths (negative values are treated as absolute)
     * @return Total width of all columns plus spacing
     */
    private static int calculateTotalWidth(int... columnWidths) {
        int total = 0;
        for (int width : columnWidths) {
            total += Math.abs(width);
        }
        // Add spacing between columns (n-1 spaces for n columns)
        if (columnWidths.length > 1) {
            total += (columnWidths.length - 1);
        }
        return total;
    }
}
