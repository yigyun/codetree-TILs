import java.util.*;
import java.io.*;

/**
    n x n 명의 학생
    
**/

public class Main {
    
    static int n;
    static int[] point = new int[]{0, 1, 10, 100, 1000};
    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, -1, 1};

    static int[][] map;
    static Set<Integer>[] student;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        student = new HashSet[(n*n)+1];

        for(int i = 0; i < (n*n); i++){
            st = new StringTokenizer(br.readLine());
            int num = Integer.parseInt(st.nextToken());
            student[num] = new HashSet<>();
            for(int j = 0; j < 4; j++){
                student[num].add(Integer.parseInt(st.nextToken()));
            }
            move(num);
        }
        
        int result = cal();
        System.out.print(result);
    }

    static int cal(){
        int count = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                int num = map[i][j];
                int fc = 0;
                for(int dir = 0; dir < 4; dir++){
                    int nx = i + dx[dir];
                    int ny = j + dy[dir];
                    if(isRange(nx, ny) && student[num].contains(map[nx][ny])){
                        fc++;
                    }
                }
                count += point[fc];
            }
        }
        return count;
    }

    static void printMap(){
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                sb.append(map[i][j]).append(" ");
            }
            sb.append("\n");
        }
        System.out.print(sb.toString());
    }

    static void move(int num){

        // 1 친구의 수
        // 2 주변의 빈 칸 수
        // 3 행 번호가 작은 수
        // 4 열 번호가 작은 수
        PriorityQueue<int[]> que = new PriorityQueue<>((o1, o2) -> {
            int fc1 = 0;
            int fc2 = 0;
            for(int dir = 0; dir < 4; dir++){
                int nx = o1[0] + dx[dir];
                int ny = o1[1] + dy[dir];
                if(isRange(nx, ny) && student[num].contains(map[nx][ny])){
                    fc1++;
                }
                nx = o2[0] + dx[dir];
                ny = o2[1] + dy[dir];
                if(isRange(nx, ny) && student[num].contains(map[nx][ny])){
                    fc2++;
                }
            }
            if(fc1 == fc2){
                int b1 = 0;
                int b2 = 0;
                for(int dir = 0; dir < 4; dir++){
                    int nx = o1[0] + dx[dir];
                    int ny = o1[1] + dy[dir];
                    if(isRange(nx, ny) && map[nx][ny] == 0){
                        b1++;
                    }
                    nx = o2[0] + dx[dir];
                    ny = o2[1] + dy[dir];
                    if(isRange(nx, ny) && map[nx][ny] == 0){
                        b2++;
                    }
                }
                if(b1 == b2){
                    if(o1[0] == o2[0]){
                        return o1[1] - o2[1];
                    }
                    return o1[0] - o2[0];
                }
                return b2 - b1;
            }
            return fc2 - fc1;
        });

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(map[i][j] == 0){
                    que.offer(new int[]{i, j});
                }
            }
        }

        int[] node = que.poll();
        map[node[0]][node[1]] = num;
    }


    static boolean isRange(int nx, int ny){
        return !(nx < 0 || nx >= n || ny < 0 || ny >= n);
    }
}