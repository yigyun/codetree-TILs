import java.util.*;
import java.io.*;

/**
    r x c 격자 1,1이 좌상단임.
    동쪽, 서쪽, 남쪽은 막혀있음.
    총 K명의 정령이 있다.
    골렘은 십자 모양의 구조를 가진다.
    총 5칸을 차지한다.
    어느 방향에서든 탑승 가능
    내리는거는 정해진 출구로만 가능하다.(중앙 제외)

    i번째로 숲을 탐색하는 골렘은 숲 가장 북쪽에서 시작한다.
    골렘의 중앙이 입려되는 ci 열이고 출구는 입력되는 di 방향에 있다.

    우선순위 맞게 이동한다. 더 이상 움직이지 못할 때까지 골렘이 이동함
    
    * 골렘의 이동
    1. 남쪽으로 한 칸 이동
    2. 남쪽으로 이동 불가하면 서쪽 방향으로 회전하고 이동
    - 서쪽 방향으로 이동 1칸하고 출구 반시계방향으로 이동하기
    3. 1,2 둘다 이동못했으면 동쪽 방향으로 회전하면서 내려가기.
    - 동쪽 방향으로 이동 1칸하기, 출구 시계방향으로 회전시키기.
    
    * 정령은 상하좌우 인접한 칸을 이동하면서 출구와 붙어있는 다른 정령칸도 이동하면서,
     가장 낮은 행 위치를 찾아낸다.
    
    * 골렘이 최대한 움직였는데 숲을 벗어난 상태인경우
    - 해당 골렘을 포함해서 모든 골렘을 숲에서 뺀다
    - 다음 골렘부터 새로 숲의 탐색이 시작된다.
    - 값 추가 x
    - 합이 사라지는 것은 아니다.

    * 


    정령의 최종 위치의 행 번호의 합을 구한는 것이 목적이다.
**/

/**
    자료구조 정리
    map[r][c]
    좌표
    K(정령 수)
    정령을 관리할 것.
**/

public class Main {

    static int r;
    static int c;
    static int k;

    // 북동남서
    static int[] dx = new int[]{-1, 0, 1, 0};
    static int[] dy = new int[]{0, 1, 0, -1};

    // 골렘이 남쪽으로 최대한 이동하고 나서 확인하고 맵에 반영
    static int[][] map;
    static int[][] exit;

    // 정령
    static class Sprit{
        int x; int y; // 중앙
        int dir; // 출구 방향
        Sprit(int x, int y, int dir){
            this.x = x;
            this.y=  y;
            this.dir = dir;
        }
    }

    static List<Sprit> sprits;

    static int result;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        r = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        map = new int[r][c];
        exit = new int[r][c];
        sprits = new ArrayList<>();
        result = 0;
        
        for(int i = 0; i < k; i++){
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int dir = Integer.parseInt(st.nextToken());
            sprits.add(new Sprit(-2, y, dir));
        }

        for(int i = 0; i < k; i++){
            golem(i);
            // 이때 골렘의 위치가 범위 밖에 있으면 숲 비우기(맵 초기화)
            if(!away(i)) { clearMap(); continue;}
            moveSprit(i);
        }

