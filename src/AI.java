import java.awt.*;
import java.util.*;

class Player {
    private String username;
    private float score;

    Player(String name){
        this.username = name;
        this.score = 0f;
    }

    String getUserName()
    {
        return this.username;
    }
}

class MapMove{
    ArrayList<Integer> source;
    ArrayList<Integer> dest;
    String color;
    int value;

    MapMove(ArrayList<Integer> source,ArrayList<Integer> dest,String color,int value){
        this.value=value;
        this.source=source;
        this.dest=dest;
        this.color=color;
    }

    @Override
    public String toString() {
        return "MapMove{" +
                "source=" + source +
                "dest"+ dest+
                ", color='" + color + '\'' +
                ", value=" + value +
                '}';
    }
}

class correspondingColors{
    private String color1;
    private String color2;

    correspondingColors(String color1,String color2){
        this.color1 = color1;
        this.color2 = color2;
    }
    public String getFirstColor() {
        return color1;
    }

    public String getSecondColor() {
        return color2;
    }
}

class Triangle{
    private LinkedHashMap<ArrayList<Integer>,String> map;
    private String colour;

    Triangle(LinkedHashMap<ArrayList<Integer>,String> indexes,String color){
        this.colour =color;
        this.map = indexes;
    }

    public Triangle(String color) {
        this.colour =color;
        this.map = new LinkedHashMap<>();
    }

    public Triangle(Triangle triangle) {
        this.colour =triangle.getColour();
        this.map = triangle.getMap();
    }


    public void setColour(String colour) {
        this.colour = colour;
    }

    void setMap(LinkedHashMap<ArrayList<Integer>,String> map){
        this.map=map;
    }

    String getColour(){
        return this.colour;
    }

    LinkedHashMap<ArrayList<Integer>,String> getMap(){
        return this.map;
    }

    void printTriangle(){
        System.out.println("Triangle "+colour);
        System.out.println(map);
    }



    boolean isFinished(){
        ArrayList<ArrayList<Integer>> keys = new ArrayList<>(map.keySet());
        String color = map.get(keys.get(0));

        for(int i=1;i< map.size();i++){
            if(!map.get(keys.get(i)).equalsIgnoreCase(color)) {
                return false;
            }
        }

        return true;

    }
}



class Board{
    private LinkedHashMap<ArrayList<Integer>,String> map;
    private ArrayList<Triangle> triangles;
    private ArrayList<correspondingColors> colors;
    private Player human,computer;

    Player getHuman(){
        return  this.human;
    }

    Player getComputer(){
        return this.computer;
    }

    ArrayList<correspondingColors> getColors(){
        return this.colors;
    }

    ArrayList<Triangle> getTriangles(){
        return this.triangles;
    }
    Board(){
        map = new LinkedHashMap<>();
        this.triangles = new ArrayList<>();
        buildBoard();
        this.colors = new ArrayList<>();
        colors.add(new correspondingColors("Green","Red"));
        colors.add(new correspondingColors("Blue","Orange"));
        colors.add(new correspondingColors("Purple","Yellow"));
        human = new Player("Human");
        computer = new Player("Computer");
    }

    Board(Board board){
        map = new LinkedHashMap<>(board.getMap());
        this.triangles = new ArrayList<>(board.getTriangles());
        this.colors = new ArrayList<>(board.getColors());
        human = board.getHuman();
        computer = board.getComputer();
    }




    ArrayList<MapMove> calculateUtiltyForEach(){
        ArrayList<ArrayList<Integer>> pieces = getAllComputerPieces();
        ArrayList<MapMove> result = new ArrayList<>();

        for(int i=0;i<pieces.size();i++){
            ArrayList<ArrayList<Integer>> possibleMoves = getPossibleAllValidMoves(pieces.get(i).get(0),pieces.get(i).get(1));
            if(!possibleMoves.isEmpty()){
                for(int j=0;j<possibleMoves.size();j++){
                    Board board = new Board(this);


                    board.updateCheckBoard(possibleMoves.get(j), board.getMap(), pieces.get(i));
                    board.getUtilityFunction();
                    MapMove temp = new MapMove(pieces.get(i),possibleMoves.get(j),board.getMap().get(possibleMoves.get(j)),board.getUtilityFunction());
                    //temp.put(possibleMoves.get(j),board.getUtilityFunction());
                    //System.out.println(possibleMoves.get(j));

                    result.add(temp);
                }
            }
        }

        System.out.println(result);
        return result;
    }

    Player getWinner(){
        int counter =0;
        for(int i =0;i<3;i++) {
            if (triangles.get(i).isFinished()){
                counter++;
            }
        }

        if(counter==3){
            return human;
        }else {
            return computer;
        }

    }

    MapMove getBestMove(){
        ArrayList<MapMove> allMoves = calculateUtiltyForEach();
        //ArrayList<ArrayList<Integer>> key = new ArrayList<>(allMoves.get(0).keySet());
        int max = 0;
        for(int i=1;i<allMoves.size();i++){

            if(allMoves.get(max).value < allMoves.get(i).value){
                max = i;
            }
        }

        return allMoves.get(max);
    }

    ArrayList<ArrayList<Integer>> getAllComputerPieces(){
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for(int i=3;i<triangles.size();i++){
            result.addAll(getAllPieces(triangles.get(i).getColour()));
        }
        return result;
    }

    ArrayList<ArrayList<Integer>> getAllPieces(String color){
        ArrayList<ArrayList<Integer>> keys = new ArrayList<>(map.keySet());
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for(int i=0;i<map.size();i++){
            if(map.get(keys.get(i)).equalsIgnoreCase(color)){
                result.add(keys.get(i));
            }
        }

        //System.out.println(result);
        return result;
    }

    void printAllPieces(String color){
        ArrayList<ArrayList<Integer>> keys = new ArrayList<>(map.keySet());
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for(int i=0;i<map.size();i++){
            if(map.get(keys.get(i)).equalsIgnoreCase(color)){
                result.add(keys.get(i));
            }
        }

        System.out.println(result);
        //return result;
    }

    int calcutateNumberOfForigenPieces(Triangle triangle){

        String correpondingColor = getCorrespondingColor(triangle.getColour());
        int counter =0;
        ArrayList<ArrayList<Integer>> keys = new ArrayList<>(triangle.getMap().keySet());
        for(int i =0;i<triangle.getMap().size();i++){
            if(triangle.getMap().get(keys.get(i)).equalsIgnoreCase(correpondingColor)){
                counter++;
            }
        }
        return counter;
    }

