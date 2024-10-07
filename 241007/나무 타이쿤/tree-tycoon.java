import java.util.*;
import java.io.*;

/**
    n x n
    특수 영양제는 높이를 1 증가시키고, 씨앗은 1로만들어줌.
    8방향으로 규칙에 따라 1번부터8번
    1. 이동하기
    2. 투입하고 투입된 영양제는 사라진다.
    3. 대각선으로 인접한 방향에 높이가 1이상인 리브로수가 있는 만큼 성장한다.
    4.

    자료구조
    영양제 관리하는 배열? 리스트?
    맵
    
**/

public class Main {
    
    static int n;
    static int m;
    static int d;
    static int p;
    static int[][] map;
    static boolean[][] visited;

    static int[] dx = new int[]{0, -1, -1, -1, 0, 1, 1, 1};
    static int[] dy = new int[]{1, 1, 0, -1, -1, -1, 0, 1};

    static List<int[]> unique;
    static List<int[]> grow;
    
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        unique = new ArrayList<>();
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        
        map = new int[n][n];
        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        unique = new ArrayList<>();
        for(int i = 0; i <= 1; i++){
            unique.add(new int[]{n-1, i});
            unique.add(new int[]{n-2, i});
        }


        for(int t = 0; t < m; t++){
            st = new StringTokenizer(br.readLine());
            d = Integer.parseInt(st.nextToken()) - 1;
            p = Integer.parseInt(st.nextToken());
            move();
            growUp();
        }

        print();
    }

    static void print(){
        int sum = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                sum += map[i][j];
            }
        }
        System.out.print(sum);
    }

    static int[] gdx = new int[]{-1, 1, 1, -1};
    static int[] gdy = new int[]{-1, 1, -1, 1};

    static void growUp(){

        visited = new boolean[n][n];

        for(int i = 0; i < grow.size(); i++){
            int[] current = grow.get(i);
            int x = current[0]; int y = current[1];
            visited[x][y] = true;
            for(int dir = 0; dir < 4; dir++){
                int nx = x + gdx[dir];
                int ny = y + gdy[dir];
                if(isRange(nx, ny) && map[nx][ny] >= 1){
                    map[x][y]++;
                }
            }
        }

        unique = new ArrayList<>();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(!visited[i][j] && map[i][j] >= 2){
                    map[i][j] -= 2;
                    unique.add(new int[]{i, j});
                }
            }
        }

        // StringBuilder sb = new StringBuilder();
        // for(int i = 0; i < n; i++){
        //     for(int j = 0; j < n; j++){
        //         sb.append(map[i][j]).append(" ");
        //     }
        //     sb.append('\n');
        // }
        // System.out.println(sb.toString());
    }

    static void move(){
        grow = new ArrayList<>();
        for(int i = 0; i < unique.size(); i++){
            int[] current = unique.get(i);
            int x = current[0]; int y = current[1];
            for(int m = 1; m <= p; m++){
                int nx = x + dx[d];
                int ny = y + dy[d];
                if(!isRange(nx, ny)){
                    if(nx < 0) nx = n-1;
                    else if(nx >= n) nx = 0;
                    if(ny < 0) ny = n-1;
                    else if(ny >= n) ny = 0;
                }
                x = nx; y = ny;
            }
            grow.add(new int[]{x, y});
            map[x][y] +=1;
        }
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= n || ny < 0 || ny >= n);
    }
}