        System.out.print(result);
    }

    // * 정령은 상하좌우 인접한 칸을 이동하면서 출구와 붙어있는 다른 정령칸도 이동하면서,
    //  가장 낮은 행 위치를 찾아낸다.

    static void moveSprit(int num){
        // 맵에 현재 sprit 표기하기
        Sprit sprit = sprits.get(num);
        map[sprit.x][sprit.y] = num + 1;

        for(int dir = 0; dir < 4; dir++){
            int nx = sprit.x + dx[dir];
            int ny = sprit.y + dy[dir];
            map[nx][ny] = num + 1;
            if(dir == sprit.dir) exit[nx][ny] = 1;
        }
        
        // 정령이 이동한다.
        // 정령이 자신이랑 같은 수의 위치를 큐에 담는다.
        // 만약 0이 아닌 다른 숫자를 만나면 현재 위치가 exit인지 따지고
        // exit 위치에 있다면 큐에 넣어준다.
        // 모든 큐에 들어간 수 중 가장 큰 수의 x 값을 결과에 추가한다.
        Queue<int[]> que = new LinkedList<>();
        boolean[][] visited = new boolean[r][c];
        visited[sprit.x][sprit.y] = true;
        que.offer(new int[]{sprit.x, sprit.y, map[sprit.x][sprit.y]});
        int max = -1;
        while(!que.isEmpty()){
            int[] current = que.poll();
            max = Math.max(max, current[0]);
            for(int dir = 0; dir < 4; dir++){
                int nx = current[0] + dx[dir];
                int ny = current[1] + dy[dir];
                if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] != 0){
                    if(map[nx][ny] != current[2] && exit[current[0]][current[1]] == 1){
                        que.offer(new int[]{nx, ny, map[nx][ny]});
                        visited[nx][ny] = true;
                    }else if(map[nx][ny] == current[2]){
                        que.offer(new int[]{nx, ny, current[2]});
                        visited[nx][ny] = true;
                    }
                }
            }
        }
        if(max > 0)
            result += (max + 1);
    }

    static void clearMap(){
        map = new int[r][c];
        exit = new int[r][c];
    }

    static boolean away(int num){
        Sprit sprit = sprits.get(num);
        if(!isRange(sprit.x, sprit.y)) return false;
        for(int dir = 0; dir < 4; dir++){
            int nx = sprit.x + dx[dir];
            int ny = sprit.y + dy[dir];
            if(!isRange(nx, ny)) return false;
        }

        return true;
    }

    static void print(){
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        // for(int i = 0; i < k; i++){
        // sb.append(i).append(" - ").append("x: ").append(sprits.get(i).x)
        // .append(" y: ").append(sprits.get(i).y).append('\n');
        // }

        for(int i = 0; i < r; i++){
            for(int j = 0; j < c; j++){
                sb.append(map[i][j]).append(" ");
                sb2.append(exit[i][j]).append(" ");
            }
            sb.append('\n');
            sb2.append('\n');
        }

        System.out.println(sb.toString());
        System.out.println(sb2.toString());
    }

    // * 골렘의 이동
    // 1. 남쪽으로 한 칸 이동
    // 2. 남쪽으로 이동 불가하면 서쪽 방향으로 회전하고 이동
    // - 서쪽 방향으로 이동 1칸하고 출구 반시계방향으로 이동하기
    // 3. 1,2 둘다 이동못했으면 동쪽 방향으로 회전하면서 내려가기.
    // - 동쪽 방향으로 이동 1칸하기, 출구 시계방향으로 회전시키기. 
    static void golem(int num){
        Sprit sprit = sprits.get(num);

        boolean isMove = true;        
        while(isMove){
            isMove = false;
            isMove = south(sprit);
            if(!isMove) isMove = west(sprit);
            if(!isMove) isMove = east(sprit);
        }
    }

    static boolean east(Sprit sprit){
        if(isRange(sprit.x, sprit.y + 2)){
           if(map[sprit.x][sprit.y + 2] != 0) return false;
        }
        if(isRange(sprit.x - 1, sprit.y + 1)){
           if(map[sprit.x - 1][sprit.y + 1] != 0) return false;
        }
        if(isRange(sprit.x + 1, sprit.y + 1)){
           if(map[sprit.x + 1][sprit.y + 1] != 0) return false;
        }
        if(sprit.x == r -2 || sprit.y + 1 >= c || sprit.y + 2 >= c) return false;
        // 방향 회전하고 값 바꿔줘야함.
        sprit.dir = (sprit.dir + 1) % 4;
        sprit.y += 1;
        if(!south(sprit)){
            sprit.dir = sprit.dir - 1 < 0 ? 3 : sprit.dir - 1;
            sprit.y -= 1;
            return false;
        }

        return true; 
    }

    static boolean west(Sprit sprit){
        if(isRange(sprit.x, sprit.y - 2)){
           if(map[sprit.x][sprit.y - 2] != 0) return false;
        }
        if(isRange(sprit.x - 1, sprit.y - 1)){
           if(map[sprit.x - 1][sprit.y - 1] != 0) return false;
        }
        if(isRange(sprit.x + 1, sprit.y - 1)){
           if(map[sprit.x + 1][sprit.y - 1] != 0) return false;
        }
        if(sprit.x == r-2 || sprit.y - 1 < 0 || sprit.y - 2 < 0) return false;

        
        // 방향 회전하고 값 바꿔줘야함.
        sprit.dir = sprit.dir - 1 < 0 ? 3 : sprit.dir - 1;
        sprit.y -= 1;

        if(!south(sprit)){
            sprit.dir = (sprit.dir + 1) % 4;
            sprit.y += 1;
            return false;
        }
        return true;
    }

    static boolean south(Sprit sprit){
        // 남쪽은 중심 기준 (x + 2, y), (x + 1, y - 1), (x + 1, y -1)
        if(isRange(sprit.x + 2, sprit.y)){
            if(map[sprit.x+2][sprit.y] != 0) return false;
        }
        if(isRange(sprit.x + 1, sprit.y + 1)){
            if(map[sprit.x + 1][sprit.y + 1] != 0) return false;
        }
        if(isRange(sprit.x + 1, sprit.y - 1)){
            if(map[sprit.x + 1][sprit.y - 1] != 0) return false;
        }
        if(sprit.x+2 >= r || sprit.x + 1 >= r){
            return false;
        }

        sprit.x += 1;
        return true;
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || ny < 0 || nx >= r || ny >= c);
    }
}