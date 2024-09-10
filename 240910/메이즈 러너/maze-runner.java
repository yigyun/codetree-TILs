import java.util.*;
import java.io.*;

public class Main {

    /*
    N x N 크기 미로는 빈칸, 벽, 출구로 나뉨.
    벽은 내구도를 갖는다.
    내구도가 0이되면 빈칸으로 변경한다.
    1초마다 모든 참가자가 이동, 동시에 이동한다.
    상하가 우선시 된다.
    움직일 수 없으면 패스, 한 칸에 두 명이상의 참가자 가능 겹치기 가능 ㅇㅇ
    1번은 이동, 그 다음 한명 이상의 참가자와 출구를 포함하는 가장 작은 정사각형 잡기.
    r이 작은거 우선, c가 작은 거 우선. 시계방향 90도 중요!!
    */

     static class Person{
        int x, y;
        public Person(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    // 상하좌우
    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, -1, 1};

    static int n, m, k;
    static int[][] miro = new int[10][10];

    // 사람 데이터 정리하기.
    static Person[] personList = new Person[11];
    static int[] distance = new int[11]; // 최종 값 구하기 용
    static int[] alive = new int[11]; // alive 1이면 출구 통과한 애임.

    // 출구 좌표 관리하기.
    static int ex;
    static int ey;

    public static void main(String[] args)throws IOException{
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken()); m = Integer.parseInt(st.nextToken()); k = Integer.parseInt(st.nextToken());
        // 미로 입력
        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                miro[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // 사람 입력
        for(int i = 1; i <= m; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken())-1; int y = Integer.parseInt(st.nextToken())-1;
            personList[i] = new Person(x, y);
        }
        // 출구 입력
        st = new StringTokenizer(br.readLine());
        ex = Integer.parseInt(st.nextToken())-1; ey = Integer.parseInt(st.nextToken())-1;

        for(int turn = 0; turn < k; turn++){
            // K초 전에 모든 참가자가 탈출에 성공한다면, 게임이 끝납니다.
            if(!check()) break;
            // 모든 참가자가 이동합니다.
            moveAll();
            if(!check()) break;
            // 미로 회전하기
            rotate();
            if(!check()) break;
            // 현재 탈출구에 있는 사람을 alive 처리해줍니다.
            runExit();
        }
        int totalScore  = 0;
        for(int i = 1; i <= m; i++){
            totalScore += distance[i];
        }
        System.out.println(totalScore);
        System.out.print((ex+1) + " " + (ey+1));
    }

    static void runExit(){
        for(int number = 1; number <= m; number++){
            if(alive[number] == 1) continue;
            if(personList[number].x == ex && personList[number].y == ey) alive[number] = 1;
        }
    }

    static void rotate(){
        // 한명 이상의 참가자와 출구를 포함하는 가장 작은 정사각형 찾기
        // 이게 두개 이상이면 r 좌표가 작은게 우선, 그래도 같으면 c 좌표 작은게 우선이다.
        int[] find = findSquare();
        int size = find[0];
        int sx = find[1], sy = find[2];
        // 선택이 끝나고 회전 시키고 벽 내구도 깎기.
        // 미로 회전하기.
        int[][] temp = new int[n][n];
        for(int i = sx; i < size + sx; i++){
            for(int j = sy; j < size+sy; j++){
                int ox = i - sx; int oy = j - sy;
                int rx = oy; int ry = size - ox - 1;
                if(miro[i][j] > 0) miro[i][j]--;
                temp[rx+sx][ry+sy] = miro[i][j];
            }
        }

        for(int i = sx; i < size+sx; i++){
            for(int j = sy; j < size+sy; j++){
                miro[i][j] = temp[i][j];
            }
        }

        // 탈출구 회전하기
        for(int i = sx; i < size + sx; i++){
            boolean checkExit = false;
            for(int j = sy; j < size+sy; j++){
                if(i == ex && j == ey){
                    int ox = i - sx; int oy = j - sy;
                    int rx = oy; int ry = size - ox - 1;
                    ex = rx+sx; ey = ry+sy;
                    checkExit = true;
                    break;
                }
            }
            if(checkExit) break;
        }
        // 사람 회전하기
        for(int number = 1; number <= m; number++){
            if(alive[number] == 1) continue;
            for(int i = sx; i < size + sx; i++){
                boolean checkPerson = false;
                for(int j = sy; j < size+sy; j++){
                    if(i == personList[number].x && personList[number].y == j){
                        int ox = i - sx; int oy = j - sy;
                        int rx = oy; int ry = size - ox - 1;
                        personList[number].x = rx + sx; personList[number].y = ry + sy;
                        checkPerson = true;
                        break;
                    }
                }
                if(checkPerson) break;
            }
        }
    }

