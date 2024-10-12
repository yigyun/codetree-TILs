import java.util.*;
import java.io.*;


/**
    - 기본 사항 -
    L x L 크기의 격자판 int[][] map
    * 왼쪽 상단 1,1 기준임.
    빈칸 함정 벽(격자 밖도 벽)
    기사들은 상대방 밀치기 가능
    기사의 초기 위치 r,c에서 h x w 크기의 직사각형 형태를 가지는게 기사의 방패
    기사 체력은 k

    - 기사 이동 -
    왕에게 명령을 받으면, 기사는 상하좌우 중 하나로 한 칸 이동한다.
    만약!! 이동하는 위치에 다른 기사가 있으면 연쇄작용으로 밀린다.
    이때!! 만약 기사가 이동하는 위치에 벽이 있으면 해당 방향으로는 못 움직이는것이 된다.
    !!사라진 기사를 관리해야 한다. 사라진 기사는 명령을 받지 않는다.

    - 대결 데미지 - 
    명령을 받은 기사는 데미지 x
    근데 연쇄적으로 밀려난 기사들은 피해를 입어야함.
    이때 각 기사들은 해당 기사가 이동한 곳에서 w x h 직사각형 내에 놓여있는 함정의 수 만큼만 피해를 입음.
    만약 체력이 음수가 되면 체스판에서 사라진다.
    !! 기사들은 모두 밀린 이후에 대미지를 입게 된다.
    !! 만약 밀렸는데 함정이 없으면 그 기사는 피해를 전혀 입지 않는다.


    출력할 값은 생존해있는 기사들이 받은 데미지의 총 합.
**/

public class Main {

    // 주어지는게 0,1,2,3의 방향이 상 우 하 좌
    static int[] dx = new int[]{-1, 0, 1, 0};
    static int[] dy = new int[]{0, 1, 0, -1};

    static class Knight{
        int x;
        int y;
        int w;
        int h;
        int k;
        Knight(int x, int y, int h, int w, int k){
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.k = k;
        }
    }

    static int l;
    static int n;
    static int q;

    static int[][] map; // 함정, 빈칸, 벽
    static boolean[] isDead; // false는 살아있고, true면 죽음
    static int[] points; // 기사들이 받은 데미지의 합을 보관.
    static List<Knight> knights; // 기사들의 메타 데이터(좌상단 좌표 x, y 방패 크기 w, h, 체력 k)
    
    // 기사는 1번부터해야함. 그래야 맵에 표기하기 편함.


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        l = Integer.parseInt(st.nextToken());
        n = Integer.parseInt(st.nextToken());
        q = Integer.parseInt(st.nextToken());

        map = new int[l][l];
        isDead = new boolean[n];
        points = new int[n];
        knights = new ArrayList<>();

        // 함정 및 빈칸 입력

        for(int i = 0; i < l; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < l; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 기사 입력
        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());
            knights.add(new Knight(x, y, h, w, k));
        }

        // 명령 입력 받을때 기사번호 -1해주기
        // 이제 명령을 수행한다
        for(int turn = 0; turn < q; turn++){
            st = new StringTokenizer(br.readLine());
            int index = Integer.parseInt(st.nextToken()) - 1;
            int dir = Integer.parseInt(st.nextToken());
            move(index, dir);
        }

        print();
    }

    static void mapPrint(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            sb.append("i: ").append(i).append(" x: " + knights.get(i).x + " y: " + knights.get(i).y)
            .append(" k: ").append(knights.get(i).k)
            .append('\n');
        }
        System.out.println(sb.toString());
    }

    static void print(){
        int result = 0;
        for(int i = 0; i < n; i++){
            if(!isDead[i]) result += points[i];
        }
        System.out.print(result);
    }

    static void move(int ki, int dir){
        if(isDead[ki]) return;
        boolean impossible = false;

        Queue<Knight> que = new LinkedList<>();
        Set<Integer> set = new HashSet<>();
        que.offer(knights.get(ki));
        set.add(ki);
        
        while(!que.isEmpty()){
            Knight current = que.poll();
            int nx = current.x + dx[dir];
            int ny = current.y + dy[dir];
            // 4개의 꼭짓점이 범위에 있음?
            if(isRange(nx, ny) && isRange(nx + current.h - 1, ny)
            && isRange(nx, current.w + ny - 1) && isRange(nx + current.h - 1, ny + current.w - 1)
            && isWall(nx, ny, current.h, current.w)){
                // ki번 제외한 나이트 중에 지금 nx 범위에 있는 애가 있음을 찾기 찾아서 큐에 넣어줄거임.
                for(int i = 0; i < n; i++){
                    if(set.contains(i) || isDead[i]) continue;
                    // 오른쪽 끝 h, 왼쪽 끝 h
                    // w 두개 해서 4개 비교해서 범위 밖인지만 보면댐.
                    Knight next = knights.get(i);
                    if(next.x > nx + current.h - 1) continue;
                    if(next.x + next.h - 1 < nx) continue;
                    if(next.y > ny + current.w - 1) continue;
                    if(next.y + next.w - 1 < ny) continue;

                    // 같은 범위임 큐에 넣고 셋에 넣기.
                    set.add(i);
                    que.offer(next);
                }
            } else{
                impossible = true;
                break;
            }
        }
        
        if(impossible) return;

        // Set에 있는 모든 기사들 움직이기
        for(int num : set){
            Knight current = knights.get(num);
            int nx = current.x + dx[dir];
            int ny = current.y + dy[dir];
            current.x = nx;
            current.y = ny;
        }

        // set에 있는 기사들은 피해를 입어야 한다.
        for(int num : set){
            if(num == ki) continue;
            Knight current = knights.get(num);
            int count = 0;
            for(int i = current.x; i < current.x + current.h; i++){
                for(int j = current.y; j < current.y + current.w; j++){
                    if(map[i][j] == 1) count++;
                }
            }
            current.k -= count;
            if(current.k <= 0) isDead[num] = true;
            points[num] += count;
        }
    }

    static boolean isWall(int x, int y, int h, int w){
        for(int i = x; i < x + h; i++){
            for(int j = y; j < y + w; j++){
                if(map[i][j] == 2) return false;
            }
        }

        return true;
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || ny < 0 || nx >= l || ny >= l);
    }
}