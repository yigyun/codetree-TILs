import java.util.*;
import java.io.*;


/**
    n x n 크기의 격자다.
    각 격자에는 무기가 있을 수 있다.
    빈 격자에는 플레이어가 위치한다.
    초기에 능력치를 각자 가진다.

    플레이어는 순차적으로 이동한다.

    1. 첫번째 플레이가 자신이 향하는 방향으로 이동한다.
    만약 격자를 벗어나면 반대 방향으로 1칸 이동한다.
    
    2. 이동한 곳에 플레이어가 없으면 해당 칸의 총을 확인한다.
    총이 있으면, 해당 총을 획득한다. 이미 있으면 비교해서 더 강한 총을 줍는다.
    그리고 기존 총을 바닥에 둔다.

    3. 플레이어가 이동한 곳에 있다.
    싸운다. 초기 능력치 + 총의 공격력으로 비교하고 싸움.
    수치가 같으면 초기능력치가 높은 플레이어가 승리한다.
    이긴 플레이어는 각 플레이어의 초기 능력치와 가진 총의 공격력 합의 차이 만큼 포인트로 획득.
    즉 이긴 수 만큼 포인트를 얻는다.

    -- 진 플레이어
    본인의 총을 내려놓고, 본인의 방향으로 한 칸 이동한다.
    만약 다른 플레이어가 있거나, 격자 범위 밖이면 오른쪽으로 90도 씩 계속 회전하여 빈칸이 보이면 이동한다.
    그 칸에 총이 있으면 집는다.(가장 공격력이 높은 총)
    -- 이긴 플레이어
    승리한 칸에서 떨어진 총들과, 들고 있는 총 비교해서 가장 공격력이 높은 총을 얻음.
    
    이 과정을 반복함.
**/

public class Main {

    static int n; // 격자 크기
    static int m; // 사람 수
    static int k; // 라운드 수
    // 상 우 하 좌
    static int[] dx = new int[]{-1, 0, 1, 0};
    static int[] dy = new int[]{0, 1, 0, -1};
    
    static int[] point; // 플레이어들 포인트
    static int[][] map; // 사람 위치
    static int[][] people; // 사람 정보 (x, y, 방향, 능력치, 총)
    static List<Integer>[][] guns; // 총 위치
    

