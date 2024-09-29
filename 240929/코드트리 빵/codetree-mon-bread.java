import java.util.*;
import java.io.*;

public class Main {

    static int m; // m명의 사람, 1분에 1번, 2분에 2번이 이동
    static int n; // 격자 크기 n

    static int[] dx = new int[]{-1, 0, 0, 1};
    static int[] dy = new int[]{0, -1, 1, 0};
    
    /**
    대표 로직 3개
    1. 본인이 가고 싶은 편의점 방향으로 1칸 움직임. 상 좌 우 하의 우선 순위로 움직인다.
    도달하기 까지 거쳐야 하는 칸의 수가 최소가 되는 거리를 뜻한다. -> 매번 경로 길이를 알아야 한다는 거인듯
    
    2. 편의점에 도착하면 멈추고, 해당 칸은 지나갈 수 없다.
    ! 격자에 있는 사람들이 모두 이동한 뒤 해당 칸을 못 움직이게 해야함.

    3. t번 사람은 자신이 가고 싶은 편의점과 가장 가까이 있는 베이스 캠프에 들어간다.
    자신이 가고 싶은 편의점과 가장 가까이 있는 베이스 캠프에 들어간다. 겹치면 행이 작은, 열이 작은 순서로

    3번까지 진행하고 !의 불가능 칸 적용하기 + 베이스 캠프 못움직이게 적용하기.
    **/

    static int[][] map; // 여기에는 움직이지 못하는 칸 표시하기.
    static People[] peoples; // 사람 정보, 앞에는 사람 번호, 뒤에는 정보 x, y, 목표 좌표 x, y
    static boolean[][] visited;
    static boolean[] arrive;

    static class People{
        int x; int y;
        int gx; int gy;
        People(int x, int y, int gx, int gy){
            this.x = x;
            this.y = y;
            this.gx = gx;
            this.gy = gy;
        }
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        
        map = new int[n][n];
        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        arrive = new boolean[m];
        peoples = new People[m];
        for(int i = 0; i < m; i++){
            st = new StringTokenizer(br.readLine());
            int gx = Integer.parseInt(st.nextToken()) - 1;
            int gy = Integer.parseInt(st.nextToken()) - 1;
            peoples[i] = new People(0, 0, gx, gy);
        }

        // 이제 맵에서 2 또는 1이 베이스캠프임.
        int k = 0;
        boolean check = true;
        while(check){
            //1. 본인이 가고 싶은 편의점 방향으로 1칸 움직임. 상 좌 우 하의 우선 순위로 움직인다.
            //도달하기 까지 거쳐야 하는 칸의 수가 최소가 되는 거리를 뜻한다. -> 매번 경로 길이를 알아야 한다는 거인듯
            move(k);
            //2. 편의점에 도착하면 멈추고, 해당 칸은 지나갈 수 없다.
            //! 격자에 있는 사람들이 모두 이동한 뒤 해당 칸을 못 움직이게 해야함.
            findArrive(k);
            //3. t번 사람은 자신이 가고 싶은 편의점과 가장 가까이 있는 베이스 캠프에 들어간다.
            //자신이 가고 싶은 편의점과 가장 가까이 있는 베이스 캠프에 들어간다. 겹치면 행이 작은, 열이 작은 순서로
            //3번까지 진행하고 !의 불가능 칸 적용하기 + 베이스 캠프 못움직이게 적용하기.
            baseCamp(k);
            check = next();
            findBasecamp(k);
            check = next();
            k++;

        }

        System.out.print(k);
    }

    static boolean next(){
        for(int i = 0; i < m; i++){
            if(!arrive[i]) return true;
        }

        return false;
    }

    static void baseCamp(int k){
        if(k >= m) return;
        map[peoples[k].x][peoples[k].y] = -1;
    }

    static void findArrive(int k){
        if(k > m) k = m;
        for(int i = 0; i < k; i++){
            if(!arrive[i]){
                if(peoples[i].x == peoples[i].gx && peoples[i].y == peoples[i].gy){
                    arrive[i] = true;
                    map[peoples[i].x][peoples[i].y] = -1;
                }
            }
        }
    }

    static void printPeople(){
        for(int i = 0; i < m; i++){
            System.out.printf("num: %d, x: %d, y: %d \n", i, peoples[i].x, peoples[i].y);
        }
        System.out.println(" ");
    }

    static void move(int k){
        if(k > m) k = m;
        for(int i = 0; i < k; i++){
            if(!arrive[i]){
                visited = new boolean[n][n];
                Queue<int[]> que = new LinkedList<>();
                for(int dir = 0; dir < 4; dir++){
                    int nx = peoples[i].x + dx[dir];
                    int ny = peoples[i].y + dy[dir];
                    if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] != -1){
                        que.offer(new int[]{nx, ny, dir});
                        visited[nx][ny] = true;
                    }                
                }

                while(!que.isEmpty()){
                    int[] current = que.poll();
                    if(current[0] == peoples[i].gx && current[1] == peoples[i].gy){
                        peoples[i].x += dx[current[2]];
                        peoples[i].y += dy[current[2]];
                        break;
                    }
                    for(int dir = 0; dir < 4; dir++){
                        int nx = current[0] + dx[dir];
                        int ny = current[1] + dy[dir];
                        if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] != -1){
                            que.offer(new int[]{nx, ny, current[2]});
                            visited[nx][ny] = true;
                        }
                    }
                }
            }
        }
    }

    static void findBasecamp(int number){
        if(number >= m) return;
        int gx = peoples[number].gx;
        int gy = peoples[number].gy;

        Queue<int[]> que = new LinkedList<>();
        visited = new boolean[n][n];
        visited[gx][gy] = true;
        que.offer(new int[]{gx, gy});

        while(!que.isEmpty()){
            int[] current = que.poll();
            if(map[current[0]][current[1]] == 1){
                map[current[0]][current[1]] = -1;
                peoples[number].x = current[0];
                peoples[number].y = current[1];
                break;
            }
            for(int dir = 0; dir < 4; dir++){
                int nx = current[0] + dx[dir];
                int ny = current[1] + dy[dir];
                if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] != -1){
                    visited[nx][ny] = true;
                    que.offer(new int[]{nx, ny});
                }
            }
        }
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= n || ny < 0 || ny >= n);
    }
}