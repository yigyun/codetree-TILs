import java.util.*;
import java.io.*;

/**
R행 C열 R x C
동, 서, 남만 막힘. 북쪽을 통해서 정령이 들어온다.
K명의 정령(골렘은 상하좌우 포함)
1000 + i 가 골렘의 출구 넘버
행 + 1의 값을 result에 저장하기
골렘의 최초 위치는 열에서 (-2, 열)이 되어야 함.
중요한 점 : 남쪽으로 이동을 해야 상태가 저장되어야  함. 단순하게 서쪽, 동쪽만으로 이동은 안하는거임.
동쪽 이동은 + 1, 서쪽 이동은 -1, 남쪽 이동은 변화없음. 
**/

public class Main {

    static int R;
    static int C;
    static int K;
    static int[][] matrix;
    // 북, 동, 남, 서 0,1,2,3
    static int[] dx = new int[]{-1, 0, 1, 0};
    static int[] dy = new int[]{0, 1, 0, -1};

    static class Golem{
        // 중심 위치 x, y
        int x; int y;
        // 출구 방향
        int dir;
        Golem(int x, int y, int dir){
            this.x = x; this.y = y; this.dir = dir;
        }
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        // 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        matrix = new int[R][C];
        boolean[][] visited = new boolean[R][C];
        int result = 0;
        // K 번 반복하기
        for(int t = 1; t <= K; t++){
            st = new StringTokenizer(br.readLine());
            // 열, 출구 방향
            int ci = Integer.parseInt(st.nextToken()) - 1;
            int di = Integer.parseInt(st.nextToken());
            // 골렘 생성
            Golem golem = new Golem(-2, ci, di);
            // move
            boolean move = true;
            while(move){
                move = false;
                // 남쪽
                move = south(t, golem);
                // 서쪽
                if(!move){
                    move = west(t, golem);
                }
                // // 동쪽
                if(!move){
                    move = east(t, golem);
                }
            }
            // 확인할 부분, 현재 골렘의 위치
            if(golem.x < 1 || (golem.y < 1 || golem.y >= C-1)){
                matrix = new int[R][C];
            }else{
                matrix[golem.x + dx[golem.dir]][golem.y + dy[golem.dir]] += 1000;
                // 맵 초기화 없이 점수 계산하기.
                // 현재의 골렘이 이동을 시작해서 최대한 남쪽으로 이동하기.
                Queue<Golem> que = new LinkedList<>();
                visited = new boolean[R][C];
                visited[golem.x][golem.y] = true;
                que.offer(golem);
                int max = golem.x + 1;
                while(!que.isEmpty()){
                    Golem temp = que.poll();
                    int number = matrix[temp.x][temp.y];
                    if(temp.x == R-1){
                        max = R-1; break;
                    }
                    for(int dir = 0; dir < 4; dir++){
                        int nx = temp.x + dx[dir];
                        int ny = temp.y + dy[dir];
                        if(!(nx < 0 || nx >= R || ny < 0 || ny >= C) && !visited[nx][ny]){
                            if(matrix[nx][ny] == number){
                                visited[nx][ny] = true;
                                que.offer(new Golem(nx, ny, temp.dir));
                                max = Math.max(nx, max);
                            }else{
                                if(number > 1000 && matrix[nx][ny] != 0){
                                    visited[nx][ny] = true;
                                    que.offer(new Golem(nx, ny, temp.dir));
                                    max = Math.max(nx, max);
                                }else if(matrix[nx][ny] - 1000 == number){
                                    visited[nx][ny] = true;
                                    que.offer(new Golem(nx, ny, temp.dir));
                                    max = Math.max(nx, max);
                                }
                            }
                        }
                    }
                }
                // StringBuilder sb = new StringBuilder();
                // for(int i = 0; i < R; i++){
                //     for(int j = 0; j < C; j++){
                //         sb.append(matrix[i][j]).append(" ");
                //     }
                //     sb.append('\n');
                // }
                // sb.append("dir: ").append(golem.dir).append("max: ").append(max + 1).append('\n');
                // System.out.print(sb.toString());
                result += max + 1;
            }
        }
        System.out.print(result);
    }