    public static void main(String[] args) throws IOException {
        // 입력.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        people = new int[m+1][5];
        map = new int[n][n];
        point = new int[m+1];
        guns = new ArrayList[n][n];

        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                guns[i][j] = new ArrayList<>();
                int power = Integer.parseInt(st.nextToken());
                if(power > 0)
                    guns[i][j].add(power);
            }
        }

        for(int i = 1; i <= m; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            people[i][0] = x;
            people[i][1] = y;
            people[i][2] = d;
            people[i][3] = s;
            map[x][y] = i;
        }

        // k번의 라운드 진행하기
        for(int t = 0; t < k; t++){
            // 모든 아이들이 순차적으로 진행함
            for(int i = 1; i <= m; i++){
                move(i);
            }
            // printMap();
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= m; i++){
            sb.append(point[i]).append(" ");
        }
        System.out.print(sb.toString());
    }

    static void printMap(){
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                sb.append(map[i][j]).append(" ");
                if(map[i][j] != 0){
                    sb2.append(map[i][j]).append(" ").append(people[map[i][j]][3]).append(" ").append(people[map[i][j]][4]).append('\n');
                }
                if(guns[i][j].size() != 0){
                    sb3.append("x: ").append(i).append(" y: ").append(j);
                    for(int num : guns[i][j]){
                        sb3.append(" ").append(num);
                    }
                    sb3.append('\n');
                }
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
        System.out.println(sb2.toString());
        System.out.println(sb3.toString());
    }

    static void move(int num){
        int nx = people[num][0] + dx[people[num][2]];
        int ny = people[num][1] + dy[people[num][2]];
        if(!isRange(nx, ny)){
            people[num][2] = (people[num][2] + 2) % 4;
            nx = people[num][0] + dx[people[num][2]];
            ny = people[num][1] + dy[people[num][2]];
        }
        if(map[nx][ny] != 0){
            isFight(num);
            }else{
                map[people[num][0]][people[num][1]] = 0;
                people[num][0] = nx;
                people[num][1] = ny;
                map[nx][ny] = num;
                if(guns[nx][ny].size() != 0){
                    Collections.sort(guns[nx][ny], Collections.reverseOrder());
                    if(people[num][4] < guns[nx][ny].get(0)){
                        if(people[num][4] > 0){
                            guns[nx][ny].add(people[num][4]);
                        }
                        people[num][4] = guns[nx][ny].get(0);
                        guns[nx][ny].remove(0);
                    }
                }
            }
    }

    static void isFight(int f1){
        map[people[f1][0]][people[f1][1]] = 0;

        int nx = people[f1][0] + dx[people[f1][2]];
        int ny = people[f1][1] + dy[people[f1][2]];
        
        int f2 = map[nx][ny];
        // 현재위치는 nx, ny임. 여기서 싸우는거고 끝나면 여기서 움직임 주면됨.
        int winner = 0; int loser = 0;
        if(people[f1][3] + people[f1][4] == people[f2][3] + people[f2][4]){
            winner = people[f1][3] > people[f2][3] ? f1 : f2;
            loser = f1 == winner ? f2 : f1;
            point[winner] += (people[winner][3] + people[winner][4]) - (people[loser][3] + people[loser][4]);
        }else{
            winner = people[f1][3] + people[f1][4] > people[f2][3] + people[f2][4] ? f1 : f2;
            loser = f1 == winner ? f2 : f1;
            point[winner] += (people[winner][3] + people[winner][4]) - (people[loser][3] + people[loser][4]);
        }

        lose(loser, nx, ny);
        win(winner, nx, ny);
    }

    // -- 진 플레이어
    // 본인의 총을 내려놓고, 본인의 방향으로 한 칸 이동한다.
    // 만약 다른 플레이어가 있거나, 격자 범위 밖이면 오른쪽으로 90도 씩 계속 회전하여 빈칸이 보이면 이동한다.
    // 그 칸에 총이 있으면 집는다.(가장 공격력이 높은 총)
    // -- 이긴 플레이어
    // 승리한 칸에서 떨어진 총들과, 들고 있는 총 비교해서 가장 공격력이 높은 총을 얻음.

    static void lose(int loser, int x, int y){
        if(people[loser][4] > 0){
            guns[x][y].add(people[loser][4]);
            people[loser][4] = 0;
        }
        
        int nx = x + dx[people[loser][2]];
        int ny = y + dy[people[loser][2]];
        if(isRange(nx, ny) && map[nx][ny] == 0){
            // 그 자리로 이동.
            people[loser][0] = nx;
            people[loser][1] = ny;
            map[nx][ny] = loser;
            if(guns[nx][ny].size() != 0){
                Collections.sort(guns[nx][ny], Collections.reverseOrder());
                if(people[loser][4] < guns[nx][ny].get(0)){
                    if(people[loser][4] > 0){
                        guns[nx][ny].add(people[loser][4]);
                    }
                    people[loser][4] = guns[nx][ny].get(0);
                    guns[nx][ny].remove(0);
                }
            }
        }else{
            // 90도씩 회전하면서 빈칸이 보이는 순간 이동한다.
            int dir = 1;
            while(dir < 4){
                people[loser][2] = (people[loser][2] + dir) % 4;
                nx = x + dx[people[loser][2]];
                ny = y + dy[people[loser][2]];
                if(isRange(nx, ny) && map[nx][ny] == 0){
                    people[loser][0] = nx;
                    people[loser][1] = ny;
                    map[nx][ny] = loser;
                    if(guns[nx][ny].size() != 0){
                        Collections.sort(guns[nx][ny], Collections.reverseOrder());
                        if(people[loser][4] < guns[nx][ny].get(0)){
                            if(people[loser][4] > 0){
                                guns[nx][ny].add(people[loser][4]);
                            }
                            people[loser][4] = guns[nx][ny].get(0);
                            guns[nx][ny].remove(0);
                        }
                    }
                    break;
                }
            }
        }
        
    }

    static void win(int winner, int nx, int ny){
        people[winner][0] = nx;
        people[winner][1] = ny;
        map[nx][ny] = winner;
        if(guns[nx][ny].size() != 0){
            Collections.sort(guns[nx][ny], Collections.reverseOrder());
            if(people[winner][4] < guns[nx][ny].get(0)){
                if(people[winner][4] > 0){
                    guns[nx][ny].add(people[winner][4]);
                }
                people[winner][4] = guns[nx][ny].get(0);
                guns[nx][ny].remove(0);
            }
        }
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= n || ny < 0 || ny >= n);
    }
}