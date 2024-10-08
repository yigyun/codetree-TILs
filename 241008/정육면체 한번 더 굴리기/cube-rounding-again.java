import java.util.*;
import java.io.*;

/**
    1 ~ 6의 숫자가 그려진 n x n
    m번에 걸쳐 주사위를 계속 1칸씩 굴린다.
    시작은 1,1(좌상단)에서 시작하는 주사위가 있다.
    주사위는 내가 아는 방식 합이 양쪽 7
    처음에 주사위는 오른쪽으로 움직인다.

    이때 주사위가 놓인 칸 숫자 + 상하좌우 인접하며 같은 숫자가 적힌 모든 칸의 합만큼 점수가 쌓인다.


    주사위 규칙
    1. 주사위가 방향대로 일단 굴러감.
    2. 점수 증가시키기
    3. 바닥면을 살펴보고 주사위의 바닥면, 현재 맵에서의 값의 차이를 보고 방향을 정함.
        만약 주사위가 크면 90도 시계방향
        작다면 반 시계방향
        동일하면 같은 방향

    만약 격자판을 벗어나는 경우 반대 방향으로 바뀌게 된 뒤 한 칸 움직이게 된다.

**/

public class Main {

    static int n;
    static int m;
    static int result;

    static int[][] map;
    static int[][] point;
    static boolean[][] visited;

    // 맨위, 위의 상, 위의 하, 오른쪽, 왼쪽, 맨아래
    static int[] dice = new int[]{1, 5, 3, 4, 2, 6};
    static int x; static int y;

    //상, 좌, 하, 우
    static int[] dx = new int[]{-1, 0, 1, 0};
    static int[] dy = new int[]{0, 1, 0, -1};
        
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        point = new int[n][n];
        result = 0;
        x = 0; y = 0;

        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        visited = new boolean[n][n];
        for(int i = 0; i < n; i++){
            for(int j =0; j < n; j++){
                if(!visited[i][j]){
                    make(i, j);
                }
            }
        }

        int dir = 1;

        for(int i = 1; i <= m; i++){
            move(dir);
            dir = sum(dir);
        }
        
        System.out.print(result);
    }

    static void print(){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                sb.append(point[i][j]).append(" ");
            }
            sb.append('\n');
        }

        System.out.println(sb.toString());
    }

    static void make(int i, int j){
        Queue<int[]> que = new LinkedList<>();
        List<int[]> list = new ArrayList<>();
        que.offer(new int[]{i, j});
        list.add(new int[]{i, j});
        visited[i][j] = true;
        int number = map[i][j];

        while(!que.isEmpty()){
            int[] current = que.poll();
            for(int dir = 0; dir < 4; dir++){
                int nx = current[0] + dx[dir];
                int ny = current[1] + dy[dir];
                if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] == number){
                    que.offer(new int[]{nx, ny});
                    list.add(new int[]{nx, ny});
                    visited[nx][ny] = true;
                }
            }
        }

        number *= list.size();
        
        for(int q = 0; q < list.size(); q++){
            int[] current = list.get(q);
            point[current[0]][current[1]] = number;
        }
    }

    static int sum(int dir){
        int number = dice[5];
        // 현재 위치 x, y에서 point 가져오면 끝.
        result += point[x][y];
        
        // 방향 수정해주기.
        if(map[x][y] > dice[5]){
            dir -= 1;
            if(dir < 0) dir = 3;
        }else if(map[x][y] < dice[5]){
            dir = (dir + 1) % 4;
        }

        return dir;
    }

    // 주사위가 움직인다. 초기는 오른쪽으로 움직인다.
    static void move(int dir){

        int nx = x + dx[dir];
        int ny = y + dy[dir];

        if(!isRange(nx, ny)){
            dir = (dir + 2) % 4;
            nx = x + dx[dir];
            ny = y + dy[dir];
        }

        int[] newDice = dice.clone();
        if(dir == 0){
            newDice[1] = dice[0];
            newDice[5] = dice[1];
            newDice[4] = dice[5];
            newDice[0] = dice[4];
        }else if(dir == 2){
            newDice[4] = dice[0];
            newDice[5] = dice[4];
            newDice[1] = dice[5];
            newDice[0] = dice[1];
        }else if(dir == 3){
            newDice[3] = dice[0];
            newDice[0] = dice[2];
            newDice[5] = dice[3];
            newDice[2] = dice[5];
        }else if(dir == 1){
            newDice[2] = dice[0];
            newDice[5] = dice[2];
            newDice[0] = dice[3];
            newDice[3] = dice[5];
        }
        x = nx; y = ny;
        dice = newDice;
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= n || ny < 0 || ny >= n);
    }
}