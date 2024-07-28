import java.util.*;
import java.io.*;

public class Main {

    static int[][] matrix;
    static int n;
    static int m;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        matrix = new int[n][m];
        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < m; j++){
                matrix[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int result = Math.max(bar(), ta());
        System.out.print(result);
    }

    static int bar(){
        int max = 0;
        
        // 가로
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m - 2; j++){
                max = Math.max(max, matrix[i][j] + matrix[i][j+1] + matrix[i][j+2]);
            }
        }

        // 세로
        for(int i = 0; i < n-2; i++){
            for(int j = 0; j < m; j++){
                max = Math.max(max, matrix[i][j] + matrix[i+1][j] + matrix[i+2][j]);
            }
        }

        return max;
    }

    static int ta(){
        int max = 0;

        for(int i = 0; i < n-1; i++){
            for(int j = 0; j < m-1; j++){
                max = Math.max(max, matrix[i][j] + matrix[i][j+1] + matrix[i+1][j]);
                max = Math.max(max, matrix[i][j] + matrix[i+1][j] + matrix[i+1][j+1]);
            }
        }
        return max;
    }
}