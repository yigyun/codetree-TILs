import java.util.*;
import java.io.*;

/**
5X5 격자에서 3X3 격자 회전 90도 180도 270도에서 선택하기

1. 가능한 회전 방법 중 획득 가치 최소 각도 찾기, 가치가 같다면 각도가 작은 방법 선택하기, 그것도 같으면 열이 가장 작은 구간, 열이 같다면 행이 가장 작은 구간
2. 상하좌우가 3개 이상 연결된 유물 조각은 모여서 유물이 되고 사라진다. 유물의 가치는 사라지는 유물의 수임
3. 유물 조각이 사라진 위치 우선 순위, 열 번호 작은 순 -> 행 번호 큰 순
4. 새로운 유물조각 채우고나서 2번 다시하고 1번 프로세스로 진행하기.
**/


public class Main {

    static int[][] matrix;
    static int K; // 탐사 반복 횟수, 즉 총 라운드
    static int M; // 유물 조각 갯수 M
    static Queue<Integer> plus;
    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, -1, 1};

    static class Node{
        int x; int y; int r; int price;
        Node(int x, int y, int r, int price){
            this.x = x; this.y = y; this.r = r; this.price = price;
        }
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        // 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        StringBuilder sb = new StringBuilder();
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        matrix = new int[5][5];
        plus = new LinkedList<>();
        for(int i = 0; i < 5; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < 5; j++){
                matrix[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        st = new StringTokenizer(br.readLine());
        for(int i = 0; i < M; i++){
            plus.offer(Integer.parseInt(st.nextToken()));
        }
        // 회전하면서 가능한거 찾기
        for(int t = 0; t < K; t++){
            Node node = rotate();
            // 적용하기
            int result = apply(node);
            if(result == 0) break;
            sb.append(result).append(" ");
        }
        System.out.print(sb.toString());
    }

    public static int apply(Node node){
        int total = 0;
        rotateSub(node.x, node.y, node.r);
        total += applyPrice();
        // 채우기.
        fillMatrix();
        int num = applyPrice();
        total += num;
        while(num != 0){
            fillMatrix();
            num = applyPrice();
            total += num;
        }
        return total;
    }


    public static Node rotate(){
        // 중심 좌표 찾기
        Node bestNode = null;
        int bestPrice = -1;

        for(int i = 1; i < 4; i++){
            for(int j = 1; j < 4; j++){
                // 해당 i,j부터 범위 잡고 회전시키면서 가장 큰 놈만 살려서 리스트에 추가하기.
                // 여기서 깊은 복사 해서 matrix 넘기기.
                for(int r = 0; r < 4; r++){
                    rotateSub(i, j, r);
                    int price = findPrice(matrix);
                    if(price > bestPrice || (price == bestPrice && (bestNode == null || compare(i, j, r, bestNode)))){
                        bestNode = new Node(i, j, r, price);
                        bestPrice = price;
                    }
                    rotateSub(i, j, 4 - r);
                }
            }
        }
        // 정렬해서 최고인놈
        return bestNode;
    }

    public static boolean compare(int x, int y, int r, Node bestNode){
        if(r != bestNode.r) return r < bestNode.r;
        if(y != bestNode.y) return y < bestNode.y;
        return x < bestNode.x;
    }

    public static void rotateSub(int x, int y, int r){

        for(int rotation = 0; rotation < r; rotation++){
            int[][] newMatrix = new int[3][3];
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    newMatrix[j][2-i] = matrix[x - 1 + i][y - 1 + j];
                }
            }
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    matrix[x - 1 + i][y - 1 + j] = newMatrix[i][j];
                }
            }
        }
    }

    public static int findPrice(int[][] map){
        boolean[][] visited = new boolean[5][5];
        int price = 0;
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(!visited[i][j]){
                    int number = map[i][j];
                    Queue<int[]> que = new LinkedList<>();
                    ArrayList<int[]> list = new ArrayList<>();
                    que.offer(new int[]{i, j});
                    list.add(new int[]{i, j});
                    visited[i][j] = true;
                    while(!que.isEmpty()){
                        int[] temp = que.poll();
                        for(int dir = 0; dir < 4; dir++){
                            int nx = temp[0] + dx[dir];
                            int ny = temp[1] + dy[dir];
                            if(!(nx < 0 || nx >= 5 || ny < 0 || ny >= 5) && !visited[nx][ny] && map[nx][ny] == number){
                                que.offer(new int[]{nx, ny});
                                list.add(new int[]{nx, ny});
                                visited[nx][ny] = true;
                            }
                        }
                    }
                    if(list.size() >= 3){
                        price += list.size();
                    }
                }
            }
        }
        return price;
    }

    public static int applyPrice(){
        boolean[][] visited = new boolean[5][5];
        int price = 0;
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                if(!visited[i][j]){
                    int number = matrix[i][j];
                    Queue<int[]> que = new LinkedList<>();
                    ArrayList<int[]> list = new ArrayList<>();
                    que.offer(new int[]{i, j});
                    list.add(new int[]{i, j});
                    visited[i][j] = true;
                    while(!que.isEmpty()){
                        int[] temp = que.poll();
                        for(int dir = 0; dir < 4; dir++){
                            int nx = temp[0] + dx[dir];
                            int ny = temp[1] + dy[dir];
                            if(!(nx < 0 || nx >= 5 || ny < 0 || ny >= 5) && !visited[nx][ny] && matrix[nx][ny] == number){
                                que.offer(new int[]{nx, ny});
                                list.add(new int[]{nx, ny});
                                visited[nx][ny] = true;
                            }
                        }
                    }
                    if(list.size() >= 3){
                        price += list.size();
                        for(int[] temp : list){
                            matrix[temp[0]][temp[1]] = 0;
                        }
                    }
                }
            }
        }
        return price;
    }

    public static void fillMatrix(){
        for(int i = 0; i < 5; i++){
            for(int j = 4; j >= 0; j--){
                if(matrix[j][i] == 0){
                    int number = plus.poll();
                    matrix[j][i] = number;
                }
            }
        }
    }
}