import java.util.*;
import java.io.*;

/**
    n x n 크기의 격자이다.
    좌상단은 1,1이다

    미로의 상태
    빈칸(이동 가능)
    벽(이동 불가, 내구도 1~9를 갖는다, 회전하면 내구도가 1씩 까인다)
    내구도가 0이되면 빈칸임.
    출구에 도착하면 '즉시' 탈출한다.

    1초마다 모든 참가자 '한 칸'씩 이동함.
    - 최단거리 |x1 - x2| + |y1 - y2|로 정의된다. (distance 함수)
    - 모든 참가자가 '동시'에 움직인다.
    - 상하좌우로 움직이고, 벽이 없 곳으로 이동한다.
    - 움직인 칸이 현재 칸보다 출구에 가까워야 움직인다.
    - 움직이는 칸이 2개이상이면 상,하로 움직이는 것을 우선시 한다.
    - 움직일 수 없으면 안 움직임
    - 한 칸에 2명 이상의 참가자가 있을 수 있다. '겹치기 가능'
    
    미로의 회전
    - 한 명 이상의 참가와 출구를 포함한 가장 작은 정사각형을 잡는다.
    - 가장 작은 크기를 갖는 정사각형이 2개 이상이면, r좌표가 작은, c좌표가 작은 순서로 '우선순위'
    - 시계방향으로 회전하기, 벽 내구도 1씩 깎인다.
    
    K초 동안 위의 과정이 반복된다.
    만약 모든 참가자가 탈출에 성공하면 게임이 끝난다.
    모든 참가자들의 이동 거리 합과 출구 좌표를 출력하는 프로그램
    '참가자가 도착했는지 표시하는 배열'
    '참가자 이동 거리 합을 표시하는 배열'


    특이사항
    '맵에는 벽이랑 빈칸만 표시하기'
**/

public class Main {

    static int n;
    static int m;
    static int k;

    static int[][] map;
    static int[] isArrive;
    static int[] points;

    // 목표 출구 좌표
    static int gx;
    static int gy;

    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, -1, 1};

    static List<int[]> peoples;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        isArrive = new int[m];
        points = new int[m];

        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        peoples = new ArrayList<>();
        

        for(int i = 0; i < m; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            peoples.add(new int[]{x, y});
        }

         st = new StringTokenizer(br.readLine());
         gx = Integer.parseInt(st.nextToken()) - 1;
         gy = Integer.parseInt(st.nextToken()) - 1;

         for(int t = 0; t < k; t++){
            // 사람이 움직인다.
            move();
            if(isAll()) break;
            rotate();
            if(isAll()) break;
         }

        int result = 0;
        for(int i = 0; i < m; i++){
            result += points[i];
        }
        System.out.println(result);
        System.out.print((gx+1)  + " " + (gy+1));
    }

    static boolean isAll(){
        for(int i = 0; i < m; i++){
            if(isArrive[i] == 0) return false;
        }
        return true;
    }

    static void rotate(){
        // 최종정사각형 크기, 좌표
        int fsize = n;
        int fx = 0; int fy = 0;

        for(int size = 2; size < n; size++){
            boolean findCheck = false;
            for(int i = 0; i <= n - size; i++){
                for(int j = 0; j <= n - size; j++){
                    if(findSquare(i, j, size)){
                        findCheck = true;
                        fx = j; fy = i;
                        fsize = size;
                        break;
                    }
                }
                if(findCheck) break;
            }
            if(findCheck) break;
        }

        // System.out.println("size: " + fsize + " fx: " + fx + " fy: " + fy);

        // 회전하기
        int[][] temp = new int[fsize][fsize];

        for(int i = 0; i < fsize; i++){
            for(int j = 0; j < fsize; j++){
                temp[j][fsize - 1 - i] = map[fx + i][fy + j];
            }
        }

        for(int i = 0; i < fsize; i++){
            for(int j = 0; j < fsize; j++){
                if(temp[i][j] > 0)
                    map[fx + i][fy + j] = temp[i][j] - 1;
                else
                     map[fx + i][fy + j] = temp[i][j];
            }
        }

        peopleRotate(fx, fy, fsize);
        // 사람도 회전해야한다.
        
    }

    static void peopleRotate(int x, int y, int size){
        for(int i = 0; i < m; i++){
            if(isArrive[i] != 0) continue;
            int[] current = peoples.get(i);
            
            if(current[0] >= x && current[0] < x + size
            && current[1] >= y && current[1] < y + size){
                // 회전 대상이다 위치 바꿔주고 값 수정해주면 끝.
                int tx = current[1] - y;
                int ty = size - 1 -(current[0] - x);
                current[0] = x + tx;
                current[1] = y + ty;
            }
        }

        int nx = gy - y;
        int ny = size - 1 - (gx - x);
        gx = x + nx;
        gy = y + ny; 
    }

    static boolean findSquare(int x, int y, int size){
        boolean flag1 = false;
        boolean flag2 = false;
        
        for(int i = y; i < y + size; i++){
            for(int j = x; j < x + size; j++){
                if(gx == i && gy == j){
                    flag1 = true;
                }
                for(int u = 0; u < m; u++){
                    if(isArrive[u] != 0) continue;
                    int[] node = peoples.get(u);
                    if(node[0] == i && node[1] == j){
                        flag2 = true;
                    }
                }
                if(flag1 && flag2) break;
            }
            if(flag1 && flag2) break;
        }

        return (flag1 && flag2);
    }

    static void print(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                sb.append(map[i][j]).append(" ");
            }
            sb.append('\n');
        }

        for(int i = 0; i < m; i++){
            sb.append(i).append(" x: ").append(peoples.get(i)[0]).append(" y: ").append(peoples.get(i)[1]).append('\n');
        }
        sb.append("gx: ").append(gx).append(" gy: ").append(gy).append('\n');

        System.out.println(sb.toString());
    }

    static void move(){
        for(int i = 0; i < m; i++){
            if(isArrive[i] != 0) continue;
            int[] current = peoples.get(i);
            int currentDistance = distance(current[0], current[1], gx, gy);
            int fdir = -1;
            for(int dir = 0; dir < 4; dir++){
                int nx = current[0] + dx[dir];
                int ny = current[1] + dy[dir];
                if(isRange(nx, ny) && map[nx][ny] == 0 && currentDistance > distance(nx, ny, gx, gy)){
                    currentDistance = distance(nx, ny, gx, gy);
                    fdir = dir;
                }
            }
            if(fdir == -1) continue;
            int nx = current[0] + dx[fdir];
            int ny = current[1] + dy[fdir];
            current[0] = nx;
            current[1] = ny;
            points[i]++;
            if(nx == gx && ny == gy) isArrive[i] = 1;
        }
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || ny < 0 || nx >= n || ny >= n);
    }

    static int distance(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}