    int getUtilityFunction(){
        int i , C=0, H=0;


        for(i=0;i<3;i++){
            C+=calcutateNumberOfForigenPieces(triangles.get(i));
        }

        for( i=3;i<6;i++){
            H+=calcutateNumberOfForigenPieces(triangles.get(i));
        }


        return C-H;
    }

    void myGame(String color)
    {
        Scanner input = new Scanner(System.in);

        String correspondingColor = getCorrespondingColor(color);
        color = correspondingColor;

        while(checkWinner() == null) {

            Player player = takeTurn(color);


            if(player.getUserName().equalsIgnoreCase("Human"))
            {

                System.out.println(player.getUserName() + "'s Turn");
                printBoard();
                System.out.println("Write your Color");
                System.out.println(getHumanColor());
                color = input.next();

                printAllPieces(color);

                System.out.println("Select your piece by row and column");

                System.out.println("Select the row ");
                int row = input.nextInt();

                System.out.println("Select the Column ");
                int col = input.nextInt();



                ArrayList<Integer> currentState = new ArrayList<>(Arrays.asList(row,col));
                ArrayList<ArrayList<Integer>> moves = getPossibleAllValidMoves(currentState.get(0), currentState.get(1));

                ArrayList<Integer> updatedState = updateBoard(moves,getMap(), currentState.get(0),currentState.get(1));

                int ii,jj;

                ii = updatedState.get(0);
                jj = updatedState.get(1);

                while(canJumb(ii,jj) ) {

                    moves = getPossibleJumbMoves(ii, jj);

                    currentState = updateBoard(moves,getMap(), ii, jj);
                    ii = currentState.get(0);
                    jj = currentState.get(1);

                    if (canJumb(ii, jj)) {
                        System.out.println("can jump again :)");
                    }
                }
            }

            else if(player.getUserName().equalsIgnoreCase("Computer")) {
                System.out.println(player.getUserName() + "'s Turn");


                MapMove selectedMove = getBestMove();
                updateBoard(selectedMove.dest,getMap(), selectedMove.source);
                color = selectedMove.color;
            }
            else
            {
                System.out.println("Error");
            }
        }

    }

    ArrayList<String> getHumanColor()
    {
        ArrayList<String> result =  new ArrayList();
        for(int i = 0 ; i < 3;i++)
        {
            result.add(triangles.get(i).getColour());
        }
        return result;
    }

    ArrayList<String> getComputerColor()
    {
        ArrayList<String> result =  new ArrayList();
        for(int i = 3 ; i < 6;i++)
        {
            result.add(triangles.get(i).getColour());
        }
        return result;
    }