    static int southCount(int nx, int ny){
        if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
            if(matrix[nx][ny] == 0){
                return 1;
            }
            return 0;
        }else if(nx <= R-1) return 1;
        else return 0;
    }

    static boolean south(int num, Golem golem){
        int x = golem.x;
        int y = golem.y;
        int di = golem.dir;
        // x,y를 기준으로 상하좌우 + 중심좌표 이동이 가능한지 보기.
        // (x+2 y), (x+1 y-1),(x+1 y+1)
        int count = 0;
        count += southCount(x+2, y);
        count += southCount(x+1, y-1);
        count += southCount(x+1, y+1);

        // 이동 가능한 곳이다.
        if(count == 3){
            // 이동 처리하기.
            golem.x = x + 1;
            // 비우기 및 채우기
            if(!(x < 0 || x >= R || y < 0 || y >= C)) matrix[x][y] = 0;
            for(int dir = 0; dir < 4; dir++){
                int nx = x + dx[dir];
                int ny = y + dy[dir];
                if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                    matrix[nx][ny] = 0;
                }
            }

            if(!(golem.x < 0 || golem.x >= R || golem.y < 0 || golem.y >= C)) matrix[golem.x][golem.y] = num;
            for(int dir = 0; dir < 4; dir++){
                int nx = golem.x + dx[dir];
                int ny = golem.y + dy[dir];
                if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                    matrix[nx][ny] = num;
                }
            }
            return true;
        }else return false;
    }

    static int westCount(int nx, int ny){
        if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
            if(matrix[nx][ny] == 0){
                return 1;
            }
            return 0;
        }else if(ny >= 0) return 1;
        else return 0;
    }

    static boolean west(int num, Golem golem){
        int x = golem.x;
        int y = golem.y;
        int di = golem.dir;
        int count = 0;
        count += westCount(x, y-2);
        count += westCount(x-1, y-1);
        count += westCount(x+1, y-1);
        if(count == 3){
            // 서쪽 가능 이동 후에 만약 south가 안되면 다시 채우기 비우기 초기화 시켜줘야 함.
            // 이동 처리하기.
            golem.y = y - 1;
            // 비우기 및 채우기
            if(!(x < 0 || x >= R || y < 0 || y >= C)) matrix[x][y] = 0;
            for(int dir = 0; dir < 4; dir++){
                int nx = x + dx[dir];
                int ny = y + dy[dir];
                if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                    matrix[nx][ny] = 0;
                }
            }

            if(!(golem.x < 0 || golem.x >= R || golem.y < 0 || golem.y >= C)) matrix[golem.x][golem.y] = num;
            for(int dir = 0; dir < 4; dir++){
                int nx = golem.x + dx[dir];
                int ny = golem.y + dy[dir];
                if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                    matrix[nx][ny] = num;
                }
            }

            if(south(num, golem)){
                // 이동 성공
                // 방향 바꾸기
                int newDir = golem.dir - 1;
                if(newDir < 0) newDir = 3;
                golem.dir = newDir;
                return true;
            }else{
                // 이동 불가, 원위치 시키기.
                if(!(golem.x < 0 || golem.x >= R || golem.y < 0 || golem.y >= C)) matrix[golem.x][golem.y] = 0;
                for(int dir = 0; dir < 4; dir++){
                    int nx = golem.x + dx[dir];
                    int ny = golem.y + dy[dir];
                    if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                        matrix[nx][ny] = 0;
                    }
                }
                if(!(x < 0 || x >= R || y < 0 || y >= C)) matrix[x][y] = num;
                for(int dir = 0; dir < 4; dir++){
                    int nx = x + dx[dir];
                    int ny = y + dy[dir];
                    if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                        matrix[nx][ny] = num;
                    }
                }
                golem.y = y;
                return false;
            }
        }else return false;
    }

    static int eastCount(int nx, int ny){
        if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
            if(matrix[nx][ny] == 0){
                return 1;
            }
            return 0;
        }else if(ny <= C-1) return 1;
        else return 0;
    }

    static boolean east(int num, Golem golem){
        int x = golem.x;
        int y = golem.y;
        int di = golem.dir;
        int count = 0;
        count += eastCount(x, y+2);
        count += eastCount(x-1, y+1);
        count += eastCount(x+1, y+1);
        if(count == 3){
            // 서쪽 가능 이동 후에 만약 south가 안되면 다시 채우기 비우기 초기화 시켜줘야 함.
            // 이동 처리하기.
            golem.y = y + 1;
            // 비우기 및 채우기
            if(!(x < 0 || x >= R || y < 0 || y >= C)) matrix[x][y] = 0;
            for(int dir = 0; dir < 4; dir++){
                int nx = x + dx[dir];
                int ny = y + dy[dir];
                if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                    matrix[nx][ny] = 0;
                }
            }

            if(!(golem.x < 0 || golem.x >= R || golem.y < 0 || golem.y >= C)) matrix[golem.x][golem.y] = num;
            for(int dir = 0; dir < 4; dir++){
                int nx = golem.x + dx[dir];
                int ny = golem.y + dy[dir];
                if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                    matrix[nx][ny] = num;
                }
            }

            if(south(num, golem)){
                // 이동 성공
                int newDir = golem.dir + 1;
                if(newDir > 3) newDir = 0;
                golem.dir = newDir;
                return true;
            }else{
                // 이동 불가, 원위치 시키기.
                if(!(golem.x < 0 || golem.x >= R || golem.y < 0 || golem.y >= C)) matrix[golem.x][golem.y] = 0;
                for(int dir = 0; dir < 4; dir++){
                    int nx = golem.x + dx[dir];
                    int ny = golem.y + dy[dir];
                    if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                        matrix[nx][ny] = 0;
                    }
                }
                if(!(x < 0 || x >= R || y < 0 || y >= C)) matrix[x][y] = num;
                for(int dir = 0; dir < 4; dir++){
                    int nx = x + dx[dir];
                    int ny = y + dy[dir];
                    if(!(nx < 0 || nx >= R || ny < 0 || ny >= C)){
                        matrix[nx][ny] = num;
                    }
                }
                golem.y = y;
                return false;
            }
        }else return false;
    }

}