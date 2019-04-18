package testscripts;

public class Estimator {

    public static void main(String args[]) {
        int n = 3;
        int total = 0;
        for(int i = 0; i < n; i++) {
            if(i == 0) {
                total += 1;
            } else if(i < 3) {
                total += 2;
            }  else if(i < 7) {
                total += 3;
            } else if(i < 15) {
                total += 4;
            }  else if(i < 45) {
                total += 5;
            }
        }
        System.out.println(total);
    }

}
