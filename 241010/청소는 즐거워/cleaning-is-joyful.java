import java.util.*;
import java.io.*;


/**
    n x n
    n은 홀수이다.
    가운데에서 시작하고, 나선형으로 바닥을 청소한다.

    빗자루가 이동할 때마다 빗자루가 이동한 위치의 격자에 있는 먼지가 함께 이동한다.
    비율에 맞게 이동함.
    이동한 위치에 있는 먼지는 모두 없어지고, 비율 만큼 이동함.

**/

public class Main {

    static int[][] map;
    static int n;

    static int[] dx = new int[]{0, 1, 0, -1};
    static int[] dy = new int[]{-1, 0, 1, 0};
    
    static int x;
    static int y;

    static int result;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        result = 0;

        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        x = n / 2;
        y = n / 2;

        int count = 0; // count가 2가되면 move를 1 증가
        int moveP = 1; // move번 이동하고 나면 count 1 증가
        int dir = 0; // move번 이동하고 나면 dir 증가
        int moveCount = 0;

        for(int i = 1; i < (n * n); i++){
            move(dir);
            x = x + dx[dir];
            y = y + dy[dir];
            moveCount++;
            if(moveCount == moveP){
                count++;
                if(count == 2){
                    moveP++;
                    count = 0;
                }
                dir = (dir + 1) % 4;
                moveCount = 0;
            }
        }
        System.out.print(result);
    }

    static void move(int dir){
        map[x][y] = 0;
        int nx = x + dx[dir];
        int ny = y + dy[dir];

        double dust = (double)map[nx][ny];
        map[nx][ny] = 0;
        int num = (int)dust;

        int ndir1 = (dir + 1) % 4;
        int ndir2 = (dir + 3) % 4;

        int p1 = (int)((dust / 100));
        int p2 = (int)((dust / 100) * 2);
        int p5 = (int)((dust / 100) * 5);
        int p7 = (int)((dust / 100) * 7);
        int p10 = (int)((dust / 100) * 10);

        // 1%
        if(isRange(x + dx[ndir1], y + dy[ndir1])){
            map[x+dx[ndir1]][y+dy[ndir1]] += p1;
        }else result += p1;
        if(isRange(x + dx[ndir2], y + dy[ndir2])){
            map[x+dx[ndir2]][y+dy[ndir2]] += p1;
        }else result += p1;
        // 7%
        if(isRange(nx + dx[ndir1], ny + dy[ndir1])){
            map[nx+dx[ndir1]][ny+dy[ndir1]] += p7;
        }else result += p7;
        if(isRange(nx + dx[ndir2], ny + dy[ndir2])){
            map[nx+dx[ndir2]][ny+dy[ndir2]] += p7;
        }else result += p7;
        // 2%
        if(isRange(nx + (dx[ndir1] * 2), ny + (dy[ndir1] * 2))){
            map[(nx + dx[ndir1] * 2)][ny + (dy[ndir1] * 2)] += p2;
        }else result += p2;
        if(isRange(nx + (dx[ndir2] * 2), ny + (dy[ndir2] * 2))){
            map[nx + (dx[ndir2] * 2)][ ny + (dy[ndir2] * 2)] += p2;
        }else result += p2;
        // 10%
        nx += dx[dir];
        ny += dy[dir];
        if(isRange(nx + dx[ndir1], ny + dy[ndir1])){
            map[nx+dx[ndir1]][ny+dy[ndir1]] += p10;
        }else result += p10;
        if(isRange(nx + dx[ndir2], ny + dy[ndir2])){
            map[nx+dx[ndir2]][ny+dy[ndir2]] += p10;
        }else result += p10;
        // 5%
        if(isRange(nx + dx[dir], ny + dy[dir])){
            map[nx+dx[dir]][ny+dy[dir]] += p5;
        }else result += p5;

        num -= (p1 * 2 + p2 * 2 + p5 + p7 * 2 + p10 * 2);
        
        if(isRange(nx, ny)){
            map[nx][ny] += num;
        }else result += num;
    }

    static void print(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                sb.append(map[i][j]).append(" ");
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || ny < 0 || nx >= n || ny >= n);
    }
}