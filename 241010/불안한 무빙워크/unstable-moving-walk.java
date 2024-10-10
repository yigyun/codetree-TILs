import java.util.*;
import java.io.*;

/**
    길이가 n의 무빙 워크의 안정성 테스트를 한다.
    1부터 2n까지 있고 2n 다음은 1번 위치로 이동한다.
    안정성은 1씩 감소한다.
    안정성이 0인칸은 올라갈 수 없다.
    각 사람은 1번칸에 올라서서 n번칸에서 내린다.

    1. 무빙워크가 한 칸 회전한다.
    2. 가장 먼저 올라간 사람부터 무빙워크가 회전하는 방향으로 한칸 이동할 수 있다.
    다음 칸이 사람이 있거나, 안정성이 0이면 이동하지 않는다.
    3. 1번 칸에 사람이 없고 안정성이 0이 아니라면 사람을 한 명 더 올린다.
    4. 안정성이 0인 칸이 k개 이상이면 과정을 종료한다.

    시작 인덱스는 0 - turn으로하고 0보다 작으면 2n에서 뺀만큼 하면될듯
    이 시작 인덱스에 사용자가 올라가고 n번 이동하고 종료하기
    
**/

public class Main {

    static int n;
    static int n2;
    static int k;
    static int[] belt;
    static int[] peoples;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        n2 = n * 2;

        peoples = new int[n2];
        belt = new int[n2];
        st = new StringTokenizer(br.readLine());
        for(int i = 0; i < n2; i++){
            belt[i] = Integer.parseInt(st.nextToken());
        }

        int day = 1;
        int index = 0;
        
        while(true){
            // 벨트 움직임
            index -= 1;
            if(index < 0) index = n2 - 1;
            // 무빙워크 위에 있는 사람이 움직일 수 있는지 확인
            for(int i = index; i < (index + n); i++){
                int currentIndex = i % n2;
                int moveIndex = (i + 1) % n2;
                if(peoples[currentIndex] == 0) continue;
                // i에 있는거를 moveIndex로 옮긴다.
                if(peoples[moveIndex] == 0 && belt[moveIndex] > 0){
                    peoples[moveIndex] = 1;
                    belt[moveIndex] -= 1;
                    peoples[currentIndex] = 0;
                }
            }
            // 1번 칸에 사람이 없고 안정성이 0이 아니면 사람을 한 명 더 올리기.
            if(peoples[index] == 0 && belt[index] > 0){
                belt[index] -= 1;
                peoples[index] = 1;
            }
            // 안정성이 0인 칸이 k개 이상이면 과정을 종료한다.
            int count = 0;
            for(int i = 0; i < n2; i++){
                if(belt[i] == 0){
                    count++;
                }
            }
            if(count >= k) break;

            day++;
        }

        System.out.print(day);
    }
    
}