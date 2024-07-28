import java.util.*;
import java.io.*;

public class Main {

    static int[][] matrix;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        matrix = new int[n][n];

        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                matrix[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int max = 0;
        for(int i = 0; i < n-2; i++){
            for(int j = 0; j < n-2; j++){
                max = Math.max(max, cal(i,j));
            }
        }

        System.out.print(max);
    }

    static int cal(int i, int j){
        int count = 0;
        for(int x = i; x < i+3; x++){
            for(int y = j; y < j+3; y++){
                if(matrix[x][y] == 1) count++;
            }
        }

        return count;
    }
}