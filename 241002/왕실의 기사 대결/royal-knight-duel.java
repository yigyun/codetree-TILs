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
    static List<Knight> knightList;
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

        knightList = new ArrayList<>();
        alive = new boolean[N];
        damages = new int[N];
        for(int i = 0; i < N; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());
            knightList.add(new Knight(x, y, h, w, k));
        }


        for(int t = 0; t < Q; t++){
            st = new StringTokenizer(br.readLine());
            int knighNum = Integer.parseInt(st.nextToken()) - 1;
            int dir = Integer.parseInt(st.nextToken());
            bfs(knighNum, dir);
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
            Knight currentKnight = knightList.get(current);
            int nx = currentKnight.x + dx[dir];
            int ny = currentKnight.y + dy[dir];
            if(isRange(nx, ny) && isWall(nx, ny, currentKnight.h, currentKnight.w)){
                for(int i = 0; i < knightList.size(); i++){
                    if(set.contains(i) || alive[i]) continue;
                    Knight checkKnight = knightList.get(i);
                    if(isPush(nx, ny, currentKnight, checkKnight)){
                        que.offer(i);
                        set.add(i);
                    }
                }
            } else { check = true; break;}
        }
        if(check) return;

        for(int num : set){
            Knight kn = knightList.get(num);
            // 처음 기사를 제외한 애들은 자기 범위에 함정이 있으면 체력을 줄여야 함.
            int damage = 0;
            int nx = kn.x + dx[dir]; int ny = kn.y + dy[dir];
            kn.x = nx; kn.y = ny;
            if(num == start) continue;
            for(int i = nx; i < nx + kn.h; i++){
                for(int j = ny; j < ny + kn.w; j++){
                    if(map[i][j] == 1) damage++;
                }
            }
            kn.k -= damage;
            damages[num] += damage;
            if(kn.k <= 0){ 
                alive[num] = true;
            }
        }
    }

    static boolean isWall(int x, int y, int h, int w){
        for(int i = x; i <= (x + h - 1); i++){
            for(int j = y; j <= (y + w - 1); j++){
                if(map[i][j] == 2) return false;
            }
        }
        return true;
    }

    static boolean isPush(int nx, int ny, Knight knight1 , Knight knight2){
        return !(nx > knight2.x + knight2.h - 1 || 
            nx + knight1.h - 1 < knight2.x || 
            ny > knight2.y + knight2.w - 1 || 
            ny + knight1.w - 1 < knight2.y);
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= L || ny < 0 || ny >= L);
    }
}