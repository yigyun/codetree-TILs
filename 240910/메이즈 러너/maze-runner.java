import java.util.*;
import java.io.*;

/**
- 기본 조건 -
미로는 N x N
좌상단 1,1 기준임.
빈칸(참가자 이동 가능)
벽(이동 불가 1~9의 내구도, 회전하면 내구도 1 감소, 0이 되면 빈칸이 됨)
출구에 도착하면 즉시 탈출

1. 이동
1초마다 모든 참가자가 한 칸씩 움직인다.
출구까지의 최단거리 |x1 - x2| + |y1 - y2|
동시에 움직인다고 생각해야 한다.
상하좌우로 움직임 가능, 4방향
움직이면 그 칸은 출구까지의 거리에 가까워야 한다.
움직일 수 있다면 상,하가 우선된다.
한 칸에 여러명이 가능하다.

2. 회전
한명 이상의 참가자 + 출구를 포함하는 가장 작은 정사각형을 잡는다.
r 좌표가 작은것 먼저, 그 중에서는 c좌표가 작은 것 우선
사각형 찾고 90도 회전 + 벽 내구도 1 감소(해당 범위)


구할 것 참가자 이동 거리 합 + 출구 좌표

순서
이동 -> 회전

**/

public class Main {
    
    // 미로, 출구 좌표, 상하좌우
    static int[][] miro;
    static int gx;
    static int gy;
    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, -1, 1};
    // N 미로의 크기, M 참가자의 수, K 게임 턴
    static int N;
    static int M;
    static int K;
    // 살아있는 참가자 유지, 전체 참가자 명단 alive 1이면 살아있는 거임.
    static int[] alive;
    static class Person{
        int x; int y;
        Person(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    static Map<Integer, Person> peoples;
    // 거리길이
    static int result;

    public static void main(String[] args) throws IOException {
        // N, M, K입력
        StringBuilder sb = new StringBuilder();        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken()); M = Integer.parseInt(st.nextToken()); K = Integer.parseInt(st.nextToken());
        // 미로 입력
        miro = new int[N][N];
        for(int i = 0; i < N; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++){
                miro[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // 참가자 입력 1번 참가자부터 시작임.
        alive = new int[M + 1];
        peoples = new HashMap<>();
        for(int i = 1; i <= M; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1; int y = Integer.parseInt(st.nextToken()) - 1;
            alive[i] = 1;
            peoples.put(i, new Person(x, y));
        }
        // 출구 입력받기
        st = new StringTokenizer(br.readLine());
        gx = Integer.parseInt(st.nextToken()) - 1; gy = Integer.parseInt(st.nextToken()) - 1; 
        // K번 반복하기
        for(int t = 0; t < K; t++){
            // 움직임
            move();
            // 회전
            rotate();
            // StringBuilder sb = new StringBuilder();
            // for(int i = 0; i < N; i++){
            //     for(int j = 0; j < N; j++){
            //         sb.append(miro[i][j]).append(" ");
            //     }
            //     sb.append('\n');
            // }
            // System.out.println(sb.toString());
            // sb = new StringBuilder();
            // sb.append("Person").append('\n');
            // for(int i = 1; i <= M; i++){
            //     if(alive[i] == 1){
            //         Person person = peoples.get(i);
            //         sb.append(person.x).append(" ").append(person.y).append('\n');
            //     }
            // }
            // sb.append("gx, gy: ").append(gx).append(" ").append(gy).append('\n');
            // sb.append("result: ").append(result).append('\n');
            // System.out.println(sb.toString());
        }
        sb.append(result).append('\n');
        sb.append(gx+1).append(" ").append(gy+1);
        System.out.print(sb.toString());
    }

    static void rotate(){
        // 회전 범위 찾기
        // 10 x 10 크기니까 
        int rx = 0; int ry = 0; int size = 0;
        int[][] participant = new int[N][N];
        for(int i = 1; i <= M; i++){
            if(alive[i] == 1){
                Person person = peoples.get(i);
                participant[person.x][person.y] = i;
            }
        }
        participant[gx][gy] = 11;
        boolean check = false;
        for(int i = 2; i <= N; i++){
            for(int x = 0; x < N - i + 1; x++){
                for(int y = 0; y < N - i + 1; y++){
                    // 이 x, y 좌표로부터 i 크기의 사각형에 gx, gy가 있고 사람이 있으면 통과
                    // 출구가 포함되어 있나?
                    if(gx >= x && gx < x + i && gy >= y && gy < y + i){
                        for(int p = 1; p <= M; p++){
                            if(alive[p] == 1){
                                Person person = peoples.get(p);
                                if(person.x >= x && person.x < x + i && person.y >= y && person.y < y + i){
                                    check = true;
                                    rx = x; ry = y; size = i;
                                    break;
                                }
                            }
                        }
                        if(check) break;
                    }
                }
                if(check) break;
            }
            if(check) break;
        }
        // System.out.printf("rx: %d, ry: %d, size: %d\n", rx, ry, size);
        // 회전 하기 rx, ry, size 기준으로 회전
        // 회전 할때 0,0 기준으로 생각해서 i == j 부분을 둔 점이 문제였음.
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(i == j) break;
                // 미로
                int temp = miro[i+rx][j+ry];
                miro[i+rx][j+ry] = miro[j+rx][i+ry];
                miro[j+rx][i+ry] = temp;
                // 참가자
                temp = participant[i+rx][j+ry];
                participant[i+rx][j+ry] = participant[j+rx][i+ry];
                participant[j+rx][i+ry] = temp;
            }
        }

        for(int i = rx; i < rx + size; i++){
            int temp = miro[i][ry];
            miro[i][ry] = miro[i][ry+size-1];
            miro[i][ry+size-1] = temp;

            temp = participant[i][ry];
            participant[i][ry] = participant[i][ry+size-1];
            participant[i][ry+size-1] = temp;
        }
        // 벽 내구도 감소, 참가자 갱신 및 출구 갱신
        for(int i = rx; i < rx+size; i++){
            for(int j = ry; j < ry+size; j++){
                if(miro[i][j] > 0) miro[i][j]--;
                
                if(participant[i][j] == 11){
                    gx = i; gy = j;
                }else if(participant[i][j] > 0){
                    Person person = peoples.get(participant[i][j]);
                    person.x = i; person.y = j;
                    peoples.put(participant[i][j], person);
                }
            }
        }
    }

    static void move(){
        // 모든 참가자 동시에 이동하기
        for(int i = 1; i <= M; i++){
            if(alive[i] == 1){
                // 움직인다.
                Person person = peoples.get(i);
                int currentDistance = distance(person.x, person.y);
                for(int dir = 0; dir < 4; dir++){
                    int nx = person.x + dx[dir];
                    int ny = person.y + dy[dir];
                    if(isRange(nx, ny) && miro[nx][ny] == 0){
                        int newDistance = distance(nx, ny);
                        if(currentDistance > newDistance){
                            // 움직임 가능하다 움직이기, 상하좌우 순서니까 그냥 넣으면됨.
                            if(gx == nx && gy == ny){
                                alive[i] = 0;
                            }
                            person.x = nx; person.y = ny;
                            peoples.put(i, person);
                            result++;
                            break;
                        }
                    }
                }
            }
        }
    }

    static boolean isRange(int x, int y){
        return !(x < 0 || x >= N || y < 0 || y >= N);
    }

    static int distance(int x,  int y){
        return Math.abs(x - gx) + Math.abs(y - gy);
    }
}