    static int[] findSquare(){
        int curSize = n;
        int rx = 0;
        int ry = 0;
        boolean person = false;
        boolean exit = false;
        for(int size = 2; size < n; size++){
            for(int i = 0; i <= n-size; i++){
                for(int j = 0; j <= n-size; j++){
                    // 출구가 해당 범위 내에 있는지
                    if(i <= ex && ex < i+size && j <= ey && ey < j+size) exit = true;
                    // 사람이 한명이라도 해당 범위에 있다면 종료.
                    for(int number = 1; number <= m; number++){
                        if(alive[number] == 1) continue;
                        if(personList[number].x >= i && personList[number].x < i + size &&
                                personList[number].y >= j && personList[number].y < j+size){
                            person = true;
                            break;
                        }
                    }
                    // true 라는 것은 이 범위가 정사각형으로 선택되었음을 알림.
                    if(exit && person){
                        if(curSize > size){
                            curSize = size; rx = i; ry = j;
                        } else if(curSize == size){
                            if(rx > i){
                                rx = i; ry = j;
                            }else if(rx == i){
                                if(ry > j) ry = j;
                            }
                        }
                    }
                    exit = false;
                    person = false;
                }
            }
        }
        return new int[]{curSize, rx, ry};
    }

    static void moveAll(){
        // 모든 참가자를 확인합니다.
        for(int i = 1; i <= m; i++){
            // 이때 alive해야합니다.
            if(alive[i] != 1){
                // 현재 위치 보다 최단거리가 가까운 방향을 찾는다.
                int curDistance = shortest(personList[i].x, personList[i].y, ex, ey);
                int dir = -1;
                for(int q = 0; q < 4; q++){
                    int nx = personList[i].x + dx[q];
                    int ny = personList[i].y + dy[q];
                    // inRange에서 false를 받으면 이동 가능함.
                    if(!inRange(nx, ny)){
                        // 여기서 빈칸인지 확인하고 이동.
                        if(miro[nx][ny] == 0){
                            // 이제 거리 계산하고 현재 머물러 있던 칸보다 가까우면 방향 넣기.
                            int nextDistance = shortest(nx, ny, ex, ey);
                            if(curDistance > nextDistance){
                                curDistance = nextDistance;
                                dir = q;
                            }
                        }
                    }
                }

                // 참가자가 움직일 수 있다면 움직인 거리 추가하고 좌표 수정하기.
                if(dir != -1){
                    int nx = personList[i].x + dx[dir]; int ny = personList[i].y + dy[dir];
                    personList[i].x = nx; personList[i].y = ny;
                    if(nx == ex && ny == ey) {
                        alive[i] = 1;
                    }
                    distance[i]++;
                }
            }
        }
    }

    static boolean inRange(int nx, int ny){
        return nx < 0 || nx >= n || ny < 0 || ny >= n;
    }

    // 살아있는 사람 없으면 멈춤.
    static boolean check(){
        boolean ch = false;
        for(int i = 1; i <= m; i++){
            if(alive[i] == 0) ch = true;
        }
        return ch;
    }

    // 최단거리 계산 리턴
    static int shortest(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}