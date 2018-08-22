package norainu2;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		ArrayListie<Integer> ar = new ArrayListie<>();
		for (int i = 0, l = 10; i < l; i++) {
			ar.add(i);
		}

		mapProcedure<Integer> proc = new mapProcedure<Integer>() {

			@Override
			public boolean process(Integer o) {
				return null != o && o > 5;
			}
		};

		ArrayList<Integer> ar2 = ar.map2(proc);

		for (Integer x : ar2) {
			System.out.println(x);
		}
	}

}
