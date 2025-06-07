import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GlobalCorruptionCommandDriven {

    static class CountryData {
        int score;
        String country;
        int rank;

        CountryData(int score, String country, int rank) {
            this.score = score;
            this.country = country;
            this.rank = rank;
        }
    }

    // Read CSV file, return list
    public static ArrayList<CountryData> readCSV(String filepath) {
        ArrayList<CountryData> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    try {
                        int score = Integer.parseInt(parts[0].trim());
                        String country = parts[1].trim();
                        int rank = Integer.parseInt(parts[2].trim());
                        list.add(new CountryData(score, country, rank));
                    } catch (NumberFormatException e) {
                        // skip invalid row
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: "+ e.getMessage());
        }
        return list;
    }

    // Bubble Sort by field; order=true means ascending, false descending
    public static void bubbleSort(ArrayList<CountryData> list, String field, boolean ascending) {
        int n = list.size();
        boolean swapped;

        for (int i = 0; i < n -1; i++) {
            swapped = false;
            for (int j = 0; j < n - i -1; j++) {
                boolean condition = false;
                CountryData left = list.get(j);
                CountryData right = list.get(j+1);
                switch (field) {
                    case "score":
                        condition = ascending ? left.score > right.score : left.score < right.score;
                        break;
                    case "rank":
                        condition = ascending ? left.rank > right.rank : left.rank < right.rank;
                        break;
                    case "country":
                        // For country string alphabetical order ascending or descending
                        int comp = left.country.compareToIgnoreCase(right.country);
                        condition = ascending ? comp > 0 : comp < 0;
                        break;
                    default:
                        // default to score descending
                        condition = ascending ? left.score > right.score : left.score < right.score;
                        break;
                }
                if (condition) {
                    CountryData temp = list.get(j);
                    list.set(j, list.get(j+1));
                    list.set(j+1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break; // already sorted
        }
    }

    // Linear search by country (case insensitive)
    public static int linearSearchCountry(ArrayList<CountryData> list, String target) {
        target = target.toLowerCase();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).country.toLowerCase().equals(target)) {
                return i;
            }
        }
        return -1;
    }

    // Print records with fields set
    public static void printRecord(CountryData c, Set<String> fields) {
        StringBuilder sb = new StringBuilder();
        if (fields.contains("country")) sb.append(String.format("Country: %-25s  ", c.country));
        if (fields.contains("score")) sb.append(String.format("Score: %3d  ", c.score));
        if (fields.contains("rank")) sb.append(String.format("Rank: %3d  ", c.rank));
        System.out.println(sb.toString().trim());
    }

    public static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  sort by [score|rank|country] [asc|desc]  - Sort data by field and order");
        System.out.println("  search country [country_name]             - Search for a country by name");
        System.out.println("  show fields [country] [score] [rank]     - Choose which fields to display");
        System.out.println("  show top [number]                         - Show top N records (default 10)");
        System.out.println("  help                                     - Show this help message");
        System.out.println("  exit                                     - Exit the program");
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Update CSV path accordingly
        String filePath = "C:\\Users\\M S I\\Downloads\\globalCorruption\\Corruption Data.csv";
        ArrayList<CountryData> dataList = readCSV(filePath);
        if (dataList.isEmpty()) {
            System.out.println("No data loaded. Please verify CSV file path and content.");
            scanner.close();
            return;
        }

        // Defaults
        Set<String> displayFields = new HashSet<>();
        displayFields.add("country");
        displayFields.add("score");
        displayFields.add("rank");
        int topN = 10;
        String currentSortField = "score";
        boolean ascending = false; // default descending by score

        System.out.println("Loaded " + dataList.size() + " records from CSV file.");
        System.out.println("Type 'help' to see available commands.\n");

        // Initial sort
        bubbleSort(dataList, currentSortField, ascending);

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");

            String cmd = parts[0].toLowerCase();

            if (cmd.equals("exit")) {
                System.out.println("Exiting program.");
                break;
            } else if (cmd.equals("help")) {
                printHelp();
            } else if (cmd.equals("sort")) {
                if (parts.length >= 4 && parts[1].equalsIgnoreCase("by")) {
                    String field = parts[2].toLowerCase();
                    String order = parts[3].toLowerCase();
                    if (!(field.equals("score") || field.equals("rank") || field.equals("country"))) {
                        System.out.println("Unknown sort field: " + field);
                        continue;
                    }
                    if (!(order.equals("asc") || order.equals("desc"))) {
                        System.out.println("Order must be 'asc' or 'desc'.");
                        continue;
                    }
                    ascending = order.equals("asc");
                    currentSortField = field;
                    bubbleSort(dataList, currentSortField, ascending);
                    System.out.println("Sorted by " + currentSortField + " in " + order + " order.");
                } else {
                    System.out.println("Invalid sort command. Usage: sort by [score|rank|country] [asc|desc]");
                }
            } else if (cmd.equals("search")) {
                if (parts.length >= 3 && parts[1].equalsIgnoreCase("country")) {
                    String countryName = line.substring(line.toLowerCase().indexOf("country") + 7).trim();
                    if (countryName.isEmpty()) {
                        System.out.println("Please provide a country name to search.");
                        continue;
                    }
                    int idx = linearSearchCountry(dataList, countryName);
                    if (idx == -1) {
                        System.out.println("Country '" + countryName + "' not found.");
                    } else {
                        System.out.println("Search result:");
                        printRecord(dataList.get(idx), displayFields);
                    }
                } else {
                    System.out.println("Invalid search command. Usage: search country [country_name]");
                }
            } else if (cmd.equals("show")) {
                if (parts.length >= 2) {
                    if (parts[1].equalsIgnoreCase("fields")) {
                        if (parts.length < 3) {
                            System.out.println("Usage: show fields [country] [score] [rank]");
                            continue;
                        }
                        Set<String> newFields = new HashSet<>();
                        boolean valid = false;
                        for (int i = 2; i < parts.length; i++) {
                            String f = parts[i].toLowerCase();
                            if (f.equals("country") || f.equals("score") || f.equals("rank")) {
                                newFields.add(f);
                                valid = true;
                            } else {
                                System.out.println("Unknown field: " + f);
                            }
                        }
                        if (!valid) {
                            System.out.println("No valid fields specified. Fields remain unchanged.");
                        } else {
                            displayFields = newFields;
                            System.out.println("Display fields set to: " + displayFields);
                        }
                    } else if (parts[1].equalsIgnoreCase("top")) {
                        if (parts.length >= 3) {
                            try {
                                int n = Integer.parseInt(parts[2]);
                                if (n <= 0) {
                                    System.out.println("Number must be positive.");
                                    continue;
                                }
                                topN = n;
                                System.out.println("Top N set to " + topN);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number format.");
                                continue;
                            }
                        }
                        System.out.println("Top " + topN + " records:");
                        for (int i = 0; i < Math.min(topN, dataList.size()); i++) {
                            printRecord(dataList.get(i), displayFields);
                        }
                    } else {
                        System.out.println("Unknown show command: " + parts[1]);
                    }
                } else {
                    System.out.println("Incomplete command. Usage examples:");
                    System.out.println("  show fields country score");
                    System.out.println("  show top 10");
                }
            } else {
                System.out.println("Unknown command. Type 'help' to see available commands.");
            }
        }

        scanner.close();
    }
}

