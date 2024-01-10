import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {



        //files set up
        String[] letters = readFiles("/Users/perlielui/IdeaProjects/MineSweeper/src/letters.txt", 26);
        String[] symbols = readFiles("/Users/perlielui/IdeaProjects/MineSweeper/src/symbols.txt", 12);
        //String[] colour = readFiles("/Users/perlielui/IdeaProjects/MineSweeper/src/colour.txt", 12);
        String[] colour = new String[] {"\u001B[37m", "\u001B[34m","\u001B[32m","\u001B[31m", "\u001B[35m", "\u001B[36m", "\u001B[30m", "\u001B[37m","\u001B[37m", "\u001B[37m","\u001B[30m", "\u001B[41m"};
        Scanner sc = new Scanner(System.in);

        // Introduction
        instructions();

        //Ask User for grid input
        int length = readIntAnswer("Length (*** must be larger than 3 and less than or equal to 26):", 3, 27);
        int width = readIntAnswer("Width (*** must be larger than 3 and less than or equal to 26):", 3, 27);
        int dimensions = (length * width) / 2;
        int mines = readIntAnswer("Number of mines (must be larger than 1 and less than " + dimensions + "):", 1, dimensions);
        dimensions = length * width;

        //generate grid and mines
        String[] grid = generateEmptyGrid(length, width);
        printGrid(length, width, grid, letters, colour, symbols);

        //first circle to click on, generating mines and the answer grid
        System.out.println("Please input the first circle you would like to click on:");
        String input = sc.nextLine();
        while(!isAlphaNumeric(length, width, input, letters)){
            System.out.println("Please enter a valid index!");
            input = sc.nextLine();
        }
        int idx = readAlphaNumericInput(length, width, input, letters);
        int[] mineGrid;
        mineGrid = generateMines(length, width, idx, mines);
        generateMineAnswer(length, width, mineGrid);
        grid = click(length, width, idx, mineGrid, grid, symbols);
        //printGrid(length, width, mineGrid, letters);

        //start game
        //define all variables
        String alphanumeric;
        int unknownCircles;
        boolean playAgain = false;
        boolean win = false;

        //game loop
        while(true){
            //print out the grid, the number of flags remaining, and the unknown circles remaining.
            printGrid(length, width, grid, letters, colour, symbols);
            System.out.println("Flags remaining: " + mines);
            unknownCircles = 0;
            for(String s : grid){
                if(s == "ⓞ"){
                    unknownCircles++;
                }
            }
            System.out.println("Unknown circles remaining: " + unknownCircles);

            System.out.println("Type in the command along with the alphanumeric circle: ");
            input = sc.nextLine();

            while(!isInputValid(length, width, input, letters)){
                printGrid(length, width, grid, letters, colour, symbols);
                System.out.println("Type out an appropriate command!");
                input = sc.nextLine();
            }
            alphanumeric = input.substring(2);
            idx = readAlphaNumericInput(length, width, alphanumeric, letters);
            if(input.charAt(0) == 'f'){
                mines = flag(idx, grid, mines);
            }
            else if (input.charAt(0) == 'c'){
                grid = click(length, width, idx, mineGrid, grid, symbols);
                if(grid == null) {
                    playAgain = gameOver();
                    if (playAgain) {
                        grid = generateEmptyGrid(length, width);
                        printGrid(length, width, grid, letters, colour, symbols);
                        System.out.println("Please input the first circle you would like to click on:");
                        input = sc.nextLine();
                        while(!isAlphaNumeric(length, width, input, letters)){
                            System.out.println("Please enter a valid index!");
                            input = sc.nextLine();
                        }
                        idx = readAlphaNumericInput(length, width, input, letters);
                        mineGrid = generateMines(length, width, idx, mines);
                        generateMineAnswer(length, width, mineGrid);
                        grid = click(length, width, idx, mineGrid, grid, symbols);
                    } else {
                        break;
                    }
                }
                for(String s : grid){
                    if (s.equals("ⓞ")) {
                        win = true;
                        break;
                    }
                }
                if(win){
                    win = false;
                }
                else{
                    playAgain = gameWin();
                    if(playAgain){
                        grid = generateEmptyGrid(length, width);
                        printGrid(length, width, grid, letters, colour, symbols);
                        System.out.println("Please input the first circle you would like to click on:");
                        input = sc.nextLine();
                        while(!isAlphaNumeric(length, width, input, letters)){
                            System.out.println("Please enter a valid index!");
                            input = sc.nextLine();
                        }
                        idx = readAlphaNumericInput(length, width, input, letters);
                        mineGrid = generateMines(length, width, idx, mines);
                        generateMineAnswer(length, width, mineGrid);
                        grid = click(length, width, idx, mineGrid, grid, symbols);
                    }
                    else{
                        break;
                    }
                }
            }
        }
        System.out.println("Thank you for playing the game Minesweeper on console!");





    }
    public static void instructions(){
        Scanner sc = new Scanner(System.in);
        printWithEnter("Welcome to the game of minesweeper, but on console!");
        printWithEnter("                Hello user! Welcome to the game of minesweeper, but on console!\n" +
                "                The objective of this game is to avoid all of the mines, and click on all the safe circles!\n" +
                "                To start the game, you firstly choose your grid size, and how many mines do you want on your grid.\n" +
                "                Then, you would choose a starting circle, and the starting square will always be safe.\n" +
                "                By picking the starting circle, it would reveal a small patch of circles that are safe.\n" +
                "                There will be some numbers on the circles, e.g.\n" +
                "                ① ② ③ ④ ⑤\n" +
                "                These circles show how many mines are next to it in total diagonally, horizontally or vertically. \n" +
                "                If there are no mines right next to that circle,\n" +
                "                then it will automatically reveal the other safe circles until all circles have a number on it, or it reaches the edge. \n" +
                "                \n" +
                "                ");

        printWithEnter("                Symbol Identification:\n" +
                "                ⓞ - circle that has not been clicked or flagged\n" +
                "                ㊏ - circle that has been flagged by the user\n" +
                "                ⓪ - circle that has no mines next to it\n" +
                "                ① - circle that has 1 mine next to it\n" +
                "                ② - circle that has 2 mines next to it\n" +
                "                ③ - circle that has 3 mines next to it\n" +
                "                ④ - circle that has 4 mines next to it\n" +
                "                ⑤ - circle that has 5 mines next to it\n" +
                "                ⑥ - circle that has 6 mines next to it\n" +
                "                ⑦ - circle that has 7 mines next to it\n" +
                "                ⑧ - circle that has 8 mines next to it\n" +
                "                \n" +
                "                ");
        printWithEnter("                If the circle looks like this ⓞ, it means there are no action commands performed on it yet.\n" +
                "                You are unsure whether it is safe or not.\n" +
                "                The commands that you can perform on it are:\n" +
                "                \n" +
                "                \"c\" = click if you think it is safe \n" +
                "                * ONLY CHOOSE CLICK ON THE CIRCLE IF YOU REALLY THINK THE CIRCLE IS SAFE\n" +
                "                * If the user clicks on a circle that is a mine, THE USER LOSES THE GAME AUTOMATICALLY\n" +
                "                \n" +
                "                or\n" +
                "                \n" +
                "                \"f\" = flag if you think it is a mine\n" +
                "                * flagging the circle will change the symbol from ⓞ to ㊏\n" +
                "                * It is only an indication of the user that it is a mine, the game will not tell you if it is a mine or not.\n" +
                "                * You will not lose the game if you accidentally flag a circle that is not a mine.\n" +
                "                * You can always unflag the circle if you realise that it is not a mine by using the command \"f\"\n" +
                "                * You cannot click on circles that are flagged unless you unflag them\n" +
                "                \n" +
                "                Example of using a command\n" +
                "                f a6\n" +
                "                This means that the user wants to flag the circle a6 \n" +
                "                a6 means the alphanumeric place of the circle\n" +
                "                There will be indications of the grid markings, think of it just like a chess board. \n" +
                "                type the alphabetical letter (column) before the number (row)\n" +
                "                \n" +
                "                c d12\n" +
                "                This means that the user wants to click the circle d12 \n" +
                "                d12 means the alphanumeric place of the circle\n" +
                "                There will be indications of the grid markings, think of it just like a chess board. \n" +
                "                type the alphabetical letter (column) before the number (row)\n" +
                "                ");
    }
    public static void printWithEnter(String msg){
        Scanner sc = new Scanner(System.in);
        System.out.println(msg);
        System.out.println("Press enter to continue:");
        sc.nextLine();
        System.out.println("_______________________________________________________");
    }
    public static String[] readFiles(String fileName, int length) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        String[] array = new String[length];
        int idx = 0;
        while(sc.hasNextLine()){
            array[idx] = sc.nextLine();
            idx++;
        }
        return array;
    }
    public static boolean isNumeric(String val, int min, int max) {
        int i;
        try {
            i = Integer.parseInt(val);

        } catch (NumberFormatException e) {
            return false;
        }
        return i > min && i < max;
    }

    public static int readIntAnswer(String msg, int min, int max){
        System.out.println(msg);
        Scanner sc = new Scanner(System.in);
        String answer = sc.nextLine();
        int i = 0;
        boolean b = false;
        while(i <= min){
            if(isNumeric(answer, min, max)){
                i = Integer.parseInt(answer);
            }
            else{
                System.out.println("Please enter a valid Integer:");
                answer = sc.nextLine();
            }
        }
        return i;

    }
    public static String[] generateEmptyGrid(int length, int width){
        int dimensions = length * width;
        String[] grid = new String[dimensions];
        Arrays.fill(grid, "ⓞ");
        return grid;
    }


    public static int[] oldGenerateMines(int length, int width, int mines, String[] grid){
        int dimensions = length * width;
        int[] array = new int[dimensions];
        int num = (int) Math.round(dimensions * 0.2);
        int n;
        Random random = new Random();
        for(int i = 0; i < num; i++){
            n = random.nextInt(dimensions);
            while(true){
                if(!grid[n].equals("ⓞ")){
                    n = random.nextInt(dimensions);
                }
                if(array[n] == 1){
                    n = random.nextInt(dimensions);
                }
                else {
                    break;
                }
            }
            array[n] = 1;
        }

        return array;
    }
    public static int[] generateMines(int length, int width, int id, int mines){

        int[] safe = new int[9];
        int safeIndex = id;
        safeIndex -= length;
        safeIndex--;
        int idx = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j <3; j++) {
                safe[idx] = safeIndex;
                safeIndex++;
                idx++;
            }
            safeIndex += length;
            safeIndex -= 3;
        }
        for(int item : safe){
            //System.out.println(item);
        }
        int maxIndex = width * length;
        int[] mineGrid = new int[width * length];
        boolean b = false;
        List<int[]> safeList = List.of(safe);
        for(int i = 0; i < mines; i++){
            idx = (int) (Math.random() * maxIndex);
            while(true){
                for(int ii : safe){
                    if(ii == idx){
                        b = true;
                        break;
                    }
                }
                if(b || mineGrid[idx] == 9){
                    idx = (int) (Math.random() * maxIndex);
                    b = false;
                }
                else{
                    break;
                }

            }
            //marking for a mine is 9
            //System.out.println(idx);
            mineGrid[idx] = 9;
        }

        return mineGrid;



    }

    public static void generateMineAnswer(int length, int width, int[] mineGrid){
        int num;
        for (int i = 0; i < length * width; i++){
            if(mineGrid[i] == 9){
                continue;
            }

            num = checkMines(length, width, i, mineGrid);
            mineGrid[i] = num;

        }
    }
    public static void printGrid(int length, int width, int[] grid, String[] letters){
        String line = "  ";
        for(int i = 0; i < length; i++){
            line += letters[i] + "  ";
        }
        System.out.println(line);
        int idx = 0;
        for(int i = 0; i < width; i++){
            if(i < 10){
                line = "0";
            }
            line += i + " ";
            for(int j = 0; j < length; j++){
                line = line + grid[idx] + " ";
                idx++;
            }
            System.out.println(line);
            line = "";
        }
    }
    public static void printGrid(int length, int width, String[] grid, String[] letters, String[] colour, String[] symbols){
        List<String> colourList = Arrays.asList(colour);
        List <String> symbolsList = Arrays.asList(symbols);
        String color;
        String line = "   ";
        String RESET = "\u001B[0m";
        for(int i = 0; i < length; i++){
            line += letters[i] + "　";
        }
        System.out.println(line);
        int idx = 0;
        for(int i = 0; i < width; i++){
            if(i < 10){
                line = "0";
            }
            line += i + " ";
            for(int j = 0; j < length; j++){
                color = colour[symbolsList.indexOf(grid[idx])];
                line = line + color + grid[idx] + RESET + " ";
                idx++;
            }
            System.out.println(line);
            line = "";
        }
    }

    public static boolean isAlphaNumeric(int length, int width, String input, String[] letters){
        if(input.isEmpty()){
            return false;
        }
        String letter = input.substring(0, 1);
        boolean b = false;
        String l;
        for(int i = 0; i < length; i++){
            l = letters[i];
            if(l.equals(letter)){
                b = true;
                break;
            }
        }
        if(!b){
            return false;
        }
        letter = input.substring(1);
        int i;
        try{
            i = Integer.parseInt(letter);
        } catch (NumberFormatException e){
            return false;
        }

        return i >= 0 && i < width;


    }

    public static int readAlphaNumericInput(int length, int width, String input, String[] letters){

        String letter = input.substring(0, 1);
        String l;
        int alpha = 0;
        int number = Integer.parseInt(input.substring(1));
        for(int i = 0; i < letters.length; i++){
            l = letters[i];
            if(l.equals(letter)){
                alpha = i;
                break;
            }
        }
        int idx = number * width + alpha;
        return idx;
    }

    public static String checkPosition(int length, int width, int idx){
        String val = "";
        if (idx < length){
            val = "up";
        }
        else if(idx >= length * width - length){
            val = "down";
        }

        if(idx % length == 0){
            val += " left";
        }
        else if(idx % length == length - 1){
            val += " right";
        }

        return val;
    }

    public static int checkMines(int length, int width, int idx, int[] mineGrid){
        String position = checkPosition(length, width, idx);
        int index = idx;
        if(!position.contains("up")){
            index -= length;
        }
        index--;

        int minesNum = 0;
        for(int i = 0; i < 3; i++){
            if(position.contains("up") && i == 0){
                continue;
            }
            if(position.contains("down") && i == 2){
                break;
            }
            for(int j = 0; j < 3; j++){
                if(position.contains("left") && j == 0){
                    index++;
                    continue;
                }
                else if(position.contains("right") && j == 2){
                    index++;
                    break;
                }
                //System.out.println(index);
                if(mineGrid[index] == 9){
                    minesNum++;
                }
                index++;
            }
            index += length;
            index -= 3;
        }

        return minesNum;
    }

    public static List<Integer> checkZeroes(int length, int width, int idx, int[] mineGrid, String[] grid, String[] symbols){
        List<Integer> zeroList = new ArrayList<Integer>();
        int ii = 0;
        String position = checkPosition(length, width, idx);
        int index = idx;
        index -= length;
        index--;
        for(int i = 0; i < 3; i++){
            //System.out.println(index);
            if(position.contains("up") && i == 0){
                index += length;
                //System.out.println(index);
                continue;
            }
            if(position.contains("down") && i == 2){
                break;
            }
            for(int j = 0; j < 3; j++){
                //System.out.println(position);
                //System.out.println(index);
                if(position.contains("left") && j == 0) {
                    index++;
                    continue;
                }
                if(position.contains("right") && j == 2){
                    index++;
                    break;
                }
                if(!Objects.equals(grid[index], "ⓞ")){
                    index++;
                    continue;
                }


                ii = mineGrid[index];
                //System.out.println(ii);
                if(mineGrid[index] == 0){
                    zeroList.add(index);
                }
                if(mineGrid[index] == 1){
                    //System.out.println(index);
                }

                if(mineGrid[index] != 9){
                    //System.out.println(symbols[ii]);
                    grid[index] = symbols[ii];
                }


                index++;
            }
            index += length;
            index -= 3;
        }

        return zeroList;
    }

    public static String[] click(int length, int width, int idx, int[] mineGrid, String[] grid, String[] symbols){
        int num = mineGrid[idx];
        if(num == 9){
            return null;
        }
        else if(!Objects.equals(grid[idx], "ⓞ")){
            System.out.println("This is not a valid circle to click!");
            return grid;
        }

        if(num != 0){
            grid[idx] = symbols[num];
            return grid;
        }
        List<Integer> zeroList;
        int[] otherZeroList;

        zeroList = checkZeroes(length, width, idx, mineGrid, grid, symbols);


        while (!zeroList.isEmpty()) {

            zeroList.addAll(checkZeroes(length, width, zeroList.get(0), mineGrid, grid, symbols));
            zeroList.remove(0);


        }

        return grid;








    }

    public static int flag(int idx, String[] grid, int flags){
        if(Objects.equals(grid[idx], "ⓞ")){
            grid[idx] = "㊏";
            flags--;
        }
        else if(Objects.equals(grid[idx], "㊏")){
            grid[idx] = "ⓞ";
            flags++;
        }
        else{
            System.out.println("You cannot flag this circle!");
        }

        return flags;

    }

    public static boolean isInputValid(int length, int width, String input, String[] letters){
        if(input.isEmpty()){
            return false;
        }
        if(input.charAt(0) != 'c' && input.charAt(0) != 'f'){
            return false;
        }
        else if(input.charAt(1) != ' '){
            return false;
        }
        else return isAlphaNumeric(length, width, input.substring(2), letters);
    }

    public static boolean gameOver(){
        System.out.println("Game Over. You clicked a mine. ");
        return(playAgain());
    }

    public static boolean gameWin(){
        System.out.println("Congratulations! You have completed the game!");
        return(playAgain());
    }

    public static boolean playAgain(){
        System.out.println("Would you like to play again? (y/n)");
        Scanner sc = new Scanner(System.in);
        String answer = sc.nextLine();
        while(!answer.equals("y") && !answer.equals("n")){
            System.out.println("Please use 'y' for yes and 'no' for no: ");
            answer = sc.nextLine();
        }
        if(Objects.equals(answer, "y")){
            return true;
        }
        else{
            return false;
        }
    }



}






