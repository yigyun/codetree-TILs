import java.util.*;
import java.io.*;

public class Main {
    // 북, 남, 서, 동
    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, 1, -1};
    public static void main(String[] args) {
        // 여기에 코드를 작성해주세요.
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int x = 0; int y = 0;
        for(int i = 0; i < n; i++){
            String dir = sc.next();
            int dis = sc.nextInt();
            if(dir.equals("N")){
                x += dx[2] * dis;
                y += dy[2] * dis;
            } else if(dir.equals("S")){
                x += dx[3] * dis;
                y += dy[3] * dis;
            } else if(dir.equals("W")){
                x += dx[0] * dis;
                y += dy[0] * dis;
            } else if(dir.equals("E")){
                x += dx[1] * dis;
                y += dy[1] * dis;
            }
        }
        System.out.printf("%d %d", x, y);
    }
}