import java.util.*;
import java.io.*;

public class Main {

    static int n; // 크기는 n
    static int[][] map;
    static int[][] teamNum;
    static int m; // 팀의 수
    static int k; // 라운드 수
    
    static int score; // 전체 스코어
    
    static int[] dx = new int[]{0, -1, 0, 1};
    static int[] dy = new int[]{1, 0, -1, 0};

    /**
    head, tail 사람이 있음, 3명 이상이 한팀임.
    이동 선은 사이클이 있음.

    로직 1.
    머리사람의 방향으로 한 칸 이동한다

    로직 2.
    라운드마다 공이 정해진 선을 따라 던져진다.
    방향으로 우, 상, 좌, 하로 반복한다.

    로직 3.
    각 행이나 열로 공이 던져지고 해당 방향으로 이동하면서 최초에 만나는 사람은 점수를 얻음.
    이때, 머리로부터 몇 번째 사람인지를 곱해서 점수를 넣어야 한다.
    머리사람과 꼬리사람이 바뀐다. 방향이 바뀐다

    자료구조
    팀의 점수 합
    Team 클래스에 뭘 넣을까
    팀 번호, 
    
    **/

    static List<Team> teams;

    static class Team{
        List<int[]> middle;
        int[] head;
        int[] tail;
        Team(){
            middle = new ArrayList<>();
            head = new int[2];
            tail = new int[2];
        }
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        score = 0;
        map = new int[n][n];

        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 팀 만들기 bfs
        teams = new ArrayList<>();
        teamNum = new int[n][n];
        makeTeam();

        for(int t = 0; t < k; t++){
            // 팀 이동
            moveTeam();
            // for(int i = 0; i < n; i++){
            //     for(int j = 0; j < n; j++){
            //         System.out.printf("%d ", map[i][j]);
            //     }
            //     System.out.println(" ");
            // }
            // System.out.println("----------");
            int teamNumber = throwBall(t);
            if(teamNumber != -1)
                change(teamNumber);
            // for(int i = 0; i < n; i++){
            //     for(int j = 0; j < n; j++){
            //         System.out.printf("%d ", map[i][j]);
            //     }
            //     System.out.println(" ");
            // }
            // System.out.println("----------");
        }
        System.out.print(score);
    }

    static void change(int teamNumber){
        Team team = teams.get(teamNumber);
        int[] temp = team.head;
        team.head = team.tail;
        team.tail = temp;
        map[team.head[0]][team.head[1]] = 1;
        map[team.tail[0]][team.tail[1]] = 3;
        Collections.reverse(team.middle);
    }

    static int throwBall(int turn){
        int dir = (turn % (n * 4)) / n;
        int num = (turn % (n * 4)) % n;
        int teamNumber = -1;
        if(dir == 0){
            // 오른쪽으로 볼 시작점 0, 0
            for(int y = 0; y < n; y++){
                if(map[num][y] != 0 && map[num][y] != 4){
                    teamNumber = teamNum[num][y];
                    plusScore(num, y, teamNumber);
                    break;
                }
            }
        } else if(dir == 1){
            // 위쪽임 시작점 N-1, 0
            for(int x = n-1; x >= 0; x--){
                if(map[x][num] != 0 && map[x][num] != 4){
                    teamNumber = teamNum[x][num];
                    plusScore(x, num, teamNumber);
                    break;
                }
            }
        } else if(dir == 2){
            // 왼쪽 방향 시작점 N-1, N-1
            num = (n-1) - num;
            for(int y = n-1; y >= 0; y--){
               if(map[num][y] != 0 && map[num][y] != 4){
                    teamNumber = teamNum[num][y];
                    plusScore(num, y, teamNumber);
                    break;
                }
            }
        } else if(dir == 3){
            num = (n-1) - num;
            for(int x = 0; x < n; x++){
                if(map[x][num] != 0 && map[x][num] != 4){
                    teamNumber = teamNum[x][num];
                    plusScore(x, num, teamNumber);
                    break;
                }
            }
        }

        return teamNumber;
    }

    static void plusScore(int x, int y, int teamNumber){
        Team team = teams.get(teamNumber);
        if(team.head[0] == x && team.head[1] == y){
            score += 1;
        } else if(team.tail[0] == x && team.tail[1] == y){
            score += Math.pow(team.middle.size() + 2, 2);
        } else {
            for(int i = 0; i < team.middle.size(); i++){
                int[] temp = team.middle.get(i);
                if(temp[0] == x && temp[1] == y){
                    score += Math.pow(i+2, 2);
                    break;
                }
            }
        }
    }

    static void moveTeam(){
        for(Team team : teams){
            moveHead(team);
            moveMiddle(team);
            moveTail(team);
        }
    }

    static void moveTail(Team team){
        int x = team.tail[0];
        int y = team.tail[1];

        for(int dir = 0; dir < 4; dir++){
            int nx = x + dx[dir];
            int ny = y + dy[dir];
            if(isRange(nx,ny) && map[nx][ny] == 1){
                map[x][y] = 4;
                map[nx][ny] = 3;
                team.tail[0] = nx; team.tail[1] = ny;
                break;
            }
        }
    }

    static void moveMiddle(Team team){
        for(int i = 0; i < team.middle.size(); i++){
            for(int dir = 0; dir < 4; dir++){
                int nx = team.middle.get(i)[0] + dx[dir];
                int ny = team.middle.get(i)[1] + dy[dir];
                if(isRange(nx, ny) && map[nx][ny] == 1){
                    map[team.middle.get(i)[0]][team.middle.get(i)[1]] = 1;
                    map[nx][ny] = 2;
                    team.middle.get(i)[0] = nx; team.middle.get(i)[1] = ny;
                    break;
                }
            }
        }
    }

    static void moveHead(Team team){
        int x = team.head[0];
        int y = team.head[1];

        for(int dir = 0; dir < 4; dir++){
            int nx = x + dx[dir];
            int ny = y + dy[dir];
            if(isRange(nx,ny) && map[nx][ny] == 4){
                map[nx][ny] = 1;
                team.head[0] = nx; team.head[1] = ny;
                break;
            }
        }
    }

    static void makeTeam(){
        
        int teamNumber = 0;
        boolean[][] visited = new boolean[n][n];

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(map[i][j] != 0 && !visited[i][j] && map[i][j] != 4){
                    Queue<int[]> que = new LinkedList<>();
                    visited[i][j] = true;
                    que.offer(new int[]{i, j});
                    Team team = new Team();
                    
                    while(!que.isEmpty()){
                        int[] current = que.poll();
                        if(map[current[0]][current[1]] == 1){
                            team.head[0] = current[0];
                            team.head[1] = current[1];
                        }else if(map[current[0]][current[1]] == 2){
                            team.middle.add(new int[]{current[0], current[1]});
                        }else if(map[current[0]][current[1]] == 3){
                            team.tail[0] = current[0];
                            team.tail[1] = current[1];
                        }
                        teamNum[current[0]][current[1]] = teamNumber;

                        for(int dir = 0; dir < 4; dir++){
                            int nx = current[0] + dx[dir];
                            int ny = current[1] + dy[dir];
                            if(isRange(nx, ny) && !visited[nx][ny] && map[nx][ny] != 0 && map[nx][ny] != 4){
                                visited[nx][ny] = true;
                                que.offer(new int[]{nx, ny});
                            }
                        }
                    }

                    teams.add(team);
                    teamNumber++;
                }
            }
        }
    }

    static boolean isRange(int x, int y){
        return !(x < 0 || x >= n || y < 0 || y >= n);
    }

}