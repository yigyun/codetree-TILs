import java.util.*;
import java.io.*;

/**
    L x L 크기의 체스판, 기준 1,1
    빈칸, 함정, 벽(격자 밖 포함)
    기사는 마력으로 상대방을 밀쳐냄.
    h x w 크기의 방패를 각자 가짐, 각 기사의 체력은 k
    기사는 1번부터 시작임.
    1. 기사의 이동
    - 상하좌우 중 한 칸
    - 다른 기사가 해당 위치에 있으면 연쇄적으로 밀어냄.
    - 만약 다 밀어냈는데 끝에 벽때문에 못 밀리면 이동 x
    - 죽은 기사는 반응 x 

    2. 데미지
    - 밀려난 기사들은 피해를 받음.
    - 기사가 이동한 곳에서 w x h 직사각형 내에 놓인 함정의 수 만큼 피해를 받음, 즉 체력이 깎임.
    - 처음 기사는 피해 x, 밀리고 나서 대미지를 입는다.

**/

public class Main {

    static int L;
    static int N;
    static int Q;
    
    static int[][] map;
    
    //상 우 하 좌
    static int[] dx = new int[]{-1, 0, 1, 0};
    static int[] dy = new int[]{0, 1, 0, -1};
    
    static class Knight{
        int x; int y;
        int h; int w;
        int k;
        Knight(int x, int y, int h, int w, int k){
            this.x = x; this.y = y; this.h = h;
            this.w = w; this.k = k;
        }
    }
    static Knight[] knightList;
    static boolean[] alive;
    static int[] damages;

    static int totalDamage;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        // 함정 입력 1은 함정, 2는 벽
        map = new int[L][L];
        for(int i = 0; i < L; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < L; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        knightList = new Knight[N];
        alive = new boolean[N];
        damages = new int[N];
        for(int i = 0; i < N; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());
            knightList[i] = (new Knight(x, y, h, w, k));
        }


        for(int t = 0; t < Q; t++){
            st = new StringTokenizer(br.readLine());
            int knighNum = Integer.parseInt(st.nextToken()) - 1;
            int dir = Integer.parseInt(st.nextToken());
            if(!alive[knighNum]){
                bfs(knighNum, dir);
            }
        }
        printDamage();
    }

    static void printDamage(){
        int damage = 0;
        for(int i = 0; i < N; i++){
            if(!alive[i]){
                damage += damages[i];
            }
        }
        System.out.print(damage);
    }

    static void bfs(int start, int dir){
        Queue<Integer> que = new LinkedList<>();
        que.offer(start);
        Set<Integer> set = new HashSet<>();
        set.add(start);
        boolean check = false;

        while(!que.isEmpty()){
            int current = que.poll();
            Knight currentKnight = knightList[current];
            int nx = currentKnight.x + dx[dir];
            int ny = currentKnight.y + dy[dir];


            if(!canMove(nx, ny, currentKnight.h, currentKnight.w)){
                check = true;
                break;
            }
            for(int i = 0; i < N; i++){
                if(set.contains(i) || alive[i]) continue;
                Knight checkKnight = knightList[i];

                if(isColliding(nx, ny, currentKnight, checkKnight)){
                    que.offer(i);
                    set.add(i);
                }
            }
        }
        if(check) return;

        for (int idx : set) {
            Knight kn = knightList[idx];
            kn.x += dx[dir];
            kn.y += dy[dir];
        }


        for(int idx : set){
            if (idx == start) continue;

            Knight kn = knightList[idx];
            int damage = calculateDamage(kn);

            kn.k -= damage;
            damages[idx] += damage;

            if(kn.k <= 0){ 
                alive[idx] = true;
            }
        }
    }

    static boolean canMove(int x, int y, int h, int w) {
        for (int i = x; i < x + h; i++) {
            for (int j = y; j < y + w; j++) {
                if (i < 0 || i >= L || j < 0 || j >= L || map[i][j] == 2) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isColliding(int x, int y, Knight k1, Knight k2) {
        return !(x + k1.h - 1 < k2.x || k2.x + k2.h - 1 < x ||
                 y + k1.w - 1 < k2.y || k2.y + k2.w - 1 < y);
    }

    static int calculateDamage(Knight kn) {
        int damage = 0;
        for (int i = kn.x; i < kn.x + kn.h; i++) {
            for (int j = kn.y; j < kn.y + kn.w; j++) {
                if (map[i][j] == 1) damage++;
            }
        }
        return damage;
    }
}