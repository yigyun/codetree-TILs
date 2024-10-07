import java.util.*;
import java.io.*;
// 11시 30분 시작

/**
    N x M, 모든 위치에는 포탑이 있다 포탑은 N x M
    공격력이 0 이하가 되면 부서진 포탑이다.
    공격자에서 배제된다.
    처음부터 공격력이 0인 포탑이 있을 수 있음.
    좌상단 1,1로 시작함.

    4가지 액션을 순서대로 K번반복한다.
    만약 부서지지 않은 포탑이 1개가 된다면 그 즉시 중지된다.
    살아있는 애가 몇명이냐 알아야 한다는 뜻.

    1. 공격자 선정
    살아있는 포탑 중 가장 약한 포탑이 공격자로 선정
    공격력 + N + M을 가지게 한다.

    선정 기준
        1. 공격력이 낮아야 한다.
        2. 가장 최근에 공격한 포탑
        3. 행 + 열의 합이 가장 큰 포탑
        4. 열 이 가장 큰 포탑

    2. 공격자가 공격하기
    공격자를 제외하고, 가장 강한 포탑을 공격한다.
    공격 대상 선정하기

    선정 기준
        1. 공격력이 가장 큰 포탑을 선정.
        2. 공격한지 가장 오래된 포탑이 선정
        3. 행 + 열의 합이 가장 작은 포탑 선정
        4. 열이 가장 작은 포탑

    2 - 1 레이저 공격
        1. 상하좌우의 4개 방향으로 이동하기
        2. 부서진 포탑은 지나갈 수 없다.
        3. 막힌 방향은 반대편으로 나온다. 
        * 우-하-좌-상 우선순위대로 먼저 움직인 경로가 선택된다.
        도달할 수 없다면 포탄 공격으로 넘어감.
    2 - 2 포탄 공격
        공격 대상에게 포탄을 던지는데, 공격자 공격력 만큼의 피해를 받는다.(공격 대상)
        주위 8개 방향에 있는 포탑이 피해를 받는다. (공격자 공격력 / 2)
        공격자는 영향 x

    3. 포탑 부서짐
    공격력이 0이하가 된 포탑은 부서진다.

    4. 포탑 정비
    부서지지 않은 포탑 중, 공격과 무관했던 포탑은 공격력이 1씩 올라간다.
    공격자도 아니고, 피해도 없는 포탑

    만약 부서지지 않은 포탑이 1개가 된다면 그 즉시 중지된다.

     // 자료구조 정리하기 전체에서 쓸거
     map, 살아있는 포탑 관리하기, potab 클래스에 공격 or 피해 여부 + 공격력 + 마지막 공격한 라운드
     List[N][M] 좌표에 potab 정보를 담아낼 것.
     List<Potab>은 살아있는 포탑 관리?
     visited, N, M, K

**/

public class Main {

    static int N;
    static int M;
    static int K;
    
    static int[][] map;
    
    static class Potab{
        int x; int y;
        boolean isAttack;
        int power;
        int last;
        Potab(int x, int y, boolean isAttack, int power, int last){
            this.x = x;
            this.y = y;
            this.isAttack = isAttack;
            this.power = power;
            this.last = last;
        }
    }

