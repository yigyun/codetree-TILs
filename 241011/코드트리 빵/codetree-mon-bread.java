import java.util.*;
import java.io.*;

/**
    m명의 사람이 있다
    1번 사람은 1분 ~ m번 사람은 m분에 출발한다.
    n x n 격자

    1. 이동
    - 본인이 가고 싶은 편의점 방향으로 1칸 움직인다.
    - 최단거리로 이동한다 상 좌 우 하의 우선 순위로 움직인다.
    2. 편의점에 도착
    - 해당 편의점에서 멈추고, 해당 칸은 지나갈 수 없다.
    - 모든 사람이 이동 후에 적용해야 한다.
    3. t번 사람이 자신이 가고 싶은 편의점과 가까운 베이스 캠프에 들어간다.
    - 최단거리에 해당하는 곳의 베이스 캠프에 들어간다.
    - 행이 작은, 열이 작은 순서로 베이스 캠프에 들어간다.
    4. 해당 베이스 캠프 칸을 움직일 수 없는 칸으로 만든다.


    도착한 사람 관리 배열
    맵
    사람 정보 보관 하는 리스트나 배열
    가지 못하는 곳 -1, 빈칸 0, 베캠 0, 편의점 2
    t로 시간을 관리하기


**/

public class Main {

    static int n;
    static int m;
    static int t;

    static int[] dx = new int[]{-1, 0, 0, 1};
    static int[] dy = new int[]{0, -1, 1, 0};

    static int[][] map;

    static int[] isArrive;

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
    
    static List<People> peoples;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        t = 0;
        
        map = new int[n][n];
        isArrive = new int[m];
        peoples = new ArrayList<>();

        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for(int i = 0; i < m; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            map[x][y] = 2; // 편의점
            peoples.add(new People(0, 0, x, y));
        }

        while(isAll()){
            move();
            store();
            baseCamp();
            t++;
        }

        System.out.print(t);
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
            sb.append("x: ").append(peoples.get(i).x).append(" y: ").append(peoples.get(i).y).append('\n');
        }

        for(int i = 0; i < m; i++){
            sb.append("people - ").append("gx: ").append(peoples.get(i).gx).append(" gy: ").append(peoples.get(i).gy).append('\n');
        }
        System.out.println(sb.toString());
    }

    static void baseCamp(){
        if(t >= m) return;
        // t번 사람이 베이스 캠프에 들어간다
        People people = peoples.get(t);
        Queue<int[]> que = new LinkedList<>();
        boolean[][] visited = new boolean[n][n];
        visited[people.gx][people.gy] = true;
        que.offer(new int[]{people.gx, people.gy});

        int fx = Integer.MAX_VALUE;
        int fy = Integer.MAX_VALUE;

        while(!que.isEmpty()){
            int size = que.size();
            boolean check = true;
            for(int i = 0; i < size; i++){
                int[] current = que.poll();
                if(map[current[0]][current[1]] == 1){
                    if(fx == current[0]){
                        fy = fy > current[1] ? current[1] : fy;
                    }else if(fx > current[0]){
                        fx = current[0];
                        fy = current[1];
                    }
                    check = false;
                    continue;
                }
                for(int dir = 0; dir < 4; dir++){
                    int nx = current[0] + dx[dir];
                    int ny = current[1] + dy[dir];
                    if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] != -1){
                        que.offer(new int[]{nx, ny});
                        visited[nx][ny] = true;
                    }
                }
            }
            if(!check) break;
        }

        people.x = fx;
        people.y = fy;
        map[people.x][people.y] = -1;
    }

    //  2. 편의점에 도착
    // - 해당 편의점에서 멈추고, 해당 칸은 지나갈 수 없다.
    // - 모든 사람이 이동 후에 적용해야 한다.

    static void store(){
        int size = t;
        if(size > m) size = m;
        for(int i = 0; i < size; i++){
            if(isArrive[i] != 0) continue;
            People people = peoples.get(i);
            if(people.x == people.gx && people.y == people.gy){
                isArrive[i] = 1;
                map[people.x][people.y] = -1;
            }
        }
    }

    //  1. 이동
    // - 본인이 가고 싶은 편의점 방향으로 1칸 움직인다.
    // - 최단거리로 이동한다 상 좌 우 하의 우선 순위로 움직인다.
    // people 이동하기. basecamp가 유저한테 소속되면 표시 변화시켜서 못가게 해야함.
    // 베이스캠프 3으로 표시하기.
    static void move(){
        int size = t;
        if(size > m) size = m;
        for(int i = 0; i < size; i++){
            if(isArrive[i] != 0) continue;
            People current = peoples.get(i);

            Queue<int[]> que = new LinkedList<>();
            boolean[][] visited = new boolean[n][n];
            visited[current.x][current.y] = true;

            int fdir = -1;

            for(int dir = 0; dir < 4; dir++){
                int nx = current.x + dx[dir];
                int ny = current.y + dy[dir];
                if(isRange(nx, ny) && map[nx][ny] != -1){
                    que.offer(new int[]{nx, ny, dir});
                    visited[nx][ny] = true;
                }
            }

            while(!que.isEmpty()){
                int[] node = que.poll();
                
                if(node[0] == current.gx && node[1] == current.gy){ 
                    fdir = node[2];
                    break;
                }

                for(int dir = 0; dir < 4; dir++){
                    int nx = node[0] + dx[dir];
                    int ny = node[1] + dy[dir];
                    if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] != -1){
                        que.offer(new int[]{nx, ny, node[2]});
                        visited[nx][ny] = true;
                    }
                }
            }

            if(fdir != -1){
                int nx = current.x + dx[fdir];
                int ny = current.y + dy[fdir];
                current.x = nx; current.y = ny;
            }
        }
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || ny < 0 || nx >= n || ny >= n);
    }

    static boolean isAll(){
        for(int i = 0; i < m; i++){
            if(isArrive[i] == 0) return true;
        }
        return false;
    }
}