import java.util.*;
import java.io.*;

/**
    4 x 4 격자 크기
    m개의 몬스터와 1개의 팩맨이 주어진다.
    8개의 방향 중 하나를 몬스터는 갖는다.


    1. 몬스터 복제 시도
    2. 몬스터 이동
    3. 팩맨 이동
    4. 몬스터 시체 소멸
    5. 몬스터 복제 소멸
**/


// 살아남은 몬스터의 수가 출력되면 됨.

public class Main {

    static int m;
    static int t;
    // r,c는 팩맨의 좌표
    static int r;
    static int c;

    static int[] dx = new int[]{-1, -1, 0, 1, 1, 1, 0, -1};
    static int[] dy = new int[]{0, -1, -1, -1, 0, 1, 1, 1};

    // 몬스터의 최대 수가 100만개가 넘는 입력은 없다.

    static int[][] map = new int[4][4];
    static List<Integer>[][] monster = new ArrayList[4][4];
    static int[][] deads = new int[4][4];
    static List<Integer>[][] eggs = new ArrayList[4][4];

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        m = Integer.parseInt(st.nextToken());
        t = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        r = Integer.parseInt(st.nextToken()) - 1;
        c = Integer.parseInt(st.nextToken()) - 1;
        
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                monster[i][j] = new ArrayList<>();
                eggs[i][j] = new ArrayList<>();
            }
        }

        for(int i = 0; i < m; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken()) - 1;
            monster[x][y].add(d);
        }

        // 몬스터의 최대 수가 100만개가 넘는 입력은 없다.
        
        for(int i = 1; i <= t; i++){
            monsterClone();
            monsterMove(i);
            movePackman(i);
            eggToMonster();
        }

        print();
        
    }

    static void tempPrint(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(monster[i][j].size() != 0){
                    for(int dir : monster[i][j]){
                        sb.append("x: ").append(i).append(" y: ").append(j).append(" dir: ").append(dir).append('\n');
                    }
                }
            }
        }
        sb.append("r: ").append(r).append(" c: ").append(c).append('\n');
        System.out.println(sb.toString());
    }

    static void print(){
        int count = 0;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                count += monster[i][j].size();
            }
        }

        System.out.print(count);
    }
    
    // 1. 몬스터 복제 시도
    /**
        몬스터는 현재의 위치에서 자신과 같은 방향을 가진 몬스터를 복제한다.
        이때 복제된 몬스터는 아직은 부화되지 않은 상태이다.
    **/

    static void monsterClone(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(monster[i][j].size() != 0){
                    for(int dir : monster[i][j]){
                        eggs[i][j].add(dir);
                    }
                }
            }
        }
    }

    /**
        2. 몬스터 이동

        자신이 가진 방향으로 이동한다.
        해당 칸에 시체가 있거나, 팩맨이 있거나, 격자를 벗어나는 방향이거나 면
        반시계 방향으로 45도 회전한다.
        그 다음 이동을 확인한다.
        그래도 못가면 가능할 때까지 반시계 방향으로 45도씩 회전한다.
        8방향을 다 회전했는데 못 움직이면 안 움직인다.
    **/

    static void monsterMove(int turn){
        List<Integer>[][] tempMonster = new ArrayList[4][4];

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                tempMonster[i][j] = new ArrayList<>();
            }
        }

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(monster[i][j].size() != 0){
                    for(int dir : monster[i][j]){
                        boolean check = true;
                        for(int ndir = dir; ndir < dir + 8; ndir++){
                            int nx = i + dx[ndir % 8];
                            int ny = j + dy[ndir % 8];
                            if(!isRange(nx, ny) || (nx == r && ny == c) || (turn > 2 && deads[nx][ny] >= turn - 2)){
                                continue;
                            }else{
                                tempMonster[nx][ny].add(ndir % 8);
                                check = false;
                                break;
                            }
                        }
                        if(check) tempMonster[i][j].add(dir);
                    }
                }
            }
        }
        monster = tempMonster;
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= 4 || ny < 0 || ny >= 4);
    }

    /**
        3. 팩맨 이동

        팩맨은 3칸을 이동한다.
        64개의 이동 방법 중, 몬스터를 가장 많이 먹는 방향으로 움직인다.
        - 많이 먹을 수 있는 방법이 여러 개면, 상-좌-하-우의 우선순위를 가진다.
        상상상 - 상상좌 - 상상하 - 상상우 이런식으로 나타남.

        마지막에 이동하면서 몬스터 죽이고 시체 표시하기.
    **/

    static int[] pdx = new int[]{-1, 0, 1, 0};
    static int[] pdy = new int[]{0, -1, 0, 1};

    static class Node{
        int x; int y;
        List<Integer> from;
        int depth;
        Node(int x, int y, int depth, List<Integer> from){
            this.x = x; this.y = y;
            this.depth = depth;
            this.from = from;
        }
    }

    static void movePackman(int turn){
        Queue<Node> que = new LinkedList<>();
        que.offer(new Node(r, c, 0, new ArrayList<>()));
        List<Node> list = new ArrayList<>();
        
        while(!que.isEmpty()){
            Node current = que.poll();
            if(current.depth == 3){
                list.add(current);
                continue;
            }
            for(int dir = 0; dir < 4; dir++){
                int nx = current.x + pdx[dir];
                int ny = current.y + pdy[dir];
                if(isRange(nx, ny)){
                    List<Integer> newList = new ArrayList<>(current.from);
                    newList.add(dir);
                    que.offer(new Node(nx, ny, current.depth + 1, newList));
                }
            }
        }

        Node node = list.get(0); int maxCount = 0;

        for(Node current : list){
            boolean[][] visited = new boolean[4][4];
            int count = 0;
            int x = r; int y = c;
            boolean pathCheck = false;

            for(int dir : current.from){
                int nx = x + pdx[dir];
                int ny = y + pdy[dir];
                if(visited[nx][ny]){pathCheck = true; break;}
                visited[nx][ny] = true;
                count += monster[nx][ny].size();
                x = nx; y = ny;
            }
            if(pathCheck) continue;

            if(maxCount < count){
                node = current;
                maxCount = count;
            }else if(maxCount == count){
                for(int q = 0; q < 3; q++){
                    int dir1 = node.from.get(q);
                    int dir2 = current.from.get(q);
                    if(dir1 != dir2){
                        if(dir1 > dir2){
                            node = current;
                        }
                        break;
                    }
                }
            }
        }

        for(int dir : node.from){
            r = r + pdx[dir];
            c = c + pdy[dir];
            if(monster[r][c].size() > 0){
                deads[r][c] = turn;
                monster[r][c].clear();
            }
        }
    }

    /**
        4. 시체 소멸

        어차피 4 x 4니까
        함수 만들어서 2턴 동안만 유지되는 시체를 회수해주면됨.

    **/
    // 일단 스킵해봄. 지금 어차피 4 x 4 크기 배열에 시간을 저장시키고 있음.

    /**
        5. 몬스터 복제 완성

        1번에서 만들었던 몬스터들이 부활한다.
    **/
    static void eggToMonster(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(eggs[i][j].size() != 0){
                    for(int dir : eggs[i][j]){
                        monster[i][j].add(dir);
                    }
                }
                eggs[i][j] = new ArrayList<>();
            }
        }
    }
}