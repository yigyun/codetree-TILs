import java.util.*;
import java.io.*;
    // 11시 15분

public class Main {

    static int n; // 격자 크기
    static int m; // 도망자 수, 도망자는 좌우(우) or 상하(하) 두 방향성만 가질 수 있음.
    static int[] dx = new int[]{1, 0, -1, 0}; // 하,우,상,좌
    static int[] dy = new int[]{0, 1, 0, -1};
    static int h; // 나무의 수, 나무가 도망자와 겹치는 것 가능하다, 즉 따로 관리해야한다.
    static int[][] trees;
    static int distance(int x1, int y1, int x2, int y2){ // 두 사람간의 거리 계산하는 함수.
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
    static int k; // 턴을 의미함.
    static List<Integer> route;
    
    static boolean[] live; // 도망자가 잡혔느지 여부
    static List<Integer>[][] list; // x, y에 있는 도망자 표시
    static int[][] people;

    static int[] chaser;

    static int result;
    // 로직 1, 3이하의 거리에 있는 도망자는 움직인다.
    // 이때의 조건
    // 격자(내) 움직이는 칸에 술래가 있으면 이동 X, 없으면 이동 나무 여부는 상관 X
    // 격자(외) 방향을 틀어준다(반대), 이후 방향으로 1칸 움직인다. 이때의 조건은 (내)와 같음.

    // 로직 2, 달팽이 모양의 술래 움직임을 만들어야 함.
    // 미리 루트를 만들고 반복시키기. 어차피 k가 100보다 작거나 같음.
    // 즉 100개의 루트만 만들면됨. 만약 왕복이 100보다 적으면 그냥 반복시키기. 넘는경우 추가할 필요가없다는거임.
    // k 턴을 통해 % size()로 리스트를 접근시키면 될듯

    // 로직 3, 술래가 움직인다.
    // 로직 2를 통해 얻은 방향으로 술래가 움직이는데 이때, 움직인 후 방향을 다음 거를 바로 넣어줘야 한다.

    // 로직 4, 술래가 도망자를 잡는다.
    // 나무와 같은 칸의 도망자는 잡지 못한다. 본인 위치를 포함해서 보는 방향으로 3칸을 확인함.
    // 도망자 수 x k 턴의 점수를 획득한다.
    // 도망자끼리 위치가 이동 중에 겹칠 수 있음.(배열로 표시 불가)
    // List로 [x][y].add() 하면서 관리하던가?
    

    // 유의할점
    // k턴의 시작을 0으로 할거니까 점수 때 + 1해서 곱해야함.
    // 0,0 기준으로 할거니까 입력받을 때, -1 해주기.


    /**
    자료구조 필요한 거 정리하기
    1) 도망자 관리(x, y, 방향)
    2) 술래 관리(방향을 바로 틀어줘야 함을 유의하기)
    3) 잡힌 도망자는 다음에 처리해야함. live 배열
    4) List[][]<Integer>? 해서 해당 위치에 있는 도망자 관리하기
    5) chaser로 술래의 위치랑 방향 관리하기
    6) 나무들 표시하는 trees[][]
    **/

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        h = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        result = 0;
        
        list = new ArrayList[n][n];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                list[i][j] = new ArrayList<>();
            }
        }
        // 도망자 입력 받기
        live = new boolean[m];
        people = new int[m][3]; // 도망자 정보 x, y, 방향
        for(int i = 0; i < m; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int t = Integer.parseInt(st.nextToken());
            if(t == 2) t = 0;
            people[i][0] = x; people[i][1] = y; people[i][2] = t;
            list[x][y].add(i);
        }

        trees = new int[n][n];
        for(int i = 0; i < h; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            trees[x][y] = 1;
        }
        // 술래 기본 정보 중앙 x,y 방향은 상
        chaser = new int[]{n/2, n/2, 2}; // 술래 정보 x, y, 바라보는 방향

        // 술래 달팽이 만들기
        route = makeRoute();

        int startIndex = route.size() - 1;
        for(int i = startIndex; i >= 0; i--){
            route.add((route.get(i) + 2)%4);
        }

        // 이동 방향을
        startIndex += 1;
        for(int t = 0; t < k; t++){
            movePeople();
            moveChaser(startIndex + t);
            catchPeople(t+1);
        }
        System.out.print(result);
    }

    // 로직 4, 술래가 도망자를 잡는다.
    // 나무와 같은 칸의 도망자는 잡지 못한다. 본인 위치를 포함해서 보는 방향으로 3칸을 확인함.
    // 도망자 수 x k 턴의 점수를 획득한다.
    // 도망자끼리 위치가 이동 중에 겹칠 수 있음.(배열로 표시 불가)
    // List로 [x][y].add() 하면서 관리하던가?

    static void catchPeople(int turn){
        for(int i = 0; i < 3; i++){
            int nx = chaser[0] + (dx[chaser[2]] * i);
            int ny = chaser[1] + (dy[chaser[2]] * i);
            if(isRange(nx, ny) && trees[nx][ny] != 1){
                if(list[nx][ny].size() != 0){
                    for(int number : list[nx][ny]){
                        // 해당 번호의 애를 live에서 죽이고, 리스트에서 지우기.
                        live[number] = true;
                        result += turn;
                    }
                    list[nx][ny].clear();
                }
            }
        }
    }

    // 로직 3, 술래가 움직인다.
    // 로직 2를 통해 얻은 방향으로 술래가 움직이는데 이때, 움직인 후 방향을 다음 거를 바로 넣어줘야 한다.

    static void moveChaser(int index){
        int dir = route.get((index % route.size()));
        int nx = chaser[0] + dx[dir];
        int ny = chaser[1] + dy[dir];
        chaser[0] = nx; chaser[1] = ny;
        int nextDir = route.get((index + 1) % route.size());
        chaser[2] = nextDir;
    }

     // 로직 1, 3이하의 거리에 있는 도망자는 움직인다.
    // 이때의 조건
    // 격자(내) 움직이는 칸에 술래가 있으면 이동 X, 없으면 이동 나무 여부는 상관 X
    // 격자(외) 방향을 틀어준다(반대), 이후 방향으로 1칸 움직인다. 이때의 조건은 (내)와 같음.

    static void movePeople(){
        for(int i = 0; i < m; i++){
            if(!live[i] && distance(people[i][0], people[i][1], chaser[0], chaser[1]) <= 3){
                int nx = people[i][0] + dx[people[i][2]];
                int ny = people[i][1] + dy[people[i][2]];
                if(isRange(nx,ny)){
                    if(nx != chaser[0] || ny != chaser[1]){
                        // 이동할 때 people 수정하고, list 수정해야함.
                        list[people[i][0]][people[i][1]].remove(Integer.valueOf(i));
                        list[nx][ny].add(i);
                        
                        people[i][0] = nx; people[i][1] = ny;
                    }
                } else {
                    int dir = (people[i][2] + 2) % 4;
                    nx = people[i][0] + dx[dir];
                    ny = people[i][1] + dy[dir];
                    people[i][2] = dir;
                    if(isRange(nx, ny)){
                        if(nx != chaser[0] || ny != chaser[1]){
                            list[people[i][0]][people[i][1]].remove(Integer.valueOf(i));
                            list[nx][ny].add(i);
                            people[i][0] = nx; people[i][1] = ny;
                        }
                    }
                }
            }
        }
    }

    static List<Integer> makeRoute(){
        boolean[][] visited = new boolean[n][n];
        List<Integer> list = new ArrayList<>();
        int x = 0; int y = 0; int dir = 0;
        visited[x][y] = true;
        while(true){
            if(x == n /2 && y == n / 2) break;
            int nx = x + dx[dir];
            int ny = y + dy[dir];
            if(isRange(nx, ny) && !visited[nx][ny]){
                x = nx; y = ny;
                visited[nx][ny] = true;
                list.add(dir);
            } else {
                dir = (dir + 1) % 4;
            }
        }
        return list;
    }

    static boolean isRange(int x, int y){
        return !(x < 0 || x >= n || y < 0 || y >= n);
    }
}