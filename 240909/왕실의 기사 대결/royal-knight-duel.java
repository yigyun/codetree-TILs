/**

L x L 크기의 matrix
빈칸, 함점 or 벽, 갈수없는 범위 = 벽

r,c를 기준으로 이게 좌측 상단으로 h x w 크기의 직사각형을 가짐.
기사의 체력은 k이다.

왕의 명령을 받은 기사는 상하좌우 하나로 한 칸 이동, 기사가 있으면 그 기사는 밀려남. 연쇄적임
만약 연쇄적으로 가는데 끝에 벽이 있으면 모든 기사는 이동 불가. Stack?
다른 기사를 밀치면, 밀려난 애들은 피해를 입음. 명령을 받은 기사는 피해 x 나머지만 피해를 입는다.


**/

    import java.util.*;
    import java.io.*;

    public class Main {
        
        // 상, 우, 하, 좌
        static final int[] dx = new int[]{-1, 0, 1, 0};
        static final int[] dy = new int[]{0, 1, 0, -1};

        // 체스판
        static int[][] matrix;
        static boolean[][] visited;
        static int[][] knight;

        // L 체스판의 크기, N 기사의 수, Q 명령의 수 k 기사의 체력
        static int L; static int N; static int Q;

        static class Knight{
            int x; int y; int h; int w; int k; int number;
            public Knight(int x, int y, int h, int w, int k, int number){
                this.x = x; this.y = y;
                this.h = h; this.w= w;
                this.k = k; this.number = number;
            }
        }

        public static void main(String[] args)throws IOException {
            // 여기에 코드를 작성해주세요.
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringTokenizer st = new StringTokenizer(br.readLine());
            L = Integer.parseInt(st.nextToken()); N = Integer.parseInt(st.nextToken()); Q = Integer.parseInt(st.nextToken());
            int[] knights = new int[L+1];
            int result = 0;
                        // 체스판 기입
            matrix = new int[L][L];
            for(int i = 0; i < L; i++){
                st = new StringTokenizer(br.readLine());
                for(int j = 0; j < L; j++){
                    matrix[i][j] = Integer.parseInt(st.nextToken());
                }
            }
            // 기사
            Map<Integer, Knight> knMap = new HashMap<>(); 
            for(int i = 1; i <= N; i++){
                st = new StringTokenizer(br.readLine());
                int x = Integer.parseInt(st.nextToken()) - 1;
                int y = Integer.parseInt(st.nextToken()) - 1;
                int h = Integer.parseInt(st.nextToken());
                int w = Integer.parseInt(st.nextToken());
                int k = Integer.parseInt(st.nextToken());
                knMap.put(i, new Knight(x, y, h, w, k, i));
                knights[i] = k;
            }
            
            for(int i = 0; i < Q; i++){
                st = new StringTokenizer(br.readLine());
                int knum = Integer.parseInt(st.nextToken());
                int dir = Integer.parseInt(st.nextToken());

                Queue<Knight> que = new LinkedList<>();
                Stack<Knight> stack = new Stack<>();
                // 벽은 2임 벽 통과 가능한지 찾으면서 가기.
                que.offer(knMap.get(knum));
                stack.push(knMap.get(knum));
                while(!que.isEmpty()){
                    Knight temp = que.poll();
                    int nx = temp.x + dx[dir];
                    int ny = temp.y + dy[dir];
                    if(isRange(nx, ny, temp.w, temp.h) && wall(nx, ny, temp.h, temp.w)){
                        for(int num : knMap.keySet()){
                            Knight kn = knMap.get(num);
                            if(kn.k > 0 && temp.number != num && intersect(kn, nx, ny, temp.h, temp.w)){
                                que.offer(kn);
                                stack.push(kn);
                            }
                        }
                    } else {
                        stack = new Stack<>();
                        break;
                    }
                }
                // 스택 크기 보고 뭐가 있으면 이동가능한거임.
                while(!stack.isEmpty()){
                    // 이동하고 값 바꿔주기, 체력 파악하기.
                    Knight temp = stack.pop();
                    int nx = temp.x + dx[dir];
                    int ny = temp.y + dy[dir];
                    int damage = 0;
                    if(temp.number != knum){
                        for(int x = nx; x < nx + temp.h; x++){
                            for(int y = ny; y < ny + temp.w; y++){
                                if(matrix[x][y] == 1){
                                    damage++;
                                }
                            }
                        }
                    }
                    temp.k -= damage;
                    temp.x = nx; temp.y = ny;
                    knMap.put(temp.number, temp);
                }
            }

            for(int number : knMap.keySet()){
                Knight kn = knMap.get(number);
                if(kn.k > 0) {
                    result += knights[number] - kn.k;
                }
            }
            System.out.println(result);
        }

        static boolean isRange(int nx, int ny, int h, int w){
            return !(nx < 0 || nx >= L || ny < 0 || ny >= L
            || (nx + h - 1) < 0 || (nx + h - 1) >= L || (ny + w - 1) < 0 || (ny + w - 1) >= L);
        }

        static boolean intersect(Knight kn, int nx, int ny, int h, int w){
            return (!(kn.x + kn.h-1 < nx || nx + h-1 < kn.x || kn.y + kn.w-1 < ny || ny + w-1 < kn.y));
        }

        static boolean wall(int nx, int ny, int h, int w){
            for(int i = nx; i < nx + h; i++){
                for(int j = ny; j < ny + w; j++){
                    if(matrix[i][j] == 2){
                        return false;
                    }
                }
            }
            return true;
        }

    }