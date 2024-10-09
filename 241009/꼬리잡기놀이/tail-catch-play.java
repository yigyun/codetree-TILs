import java.util.*;
import java.io.*;

/**
    n x n
    3명 이상이 한팀
    머리사람, 꼬리사람이 존재함
    각 팀의 이동 선은 끝이 이어져있습니다. 이동 선은 서로 겹치지 않는다.

    1. 각 팀의 이동
    - 머리사람을 따라서 이동한다.

    2. 오른쪽으로, 위로, 왼쪽으로, 아래쪽으로 공을 던진다.
    - 4n번째 라운드를 넘어가면 다시 1라운드 가는 방식.

    3. 공이 날라가다가 사람을 만나면 그 사람이 그 팀에서 머리사람부터 k번째 사람이면 k의 제곱만큼 점수를 얻음.
    - 아무도 안맞으면 점수 x
    - 맞고나서 머리사람과 꼬리사람이 바뀐다.
**/

public class Main {

    static int n; // 격자 크기
    static int m; // 팀의 개수
    static int k; // 라운드 수
    
    static int[] dx = new int[]{0, -1, 0, 1};
    static int[] dy = new int[]{1, 0, -1, 0};
    
    // 0이 빈칸, 1은 머리, 2는 미들 모두, 3은 꼬리, 4는 이동선
    static int[][] map;
    static int[][] teamNumberMap;
    static List<Team> teams;

    static int result;

    static class People{
        int x; int y;
        People(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    static class Team{
        List<People> middle;
        People head;
        People tail;
        List<int[]> route;
        Team(People head, People tail,  List<People> middle, List<int[]> route){
            this.head = head;
            this.tail = tail;
            this.middle = middle;
            this.route = route;
        }
    }

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        teams = new ArrayList<>();
        teamNumberMap = new int[n][n];
        result = 0;

        List<int[]> findTeam = new ArrayList<>();
        
        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
                if(map[i][j] == 1) findTeam.add(new int[]{i, j});
            }
        }

        int teamNum = 1;

        for(int[] current : findTeam){
            People head = new People(current[0], current[1]);
            People tail = null;
            List<People> middle = new ArrayList<>();
            List<int[]> route = new ArrayList<>();

            Queue<int[]> que = new LinkedList<>();
            boolean[][] visited = new boolean[n][n];
            que.offer(new int[]{current[0], current[1]});
            visited[current[0]][current[1]] = true;
            route.add(new int[]{current[0], current[1]});
            teamNumberMap[current[0]][current[1]] = teamNum;

            while(!que.isEmpty()){
                int[] node = que.poll();
                for(int dir = 0; dir < 4; dir++){
                    int nx = node[0] + dx[dir];
                    int ny = node[1] + dy[dir];
                    if(isRange(nx, ny) && !visited[nx][ny] && (map[nx][ny] <= 4 && map[nx][ny] > 1)){
                        if(map[nx][ny] == 2){
                            middle.add(new People(nx, ny));
                        }else if(map[nx][ny] == 3){
                            tail = new People(nx, ny);
                        }
                        visited[nx][ny] = true;
                        que.offer(new int[]{nx, ny});
                        route.add(new int[]{nx, ny});
                        teamNumberMap[nx][ny] = teamNum;
                    }
                }
            }

            teams.add(new Team(head, tail, middle, route));
            teamNum++;
        }

        for(int t = 0; t < k; t++){
            move();
            throwBall(t);
        }

        System.out.print(result);
    }

    static void throwBall(int turn){
        int dir = (turn / n) % 4;
        int position = turn % n;
        int x = 0; int y = 0;

        boolean findCheck = true;

        if(dir == 0){
            y = 0;
            x = position;
        }else if(dir == 1){
            x = n - 1;
            y = position;
        }else if(dir == 2){
            y = n - 1;
            x = (n - 1) - position;
        }else if(dir == 3){
            x = 0;
            y = (n - 1) - position;
        }
        if(map[x][y] >= 1 && map[x][y] < 4){
            findCheck = false;
        }else{
            for(int i = 0; i < n - 1; i++){
                int nx = x + dx[dir];
                int ny = y + dy[dir];
                if(isRange(nx, ny)){
                    x = nx; y = ny;
                }
                if(map[x][y] >= 1 && map[x][y] < 4){ findCheck = false; break;}
            }
        }

        if(!findCheck){
            // 해당 팀을 팀 맵에서 가져오고 
            int teamNum = teamNumberMap[x][y] - 1;
            // 맞은 위치가 몇번째인지 찾고
            Team team = teams.get(teamNum);
            if(x == team.tail.x && y == team.tail.y){
                result += (int)Math.pow(1 + team.middle.size() + 1, 2);
            }else if(x == team.head.x && y == team.head.y){
                result += 1;
            }else{
                for(int i = 0; i < team.middle.size(); i++){
                    People people = team.middle.get(i);
                    if(people.x == x && people.y == y){
                        result += (int)Math.pow(1 + i + 1, 2);
                        break;
                    }
                }
            }

            // 방향 바꾸기
            People temp = team.head;
            team.head = team.tail;
            team.tail = temp;
            Collections.reverse(team.middle);
        }
    }

    static void move(){
        for(Team team : teams){
            // middle의 마지막이 head의 원래 자리로 들어가면 끝이네..
            // tail은 만약 middle이 있으면 미들 끝 자리 가기, 없으면 헤드자리로 가기
            // head는 미들이 있으면 미들 반대편, 미들이 없으면 4가 있는 쪽으로 이동하기.
            
            if(team.middle.size() != 0){
                team.tail = team.middle.get(team.middle.size() - 1);
                
                team.middle.remove(team.middle.size() - 1);
                team.middle.add(0, new People(team.head.x, team.head.y));

                for(int dir = 0; dir < 4; dir++){
                    int nx = team.head.x + dx[dir];
                    int ny = team.head.y + dy[dir];
                    if(isRange(nx, ny) && map[nx][ny] != 2 && map[nx][ny] != 0){
                        team.head.x = nx;
                        team.head.y = ny;
                        break;
                    }
                }
            }else{
                team.tail.x = team.head.x;
                team.tail.y = team.head.y;

                for(int dir = 0; dir < 4; dir++){
                    int nx = team.head.x + dx[dir];
                    int ny = team.head.y + dy[dir];
                    if(isRange(nx, ny) && map[nx][ny] != 3 && map[nx][ny] != 0){
                        team.head.x = nx;
                        team.head.y = ny;
                        break;
                    }
                }
            }

            for(int[] road : team.route){
                map[road[0]][road[1]] = 4;
            }
            map[team.tail.x][team.tail.y] = 3;
            for(People people : team.middle){
                map[people.x][people.y] = 2;
            }
            map[team.head.x][team.head.y] = 1;
        }
    }

    static void print(){
        for(Team team : teams){
            System.out.println("head: x - " + team.head.x + " y - " + team.head.y);
            System.out.println("tail: x - " + team.tail.x + " y - " + team.tail.y);
            for(People people : team.middle){
                System.out.println("middle: x - " + people.x + " y - " + people.y);
            }
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                sb.append(map[i][j]).append(" ");
            }
            sb.append('\n');
        }
        System.out.print(sb.toString());
    }

    static boolean isRange(int nx, int ny){
        return !(nx < 0 || ny < 0 || nx >= n || ny >= n);
    }
}