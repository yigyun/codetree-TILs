import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        int[][] matrix = new int[n][n];
        
        for(int i = 0; i < n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++){
                matrix[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int count = 0;
        for(int i = 0; i < n; i++){
            int col = 0; int row = 0;
            int count1 = 0; int count2 = 0;
            boolean check1 = false; boolean check2 = false;
            for(int j = 0; j < n; j++){    
                if (matrix[i][j] != row){
                    count1 = 1;
                    row = matrix[i][j];
                } else count1++;
                if(count1 >= m) check1 = true;

                if(matrix[j][i] != col){
                    count2 = 1;
                    col = matrix[j][i];
                } else count2++;
                if(count2 >= m) check2 = true;
            }
            if(check1) count++;
            if(check2) count++;
        }
        System.out.print(count);
    }
}