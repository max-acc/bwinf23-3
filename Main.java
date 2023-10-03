/*
"Aufgabe 3 - Sudokopie"

@author Max Wenk
@date 20/11/2022

Note: The input file has to be a text.txt file inside the sub folder called "sudoku.txt".
The numbers have to be separated by whitespace and a linebreak.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        // --- Reading the input file and writing it into a string array -----------------------------------------------
        System.out.println("Reading the text file ...");
        String path = "sudoku.txt";
        int[][] inputFile = readFile(path);

        // --- Converting the input data -------------------------------------------------------------------------------
        // --- Writing the input as 9x9 matrix (like a sudoku)
        int[][] oldSudoku = new int[9][9];
        int[][] newSudoku = new int[9][9];
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                oldSudoku[x][y] = inputFile[x][y];
            }
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                newSudoku[x][y] = inputFile[x + 10][y]; // +10 because the second sudoku is stored below the first one 25
            }
        }

        // --- Converting the matrix type
        int[][][][] oldSudoku3 = sudoku9to3(oldSudoku);
        int[][][][] newSudoku3 = sudoku9to3(newSudoku);

        // --- Checking for renaming of numbers ------------------------------------------------------------------------
        int[][] countNumberOccurrencesOld = new int[9][2];
        int[][] countNumberOccurrencesNew = new int[9][2];
        for (int i = 0; i < 9; i++) {
            countNumberOccurrencesOld[i][0] = i+1;
            countNumberOccurrencesNew[i][0] = i+1;
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (oldSudoku[x][y] != 0) {
                    countNumberOccurrencesOld[oldSudoku[x][y] - 1][1]++;
                }
                if (newSudoku[x][y] != 0) {
                    countNumberOccurrencesNew[newSudoku[x][y] - 1][1]++;
                }
            }
        }

        // --- Calculating possible renamed numbers
        int counterTemp = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (countNumberOccurrencesOld[i][1] == countNumberOccurrencesNew[j][1]) {
                    counterTemp++;
                }
            }
        }

        int possibleNumberSwap = counterTemp;
        counterTemp = 0;
        int[][] possibleNumberSwapSave = new int[possibleNumberSwap][2];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (countNumberOccurrencesOld[i][1] == countNumberOccurrencesNew[j][1]) {
                    possibleNumberSwapSave[counterTemp][0] = countNumberOccurrencesOld[i][0];
                    possibleNumberSwapSave[counterTemp][1] = countNumberOccurrencesNew[j][0];
                    counterTemp++;
                }
            }
        }
        System.out.println("Numbers which cannot be converted to itself have to be changed!\n");
        for (int i = 0; i < possibleNumberSwap; i++) {
            System.out.println("Number swap from " + possibleNumberSwapSave[i][0] + " to " + possibleNumberSwapSave[i][1] + " is possible");
        }


        System.out.print("\n");
        // --- Checking for block permutations -------------------------------------------------------------------------
        System.out.println("Checking for possible block permutations ...");
        // --- Setting up calculation variables
        int[][] oldNumber3x3        = number3x3(oldSudoku3);          // Number of the numbers per 3x3 area
        int[][] oldNumber3x3Value   = number3x3Value(oldSudoku3);     // Sum of the numbers per 3x3 area
        int[][] newNumber3x3        = number3x3(newSudoku3);
        int[][] newNumber3x3Value   = number3x3Value(newSudoku3);

        // --- Calculating values for row block permutations
        boolean rowBlockPermutations        = blockPermutationCalcBool("row", oldNumber3x3, newNumber3x3);
        int rowBlockPermutationsNumber      = blockPermutationCalcInt("row", oldNumber3x3, newNumber3x3);
        int[][] rowBlockPermutationsSave    = blockPermutationCalcMatrix("row", oldNumber3x3, newNumber3x3);

        if (rowBlockPermutations) {
            System.out.println("Row block permutations are possible, maybe required ");
        } else {
            System.out.println("Row block permutations are not possible!");
        }

        // --- Calculating values for column block permutations
        boolean columnBlockPermutations        = blockPermutationCalcBool("column", oldNumber3x3, newNumber3x3);
        int columnBlockPermutationsNumber      = blockPermutationCalcInt("column", oldNumber3x3, newNumber3x3);
        int[][] columnBlockPermutationsSave    = blockPermutationCalcMatrix("column", oldNumber3x3, newNumber3x3);

        if (columnBlockPermutations) {
            System.out.println("Column block permutations are possible, maybe required ");
        } else {
            System.out.println("Column block permutations are not possible!");
        }

        // --- Verifying the already calculated possible permutations
        if (rowBlockPermutations && columnBlockPermutations) {
            System.out.println("Verifying possible block permutations ...");
            // Check if the possible permutations are valid
            rowBlockPermutations        = blockPermutationVerificationBool("row", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, rowBlockPermutations, rowBlockPermutationsNumber, rowBlockPermutationsSave);
            rowBlockPermutationsNumber  = blockPermutationVerificationInt("row", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, rowBlockPermutations, rowBlockPermutationsNumber, rowBlockPermutationsSave);
            rowBlockPermutationsSave    = blockPermutationVerificationMatrix("row", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, rowBlockPermutations, rowBlockPermutationsNumber, rowBlockPermutationsSave);

            columnBlockPermutations         = blockPermutationVerificationBool("column", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, columnBlockPermutations, columnBlockPermutationsNumber, columnBlockPermutationsSave);
            columnBlockPermutationsNumber   = blockPermutationVerificationInt("column", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, columnBlockPermutations, columnBlockPermutationsNumber, columnBlockPermutationsSave);
            columnBlockPermutationsSave     = blockPermutationVerificationMatrix("column", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, columnBlockPermutations, columnBlockPermutationsNumber, columnBlockPermutationsSave);


        } else if (rowBlockPermutations && !columnBlockPermutations) {
            System.out.println("Verifying possible block permutations ...");
            rowBlockPermutations        = blockPermutationVerificationBool("row", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, rowBlockPermutations, rowBlockPermutationsNumber, rowBlockPermutationsSave);
            rowBlockPermutationsNumber  = blockPermutationVerificationInt("row", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, rowBlockPermutations, rowBlockPermutationsNumber, rowBlockPermutationsSave);
            rowBlockPermutationsSave    = blockPermutationVerificationMatrix("row", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, rowBlockPermutations, rowBlockPermutationsNumber, rowBlockPermutationsSave);

        } else if (!rowBlockPermutations && columnBlockPermutations) {
            System.out.println("Verifying possible block permutations ...");
            columnBlockPermutations         = blockPermutationVerificationBool("column", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, columnBlockPermutations, columnBlockPermutationsNumber, columnBlockPermutationsSave);
            columnBlockPermutationsNumber   = blockPermutationVerificationInt("column", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, columnBlockPermutations, columnBlockPermutationsNumber, columnBlockPermutationsSave);
            columnBlockPermutationsSave     = blockPermutationVerificationMatrix("column", oldNumber3x3, newNumber3x3, oldNumber3x3Value, newNumber3x3Value, columnBlockPermutations, columnBlockPermutationsNumber, columnBlockPermutationsSave);

        }

        if (rowBlockPermutations || columnBlockPermutations) {
            if (rowBlockPermutations) {
                System.out.println("Row block permutation");
                for (int i = 0; i < rowBlockPermutationsNumber; i++) {
                    System.out.print(rowBlockPermutationsSave[i][0]);
                    System.out.println(rowBlockPermutationsSave[i][1]);
                }

            }
            if (columnBlockPermutations) {
                System.out.println("Column block permutation");
                for (int i = 0; i < columnBlockPermutationsNumber; i++) {
                    System.out.print(columnBlockPermutationsSave[i][0]);
                    System.out.println(columnBlockPermutationsSave[i][1]);
                }

            }
        } else if (!rowBlockPermutations && !columnBlockPermutations) {
            System.out.println("\nNo block permutations are possible!");
            System.out.println("Either there are no possible permutations at all, \nor if there are only permutationsin one direction, they have to be directly possible.");
        }


        // --- Checking for line permutations --------------------------------------------------------------------------
        System.out.println("\n\nChecking for possible line permutations ...");
        // --- Setting up calculation variables
        int[] oldNumberRow      = number1x9(oldSudoku, "row");        // Number of the numbers per row
        int[] oldNumberRowValue = number1x9Value(oldSudoku, "row");   // Sum of the numbers per row
        int[] oldNumberColumn       = number1x9(oldSudoku, "column");
        int[] oldNumberColumnValue  = number1x9Value(oldSudoku, "column");
        int[] newNumberRow      = number1x9(newSudoku, "row");
        int[] newNumberRowValue = number1x9Value(newSudoku, "row");
        int[] newNumberColumn       = number1x9(newSudoku, "column");
        int[] newNumberColumnValue  = number1x9Value(newSudoku, "column");

        // --- Calculating values for row permutations
        oldNumberRow      = number1x9(oldSudoku, "row");
        oldNumberRowValue = number1x9Value(oldSudoku, "row");
        newNumberRow      = number1x9(newSudoku, "row");
        newNumberRowValue = number1x9Value(newSudoku, "row");
        int rowPermutationNumber   = permutationCalc(oldNumberRow, oldNumberRowValue, newNumberRow, newNumberRowValue);
        int[][] rowPermutationSave = permutationCalcMatrix(oldNumberRow, oldNumberRowValue, newNumberRow, newNumberRowValue);;

        int tempSum = 0;
        for (int i = 0; i < rowPermutationNumber; i++) {
            // System.out.println(rowPermutationSave[i][2]);
            tempSum += rowPermutationSave[i][2];
        }
        if (tempSum != rowPermutationNumber) {
            // System.out.println(tempSum);
            System.out.println("Result can't be achieved with only row permutation");
        }

        // --- Calculating values for column permutations
        oldNumberColumn      = number1x9(oldSudoku, "column");
        oldNumberColumnValue = number1x9Value(oldSudoku, "column");
        newNumberColumn      = number1x9(newSudoku, "column");
        newNumberColumnValue = number1x9Value(newSudoku, "column");
        int columnPermutationNumber   = permutationCalc(oldNumberColumn, oldNumberColumnValue, newNumberColumn, newNumberColumnValue);
        int[][] columnPermutationSave = permutationCalcMatrix(oldNumberColumn, oldNumberColumnValue, newNumberColumn, newNumberColumnValue);;

        tempSum = 0;
        for (int i = 0; i < columnPermutationNumber; i++) {
            // System.out.println(columnPermutationSave[i][2]);
            tempSum += columnPermutationSave[i][2];
        }
        if (tempSum != columnPermutationNumber) {
            // System.out.println(tempSum);
            System.out.println("Result can't be achieved with only column permutation");
        }






    }

    // --- Functions ---------------------------------------------------------------------------------------------------
    // --- Function for reading the input file
    public static int[][] readFile (String path) throws Exception {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        // Reading and counting the lines in the text document for creating an array to save the input information
        int rowCount = 0;
        while (sc.hasNextLine()) {
            rowCount++;
            String line = sc.nextLine();
        }
        int[][] textfile = new int[rowCount][9];
        sc = new Scanner(file);
        for (int i = 0; i < rowCount; i++) {
            String str = sc.nextLine();
            String[] splitStr = str.split("\\s+");  // Separates the input by whitespace
            if (splitStr.length == 9) {
                for (int j = 0; j < 9; j++) {
                    textfile[i][j] = Integer.parseInt(splitStr[j]); // The input is saved in a string and has to be parsed to an integer for further calculations
                }
            } else if (splitStr.length == 1) {
                continue;
            } else {
                System.out.println("An error occurred while reading the input file");
            }
        }
        sc.close();

        return textfile;
    }

    // --- Printing a sudoku with a given size (size*size)
    public static void printSudoku (int[][] input, int size ) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                System.out.print(input[x][y] + " ");
            }
            System.out.println();
        }
    }

    // --- Converting a 9x9 matrix to a 3x3 by 3x3 matrix
    public static int[][][][] sudoku9to3 (int[][] sudoku9) {
        int[][][][] sudoku3 = new int[3][3][3][3];
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if ((0 <= x && x < 3) && (0 <= y && y < 3)) {
                    sudoku3[0][0][x][y] = sudoku9[x][y];
                }else if ((0 <= x && x < 3) && (3 <= y && y < 6)) {
                    sudoku3[0][1][x][y-3] = sudoku9[x][y];
                }else if ((0 <= x && x < 3) && (6 <= y && y < 9)) {
                    sudoku3[0][2][x][y-6] = sudoku9[x][y];
                }

                else if((3 <= x && x < 6) && (0 <= y && y < 3)) {
                    sudoku3[1][0][x-3][y] = sudoku9[x][y];
                }else if ((3 <= x && x < 6) && (3 <= y && y < 6)) {
                    sudoku3[1][1][x-3][y-3] = sudoku9[x][y];
                }else if ((3 <= x && x < 6) && (6 <= y && y < 9)) {
                    sudoku3[1][2][x-3][y-6] = sudoku9[x][y];
                }

                else if((6 <= x && x < 9) && (0 <= y && y < 3)) {
                    sudoku3[2][0][x-6][y] = sudoku9[x][y];
                }else if ((6 <= x && x < 9) && (3 <= y && y < 6)) {
                    sudoku3[2][1][x-6][y-3] = sudoku9[x][y];
                }else if ((6 <= x && x < 9) && (6 <= y && y < 9)) {
                    sudoku3[2][2][x-6][y-6] = sudoku9[x][y];
                }
            }
        }
        return sudoku3;
    }

    // --- Turning a 3x3 matrix 90 deg clock wise
    public static int[][] turnMatrix90degCW (int[][] matrix) {
        int[][] result = new int[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                result[y][2-x] = matrix[x][y];
            }
        }
        return result;
    }

    public static int[][] number3x3 (int[][][][] sudoku3x3) {
        int[][] sudoku3Number = new int[3][3];
        for (int x1 = 0; x1 < 3; x1++) {
            for (int y1 = 0; y1 < 3; y1++) {
                int counter = 0;
                for (int x2 = 0; x2 < 3; x2++) {
                    for (int y2 = 0; y2 < 3; y2++) {
                        if (sudoku3x3[x1][y1][x2][y2] != 0) {
                            counter++;
                        }
                    }
                }
                sudoku3Number[x1][y1] = counter;
            }
        }
        return sudoku3Number;
    }

    public static int[][] number3x3Value (int[][][][] sudoku3x3) {
        int[][] sudoku3Value = new int[3][3];
        for (int x1 = 0; x1 < 3; x1++) {
            for (int y1 = 0; y1 < 3; y1++) {
                for (int x2 = 0; x2 < 3; x2++) {
                    for (int y2 = 0; y2 < 3; y2++) {
                        if (sudoku3x3[x1][y1][x2][y2] != 0) {
                            sudoku3Value[x1][y1] += sudoku3x3[x1][y1][x2][y2];
                        }
                    }
                }
            }
        }
        return sudoku3Value;
    }

    public static int[] number1x9 (int[][] matrix, String type) {
        int[] output = new int[9];
        if (type == "row") {
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    if (matrix[x][y] != 0) {
                        output[x]++;
                    }
                }
            }
        }else if (type == "column") {
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if (matrix[x][y] != 0) {
                        output[y]++;
                    }
                }
            }
        }else {
            System.out.println("Some stupid error");
        }
        return output;
    }

    public static int[] number1x9Value (int[][] matrix, String type) {
        int[] output = new int[9];
        if (type == "row") {
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    if (matrix[x][y] != 0) {
                        output[x] += matrix[x][y];
                    }
                }
            }
        }else if (type == "column") {
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if (matrix[x][y] != 0) {
                        output[y] += matrix[x][y];
                    }
                }
            }
        }else {
            System.out.println("Some stupid error");
        }
        return output;
    }

    public static boolean blockPermutationCalcBool (String type, int[][] oldNumber3x3, int[][] newNumber3x3) {
        boolean permutation = false;
        int numberOfPermutations = 0;
        int[][] permutationSave = new int[3][2];
        if (type == "row") {
            for (int x1 = 0; x1 < 3; x1++) {
                ArrayList<Integer> tempRowOld = new ArrayList<>();
                tempRowOld.add(oldNumber3x3[x1][0]);
                tempRowOld.add(oldNumber3x3[x1][1]);
                tempRowOld.add(oldNumber3x3[x1][2]);
                for (int x2 = x1; x2 < 3; x2++) {
                    ArrayList<Integer> tempRowNew = new ArrayList<>();
                    tempRowNew.add(newNumber3x3[x2][0]);
                    tempRowNew.add(newNumber3x3[x2][1]);
                    tempRowNew.add(newNumber3x3[x2][2]);

                    if (x1 == x2) {
                        continue;
                    }

                    if (tempRowOld.containsAll(tempRowNew) && tempRowNew.containsAll(tempRowOld)) {
                        permutationSave[numberOfPermutations][0] = x1;
                        permutationSave[numberOfPermutations][1] = x2;
                        numberOfPermutations++;
                        permutation = true;
                    }
                }
            }
        }else if (type == "column") {
            for (int y1 = 0; y1 < 3; y1++) {
                int sumOldX =  oldNumber3x3[0][y1] + oldNumber3x3[1][y1] + oldNumber3x3[2][y1];
                ArrayList<Integer> tempColumnOld = new ArrayList<>();
                tempColumnOld.add(oldNumber3x3[0][y1]);
                tempColumnOld.add(oldNumber3x3[1][y1]);
                tempColumnOld.add(oldNumber3x3[2][y1]);
                for (int y2 = y1; y2 < 3; y2++) {
                    ArrayList<Integer> tempColumnNew = new ArrayList<>();
                    tempColumnNew.add(newNumber3x3[0][y2]);
                    tempColumnNew.add(newNumber3x3[1][y2]);
                    tempColumnNew.add(newNumber3x3[2][y2]);

                    int sumNewX =  newNumber3x3[0][y2] + newNumber3x3[1][y2] + newNumber3x3[2][y2];
                    if (y1 == y2) {
                        continue;
                    }

                    if (tempColumnOld.containsAll(tempColumnNew) && tempColumnNew.containsAll(tempColumnOld)) {
                        permutationSave[numberOfPermutations][0] = y1;
                        permutationSave[numberOfPermutations][1] = y2;
                        numberOfPermutations++;
                        permutation = true;
                    }
                }
            }
        }else {
            System.out.println("Type error!");
        }
        return permutation;
    }

    public static int blockPermutationCalcInt (String type, int[][] oldNumber3x3, int[][] newNumber3x3) {
        boolean permutation = false;
        int numberOfPermutations = 0;
        int[][] permutationSave = new int[3][2];
        if (type == "row") {
            for (int x1 = 0; x1 < 3; x1++) {
                ArrayList<Integer> tempRowOld = new ArrayList<>();
                tempRowOld.add(oldNumber3x3[x1][0]);
                tempRowOld.add(oldNumber3x3[x1][1]);
                tempRowOld.add(oldNumber3x3[x1][2]);
                for (int x2 = x1; x2 < 3; x2++) {
                    ArrayList<Integer> tempRowNew = new ArrayList<>();
                    tempRowNew.add(newNumber3x3[x2][0]);
                    tempRowNew.add(newNumber3x3[x2][1]);
                    tempRowNew.add(newNumber3x3[x2][2]);

                    if (x1 == x2) {
                        continue;
                    }

                    if (tempRowOld.containsAll(tempRowNew) && tempRowNew.containsAll(tempRowOld)) {
                        permutationSave[numberOfPermutations][0] = x1;
                        permutationSave[numberOfPermutations][1] = x2;
                        numberOfPermutations++;
                        permutation = true;
                    }
                }
            }
        }else if (type == "column") {
            for (int y1 = 0; y1 < 3; y1++) {
                int sumOldX =  oldNumber3x3[0][y1] + oldNumber3x3[1][y1] + oldNumber3x3[2][y1];
                ArrayList<Integer> tempColumnOld = new ArrayList<>();
                tempColumnOld.add(oldNumber3x3[0][y1]);
                tempColumnOld.add(oldNumber3x3[1][y1]);
                tempColumnOld.add(oldNumber3x3[2][y1]);
                for (int y2 = y1; y2 < 3; y2++) {
                    ArrayList<Integer> tempColumnNew = new ArrayList<>();
                    tempColumnNew.add(newNumber3x3[0][y2]);
                    tempColumnNew.add(newNumber3x3[1][y2]);
                    tempColumnNew.add(newNumber3x3[2][y2]);

                    int sumNewX =  newNumber3x3[0][y2] + newNumber3x3[1][y2] + newNumber3x3[2][y2];
                    if (y1 == y2) {
                        continue;
                    }

                    if (tempColumnOld.containsAll(tempColumnNew) && tempColumnNew.containsAll(tempColumnOld)) {
                        permutationSave[numberOfPermutations][0] = y1;
                        permutationSave[numberOfPermutations][1] = y2;
                        numberOfPermutations++;
                        permutation = true;
                    }
                }
            }
        }else {
            System.out.println("Type error!");
        }
        return numberOfPermutations;
    }

    public static int[][] blockPermutationCalcMatrix (String type, int[][] oldNumber3x3, int[][] newNumber3x3) {
        boolean permutation = false;
        int numberOfPermutations = 0;
        int[][] permutationSave = new int[3][2];
        if (type == "row") {
            for (int x1 = 0; x1 < 3; x1++) {
                ArrayList<Integer> tempRowOld = new ArrayList<>();
                tempRowOld.add(oldNumber3x3[x1][0]);
                tempRowOld.add(oldNumber3x3[x1][1]);
                tempRowOld.add(oldNumber3x3[x1][2]);
                for (int x2 = x1; x2 < 3; x2++) {
                    ArrayList<Integer> tempRowNew = new ArrayList<>();
                    tempRowNew.add(newNumber3x3[x2][0]);
                    tempRowNew.add(newNumber3x3[x2][1]);
                    tempRowNew.add(newNumber3x3[x2][2]);

                    if (x1 == x2) {
                        continue;
                    }

                    if (tempRowOld.containsAll(tempRowNew) && tempRowNew.containsAll(tempRowOld)) {
                        permutationSave[numberOfPermutations][0] = x1;
                        permutationSave[numberOfPermutations][1] = x2;
                        numberOfPermutations++;
                        permutation = true;
                    }
                }
            }
        }else if (type == "column") {
            for (int y1 = 0; y1 < 3; y1++) {
                int sumOldX =  oldNumber3x3[0][y1] + oldNumber3x3[1][y1] + oldNumber3x3[2][y1];
                ArrayList<Integer> tempColumnOld = new ArrayList<>();
                tempColumnOld.add(oldNumber3x3[0][y1]);
                tempColumnOld.add(oldNumber3x3[1][y1]);
                tempColumnOld.add(oldNumber3x3[2][y1]);
                for (int y2 = y1; y2 < 3; y2++) {
                    ArrayList<Integer> tempColumnNew = new ArrayList<>();
                    tempColumnNew.add(newNumber3x3[0][y2]);
                    tempColumnNew.add(newNumber3x3[1][y2]);
                    tempColumnNew.add(newNumber3x3[2][y2]);

                    int sumNewX =  newNumber3x3[0][y2] + newNumber3x3[1][y2] + newNumber3x3[2][y2];
                    if (y1 == y2) {
                        continue;
                    }

                    if (tempColumnOld.containsAll(tempColumnNew) && tempColumnNew.containsAll(tempColumnOld)) {
                        permutationSave[numberOfPermutations][0] = y1;
                        permutationSave[numberOfPermutations][1] = y2;
                        numberOfPermutations++;
                        permutation = true;
                    }
                }
            }
        }else {
            System.out.println("Type error!");
        }
        return permutationSave;
    }

    public static boolean blockPermutationVerificationBool (String type, int[][] oldNumber3x3, int[][] newNumber3x3, int[][] oldNumber3x3Value, int[][] newNumber3x3Value, boolean permutations, int permutationsNumber,  int [][] permutationsSave) {
        if (type == "row") {
            for (int i = 0; i < permutationsNumber; i++) {
                if (i < 0) {
                    break;
                }
                if (oldNumber3x3[permutationsSave[i][0]][0] == newNumber3x3[permutationsSave[i][1]][0] &&
                        oldNumber3x3[permutationsSave[i][0]][1] == newNumber3x3[permutationsSave[i][1]][1] &&
                        oldNumber3x3[permutationsSave[i][0]][2] == newNumber3x3[permutationsSave[i][1]][2] &&
                        (oldNumber3x3Value[permutationsSave[i][0]][0] +  oldNumber3x3Value[permutationsSave[i][0]][1] +  oldNumber3x3Value[permutationsSave[i][0]][2]) ==
                                (newNumber3x3Value[permutationsSave[i][1]][0] +  newNumber3x3Value[permutationsSave[i][1]][1] +  newNumber3x3Value[permutationsSave[i][1]][2]) &&
                        (oldNumber3x3Value[permutationsSave[i][1]][0] +  oldNumber3x3Value[permutationsSave[i][1]][1] +  oldNumber3x3Value[permutationsSave[i][1]][2]) ==
                                (newNumber3x3Value[permutationsSave[i][0]][0] +  newNumber3x3Value[permutationsSave[i][0]][1] +  newNumber3x3Value[permutationsSave[i][0]][2])){
                    System.out.println("Row permutation from " + permutationsSave[i][0] + " to " + permutationsSave[i][1]);
                }
                else if (oldNumber3x3[permutationsSave[i][0]][0] != newNumber3x3[permutationsSave[i][1]][0] ||
                        oldNumber3x3[permutationsSave[i][0]][1] != newNumber3x3[permutationsSave[i][1]][1] ||
                        oldNumber3x3[permutationsSave[i][0]][2] != newNumber3x3[permutationsSave[i][1]][2] ||
                        (oldNumber3x3Value[permutationsSave[i][0]][0] +  oldNumber3x3Value[permutationsSave[i][0]][1] +  oldNumber3x3Value[permutationsSave[i][0]][2]) !=
                                (newNumber3x3Value[permutationsSave[i][1]][0] +  newNumber3x3Value[permutationsSave[i][1]][1] +  newNumber3x3Value[permutationsSave[i][1]][2]) ||
                        (oldNumber3x3Value[permutationsSave[i][1]][0] +  oldNumber3x3Value[permutationsSave[i][1]][1] +  oldNumber3x3Value[permutationsSave[i][1]][2]) !=
                                (newNumber3x3Value[permutationsSave[i][0]][0] +  newNumber3x3Value[permutationsSave[i][0]][1] +  newNumber3x3Value[permutationsSave[i][0]][2])) {
                    if (permutationsNumber == 1) {
                        permutations = false;
                    }
                    if (i < 2) {
                        permutationsSave[i][0] = permutationsSave[i +1][0];
                        permutationsSave[i][1] = permutationsSave[i +1][1];
                    }else {
                        permutationsSave[i +1][0] = 0;
                        permutationsSave[i +1][1] = 0;
                    }
                    permutationsNumber--;
                    i--;
                }
            }
        }else if (type == "column") {
            for (int i = 0; i < permutationsNumber; i++) {
                if (i < 0) {
                    break;
                }
                if (oldNumber3x3[0][permutationsSave[i][0]] == newNumber3x3[0][permutationsSave[i][1]] &&
                        oldNumber3x3[1][permutationsSave[i][0]] == newNumber3x3[1][permutationsSave[i][1]] &&
                        oldNumber3x3[2][permutationsSave[i][0]] == newNumber3x3[2][permutationsSave[i][1]] &&
                        (oldNumber3x3Value[0][permutationsSave[i][0]] +  oldNumber3x3Value[1][permutationsSave[i][0]] +  oldNumber3x3Value[2][permutationsSave[i][0]]) ==
                                (newNumber3x3Value[0][permutationsSave[i][1]] +  newNumber3x3Value[1][permutationsSave[i][1]] +  newNumber3x3Value[2][permutationsSave[i][1]]) &&
                        (oldNumber3x3Value[0][permutationsSave[i][1]] +  oldNumber3x3Value[1][permutationsSave[i][1]] +  oldNumber3x3Value[2][permutationsSave[i][1]]) ==
                                (newNumber3x3Value[0][permutationsSave[i][0]] +  newNumber3x3Value[1][permutationsSave[i][0]] +  newNumber3x3Value[2][permutationsSave[i][0]])) {
                    System.out.println("Column permutation from " + permutationsSave[i][0] + " to " + permutationsSave[i][1]);
                }
                else if (oldNumber3x3[0][permutationsSave[i][1]] != newNumber3x3[0][permutationsSave[i][1]] ||
                        oldNumber3x3[1][permutationsSave[i][1]] != newNumber3x3[1][permutationsSave[i][1]] ||
                        oldNumber3x3[2][permutationsSave[i][1]] != newNumber3x3[2][permutationsSave[i][1]] ||
                        (oldNumber3x3Value[0][permutationsSave[i][0]] +  oldNumber3x3Value[1][permutationsSave[i][0]] +  oldNumber3x3Value[2][permutationsSave[i][0]]) !=
                                (newNumber3x3Value[0][permutationsSave[i][1]] +  newNumber3x3Value[1][permutationsSave[i][1]] +  newNumber3x3Value[2][permutationsSave[i][1]]) ||
                        (oldNumber3x3Value[0][permutationsSave[i][1]] +  oldNumber3x3Value[1][permutationsSave[i][1]] +  oldNumber3x3Value[2][permutationsSave[i][1]]) !=
                                (newNumber3x3Value[0][permutationsSave[i][0]] +  newNumber3x3Value[1][permutationsSave[i][0]] +  newNumber3x3Value[2][permutationsSave[i][0]])) {
                    if (permutationsNumber == 1) {
                        permutations = false;
                    }
                    if (i < 2) {
                        permutationsSave[i][0] = permutationsSave[i +1][0];
                        permutationsSave[i][1] = permutationsSave[i +1][1];
                    }else {
                        permutationsSave[i +1][0] = 0;
                        permutationsSave[i +1][1] = 0;
                    }
                    permutationsNumber--;
                    i--;
                }
            }
        }
        return permutations;
    }

    public static int blockPermutationVerificationInt (String type, int[][] oldNumber3x3, int[][] newNumber3x3, int[][] oldNumber3x3Value, int[][] newNumber3x3Value, boolean permutations, int permutationsNumber,  int [][] permutationsSave) {
        if (type == "row") {
            for (int i = 0; i < permutationsNumber; i++) {
                if (i < 0) {
                    break;
                }
                if (oldNumber3x3[permutationsSave[i][0]][0] == newNumber3x3[permutationsSave[i][1]][0] &&
                        oldNumber3x3[permutationsSave[i][0]][1] == newNumber3x3[permutationsSave[i][1]][1] &&
                        oldNumber3x3[permutationsSave[i][0]][2] == newNumber3x3[permutationsSave[i][1]][2] &&
                        (oldNumber3x3Value[permutationsSave[i][0]][0] +  oldNumber3x3Value[permutationsSave[i][0]][1] +  oldNumber3x3Value[permutationsSave[i][0]][2]) ==
                                (newNumber3x3Value[permutationsSave[i][1]][0] +  newNumber3x3Value[permutationsSave[i][1]][1] +  newNumber3x3Value[permutationsSave[i][1]][2]) &&
                        (oldNumber3x3Value[permutationsSave[i][1]][0] +  oldNumber3x3Value[permutationsSave[i][1]][1] +  oldNumber3x3Value[permutationsSave[i][1]][2]) ==
                                (newNumber3x3Value[permutationsSave[i][0]][0] +  newNumber3x3Value[permutationsSave[i][0]][1] +  newNumber3x3Value[permutationsSave[i][0]][2])){
                }
                else if (oldNumber3x3[permutationsSave[i][0]][0] != newNumber3x3[permutationsSave[i][1]][0] ||
                        oldNumber3x3[permutationsSave[i][0]][1] != newNumber3x3[permutationsSave[i][1]][1] ||
                        oldNumber3x3[permutationsSave[i][0]][2] != newNumber3x3[permutationsSave[i][1]][2] ||
                        (oldNumber3x3Value[permutationsSave[i][0]][0] +  oldNumber3x3Value[permutationsSave[i][0]][1] +  oldNumber3x3Value[permutationsSave[i][0]][2]) !=
                                (newNumber3x3Value[permutationsSave[i][1]][0] +  newNumber3x3Value[permutationsSave[i][1]][1] +  newNumber3x3Value[permutationsSave[i][1]][2]) ||
                        (oldNumber3x3Value[permutationsSave[i][1]][0] +  oldNumber3x3Value[permutationsSave[i][1]][1] +  oldNumber3x3Value[permutationsSave[i][1]][2]) !=
                                (newNumber3x3Value[permutationsSave[i][0]][0] +  newNumber3x3Value[permutationsSave[i][0]][1] +  newNumber3x3Value[permutationsSave[i][0]][2])) {
                    if (permutationsNumber == 1) {
                        permutations = false;
                    }
                    if (i < 2) {
                        permutationsSave[i][0] = permutationsSave[i +1][0];
                        permutationsSave[i][1] = permutationsSave[i +1][1];
                    }else {
                        permutationsSave[i +1][0] = 0;
                        permutationsSave[i +1][1] = 0;
                    }
                    permutationsNumber--;
                    i--;
                }
            }
        }else if (type == "column") {
            for (int i = 0; i < permutationsNumber; i++) {
                if (i < 0) {
                    break;
                }
                if (oldNumber3x3[0][permutationsSave[i][0]] == newNumber3x3[0][permutationsSave[i][1]] &&
                        oldNumber3x3[1][permutationsSave[i][0]] == newNumber3x3[1][permutationsSave[i][1]] &&
                        oldNumber3x3[2][permutationsSave[i][0]] == newNumber3x3[2][permutationsSave[i][1]] &&
                        (oldNumber3x3Value[0][permutationsSave[i][0]] +  oldNumber3x3Value[1][permutationsSave[i][0]] +  oldNumber3x3Value[2][permutationsSave[i][0]]) ==
                                (newNumber3x3Value[0][permutationsSave[i][1]] +  newNumber3x3Value[1][permutationsSave[i][1]] +  newNumber3x3Value[2][permutationsSave[i][1]]) &&
                        (oldNumber3x3Value[0][permutationsSave[i][1]] +  oldNumber3x3Value[1][permutationsSave[i][1]] +  oldNumber3x3Value[2][permutationsSave[i][1]]) ==
                                (newNumber3x3Value[0][permutationsSave[i][0]] +  newNumber3x3Value[1][permutationsSave[i][0]] +  newNumber3x3Value[2][permutationsSave[i][0]])) {
                }
                else if (oldNumber3x3[0][permutationsSave[i][1]] != newNumber3x3[0][permutationsSave[i][1]] ||
                        oldNumber3x3[1][permutationsSave[i][1]] != newNumber3x3[1][permutationsSave[i][1]] ||
                        oldNumber3x3[2][permutationsSave[i][1]] != newNumber3x3[2][permutationsSave[i][1]] ||
                        (oldNumber3x3Value[0][permutationsSave[i][0]] +  oldNumber3x3Value[1][permutationsSave[i][0]] +  oldNumber3x3Value[2][permutationsSave[i][0]]) !=
                                (newNumber3x3Value[0][permutationsSave[i][1]] +  newNumber3x3Value[1][permutationsSave[i][1]] +  newNumber3x3Value[2][permutationsSave[i][1]]) ||
                        (oldNumber3x3Value[0][permutationsSave[i][1]] +  oldNumber3x3Value[1][permutationsSave[i][1]] +  oldNumber3x3Value[2][permutationsSave[i][1]]) !=
                                (newNumber3x3Value[0][permutationsSave[i][0]] +  newNumber3x3Value[1][permutationsSave[i][0]] +  newNumber3x3Value[2][permutationsSave[i][0]])) {
                    if (permutationsNumber == 1) {
                        permutations = false;
                    }
                    if (i < 2) {
                        permutationsSave[i][0] = permutationsSave[i +1][0];
                        permutationsSave[i][1] = permutationsSave[i +1][1];
                    }else {
                        permutationsSave[i +1][0] = 0;
                        permutationsSave[i +1][1] = 0;
                    }
                    permutationsNumber--;
                    i--;
                }
            }
        }
        return permutationsNumber;
    }

    public static int[][] blockPermutationVerificationMatrix (String type, int[][] oldNumber3x3, int[][] newNumber3x3, int[][] oldNumber3x3Value, int[][] newNumber3x3Value, boolean permutations, int permutationsNumber,  int [][] permutationsSave) {
        if (type == "row") {
            for (int i = 0; i < permutationsNumber; i++) {
                if (i < 0) {
                    break;
                }
                if (oldNumber3x3[permutationsSave[i][0]][0] == newNumber3x3[permutationsSave[i][1]][0] &&
                        oldNumber3x3[permutationsSave[i][0]][1] == newNumber3x3[permutationsSave[i][1]][1] &&
                        oldNumber3x3[permutationsSave[i][0]][2] == newNumber3x3[permutationsSave[i][1]][2] &&
                        (oldNumber3x3Value[permutationsSave[i][0]][0] +  oldNumber3x3Value[permutationsSave[i][0]][1] +  oldNumber3x3Value[permutationsSave[i][0]][2]) ==
                                (newNumber3x3Value[permutationsSave[i][1]][0] +  newNumber3x3Value[permutationsSave[i][1]][1] +  newNumber3x3Value[permutationsSave[i][1]][2]) &&
                        (oldNumber3x3Value[permutationsSave[i][1]][0] +  oldNumber3x3Value[permutationsSave[i][1]][1] +  oldNumber3x3Value[permutationsSave[i][1]][2]) ==
                                (newNumber3x3Value[permutationsSave[i][0]][0] +  newNumber3x3Value[permutationsSave[i][0]][1] +  newNumber3x3Value[permutationsSave[i][0]][2])){
                }
                else if (oldNumber3x3[permutationsSave[i][0]][0] != newNumber3x3[permutationsSave[i][1]][0] ||
                        oldNumber3x3[permutationsSave[i][0]][1] != newNumber3x3[permutationsSave[i][1]][1] ||
                        oldNumber3x3[permutationsSave[i][0]][2] != newNumber3x3[permutationsSave[i][1]][2] ||
                        (oldNumber3x3Value[permutationsSave[i][0]][0] +  oldNumber3x3Value[permutationsSave[i][0]][1] +  oldNumber3x3Value[permutationsSave[i][0]][2]) !=
                                (newNumber3x3Value[permutationsSave[i][1]][0] +  newNumber3x3Value[permutationsSave[i][1]][1] +  newNumber3x3Value[permutationsSave[i][1]][2]) ||
                        (oldNumber3x3Value[permutationsSave[i][1]][0] +  oldNumber3x3Value[permutationsSave[i][1]][1] +  oldNumber3x3Value[permutationsSave[i][1]][2]) !=
                                (newNumber3x3Value[permutationsSave[i][0]][0] +  newNumber3x3Value[permutationsSave[i][0]][1] +  newNumber3x3Value[permutationsSave[i][0]][2])) {
                    if (permutationsNumber == 1) {
                        permutations = false;
                    }
                    if (i < 2) {
                        permutationsSave[i][0] = permutationsSave[i +1][0];
                        permutationsSave[i][1] = permutationsSave[i +1][1];
                    }else {
                        permutationsSave[i +1][0] = 0;
                        permutationsSave[i +1][1] = 0;
                    }
                    permutationsNumber--;
                    i--;
                }
            }
        }else if (type == "column") {
            for (int i = 0; i < permutationsNumber; i++) {
                if (i < 0) {
                    break;
                }
                if (oldNumber3x3[0][permutationsSave[i][0]] == newNumber3x3[0][permutationsSave[i][1]] &&
                        oldNumber3x3[1][permutationsSave[i][0]] == newNumber3x3[1][permutationsSave[i][1]] &&
                        oldNumber3x3[2][permutationsSave[i][0]] == newNumber3x3[2][permutationsSave[i][1]] &&
                        (oldNumber3x3Value[0][permutationsSave[i][0]] +  oldNumber3x3Value[1][permutationsSave[i][0]] +  oldNumber3x3Value[2][permutationsSave[i][0]]) ==
                                (newNumber3x3Value[0][permutationsSave[i][1]] +  newNumber3x3Value[1][permutationsSave[i][1]] +  newNumber3x3Value[2][permutationsSave[i][1]]) &&
                        (oldNumber3x3Value[0][permutationsSave[i][1]] +  oldNumber3x3Value[1][permutationsSave[i][1]] +  oldNumber3x3Value[2][permutationsSave[i][1]]) ==
                                (newNumber3x3Value[0][permutationsSave[i][0]] +  newNumber3x3Value[1][permutationsSave[i][0]] +  newNumber3x3Value[2][permutationsSave[i][0]])) {
                }
                else if (oldNumber3x3[0][permutationsSave[i][1]] != newNumber3x3[0][permutationsSave[i][1]] ||
                        oldNumber3x3[1][permutationsSave[i][1]] != newNumber3x3[1][permutationsSave[i][1]] ||
                        oldNumber3x3[2][permutationsSave[i][1]] != newNumber3x3[2][permutationsSave[i][1]] ||
                        (oldNumber3x3Value[0][permutationsSave[i][0]] +  oldNumber3x3Value[1][permutationsSave[i][0]] +  oldNumber3x3Value[2][permutationsSave[i][0]]) !=
                                (newNumber3x3Value[0][permutationsSave[i][1]] +  newNumber3x3Value[1][permutationsSave[i][1]] +  newNumber3x3Value[2][permutationsSave[i][1]]) ||
                        (oldNumber3x3Value[0][permutationsSave[i][1]] +  oldNumber3x3Value[1][permutationsSave[i][1]] +  oldNumber3x3Value[2][permutationsSave[i][1]]) !=
                                (newNumber3x3Value[0][permutationsSave[i][0]] +  newNumber3x3Value[1][permutationsSave[i][0]] +  newNumber3x3Value[2][permutationsSave[i][0]])) {
                    if (permutationsNumber == 1) {
                        permutations = false;
                    }
                    if (i < 2) {
                        permutationsSave[i][0] = permutationsSave[i +1][0];
                        permutationsSave[i][1] = permutationsSave[i +1][1];
                    }else {
                        permutationsSave[i +1][0] = 0;
                        permutationsSave[i +1][1] = 0;
                    }
                    permutationsNumber--;
                    i--;
                }
            }
        }
        return permutationsSave;
    }

    public static int permutationCalc (int[] oldNumber, int[] oldNumberValue, int[] newNumber, int[] newNumberValue) {
        int permutations = 0;
        int[][] permutationsSave = new int[18][3];

        for (int i = 0; i < 9; i+=3) {
            for (int j = i; j < i+3; j++) {
                for (int k = j; k < i+3; k++) {
                    if (oldNumber[j] == newNumber[k] &&
                            oldNumberValue[j] == newNumberValue[k] &&
                            oldNumber[k] == newNumber[j] &&
                            oldNumberValue[k] == newNumberValue[j]) {
                        permutationsSave[permutations][0] = j;
                        permutationsSave[permutations][1] = k;
                        if (j != k) {
                            permutationsSave[permutations][2] = 2;
                        }else {
                            permutationsSave[permutations][2] = 1;
                        }

                        permutations++;
                    }
                }
            }
        }
        return permutations;
    }

    public static int[][] permutationCalcMatrix (int[] oldNumber, int[] oldNumberValue, int[] newNumber, int[] newNumberValue) {
        int permutations = 0;
        int[][] permutationsSave = new int[18][3];

        for (int i = 0; i < 9; i+=3) {
            for (int j = i; j < i+3; j++) {
                for (int k = j; k < i+3; k++) {
                    if (oldNumber[j] == newNumber[k] &&
                            oldNumberValue[j] == newNumberValue[k] &&
                            oldNumber[k] == newNumber[j] &&
                            oldNumberValue[k] == newNumberValue[j]) {
                        permutationsSave[permutations][0] = j;
                        permutationsSave[permutations][1] = k;
                        if (j != k) {
                            permutationsSave[permutations][2] = 2;
                        }else {
                            permutationsSave[permutations][2] = 1;
                        }

                        permutations++;
                    }
                }
            }
        }
        return permutationsSave;
    }




}