    static List<Potab>[][] potabs;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[N][M];
        potabs = new ArrayList[N][M];
        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                potabs[i][j] = new ArrayList<>();
            }
        }

        for(int i = 0; i < N; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < M; j++){
                int num = Integer.parseInt(st.nextToken());
                map[i][j] = num;
                if(num != 0){
                    potabs[i][j].add(new Potab(i, j, false, num, 0));
                }
            }
        }

        for(int t = 1; t <= K; t++){
            attack(t);
            
            int live = potabM();
            if(live <= 1) break;
        }

        print();
    }

    static int potabM(){

        int count = 0;

        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                if(potabs[i][j].size() != 0){
                    Potab potab = potabs[i][j].get(0);
                    if(map[i][j] <= 0){
                        map[i][j] = 0;
                        potabs[i][j].remove(0);
                    }else{
                        count++;
                        if(!potab.isAttack){
                            map[i][j] += 1;
                            potab.power += 1;
                        }else{
                            potab.isAttack = false;
                        }
                    }
                }
            }
        }
        return count;
    }

    static int[] pdx = new int[]{0, 1, 0, -1, -1, -1, 1, 1};
    static int[] pdy = new int[]{1, 0, -1, 0, -1, 1, -1, 1};

    static void potan(Potab atc, Potab atd){
        atd.power -= atc.power;
        map[atd.x][atd.y] = atd.power;
        atd.isAttack = true;
        
        for(int dir = 0; dir < 8; dir++){
            int nx = atd.x + pdx[dir];
            int ny = atd.y + pdy[dir];
            if(isRange(nx, ny)){
                if(map[nx][ny] > 0){
                    Potab potab = potabs[nx][ny].get(0);
                    potab.power -= (atc.power / 2);
                    map[nx][ny] = potab.power;
                    potab.isAttack = true;
                }
            }else{
                // 꼭짓점이면 특별하게 처리하기.
                    if(dir == 0) ny = 0;
                    else if(dir == 1) nx = 0;
                    else if(dir == 2) ny = M - 1;
                    else if(dir == 3) nx = N - 1;
                    else if(dir == 4) {
                        if(nx < 0 || nx >= N) nx = N - 1;
                        if(ny < 0 || ny >= M) ny = M - 1;
                    }else if(dir == 5){
                        if(nx < 0 || nx >= N) nx = N -1;
                        if(ny < 0 || ny >= M) ny = 0;
                    }else if(dir == 6){
                        if(nx < 0 || nx >= N) nx = 0;
                        if(ny < 0 || ny >= M) ny = M - 1;
                    }else if(dir == 7){
                        if(nx < 0 || nx >= N) nx = 0;
                        if(ny < 0 || ny >= M) ny = 0;
                    }

                    if(atd.x == 0){
                        if(atd.y == 0){
                            if(dir == 4){
                                nx = N -1; ny = M - 1;
                            }
                        }else if(atd.y == M - 1){
                            if(dir == 5){
                                nx = N - 1; ny = 0;
                            }
                        }
                    }else if(atd.x == N - 1){
                        if(atd.y == 0){
                            if(dir == 6){
                                nx = 0; ny = M - 1;
                            }
                            
                        }else if(atd.y == M - 1){
                            if(dir == 7){
                                nx = 0; ny = 0;
                            }
                        }
                }
                if(nx == atc.x && ny == atc.y) continue;

                if(map[nx][ny] > 0){
                    Potab potab = potabs[nx][ny].get(0);
                    potab.power -= (atc.power / 2);
                    map[nx][ny] = potab.power;
                    potab.isAttack = true;
                }
            }
        }
    }

    static int[] ldx = new int[]{0, 1, 0, -1};
    static int[] ldy = new int[]{1, 0, -1, 0};

    static List<int[]> laser(Potab atc, Potab atd){
        // System.out.printf("x: %d, y: %d, power: %d\n", atc.x, atc.y, atc.power);
        // System.out.printf("x: %d, y: %d, power: %d\n", atd.x, atd.y, atd.power);
        // PriorityQueue<int[]> que = new PriorityQueue<>((o1, o2) -> {
        //     if(o1[3] == o2[3]){
        //         return o1[2] - o2[2];
        //     }
        //     return o1[3] - o2[3];
        // });

        Queue<int[]> que = new LinkedList<>();

        int[][] visited = new int[N][M];
        visited[atc.x][atc.y] = 9;

        int depth = 0;

        for(int dir = 0; dir < 4; dir++){
            int nx = atc.x + ldx[dir];
            int ny = atc.y + ldy[dir];
            if(isRange(nx, ny)){
                if(visited[nx][ny] == 0 && map[nx][ny] > 0){
                    visited[nx][ny] = dir + 2;
                    que.offer(new int[]{nx, ny, dir, depth});
                }
            }else{
                if(dir == 0) ny = 0;
                else if(dir == 1) nx = 0;
                else if(dir == 2) ny = M - 1;
                else if(dir == 3) nx = N - 1;
                if(visited[nx][ny] == 0 && map[nx][ny] > 0){
                    visited[nx][ny] = dir + 2;
                    que.offer(new int[]{nx, ny, dir, depth});
                }
            }
        }

        int count = 0;

        boolean check = false;

        while(!que.isEmpty() && !check){
            int size = que.size();
            depth++;
            for(int i = 0; i < size; i++){
                int[] current = que.poll();
                int x = current[0]; int y = current[1];
                if(x == atd.x && y == atd.y){
                     check = true; 
                     break;
                }

                for(int dir = 0; dir < 4; dir++){
                    int nx = x + ldx[dir];
                    int ny = y + ldy[dir];
                    if(!isRange(nx, ny)){
                        if(dir == 0) ny = 0;
                        else if(dir == 1) nx = 0;
                        else if(dir == 2) ny = M - 1;
                        else if(dir == 3) nx = N - 1;
                    }
                    if(visited[nx][ny] == 0 && map[nx][ny] > 0){
                        visited[nx][ny] = dir + 2;
                        que.offer(new int[]{nx, ny, dir, depth});
                    }
                }
            }
        }

        List<int[]> list = new ArrayList<>();

        if(check){ // attack하기
            //visited에 있는 숫자 -1이 내가 향한 방향임.
            int x = atd.x; int y = atd.y;
            
            while(x != atc.x || y != atc.y){
                list.add(new int[]{x, y});
                int dir = (visited[x][y]) % 4;
                int nx = x + ldx[dir];
                int ny = y + ldy[dir];
                if(isRange(nx, ny)){
                    x = nx; y = ny;
                }else{
                    if(dir == 0) ny = 0;
                    else if(dir == 1) nx = 0;
                    else if(dir == 2) ny = M - 1;
                    else if(dir == 3) nx = N - 1;
                    x = nx; y = ny;
                }
            }

            for(int i = 0; i < list.size(); i++){
                int[] node = list.get(i);
                Potab potab = potabs[node[0]][node[1]].get(0);
                if(i == 0) potab.power -= map[atc.x][atc.y];
                else{
                    potab.power -= (map[atc.x][atc.y] / 2);
                }
                potab.isAttack = true; // 공격 또는 공격 당한 대상 여부, 수리에서 false로 복원.
                map[node[0]][node[1]] = potab.power;
            }
            // StringBuilder sb = new StringBuilder();
            // for(int i = 0; i < N; i++){
            //     for(int j = 0; j < M; j++){
            //         sb.append(visited[i][j]).append(" ");
            //     }
            //     sb.append("\n");
            // }
            // System.out.println(sb.toString());
            // print();
        }
        return list;
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= N || ny < 0 || ny >= M);
    }

    static void attack(int t){
        PriorityQueue<Potab> que1 = new PriorityQueue<>((o1, o2) -> {
            if(o1.power == o2.power){
                if(o1.last == o2.last){
                    if((o1.x + o1.y) == (o2.x + o2.y)){
                        return o2.y - o1.y;
                    }
                    return (o2.x + o2.y) - (o1.x + o1.y);
                }
                return o2.last - o1.last;
            }
            return o1.power - o2.power;
        });
        PriorityQueue<Potab> que2 = new PriorityQueue<>((o1, o2) -> {
            if(o1.power == o2.power){
                if(o1.last == o2.last){
                    if((o1.x + o1.y) == (o2.x + o2.y)){
                        return o1.y - o2.y;
                    }
                    return (o1.x + o1.y) - (o2.x + o2.y);
                }
                return o1.last - o2.last;
            }
            return o2.power - o1.power;
        });

        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                if(map[i][j] != 0){
                    que1.add(potabs[i][j].get(0));
                    que2.add(potabs[i][j].get(0));
                }
            }
        }

        Potab atc = que1.poll();
        Potab atd = que2.poll();
        
        // 공격자
        atc.last = t; atc.isAttack = true; atc.power += N + M; map[atc.x][atc.y] = atc.power;
        
        List<int[]> list = laser(atc, atd);

        if(list.size() == 0){
            potan(atc, atd);
        }
    }

    static void print(){
        // StringBuilder sb1 = new StringBuilder();
        int live = 0;
        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                // sb1.append(map[i][j]).append(" ");
                if(map[i][j] != 0){
                    live = Math.max(live, map[i][j]);
                }
            }
            // sb1.append('\n');
        }
        // System.out.println(sb1.toString());
        System.out.println(live);
    }
}