    String getCorrespondingColor(String color){
        String result = "";

        for(int i=0;i< colors.size();i++){
            if(colors.get(i).getFirstColor().equalsIgnoreCase(color)){
                return colors.get(i).getSecondColor();
            }else if(colors.get(i).getSecondColor().equalsIgnoreCase(color)){
                return colors.get(i).getFirstColor();
            }
        }
        return result;
    }
    Player checkWinner(){

        for(int i=0;i<triangles.size();i++){

            if(triangles.get(i).isFinished()){
                ArrayList<ArrayList<Integer>> keys = new ArrayList<>(triangles.get(i).getMap().keySet());

                if(triangles.get(i).getMap().get(keys.get(0)).equalsIgnoreCase(getCorrespondingColor(triangles.get(i).getMap().get(keys.get(0))))){
                    if(triangles.get(i+1).getMap().get(keys.get(0)).equalsIgnoreCase(getCorrespondingColor(triangles.get(i+1).getMap().get(keys.get(0))))){
                        if(triangles.get(i+2).getMap().get(keys.get(0)).equalsIgnoreCase(getCorrespondingColor(triangles.get(i+2).getMap().get(keys.get(0))))){
                            if(i==0){
                                return human;
                            }else if(i==3){
                                return computer;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    Player takeTurn(String color){

        for(int i=0;i<3;i++){
            if(triangles.get(i).getColour().equalsIgnoreCase(color)){
                return computer;
            }
        }

        return human;
    }

    Triangle getTriangle(String color){
        System.out.println(color);
        for(int i=0;i< triangles.size();i++){
            if(triangles.get(i).getColour().equalsIgnoreCase(color)){
                return triangles.get(i);
            }
        }
        return null;
    }

    void updateTriangleData(String color,ArrayList<Integer> positionBefore,ArrayList<Integer> positionAfter){
        Triangle selectedTriangle = getTriangle(color);

        if(selectedTriangle!=null) {
            selectedTriangle.getMap().replace(positionBefore, map.get(positionBefore));
            selectedTriangle.getMap().replace(positionAfter, "None");

            selectedTriangle.printTriangle();
        }

    }

    void printBoard(){
        int remark = 0;
        int remark2 = 0;
        int remark3 = 0;
        int remark4 = 0;
        int remark5 = 0;
        int remark6 = 0;
        int remark7 = 0;
        int remark8 = 0;
        int remark9 = 0;
        int remark10 = 0;
        int remark11 = 0;
        int remark12 = 0;
        int remark13 = 0;
        int remark14 = 0;
        ArrayList<ArrayList<Integer>> find = new ArrayList<ArrayList<Integer>>();
        Iterator it = map.entrySet().iterator();
        Iterator it2 = map.entrySet().iterator();
        while(it.hasNext())
        {

            Map.Entry<ArrayList, String> Pair = (Map.Entry<ArrayList, String>) it.next();
            find.add(Pair.getKey());

        }

        while(it2.hasNext())
        {
            Map.Entry<ArrayList, String> Pair = (Map.Entry<ArrayList, String>) it2.next();

            if(Pair.getKey().equals(find.get(0)))
            {

                System.out.println("                            "+Pair.getValue());

                System.out.print("                          ");
            }

            if(Pair.getKey().equals(find.get(1)) || Pair.getKey().equals(find.get(2)))
            {

                System.out.print(Pair.getValue() + " ");

            }

            if(Pair.getKey().equals(find.get(3)) || Pair.getKey().equals(find.get(4))|| Pair.getKey().equals(find.get(5)))
            {
                if(remark == 0)
                {
                    System.out.println();
                    remark++;
                    System.out.print("                        ");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(6)) ||
                    Pair.getKey().equals(find.get(7))||
                    Pair.getKey().equals(find.get(8))||
                    Pair.getKey().equals(find.get(9)))
            {
                if(remark2 == 0)
                {
                    System.out.println();
                    remark2++;
                    System.out.print("                      ");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(10)) ||
                    Pair.getKey().equals(find.get(11))||
                    Pair.getKey().equals(find.get(12))||
                    Pair.getKey().equals(find.get(13))||
                    Pair.getKey().equals(find.get(14)) ||
                    Pair.getKey().equals(find.get(15))||
                    Pair.getKey().equals(find.get(16))||
                    Pair.getKey().equals(find.get(17))||
                    Pair.getKey().equals(find.get(18))||
                    Pair.getKey().equals(find.get(19)) ||
                    Pair.getKey().equals(find.get(20))||
                    Pair.getKey().equals(find.get(21))||
                    Pair.getKey().equals(find.get(22)))
            {
                if(remark3 == 0)
                {
                    System.out.println();
                    remark3++;
                    System.out.print("");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(23)) ||
                    Pair.getKey().equals(find.get(24))||
                    Pair.getKey().equals(find.get(25))||
                    Pair.getKey().equals(find.get(26))||
                    Pair.getKey().equals(find.get(27)) ||
                    Pair.getKey().equals(find.get(28))||
                    Pair.getKey().equals(find.get(29))||
                    Pair.getKey().equals(find.get(30))||
                    Pair.getKey().equals(find.get(31))||
                    Pair.getKey().equals(find.get(32)) ||
                    Pair.getKey().equals(find.get(33))||
                    Pair.getKey().equals(find.get(34)))
            {
                if(remark4 == 0)
                {
                    System.out.println();
                    remark4++;
                    System.out.print("  ");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(35)) ||
                    Pair.getKey().equals(find.get(36))||
                    Pair.getKey().equals(find.get(37))||
                    Pair.getKey().equals(find.get(38))||
                    Pair.getKey().equals(find.get(39)) ||
                    Pair.getKey().equals(find.get(40))||
                    Pair.getKey().equals(find.get(41))||
                    Pair.getKey().equals(find.get(42))||
                    Pair.getKey().equals(find.get(43))||
                    Pair.getKey().equals(find.get(44)) ||
                    Pair.getKey().equals(find.get(45)))
            {
                if(remark5 == 0)
                {
                    System.out.println();
                    remark5++;
                    System.out.print("     ");
                }

                System.out.print(Pair.getValue() + " ");
            }
            if(Pair.getKey().equals(find.get(46)) ||
                    Pair.getKey().equals(find.get(47))||
                    Pair.getKey().equals(find.get(48))||
                    Pair.getKey().equals(find.get(49))||
                    Pair.getKey().equals(find.get(50)) ||
                    Pair.getKey().equals(find.get(51))||
                    Pair.getKey().equals(find.get(52))||
                    Pair.getKey().equals(find.get(53))||
                    Pair.getKey().equals(find.get(54))||
                    Pair.getKey().equals(find.get(55)))
            {
                if(remark6 == 0)
                {
                    System.out.println();
                    remark6++;
                    System.out.print("        ");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(56)) ||
                    Pair.getKey().equals(find.get(57))||
                    Pair.getKey().equals(find.get(58))||
                    Pair.getKey().equals(find.get(59))||
                    Pair.getKey().equals(find.get(60)) ||
                    Pair.getKey().equals(find.get(61))||
                    Pair.getKey().equals(find.get(62))||
                    Pair.getKey().equals(find.get(63))||
                    Pair.getKey().equals(find.get(64)))
            {
                if(remark7 == 0)
                {
                    System.out.println();
                    remark7++;
                    System.out.print("           ");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(65)) ||
                    Pair.getKey().equals(find.get(66))||
                    Pair.getKey().equals(find.get(67))||
                    Pair.getKey().equals(find.get(68))||
                    Pair.getKey().equals(find.get(69)) ||
                    Pair.getKey().equals(find.get(70))||
                    Pair.getKey().equals(find.get(71))||
                    Pair.getKey().equals(find.get(72))||
                    Pair.getKey().equals(find.get(73))||
                    Pair.getKey().equals(find.get(74)))
            {
                if(remark8 == 0)
                {
                    System.out.println();
                    remark8++;
                    System.out.print("        ");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(75)) ||
                    Pair.getKey().equals(find.get(76))||
                    Pair.getKey().equals(find.get(77))||
                    Pair.getKey().equals(find.get(78))||
                    Pair.getKey().equals(find.get(79)) ||
                    Pair.getKey().equals(find.get(80))||
                    Pair.getKey().equals(find.get(81))||
                    Pair.getKey().equals(find.get(82))||
                    Pair.getKey().equals(find.get(83))||
                    Pair.getKey().equals(find.get(84)) ||
                    Pair.getKey().equals(find.get(85)))
            {
                if(remark9 == 0)
                {
                    System.out.println();
                    remark9++;
                    System.out.print("     ");
                }

                System.out.print(Pair.getValue() + " ");
            }

            if(Pair.getKey().equals(find.get(86)) ||
                    Pair.getKey().equals(find.get(87))||
                    Pair.getKey().equals(find.get(88))||
                    Pair.getKey().equals(find.get(89))||
                    Pair.getKey().equals(find.get(90)) ||
                    Pair.getKey().equals(find.get(91))||
                    Pair.getKey().equals(find.get(92))||
                    Pair.getKey().equals(find.get(93))||
                    Pair.getKey().equals(find.get(94))||
                    Pair.getKey().equals(find.get(95)) ||
                    Pair.getKey().equals(find.get(96))||
                    Pair.getKey().equals(find.get(97)))
            {
                if(remark10 == 0)
                {
                    System.out.println();
                    remark10++;
                    System.out.print("  ");
                }

                System.out.print(Pair.getValue() + " ");
            }
            if(Pair.getKey().equals(find.get(98)) ||
                    Pair.getKey().equals(find.get(99))||
                    Pair.getKey().equals(find.get(100))||
                    Pair.getKey().equals(find.get(101))||
                    Pair.getKey().equals(find.get(102)) ||
                    Pair.getKey().equals(find.get(103))||
                    Pair.getKey().equals(find.get(104))||
                    Pair.getKey().equals(find.get(105))||
                    Pair.getKey().equals(find.get(106))||
                    Pair.getKey().equals(find.get(107)) ||
                    Pair.getKey().equals(find.get(108))||
                    Pair.getKey().equals(find.get(109))||
                    Pair.getKey().equals(find.get(110)))
            {
                if(remark11 == 0)
                {
                    System.out.println();
                    remark11++;
                    System.out.print("");
                }

                System.out.print(Pair.getValue() + " ");
            }
            if(Pair.getKey().equals(find.get(111)) ||
                    Pair.getKey().equals(find.get(112))||
                    Pair.getKey().equals(find.get(113))||
                    Pair.getKey().equals(find.get(114)))
            {
                if(remark12 == 0)
                {
                    System.out.println();
                    remark12++;
                    System.out.print("                      ");
                }

                System.out.print(Pair.getValue() + "   ");
            }
            if(Pair.getKey().equals(find.get(115)) ||
                    Pair.getKey().equals(find.get(116))||
                    Pair.getKey().equals(find.get(117)))
            {
                if(remark13 == 0)
                {
                    System.out.println();
                    remark13++;
                    System.out.print("                        ");
                }

                System.out.print(Pair.getValue() + "   ");
            }
            if(Pair.getKey().equals(find.get(118)) ||
                    Pair.getKey().equals(find.get(119)))
            {
                if(remark14 == 0)
                {
                    System.out.println();
                    remark14++;
                    System.out.print("                           ");
                }

                System.out.print(Pair.getValue() + "   ");
            }

            if(Pair.getKey().equals(find.get(120)))
            {
                System.out.println();
                System.out.print("                              ");
                System.out.print(Pair.getValue() + "   ");
                System.out.println();
            }
        }
    }

    void buildBoard(){
        ArrayList<Integer> Index1 = new ArrayList<Integer>(Arrays.asList(1,13));

        ArrayList<Integer> Index2 = new ArrayList<Integer>(Arrays.asList(2,12));
        ArrayList<Integer> Index3 = new ArrayList<Integer>(Arrays.asList(2,14));


        ArrayList<Integer> Index4 = new ArrayList<Integer>(Arrays.asList(3,11));
        ArrayList<Integer> Index5 = new ArrayList<Integer>(Arrays.asList(3,13));
        ArrayList<Integer> Index6 =new ArrayList<Integer>(Arrays.asList(3,15));



        ArrayList<Integer> Index7 = new ArrayList<Integer>(Arrays.asList(4,10));
        ArrayList<Integer> Index8 = new ArrayList<Integer>(Arrays.asList(4,12));
        ArrayList<Integer> Index9 = new ArrayList<Integer>(Arrays.asList(4,14));
        ArrayList<Integer> Index10 = new ArrayList<Integer>(Arrays.asList(4,16));



        ArrayList<Integer> Index11 = new ArrayList<Integer>(Arrays.asList(5,1));
        ArrayList<Integer> Index12 = new ArrayList<Integer>(Arrays.asList(5,3));
        ArrayList<Integer> Index13 = new ArrayList<Integer>(Arrays.asList(5,5));
        ArrayList<Integer> Index14 = new ArrayList<Integer>(Arrays.asList(5,7));
        ArrayList<Integer> Index15 = new ArrayList<Integer>(Arrays.asList(5,9));
        ArrayList<Integer> Index16 = new ArrayList<Integer>(Arrays.asList(5,11));
        ArrayList<Integer> Index17 = new ArrayList<Integer>(Arrays.asList(5,13));
        ArrayList<Integer> Index18 = new ArrayList<Integer>(Arrays.asList(5,15));
        ArrayList<Integer> Index19 = new ArrayList<Integer>(Arrays.asList(5,17));
        ArrayList<Integer> Index20 = new ArrayList<Integer>(Arrays.asList(5,19));
        ArrayList<Integer> Index21 = new ArrayList<Integer>(Arrays.asList(5,21));
        ArrayList<Integer> Index22 = new ArrayList<Integer>(Arrays.asList(5,23));
        ArrayList<Integer> Index23 = new ArrayList<Integer>(Arrays.asList(5,25));


        ArrayList<Integer> Index24 = new ArrayList<Integer>(Arrays.asList(6,2));
        ArrayList<Integer> Index25 = new ArrayList<Integer>(Arrays.asList(6,4));
        ArrayList<Integer> Index26 = new ArrayList<Integer>(Arrays.asList(6,6));
        ArrayList<Integer> Index27 = new ArrayList<Integer>(Arrays.asList(6,8));
        ArrayList<Integer> Index28 = new ArrayList<Integer>(Arrays.asList(6,10));
        ArrayList<Integer> Index29 = new ArrayList<Integer>(Arrays.asList(6,12));
        ArrayList<Integer> Index30 = new ArrayList<Integer>(Arrays.asList(6,14));
        ArrayList<Integer> Index31 = new ArrayList<Integer>(Arrays.asList(6,16));
        ArrayList<Integer> Index32 = new ArrayList<Integer>(Arrays.asList(6,18));
        ArrayList<Integer> Index33 = new ArrayList<Integer>(Arrays.asList(6,20));
        ArrayList<Integer> Index34 = new ArrayList<Integer>(Arrays.asList(6,22));
        ArrayList<Integer> Index35 = new ArrayList<Integer>(Arrays.asList(6,24));



        ArrayList<Integer> Index36 = new ArrayList<Integer>(Arrays.asList(7,3));
        ArrayList<Integer> Index37 = new ArrayList<Integer>(Arrays.asList(7,5));
        ArrayList<Integer> Index38 = new ArrayList<Integer>(Arrays.asList(7,7));
        ArrayList<Integer> Index39 = new ArrayList<Integer>(Arrays.asList(7,9));
        ArrayList<Integer> Index40 = new ArrayList<Integer>(Arrays.asList(7,11));
        ArrayList<Integer> Index41 = new ArrayList<Integer>(Arrays.asList(7,13));
        ArrayList<Integer> Index42 = new ArrayList<Integer>(Arrays.asList(7,15));
        ArrayList<Integer> Index43 = new ArrayList<Integer>(Arrays.asList(7,17));
        ArrayList<Integer> Index44 = new ArrayList<Integer>(Arrays.asList(7,19));
        ArrayList<Integer> Index45 = new ArrayList<Integer>(Arrays.asList(7,21));
        ArrayList<Integer> Index46 = new ArrayList<Integer>(Arrays.asList(7,23));

        ArrayList<Integer> Index47 = new ArrayList<Integer>(Arrays.asList(8,4));
        ArrayList<Integer> Index48 = new ArrayList<Integer>(Arrays.asList(8,6));
        ArrayList<Integer> Index49 = new ArrayList<Integer>(Arrays.asList(8,8));
        ArrayList<Integer> Index50 = new ArrayList<Integer>(Arrays.asList(8,10));
        ArrayList<Integer> Index51 = new ArrayList<Integer>(Arrays.asList(8,12));
        ArrayList<Integer> Index52 = new ArrayList<Integer>(Arrays.asList(8,14));
        ArrayList<Integer> Index53 = new ArrayList<Integer>(Arrays.asList(8,16));
        ArrayList<Integer> Index54 = new ArrayList<Integer>(Arrays.asList(8,18));
        ArrayList<Integer> Index55 = new ArrayList<Integer>(Arrays.asList(8,20));
        ArrayList<Integer> Index56 = new ArrayList<Integer>(Arrays.asList(8,22));


        ArrayList<Integer> Index57 = new ArrayList<Integer>(Arrays.asList(9,5));
        ArrayList<Integer> Index58 = new ArrayList<Integer>(Arrays.asList(9,7));
        ArrayList<Integer> Index59 = new ArrayList<Integer>(Arrays.asList(9,9));
        ArrayList<Integer> Index60 = new ArrayList<Integer>(Arrays.asList(9,11));
        ArrayList<Integer> Index61 = new ArrayList<Integer>(Arrays.asList(9,13));
        ArrayList<Integer> Index62 = new ArrayList<Integer>(Arrays.asList(9,15));
        ArrayList<Integer> Index63 = new ArrayList<Integer>(Arrays.asList(9,17));
        ArrayList<Integer> Index64 = new ArrayList<Integer>(Arrays.asList(9,19));
        ArrayList<Integer> Index65 = new ArrayList<Integer>(Arrays.asList(9,21));


        ArrayList<Integer> Index66 = new ArrayList<Integer>(Arrays.asList(10,4));
        ArrayList<Integer> Index67 = new ArrayList<Integer>(Arrays.asList(10,6));
        ArrayList<Integer> Index68 = new ArrayList<Integer>(Arrays.asList(10,8));
        ArrayList<Integer> Index69 = new ArrayList<Integer>(Arrays.asList(10,10));
        ArrayList<Integer> Index70 = new ArrayList<Integer>(Arrays.asList(10,12));
        ArrayList<Integer> Index71 = new ArrayList<Integer>(Arrays.asList(10,14));
        ArrayList<Integer> Index72 = new ArrayList<Integer>(Arrays.asList(10,16));
        ArrayList<Integer> Index73 = new ArrayList<Integer>(Arrays.asList(10,18));
        ArrayList<Integer> Index74 = new ArrayList<Integer>(Arrays.asList(10,20));
        ArrayList<Integer> Index75 = new ArrayList<Integer>(Arrays.asList(10,22));


        ArrayList<Integer> Index76 = new ArrayList<Integer>(Arrays.asList(11,3));
        ArrayList<Integer> Index77 = new ArrayList<Integer>(Arrays.asList(11,5));
        ArrayList<Integer> Index78 = new ArrayList<Integer>(Arrays.asList(11,7));
        ArrayList<Integer> Index79 = new ArrayList<Integer>(Arrays.asList(11,9));
        ArrayList<Integer> Index80 = new ArrayList<Integer>(Arrays.asList(11,11));
        ArrayList<Integer> Index81 = new ArrayList<Integer>(Arrays.asList(11,13));
        ArrayList<Integer> Index82 = new ArrayList<Integer>(Arrays.asList(11,15));
        ArrayList<Integer> Index83 = new ArrayList<Integer>(Arrays.asList(11,17));
        ArrayList<Integer> Index84 = new ArrayList<Integer>(Arrays.asList(11,19));
        ArrayList<Integer> Index85 = new ArrayList<Integer>(Arrays.asList(11,21));
        ArrayList<Integer> Index86 = new ArrayList<Integer>(Arrays.asList(11,23));


        ArrayList<Integer> Index87 = new ArrayList<Integer>(Arrays.asList(12,2));
        ArrayList<Integer> Index88 = new ArrayList<Integer>(Arrays.asList(12,4));
        ArrayList<Integer> Index89 = new ArrayList<Integer>(Arrays.asList(12,6));
        ArrayList<Integer> Index90 = new ArrayList<Integer>(Arrays.asList(12,8));
        ArrayList<Integer> Index91 = new ArrayList<Integer>(Arrays.asList(12,10));
        ArrayList<Integer> Index92 = new ArrayList<Integer>(Arrays.asList(12,12));
        ArrayList<Integer> Index93 = new ArrayList<Integer>(Arrays.asList(12,14));
        ArrayList<Integer> Index94 = new ArrayList<Integer>(Arrays.asList(12,16));
        ArrayList<Integer> Index95 = new ArrayList<Integer>(Arrays.asList(12,18));
        ArrayList<Integer> Index96 = new ArrayList<Integer>(Arrays.asList(12,20));
        ArrayList<Integer> Index97 = new ArrayList<Integer>(Arrays.asList(12,22));
        ArrayList<Integer> Index98 = new ArrayList<Integer>(Arrays.asList(12,24));

        ArrayList<Integer> Index99 = new ArrayList<Integer>(Arrays.asList(13,1));
        ArrayList<Integer> Index100 = new ArrayList<Integer>(Arrays.asList(13,3));
        ArrayList<Integer> Index101 = new ArrayList<Integer>(Arrays.asList(13,5));
        ArrayList<Integer> Index102 = new ArrayList<Integer>(Arrays.asList(13,7));
        ArrayList<Integer> Index103 = new ArrayList<Integer>(Arrays.asList(13,9));
        ArrayList<Integer> Index104 = new ArrayList<Integer>(Arrays.asList(13,11));
        ArrayList<Integer> Index105 = new ArrayList<Integer>(Arrays.asList(13,13));
        ArrayList<Integer> Index106 = new ArrayList<Integer>(Arrays.asList(13,15));
        ArrayList<Integer> Index107 = new ArrayList<Integer>(Arrays.asList(13,17));
        ArrayList<Integer> Index108 = new ArrayList<Integer>(Arrays.asList(13,19));
        ArrayList<Integer> Index109 = new ArrayList<Integer>(Arrays.asList(13,21));
        ArrayList<Integer> Index110 = new ArrayList<Integer>(Arrays.asList(13,23));
        ArrayList<Integer> Index111 = new ArrayList<Integer>(Arrays.asList(13,25));


        ArrayList<Integer> Index112 = new ArrayList<Integer>(Arrays.asList(14,10));
        ArrayList<Integer> Index113 = new ArrayList<Integer>(Arrays.asList(14,12));
        ArrayList<Integer> Index114 = new ArrayList<Integer>(Arrays.asList(14,14));
        ArrayList<Integer> Index115 = new ArrayList<Integer>(Arrays.asList(14,16));

        ArrayList<Integer> Index116 = new ArrayList<Integer>(Arrays.asList(15,11));
        ArrayList<Integer> Index117 = new ArrayList<Integer>(Arrays.asList(15,13));
        ArrayList<Integer> Index118 =new ArrayList<Integer>(Arrays.asList(15,15));

        ArrayList<Integer> Index119 = new ArrayList<Integer>(Arrays.asList(16,12));
        ArrayList<Integer> Index120 = new ArrayList<Integer>(Arrays.asList(16,14));

        ArrayList<Integer> Index121 = new ArrayList<Integer>(Arrays.asList(17,13));


        LinkedHashMap<ArrayList<Integer>,String> purple = new LinkedHashMap<>();
        purple.put(Index66,"Purple");
        purple.put(Index76,"Purple");
        purple.put(Index77,"Purple");
        purple.put(Index87,"Purple");
        purple.put(Index88,"Purple");
        purple.put(Index89,"Purple");
        purple.put(Index99,"Purple");
        purple.put(Index100,"Purple");
        purple.put(Index101,"Purple");
        purple.put(Index102,"Purple");




        LinkedHashMap<ArrayList<Integer>,String> red = new LinkedHashMap<ArrayList<Integer>,String>();
        red.put(Index112,"Red");
        red.put(Index113,"Red");
        red.put(Index114,"Red");
        red.put(Index115,"Red");
        red.put(Index116,"Red");
        red.put(Index117,"Red");
        red.put(Index118,"Red");
        red.put(Index119,"Red");
        red.put(Index120,"Red");
        red.put(Index121,"Red");





        LinkedHashMap<ArrayList<Integer>,String> orange = new LinkedHashMap<ArrayList<Integer>,String>();
        orange.put(Index75,"Orange");
        orange.put(Index85,"Orange");
        orange.put(Index86,"Orange");
        orange.put(Index96,"Orange");
        orange.put(Index97,"Orange");
        orange.put(Index98,"Orange");
        orange.put(Index108,"Orange");
        orange.put(Index109,"Orange");
        orange.put(Index110,"Orange");
        orange.put(Index111,"Orange");



        LinkedHashMap<ArrayList<Integer>,String> yellow = new LinkedHashMap<ArrayList<Integer>,String>();
        yellow.put(Index20,"Yellow");
        yellow.put(Index21,"Yellow");
        yellow.put(Index22,"Yellow");
        yellow.put(Index23,"Yellow");
        yellow.put(Index33,"Yellow");
        yellow.put(Index34,"Yellow");
        yellow.put(Index35,"Yellow");
        yellow.put(Index45,"Yellow");
        yellow.put(Index46,"Yellow");
        yellow.put(Index56,"Yellow");



        LinkedHashMap<ArrayList<Integer>,String> blue = new LinkedHashMap<ArrayList<Integer>,String>();
        blue.put(Index11,"Blue");
        blue.put(Index12,"Blue");
        blue.put(Index13,"Blue");
        blue.put(Index14,"Blue");
        blue.put(Index24,"Blue");
        blue.put(Index25,"Blue");
        blue.put(Index26,"Blue");
        blue.put(Index36,"Blue");
        blue.put(Index37,"Blue");
        blue.put(Index47,"Blue");



        LinkedHashMap<ArrayList<Integer>,String> green = new LinkedHashMap<ArrayList<Integer>,String>();
        green.put(Index1,"Green");
        green.put(Index2,"Green");
        green.put(Index3,"Green");
        green.put(Index4,"Green");
        green.put(Index5,"Green");
        green.put(Index6,"Green");
        green.put(Index7,"Green");
        green.put(Index8,"Green");
        green.put(Index9,"Green");
        green.put(Index10,"Green");

        triangles.add(new Triangle(green,"Green"));
        triangles.add(new Triangle(blue,"Blue"));
        triangles.add(new Triangle(purple,"Purple"));
        triangles.add(new Triangle(red,"Red"));
        triangles.add(new Triangle(orange,"Orange"));
        triangles.add(new Triangle(yellow,"Yellow"));

        map.put(Index1, "Green");
        map.put(Index2, "Green");
        map.put(Index3, "Green");
        map.put(Index4, "Green");
        map.put(Index5, "Green");
        map.put(Index6, "Green");
        map.put(Index7, "Green");
        map.put(Index8, "Green");
        map.put(Index9, "Green");
        map.put(Index10, "Green");

        map.put(Index11, "Blue");
        map.put(Index12, "Blue");
        map.put(Index13, "Blue");
        map.put(Index14, "Blue");
        map.put(Index15, "None");
        map.put(Index16, "None");
        map.put(Index17, "None");
        map.put(Index18, "None");
        map.put(Index19, "None");
        map.put(Index20, "Yellow");
        map.put(Index21, "Yellow");
        map.put(Index22, "Yellow");
        map.put(Index23, "Yellow");

        map.put(Index24, "Blue");
        map.put(Index25, "Blue");
        map.put(Index26, "Blue");
        map.put(Index27, "None");
        map.put(Index28, "None");
        map.put(Index29, "None");
        map.put(Index30, "None");
        map.put(Index31, "None");
        map.put(Index32, "None");
        map.put(Index33, "Yellow");
        map.put(Index34, "Yellow");
        map.put(Index35, "Yellow");

        map.put(Index36, "Blue");
        map.put(Index37, "Blue");
        map.put(Index38, "None");
        map.put(Index39, "None");
        map.put(Index40, "None");
        map.put(Index41, "None");
        map.put(Index42, "None");
        map.put(Index43, "None");
        map.put(Index44, "None");
        map.put(Index45, "Yellow");
        map.put(Index46, "Yellow");


        map.put(Index47, "Blue");
        map.put(Index48, "None");
        map.put(Index49, "None");
        map.put(Index50, "None");
        map.put(Index51, "None");
        map.put(Index52, "None");
        map.put(Index53, "None");
        map.put(Index54, "None");
        map.put(Index55, "None");
        map.put(Index56, "Yellow");


        map.put(Index57, "None");
        map.put(Index58, "None");
        map.put(Index59, "None");
        map.put(Index60, "None");
        map.put(Index61, "None");
        map.put(Index62, "None");
        map.put(Index63, "None");
        map.put(Index64, "None");
        map.put(Index65, "None");


        map.put(Index66, "Purple");
        map.put(Index67, "None");
        map.put(Index68, "None");
        map.put(Index69, "None");
        map.put(Index70, "None");
        map.put(Index71, "None");
        map.put(Index72, "None");
        map.put(Index73, "None");
        map.put(Index74, "None");
        map.put(Index75, "Orange");


        map.put(Index76, "Purple");
        map.put(Index77, "Purple");
        map.put(Index78, "None");
        map.put(Index79, "None");
        map.put(Index80, "None");
        map.put(Index81, "None");
        map.put(Index82, "None");
        map.put(Index83, "None");
        map.put(Index84, "None");
        map.put(Index85, "Orange");
        map.put(Index86, "Orange");


        map.put(Index87, "Purple");
        map.put(Index88, "Purple");
        map.put(Index89, "Purple");
        map.put(Index90, "None");
        map.put(Index91, "None");
        map.put(Index92, "None");
        map.put(Index93, "None");
        map.put(Index94, "None");
        map.put(Index95, "None");
        map.put(Index96, "Orange");
        map.put(Index97, "Orange");
        map.put(Index98, "Orange");



        map.put(Index99, "Purple");
        map.put(Index100, "Purple");
        map.put(Index101, "Purple");
        map.put(Index102, "Purple");
        map.put(Index103, "None");
        map.put(Index104, "None");
        map.put(Index105, "None");
        map.put(Index106, "None");
        map.put(Index107, "None");
        map.put(Index108, "Orange");
        map.put(Index109, "Orange");
        map.put(Index110, "Orange");
        map.put(Index111, "Orange");


        map.put(Index112, "Red");
        map.put(Index113, "Red");
        map.put(Index114, "Red");
        map.put(Index115, "Red");
        map.put(Index116, "Red");
        map.put(Index117, "Red");
        map.put(Index118, "Red");
        map.put(Index119, "Red");
        map.put(Index120, "Red");
        map.put(Index121, "Red");


    }

    ArrayList<ArrayList<Integer>> getPossibleJumb(int row,int col){
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        ArrayList<Integer> ind = new ArrayList();
        ind.add(row);
        ind.add(col);
        if(map.get(ind)!= "None")
        {
            int rows = ind.get(0);
            int cols = ind.get(1);

            ArrayList<Integer> possible1 = new ArrayList();
            possible1.add(rows+1);
            possible1.add(cols+1);

            ArrayList<Integer> possible2 = new ArrayList();
            possible2.add(rows+1);
            possible2.add(cols-1);

            ArrayList<Integer> possible3 = new ArrayList();
            possible3.add(rows-1);
            possible3.add(cols+1);

            ArrayList<Integer> possible4 = new ArrayList();
            possible4.add(rows-1);
            possible4.add(cols-1);

            ArrayList<Integer> possible5 = new ArrayList();
            possible5.add(rows);
            possible5.add(cols - 2);

            ArrayList<Integer> possible6 = new ArrayList();
            possible6.add(rows);
            possible6.add(cols + 2);
            int remarkable1 = 0;
            int remarkable2 = 0;
            int remarkable3 = 0;
            int remarkable4 = 0;
            int remarkable5 = 0;
            int remarkable6 = 0;
            for(int i = 0 ; i < 6;i++)
            {
                if(map.containsKey(possible1)&& !map.get(possible1).equals("None") && remarkable1 == 0)
                {
                    remarkable1 ++;

                    result.add(possible1);
                    //System.out.println(possible1);
                }

                else if(map.containsKey(possible2)&&!map.get(possible2).equals("None") && remarkable2 == 0)
                {
                    remarkable2 ++;
                    result.add(possible2);
                    //System.out.println(possible2);
                }

                else if(map.containsKey(possible3)&& !map.get(possible3).equals("None") && remarkable3 == 0)
                {
                    remarkable3 ++;

                    result.add(possible3);
                    //System.out.println(possible3);
                }

                else if(map.containsKey(possible4) && !map.get(possible4).equals("None") && remarkable4 == 0)
                {
                    remarkable4 ++;

                    result.add(possible4);
                    //System.out.println(possible4);
                }

                else if(map.containsKey(possible5) && !map.get(possible5).equals("None") && remarkable5 == 0)
                {
                    remarkable5 ++;

                    result.add(possible5);
                    //System.out.println(possible5);
                }

                else if(map.containsKey(possible6) && !map.get(possible6).equals("None") && remarkable6 == 0)
                {
                    remarkable6 ++;

                    result.add(possible6);
                    //System.out.println(possible6);
                }
            }


        }
        return result;
    }


    ArrayList<ArrayList<Integer>> getPossibleAllValidMoves(int row,int col) {

        HashSet<ArrayList<Integer>> moves = new HashSet(getPossibleMoves(row, col));
        //System.out.println(moves);



        moves.addAll(getPossibleJumbMoves(row,col));



        //best

        ArrayList<ArrayList<Integer>> finalmoves = new ArrayList(moves);
        return finalmoves;

    }



    ArrayList<ArrayList<Integer>> getPossibleJumbMoves(int row,int col){
        ArrayList<ArrayList<Integer>> moves = getPossibleJumb(row,col);
        HashSet<ArrayList<Integer>> result = new HashSet<>();
        for(int i =0;i<moves.size();i++){
            result.addAll(getPossibleMoves(moves.get(i).get(0),moves.get(i).get(1)));
        }


        return new ArrayList<>(result);
    }

    void jumpPath(LinkedHashMap<ArrayList<Integer>,String> linkedMap,int row , int col)
    {

        ArrayList<ArrayList<Integer>> moves = getPossibleAllValidMoves(row, col);
        ArrayList<Integer> currentState = updateBoard(moves,linkedMap,row,col);

        int ii = currentState.get(0);
        int jj = currentState.get(1);

        while(canJumb(ii,jj) ) {


            //System.out.println("Valid moves");


            currentState = updateBoard(getPossibleJumbMoves(ii, jj),getMap(), ii, jj);
            ii = currentState.get(0);
            jj = currentState.get(1);

            if (canJumb(ii, jj)) {
                System.out.println("can jump again :)");
            }
        }


    }



    ArrayList<Integer> updateCheckBoard(ArrayList<Integer> selectedMove,LinkedHashMap<ArrayList<Integer>,String> linkedMap,ArrayList<Integer> currentState)
    {


        //moves = getPossibleAllValidMoves(row,col);
        //ArrayList<Integer> currentState = new ArrayList<>(Arrays.asList(row,col));

        //System.out.println("Valid moves ");
        //System.out.println(move);
        //System.out.println("Enter Move Index");
        //System.out.println("If you don't want to jump select -1");
        //Scanner scan = new Scanner(System.in);
        //int num = scan.nextInt();

        // get best move


        linkedMap.replace(selectedMove,linkedMap.get(currentState));
        updateTriangleData(linkedMap.get(currentState),selectedMove,currentState);
        linkedMap.replace(currentState,"None");
        //updateTriangleData(linkedMap.get(currentState),currentState);


        //printBoard();

        return selectedMove;
    }

    ArrayList<Integer> updateBoard(ArrayList<Integer> selectedMove,LinkedHashMap<ArrayList<Integer>,String> linkedMap,ArrayList<Integer> currentState)
    {


        //moves = getPossibleAllValidMoves(row,col);
        //ArrayList<Integer> currentState = new ArrayList<>(Arrays.asList(row,col));

        //System.out.println("Valid moves ");
        //System.out.println(move);
        //System.out.println("Enter Move Index");
        //System.out.println("If you don't want to jump select -1");
        //Scanner scan = new Scanner(System.in);
        //int num = scan.nextInt();

        // get best move


        linkedMap.replace(selectedMove,linkedMap.get(currentState));
        updateTriangleData(linkedMap.get(currentState),selectedMove,currentState);
        linkedMap.replace(currentState,"None");
        //updateTriangleData(linkedMap.get(currentState),currentState);


        printBoard();

        return selectedMove;
    }



    ArrayList<Integer> updateBoard(ArrayList<ArrayList<Integer>> moves,LinkedHashMap<ArrayList<Integer>,String> linkedMap,int row , int col)
    {


        //moves = getPossibleAllValidMoves(row,col);
        ArrayList<Integer> currentState = new ArrayList<>(Arrays.asList(row,col));

        System.out.println("Valid moves ");
        System.out.println(moves);
        System.out.println("Enter Move Index");
        System.out.println("If you don't want to jump select -1");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();


        if(num == -1)
        {
            return new ArrayList<>(Arrays.asList(0,0));
        }
        // get best move

        ArrayList<Integer> selectedMove = moves.get(num);


        linkedMap.replace(selectedMove,linkedMap.get(currentState));
        updateTriangleData(linkedMap.get(currentState),selectedMove,currentState);
        linkedMap.replace(currentState,"None");
        //updateTriangleData(linkedMap.get(currentState),currentState);


        printBoard();

        return selectedMove;
    }


    boolean canJumb(int row,int col){

        ArrayList<ArrayList<Integer>> move = getPossibleJumbMoves(row, col);
        //System.out.println(move);
        if(!move.isEmpty()){
            return true;
        }

        return false;
    }

    ArrayList<ArrayList<Integer>> getPossibleMoves(int row,int col){
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        ArrayList<Integer> ind = new ArrayList();
        ind.add(row);
        ind.add(col);
        if(map.get(ind)!= "None")
        {
            int rows = ind.get(0);
            int cols = ind.get(1);

            ArrayList<Integer> possible1 = new ArrayList();
            possible1.add(rows+1);
            possible1.add(cols+1);

            ArrayList<Integer> possible2 = new ArrayList();
            possible2.add(rows+1);
            possible2.add(cols-1);

            ArrayList<Integer> possible3 = new ArrayList();
            possible3.add(rows-1);
            possible3.add(cols+1);

            ArrayList<Integer> possible4 = new ArrayList();
            possible4.add(rows-1);
            possible4.add(cols-1);

            ArrayList<Integer> possible5 = new ArrayList();
            possible5.add(rows);
            possible5.add(cols - 2);

            ArrayList<Integer> possible6 = new ArrayList();
            possible6.add(rows);
            possible6.add(cols + 2);
            int remarkable1 = 0;
            int remarkable2 = 0;
            int remarkable3 = 0;
            int remarkable4 = 0;
            int remarkable5 = 0;
            int remarkable6 = 0;
            for(int i = 0 ; i < 6;i++)
            {
                if(map.containsKey(possible1)&&map.get(possible1).equals("None") && remarkable1 == 0)
                {
                    remarkable1 ++;

                    result.add(possible1);
                    //System.out.println(possible1);
                }

                else if(map.containsKey(possible2)&&map.get(possible2).equals("None") && remarkable2 == 0)
                {
                    remarkable2 ++;
                    result.add(possible2);
                    //System.out.println(possible2);
                }

                else if(map.containsKey(possible3)&&map.get(possible3).equals("None") && remarkable3 == 0)
                {
                    remarkable3 ++;

                    result.add(possible3);
                    //System.out.println(possible3);
                }

                else if(map.containsKey(possible4)&&map.get(possible4).equals("None") && remarkable4 == 0)
                {
                    remarkable4 ++;

                    result.add(possible4);
                    //System.out.println(possible4);
                }

                else if(map.containsKey(possible5)&&map.get(possible5).equals("None") && remarkable5 == 0)
                {
                    remarkable5 ++;

                    result.add(possible5);
                    //System.out.println(possible5);
                }

                else if(map.containsKey(possible6)&&map.get(possible6).equals("None") && remarkable6 == 0)
                {
                    remarkable6 ++;

                    result.add(possible6);
                    //System.out.println(possible6);
                }
            }


        }
        return result;
    }
    LinkedHashMap<ArrayList<Integer>,String> getMap(){
        return  this.map;
    }
}

public class AI {


    public static void main(String[] args) {

        Board board =new Board();
        //System.out.println(map);

        //find all valid moves

       /* if()
        {
;
        }*/
       // Scanner input = new Scanner(System.in);


        //board.buildBoard();
        System.out.println("/*/");
        board.printBoard();
        System.out.println("/*/");

        String color = board.getHumanColor().get(0);
        board.myGame(color);

        Player winner = board.getWinner();
        System.out.println(winner.getUserName() +" WON :)");



    }

}