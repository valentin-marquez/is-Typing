package com.nozz.it.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTomlConfig {
    private final Map<String, Map<String, String>> sections = new LinkedHashMap<>();
    private final Map<String, String> rootSection = new LinkedHashMap<>();

    // Regex for basic TOML parsing
    private static final Pattern SECTION_PATTERN = Pattern.compile("^\\[(.*?)\\]$");
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^([\\w\\-]+)\\s*=\\s*(.*)$");

    public void load(File file) {
        sections.clear();
        rootSection.clear();

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            Map<String, String> currentSection = rootSection;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                Matcher sectionMatcher = SECTION_PATTERN.matcher(line);
                if (sectionMatcher.matches()) {
                    String sectionName = sectionMatcher.group(1);
                    currentSection = sections.computeIfAbsent(sectionName, k -> new LinkedHashMap<>());
                    continue;
                }

                Matcher kvMatcher = KEY_VALUE_PATTERN.matcher(line);
                if (kvMatcher.matches()) {
                    String key = kvMatcher.group(1).trim();
                    String value = kvMatcher.group(2).trim();
                    // Remove optional quotes for strings
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    currentSection.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(File file) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // Write root section first
            for (Map.Entry<String, String> entry : rootSection.entrySet()) {
                writeKv(writer, entry.getKey(), entry.getValue());
            }
            if (!rootSection.isEmpty()) writer.newLine();

            // Write other sections
            for (Map.Entry<String, Map<String, String>> section : sections.entrySet()) {
                writer.write("[" + section.getKey() + "]");
                writer.newLine();
                for (Map.Entry<String, String> entry : section.getValue().entrySet()) {
                    writeKv(writer, entry.getKey(), entry.getValue());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeKv(BufferedWriter writer, String key, String value) throws IOException {
        writer.write(key + " = " + value);
        writer.newLine();
    }

    public void set(String section, String key, Object value) {
        String strVal = String.valueOf(value);
        if (value instanceof String && !strVal.startsWith("\"")) {
             // For strings that look like hex codes or regular text, we can quote them if needed, 
             // but for simplicity in this basic parser we'll just write them. 
             // Ideally we should escape specific chars.
             // For now, let's wrap in quotes if it contains spaces or special chars, 
             // but our current save logic doesn't strictly enforce quoting on write, 
             // the parser handles unquoted values if they are simple.
             // However, to be safe and TOML compliant-ish, let's quote strings if they aren't numbers/booleans.
             if (!isNumeric(strVal) && !strVal.equalsIgnoreCase("true") && !strVal.equalsIgnoreCase("false")) {
                 strVal = "\"" + strVal + "\"";
             }
        }
        
        if (section == null || section.isEmpty()) {
            rootSection.put(key, strVal);
        } else {
            sections.computeIfAbsent(section, k -> new LinkedHashMap<>()).put(key, strVal);
        }
    }

    public String get(String section, String key, String defaultValue) {
        Map<String, String> sec = (section == null || section.isEmpty()) ? rootSection : sections.get(section);
        if (sec == null) return defaultValue;
        return sec.getOrDefault(key, defaultValue);
    }
    
    public int getInt(String section, String key, int defaultValue) {
        String val = get(section, key, null);
        if (val == null) return defaultValue;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultValue; }
    }

    public double getDouble(String section, String key, double defaultValue) {
        String val = get(section, key, null);
        if (val == null) return defaultValue;
        try { return Double.parseDouble(val); } catch (NumberFormatException e) { return defaultValue; }
    }

    public boolean getBoolean(String section, String key, boolean defaultValue) {
        String val = get(section, key, null);
        if (val == null) return defaultValue;
        return Boolean.parseBoolean(val);
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
